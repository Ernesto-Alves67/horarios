import java.io.FileInputStream
import java.util.Properties

val keystoreProperties = Properties()
val envFile = rootProject.file("env.properties")
if (envFile.exists()) {
    keystoreProperties.load(FileInputStream(envFile))
}


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android)
    kotlin("kapt")
}


fun getEnvOrProperty(key: String): String? {
    return System.getenv(key) ?: keystoreProperties.getProperty(key)
}
android {
    namespace = "com.scherzolambda.horarios"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.scherzolambda.horarios"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "v1.10.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file(getEnvOrProperty("STORE_FILE") ?: "horarios-release-key.jks")
            storePassword = getEnvOrProperty("STORE_PASSWORD")
            keyAlias = getEnvOrProperty("KEY_ALIAS")
            keyPassword = getEnvOrProperty("KEY_PASSWORD")
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("debug") {
            isDebuggable =  true

//            applicationIdSuffix = ".debug"
//            versionNameSuffix = "-debug"

            isMinifyEnabled = false
            isShrinkResources = false
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    // Para análise de HTML
    implementation("org.jsoup:jsoup:1.17.2")
    // Para manipulação de JSON (Kotlinx Serialization)
    implementation(libs.kotlinx.serialization.json)
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation("com.google.dagger:hilt-android:2.51")
    kapt("com.google.dagger:hilt-android-compiler:2.51")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    // AndroidX SplashScreen
    implementation("androidx.core:core-splashscreen:1.0.1")
    // ConstraintLayout for Jetpack Compose
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    //    Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
}