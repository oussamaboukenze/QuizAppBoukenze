package com.example.quizapp_boukenze;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.BuildersKt;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotActivity extends AppCompatActivity {
    public static final String EXTRA_CONTEXT = "com.example.quizapp_boukenze.EXTRA_CONTEXT";

    private static final String TAG = "ChatbotActivity";
    private static final String BASE_SYSTEM_PROMPT =
            "Tu es l'assistant EMSI pour les visiteurs de l'application EMSI PassionMatch. " +
            "Reponds en francais, de facon claire et concise, uniquement aux questions sur l'EMSI, " +
            "ses campus, ses filieres, l'orientation, les inscriptions et la vie etudiante. " +
            "Si tu n'es pas certain d'une information officielle, conseille de contacter l'administration EMSI.";

    private final List<ChatMessage> chatMessages = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private RecyclerView rvChat;
    private EditText etUserQuestion;
    private ImageButton bSendQuestion;
    private ImageButton bVoiceQuestion;
    private TextView tvConnectionStatus;
    private ActivityResultLauncher<String> audioPermissionLauncher;
    private ActivityResultLauncher<Intent> speechLauncher;
    private String discoveredBaseUrl = AppConfig.ollamaLanBaseUrl();
    private String systemPrompt = BASE_SYSTEM_PROMPT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);
        SystemBarHelper.apply(findViewById(R.id.rootView), 0, 0, 0);

        ImageButton bBack = findViewById(R.id.bBack);
        rvChat = findViewById(R.id.rvChat);
        etUserQuestion = findViewById(R.id.etUserQuestion);
        bVoiceQuestion = findViewById(R.id.bVoiceQuestion);
        bSendQuestion = findViewById(R.id.bSendQuestion);
        tvConnectionStatus = findViewById(R.id.tvConnectionStatus);
        systemPrompt = buildSystemPrompt();
        setupVoiceInput();

        bBack.setOnClickListener(v -> finish());

        chatMessages.add(new ChatMessage(
                "Bonjour ! Je suis l'assistant EMSI. Posez-moi vos questions sur l'EMSI, les campus, les filieres ou l'orientation.",
                false
        ));

        chatAdapter = new ChatAdapter(chatMessages);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(chatAdapter);

        bSendQuestion.setOnClickListener(v -> sendQuestion());
        bVoiceQuestion.setOnClickListener(v -> startVoiceInput());
        etUserQuestion.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendQuestion();
                return true;
            }
            return false;
        });

        discoverOllama();
    }

    private void discoverOllama() {
        tvConnectionStatus.setText("Connexion a Ollama...");
        new Thread(() -> {
            try {
                String ip = BuildersKt.runBlocking(EmptyCoroutineContext.INSTANCE, (scope, continuation) ->
                        OllamaDiscovery.INSTANCE.findOllamaServer(this, continuation)
                );

                if (ip != null) {
                    discoveredBaseUrl = "http://" + ip + ":" + AppConfig.ollamaPort() + "/";
                }

                runOnUiThread(() -> tvConnectionStatus.setText("Serveur: " + discoveredBaseUrl));
            } catch (Exception e) {
                Log.e(TAG, "Erreur lors de la detection Ollama", e);
                runOnUiThread(() -> tvConnectionStatus.setText("Serveur: " + discoveredBaseUrl));
            }
        }).start();
    }

    private void sendQuestion() {
        String userMsg = etUserQuestion.getText().toString().trim();
        if (userMsg.isEmpty()) {
            return;
        }

        chatMessages.add(new ChatMessage(userMsg, true));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        rvChat.scrollToPosition(chatMessages.size() - 1);
        etUserQuestion.setText("");

        setComposerEnabled(false);
        chatMessages.add(new ChatMessage("Je cherche la reponse...", false));
        int loadingIndex = chatMessages.size() - 1;
        chatAdapter.notifyItemInserted(loadingIndex);
        rvChat.scrollToPosition(loadingIndex);

        List<GroqChatService.Message> apiMessages = new ArrayList<>();
        apiMessages.add(new GroqChatService.Message("system", systemPrompt));
        for (int i = 0; i < loadingIndex; i++) {
            ChatMessage msg = chatMessages.get(i);
            apiMessages.add(new GroqChatService.Message(msg.isUser() ? "user" : "assistant", msg.getContent()));
        }

        requestCompletion(apiMessages, loadingIndex, getOllamaBaseUrls(), 0);
    }

    private void requestCompletion(
            List<GroqChatService.Message> apiMessages,
            int loadingIndex,
            List<String> baseUrls,
            int baseUrlIndex
    ) {
        String baseUrl = baseUrls.get(baseUrlIndex);

        GroqChatService.create(baseUrl)
                .getCompletion("", new GroqChatService.ChatRequest(apiMessages))
                .enqueue(new Callback<GroqChatService.ChatResponse>() {
                    @Override
                    public void onResponse(Call<GroqChatService.ChatResponse> call, Response<GroqChatService.ChatResponse> response) {
                        runOnUiThread(() -> {
                            String responseContent = extractResponseContent(response);
                            if (responseContent != null) {
                                discoveredBaseUrl = baseUrl;
                                tvConnectionStatus.setText("Serveur: " + discoveredBaseUrl);
                                removeLoadingMessage(loadingIndex);
                                addBotMessage(responseContent);
                                setComposerEnabled(true);
                            } else if (hasNextBaseUrl(baseUrls, baseUrlIndex)) {
                                requestCompletion(apiMessages, loadingIndex, baseUrls, baseUrlIndex + 1);
                            } else {
                                removeLoadingMessage(loadingIndex);
                                addBotMessage("Erreur serveur Ollama (" + response.code() + "). Verifiez que le modele llama3.2 est installe.");
                                setComposerEnabled(true);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<GroqChatService.ChatResponse> call, Throwable t) {
                        Log.e(TAG, "Erreur Ollama avec " + baseUrl, t);
                        if (hasNextBaseUrl(baseUrls, baseUrlIndex)) {
                            requestCompletion(apiMessages, loadingIndex, baseUrls, baseUrlIndex + 1);
                            return;
                        }

                        runOnUiThread(() -> {
                            removeLoadingMessage(loadingIndex);
                            int port = AppConfig.ollamaPort();
                            addBotMessage("Connexion echouee. Serveurs testes: " + String.join(", ", baseUrls) + ". Verifiez que le telephone est sur le meme reseau que le PC ou utilisez adb reverse tcp:" + port + " tcp:" + port + ".");
                            setComposerEnabled(true);
                        });
                    }
                });
    }

    private List<String> getOllamaBaseUrls() {
        return AppConfig.ollamaBaseUrls(discoveredBaseUrl);
    }

    private boolean hasNextBaseUrl(List<String> baseUrls, int baseUrlIndex) {
        return baseUrlIndex + 1 < baseUrls.size();
    }

    private String extractResponseContent(Response<GroqChatService.ChatResponse> response) {
        if (response.isSuccessful() && response.body() != null
                && response.body().choices != null
                && !response.body().choices.isEmpty()
                && response.body().choices.get(0).message != null
                && response.body().choices.get(0).message.content != null) {
            return response.body().choices.get(0).message.content;
        }
        return null;
    }

    private void addBotMessage(String message) {
        chatMessages.add(new ChatMessage(message, false));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        rvChat.scrollToPosition(chatMessages.size() - 1);
    }

    private void removeLoadingMessage(int loadingIndex) {
        if (loadingIndex >= 0 && loadingIndex < chatMessages.size()) {
            chatMessages.remove(loadingIndex);
            chatAdapter.notifyItemRemoved(loadingIndex);
        }
    }

    private void setComposerEnabled(boolean enabled) {
        etUserQuestion.setEnabled(enabled);
        bSendQuestion.setEnabled(enabled);
        bVoiceQuestion.setEnabled(enabled);
        bSendQuestion.setAlpha(enabled ? 1f : 0.45f);
        bVoiceQuestion.setAlpha(enabled ? 1f : 0.45f);
    }

    private String buildSystemPrompt() {
        String context = getIntent().getStringExtra(EXTRA_CONTEXT);
        if (context == null || context.trim().isEmpty()) {
            return BASE_SYSTEM_PROMPT;
        }
        return BASE_SYSTEM_PROMPT + " Contexte actuel: " + context.trim();
    }

    private void setupVoiceInput() {
        speechLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null) {
                        return;
                    }

                    ArrayList<String> matches = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (matches == null || matches.isEmpty()) {
                        Toast.makeText(this, "Aucune voix detectee", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    etUserQuestion.setText(matches.get(0));
                    etUserQuestion.setSelection(etUserQuestion.getText().length());
                    sendQuestion();
                }
        );

        audioPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (Boolean.TRUE.equals(isGranted)) {
                        launchSpeechRecognizer();
                    } else {
                        Toast.makeText(this, "Permission micro refusee", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void startVoiceInput() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "Reconnaissance vocale indisponible sur ce telephone", Toast.LENGTH_LONG).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            launchSpeechRecognizer();
        } else {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }
    }

    private void launchSpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toLanguageTag());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Posez votre question a l'assistant EMSI");

        try {
            speechLauncher.launch(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Aucune application de reconnaissance vocale trouvee", Toast.LENGTH_LONG).show();
        }
    }
}
