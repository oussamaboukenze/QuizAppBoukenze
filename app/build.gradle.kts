import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.quizapp_boukenze"
    compileSdk = 36

    fun property(name: String): String =
        providers.gradleProperty(name).orNull
            ?: throw GradleException("Missing Gradle property: $name")

    fun property(name: String, fallback: String): String =
        providers.gradleProperty(name).getOrElse(fallback)

    defaultConfig {
        applicationId = "com.example.quizapp_boukenze"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "MONGO_API_BASE_URL", "\"${property("app.apiBaseUrl")}\"")
        buildConfigField("String", "OLLAMA_USB_BASE_URL", "\"${property("app.ollamaUsbBaseUrl")}\"")
        buildConfigField("String", "OLLAMA_LAN_BASE_URL", "\"${property("app.ollamaLanBaseUrl")}\"")
        buildConfigField("int", "MONGO_API_PORT", property("app.mongoApiPort"))
        buildConfigField("int", "OLLAMA_PORT", property("app.ollamaPort"))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)

    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

tasks.register("reverseLocalPorts") {
    group = "android"
    description = "Forward Android localhost ports to the development machine for MongoDB API and Ollama debug runs."

    doLast {
        fun property(name: String, fallback: String): String =
            providers.gradleProperty(name).getOrElse(fallback)

        val adbName = if (System.getProperty("os.name").lowercase().contains("windows")) "adb.exe" else "adb"
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { localProperties.load(it) }
        }
        val sdkDir = localProperties.getProperty("sdk.dir")
            ?: System.getenv("ANDROID_HOME")
            ?: System.getenv("ANDROID_SDK_ROOT")

        if (sdkDir == null) {
            logger.lifecycle("SDK Android introuvable: definissez sdk.dir, ANDROID_HOME ou ANDROID_SDK_ROOT.")
            return@doLast
        }

        val adbFile = file(sdkDir).resolve("platform-tools").resolve(adbName)

        if (!adbFile.exists()) {
            logger.lifecycle("ADB introuvable: ${adbFile.absolutePath}")
            return@doLast
        }

        val devicesProcess = ProcessBuilder(adbFile.absolutePath, "devices")
            .redirectErrorStream(true)
            .start()
        val devicesOutput = devicesProcess.inputStream.bufferedReader().use { it.readText() }
        val devicesExitCode = devicesProcess.waitFor()

        if (devicesExitCode != 0) {
            logger.lifecycle("Impossible de lire les appareils ADB:\n$devicesOutput")
            return@doLast
        }

        val devices = devicesOutput
            .lineSequence()
            .map { it.trim() }
            .filter { it.endsWith("\tdevice") }
            .map { it.substringBefore("\t") }
            .toList()

        if (devices.isEmpty()) {
            logger.lifecycle("Aucun telephone Android connecte: adb reverse ignore.")
            return@doLast
        }

        val ports = listOf(
            property("app.mongoApiPort", "3000") to "MongoDB API",
            property("app.ollamaPort", "11434") to "Ollama"
        )

        devices.forEach { serial ->
            ports.forEach { (port, label) ->
                val reverseProcess = ProcessBuilder(
                    adbFile.absolutePath,
                    "-s",
                    serial,
                    "reverse",
                    "tcp:$port",
                    "tcp:$port"
                )
                    .redirectErrorStream(true)
                    .start()
                val reverseOutput = reverseProcess.inputStream.bufferedReader().use { it.readText() }
                val reverseExitCode = reverseProcess.waitFor()

                if (reverseExitCode != 0) {
                    throw GradleException("Echec adb reverse $label pour $serial:\n$reverseOutput")
                }
                logger.lifecycle("$label USB reverse active pour $serial: 127.0.0.1:$port -> PC:$port")
            }
        }
    }
}

tasks.register("reverseOllamaPort") {
    group = "android"
    description = "Alias for reverseLocalPorts."
    dependsOn("reverseLocalPorts")
}

tasks.matching { it.name == "installDebug" }.configureEach {
    dependsOn("reverseLocalPorts")
}
