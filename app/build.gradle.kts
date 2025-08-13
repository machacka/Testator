plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    //id("com.google.gms.google-services")
}

android {
    namespace = "com.example.cloudedge"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.cloudedge"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Java 17 partout
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    // Active le DataBinding
    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Auth Google (Google Identity Services)
    //implementation("com.google.android.gms:play-services-auth:21.2.0")
    //implementation("com.google.android.gms:play-services-identity:18.1.0")
    //implementation("com.google.android.gms:play-services-base:18.5.0")

    // HTTP API calls
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")

    // Logging avec Timber
    implementation("com.jakewharton.timber:timber:5.0.1")
}
