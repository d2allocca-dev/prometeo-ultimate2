# Compilazione Android su Windows

1. Installa Android Studio stabile e Android SDK Platform 35, Build Tools e Platform Tools.
2. Installa JDK 17 (Android Studio Embedded JDK va bene).
3. Apri la cartella `PROMETEO_ULTIMATE_MULTIPLATFORM` in Android Studio.
4. Imposta Gradle JDK su JDK 17 e attendi il sync.
5. Da terminale nella cartella progetto esegui `gradle :androidApp:assembleDebug` (oppure usa il task Gradle `androidApp > Tasks > build > assembleDebug`).
6. L'APK debug, se la compilazione termina con BUILD SUCCESSFUL, sarà in `androidApp/build/outputs/apk/debug/androidApp-debug.apk`.
7. Installa con `adb install -r androidApp/build/outputs/apk/debug/androidApp-debug.apk`.

Il progetto fornito non contiene il Gradle Wrapper. Prima della distribuzione, genera e committa il wrapper con una versione Gradle compatibile con i plugin del progetto. Una build debug non è una release firmata per distribuzione.
