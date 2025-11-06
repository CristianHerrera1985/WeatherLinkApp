plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.gms.google.services) // necesario para Firebase
    kotlin("kapt")
}

android {
    namespace = "com.saintleo.weatherlinkapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.saintleo.weatherlinkapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // --- AndroidX Core y Lifecycle ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // --- Jetpack Compose ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // --- Material Design clásico (para XML y AppCompat) ---
    implementation("com.google.android.material:material:1.12.0")

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // (OLD)--- Credenciales y Google Sign-In (compatibles con Android 7) ---
    //implementation("androidx.credentials:credentials:1.3.0")
    //implementation("com.google.android.gms:play-services-auth:20.7.0")
    //implementation("com.google.android.gms:play-services-base:18.2.0")

    // (OLD)--- Firebase (compatibles con Android 7.0 / API 24) ---
    // implementation("com.google.firebase:firebase-auth:21.2.0")
    // implementation("com.google.firebase:firebase-common:20.3.3")

    // (NEW VERSION)--- Google Sign-In y Credenciales ---
    implementation(libs.androidx.credentials)
    implementation("com.google.android.gms:play-services-auth:21.4.0")


    // (NEW VERSION) --- Firebase Authentication ---
     implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
     implementation("com.google.firebase:firebase-auth-ktx")

    // --- Glide (carga de imágenes de perfil) ---
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    // --- Compatibilidad y UI XML ---
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    // Retrofit + Gson + Coroutines
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    // Lifecycle / ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    // Firebase (Auth ya lo tienes, agrega Firestore si quieres guardar favoritos)
    implementation("com.google.firebase:firebase-firestore-ktx:24.5.0")
    // Optional: Material Components
    implementation("com.google.android.material:material:1.9.0")
}

// Necesario para que Firebase funcione correctamente
apply(plugin = "com.google.gms.google-services")