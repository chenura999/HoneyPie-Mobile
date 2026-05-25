plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.honeypie.launcher"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.honeypie.launcher"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        // Strip all locales except English
        androidResources.localeFilters += listOf("en")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    // Disable unused build features
    buildFeatures {
        buildConfig = false
        aidl = false
        renderScript = false
        shaders = false
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "kotlin/**"
            excludes += "META-INF/*.kotlin_module"
            excludes += "META-INF/versions/**"
        }
    }
}

dependencies {
    // Only AndroidX core — no AppCompat, no Material, no Compose
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
}
