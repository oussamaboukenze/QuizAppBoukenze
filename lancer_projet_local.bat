@echo off
title QuizApp Backend Console
echo ==========================================
echo    LANCEMENT DU BACKEND LOCAL (MONGODB)
echo ==========================================
echo.

echo [1/3] Verification de MongoDB...
tasklist /FI "IMAGENAME eq mongod.exe" 2>NUL | find /I /N "mongod.exe">NUL
if "%ERRORLEVEL%"=="0" (
    echo [OK] MongoDB est deja en cours d'execution.
) else (
    echo [!] MongoDB n'est pas lance. Tentative de lancement...
    if not exist "C:\Users\oussama\mongodb_data" mkdir "C:\Users\oussama\mongodb_data"
    start /b "" "C:\Program Files\MongoDB\Server\8.2\bin\mongod.exe" --dbpath "C:\Users\oussama\mongodb_data"
    timeout /t 5
)

echo [2/3] Configuration Ollama pour le telephone...
set OLLAMA_HOST=0.0.0.0:11434
tasklist /FI "IMAGENAME eq ollama.exe" 2>NUL | find /I /N "ollama.exe">NUL
if "%ERRORLEVEL%"=="0" (
    echo [!] Ollama est deja lance. Si le telephone ne se connecte pas, fermez Ollama puis relancez ce fichier.
) else (
    echo [OK] Lancement Ollama sur 0.0.0.0:11434...
    start /b "" ollama serve
    timeout /t 2
)

echo [3/3] Lancement du serveur Node.js...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":3000" ^| findstr "LISTENING"') do (
    echo [!] Arret de l'ancien backend sur le port 3000: %%a
    taskkill /PID %%a /F >NUL 2>NUL
)
cd backend
start /b "" node index.js
echo.
echo ==========================================
echo    BACKEND PRET POUR TELEPHONE : http://192.168.1.190:3000
echo    OLLAMA PRET POUR TELEPHONE  : http://192.168.1.190:11434
echo    UTILISEZ COMPASS SUR : localhost:27017
echo ==========================================
echo.
echo Gardez cette fenetre ouverte.
pause
