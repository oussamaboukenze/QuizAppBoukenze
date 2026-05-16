package com.example.quizapp_boukenze;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GroqChatService {
    @POST("v1/chat/completions")
    Call<ChatResponse> getCompletion(
        @Header("Authorization") String authorization,
        @Body ChatRequest request
    );

    class ChatRequest {
        String model = "llama3.2";
        List<Message> messages;

        public ChatRequest(List<Message> messages) {
            this.messages = messages;
        }
    }

    class Message {
        String role;
        String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    class ChatResponse {
        List<Choice> choices;

        static class Choice {
            Message message;
        }
    }

    static GroqChatService create(String baseUrl) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(8, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GroqChatService.class);
    }
}
