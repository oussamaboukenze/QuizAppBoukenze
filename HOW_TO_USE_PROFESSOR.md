# HOW TO USE - EMSI PassionMatch

Ce fichier explique comment preparer l'environnement et lancer le projet EMSI PassionMatch sur Windows et macOS.

## 1. Objectif du projet

EMSI PassionMatch est une application Android d'orientation.

Elle contient:

- une inscription et une connexion utilisateur
- un quiz d'orientation EMSI
- une recommandation de filiere
- une sauvegarde des scores dans MongoDB
- un backend Node.js / Express
- un chatbot IA local avec Ollama
- une recherche des campus EMSI avec Google Maps

## 2. Environnement necessaire

Installer sur le PC Windows ou Mac:

- Android Studio
- JDK 17 ou plus
- Node.js
- MongoDB Community Server
- MongoDB Compass, optionnel mais utile
- Ollama
- Git, optionnel

Sur le telephone Android:

- activer le mode developpeur
- activer le debogage USB
- connecter le telephone au meme Wi-Fi que le PC, ou utiliser USB avec adb reverse

## 3. Structure du projet

```text
QuizApp_BOUKENZE/
├── app/                         Application Android
├── backend/                     API Node.js / Express
├── gradle.properties            Configuration IP backend et Ollama
├── lancer_projet_local.bat      Script de lancement local
├── Rapport_PFA_....docx         Rapport du projet
└── Presentation_PFA_....pptx    Presentation du projet
```

Fichiers importants:

```text
app/src/main/java/com/example/quizapp_boukenze/MainActivity.java
```

Ecran de connexion.

```text
app/src/main/java/com/example/quizapp_boukenze/Register.kt
```

Ecran d'inscription.

```text
app/src/main/java/com/example/quizapp_boukenze/QuizActivity.java
```

Ecran du quiz.

```text
app/src/main/java/com/example/quizapp_boukenze/Score.java
```

Ecran du resultat.

```text
app/src/main/java/com/example/quizapp_boukenze/ChatbotActivity.java
```

Ecran du chatbot.

```text
app/src/main/java/com/example/quizapp_boukenze/EmsiMapActivity.java
```

Ouverture de Google Maps avec la recherche EMSI.

```text
backend/index.js
```

Serveur backend Express.

## 4. Configuration MongoDB

MongoDB doit etre lance sur le PC.

Adresse utilisee par le backend:

```text
mongodb://127.0.0.1:27017/quizapp
```

Le fichier backend/.env doit contenir:

```env
PORT=3000
MONGODB_URI=mongodb://127.0.0.1:27017/quizapp
JWT_SECRET=supersecretkeyquizapp123
```

Pour verifier MongoDB avec Compass:

```text
mongodb://127.0.0.1:27017
```

### Windows

Si MongoDB est installe avec le service Windows, le lancer depuis Services.

Sinon, lancer manuellement:

```powershell
mongod --dbpath C:\mongodb_data
```

Si le dossier n'existe pas:

```powershell
mkdir C:\mongodb_data
mongod --dbpath C:\mongodb_data
```

### macOS

Avec Homebrew:

```bash
brew tap mongodb/brew
brew install mongodb-community
brew services start mongodb-community
```

Verifier:

```bash
mongosh
```

## 5. Configuration Ollama

Ollama doit etre installe et lance sur le PC.

Le modele utilise par l'application est:

```text
llama3.2
```

Installer le modele:

```powershell
ollama pull llama3.2
```

### Windows

Lancer Ollama:

```powershell
$env:OLLAMA_HOST="0.0.0.0:11434"
ollama serve
```

### macOS

Lancer Ollama:

```bash
export OLLAMA_HOST=0.0.0.0:11434
ollama serve
```

Verifier Ollama dans le navigateur du PC:

```text
http://127.0.0.1:11434/api/tags
```

Verifier Ollama depuis le telephone:

```text
http://IP_DU_PC:11434/api/tags
```

Si cette page ne s'ouvre pas sur le telephone, le probleme vient du Wi-Fi ou du pare-feu.

## 6. Configuration de l'adresse IP

Le telephone doit connaitre l'adresse IP du PC.

### Windows

Lancer:

```powershell
ipconfig
```

Prendre l'adresse IPv4 de la carte Wi-Fi.

Exemple:

```text
IPv4 Address . . . . . . . . . . . : 10.81.165.87
```

### macOS

Pour Wi-Fi:

```bash
ipconfig getifaddr en0
```

Pour Ethernet:

```bash
ipconfig getifaddr en1
```

Autre commande possible:

```bash
ifconfig
```

Ensuite modifier le fichier:

```text
gradle.properties
```

Changer ces lignes:

```properties
app.apiBaseUrl=http://IP_DU_PC:3000/
app.ollamaLanBaseUrl=http://IP_DU_PC:11434/
```

Exemple:

```properties
app.apiBaseUrl=http://10.81.165.87:3000/
app.ollamaLanBaseUrl=http://10.81.165.87:11434/
```

Important:

Apres modification de l'IP, il faut rebuild et reinstaller l'application Android.

## 7. Lancement du backend

### Windows

Ouvrir PowerShell dans le dossier du projet:

```powershell
cd C:\Users\oussama\AndroidStudioProjects\QuizApp_BOUKENZE
```

Installer les dependances backend:

```powershell
cd backend
npm install
```

Lancer le serveur:

```powershell
node index.js
```

### macOS

Ouvrir Terminal dans le dossier du projet:

```bash
cd ~/AndroidStudioProjects/QuizApp_BOUKENZE
```

Installer les dependances backend:

```bash
cd backend
npm install
```

Lancer le serveur:

```bash
node index.js
```

Le backend doit afficher:

```text
Server running on http://0.0.0.0:3000
Connected to MongoDB
```

Tester dans le navigateur:

```text
http://127.0.0.1:3000
```

Depuis le telephone:

```text
http://IP_DU_PC:3000
```

## 8. Remplir la base avec les questions

Si la base MongoDB ne contient pas de questions:

Windows:

```powershell
cd backend
node seed.js
```

macOS:

```bash
cd backend
node seed.js
```

Cela insere les questions du quiz dans MongoDB.

Remarque:

Si les questions ne sont pas disponibles depuis MongoDB, l'application utilise aussi des questions locales de secours dans:

```text
QuizQuestionBank.kt
```

## 9. Lancement rapide avec le script

### Windows

Le fichier suivant peut aider a lancer les services:

```text
lancer_projet_local.bat
```

Il essaie de lancer:

- MongoDB
- Ollama
- le backend Node.js

Il faut garder cette fenetre ouverte pendant la demonstration.

Remarque:

Si l'adresse IP affichee dans le script est ancienne, modifier aussi gradle.properties avec la bonne adresse IPv4.

### macOS

Il n'y a pas de fichier `.bat` pour macOS. Utiliser trois terminaux:

Terminal 1:

```bash
brew services start mongodb-community
```

Terminal 2:

```bash
export OLLAMA_HOST=0.0.0.0:11434
ollama serve
```

Terminal 3:

```bash
cd ~/AndroidStudioProjects/QuizApp_BOUKENZE/backend
node index.js
```

## 10. Lancer l'application Android

Dans Android Studio:

1. Ouvrir le dossier du projet:

```text
C:\Users\oussama\AndroidStudioProjects\QuizApp_BOUKENZE
```

2. Attendre la synchronisation Gradle.

3. Connecter un telephone Android ou lancer un emulateur.

4. Cliquer sur Run.

Si un telephone physique est utilise, verifier:

- PC et telephone sur le meme Wi-Fi
- bonne adresse IP dans gradle.properties
- backend lance sur port 3000
- Ollama lance sur port 11434
- pare-feu Windows autorise les ports 3000 et 11434

## 11. Option USB avec adb reverse

Si le Wi-Fi bloque la connexion, utiliser USB.

Brancher le telephone et lancer:

Windows:

```powershell
adb reverse tcp:3000 tcp:3000
adb reverse tcp:11434 tcp:11434
```

macOS:

```bash
adb reverse tcp:3000 tcp:3000
adb reverse tcp:11434 tcp:11434
```

Dans gradle.properties, mettre:

```properties
app.apiBaseUrl=http://127.0.0.1:3000/
app.ollamaUsbBaseUrl=http://127.0.0.1:11434/
app.ollamaLanBaseUrl=
```

Puis rebuild et reinstaller l'application.

## 12. Pare-feu

Si le telephone ne peut pas ouvrir:

```text
http://IP_DU_PC:3000
http://IP_DU_PC:11434/api/tags
```

### Windows

Autoriser les ports:

```powershell
netsh advfirewall firewall add rule name="Node Backend 3000" dir=in action=allow protocol=TCP localport=3000
netsh advfirewall firewall add rule name="Ollama 11434" dir=in action=allow protocol=TCP localport=11434
```

### macOS

Sur macOS, autoriser Node.js et Ollama dans:

```text
System Settings > Network > Firewall
```

Ou desactiver temporairement le pare-feu pendant la demonstration, si le professeur l'autorise.

## 13. Parcours de demonstration

Pendant la soutenance:

1. Lancer MongoDB.
2. Lancer Ollama.
3. Lancer le backend Node.js.
4. Ouvrir l'application Android.
5. Creer un compte ou se connecter.
6. Passer le quiz.
7. Afficher le score et la filiere recommandee.
8. Tester le chatbot.
9. Ouvrir la carte EMSI avec Google Maps.

## 14. Problmes frequents

### Connexion impossible

Verifier:

- backend lance
- MongoDB lance
- bonne IP dans gradle.properties
- telephone et PC sur le meme Wi-Fi
- port 3000 autorise dans le pare-feu

### Chatbot ne repond pas

Verifier:

- Ollama lance
- modele llama3.2 installe
- port 11434 autorise
- telephone peut ouvrir http://IP_DU_PC:11434/api/tags

### Questions non chargees

Verifier:

- backend lance
- MongoDB contient les questions
- lancer node seed.js si besoin

### Google Maps ne s'ouvre pas

Verifier:

- Google Maps installe sur le telephone
- connexion Internet active
- sinon l'application ouvre le navigateur comme solution de secours

## 15. Commandes utiles

Backend:

```powershell
cd backend
npm install
node index.js
```

Backend sur macOS:

```bash
cd backend
npm install
node index.js
```

Seed MongoDB:

```powershell
cd backend
node seed.js
```

Ollama:

```powershell
ollama pull llama3.2
$env:OLLAMA_HOST="0.0.0.0:11434"
ollama serve
```

Ollama sur macOS:

```bash
ollama pull llama3.2
export OLLAMA_HOST=0.0.0.0:11434
ollama serve
```

Build Android:

Windows:

```powershell
.\gradlew.bat :app:assembleDebug
```

macOS:

```bash
./gradlew :app:assembleDebug
```

Test backend:

```text
http://127.0.0.1:3000
```

Test Ollama:

```text
http://127.0.0.1:11434/api/tags
```

## 16. Resume technique

```text
Application Android
        |
        v
Backend Node.js / Express
        |
        v
MongoDB
```

Et pour le chatbot:

```text
Application Android
        |
        v
Ollama local sur PC
        |
        v
Modele llama3.2
```

Le JWT est utilise pour securiser les routes utilisateur, surtout la sauvegarde et la recuperation des scores.
