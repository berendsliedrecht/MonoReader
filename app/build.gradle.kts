plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.berend.rssreader"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.berend.rssreader"
        minSdk = 28
        targetSdk = 35
        versionCode = 2
        versionName = "0.1.1"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // Debug-signed so the release APK is directly sideloadable
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.0")

    implementation("com.google.code.gson:gson:2.10.1")
    // 6.0.6 is the last release built with Kotlin 1.9; 6.0.9+ moved to Kotlin 2.1
    // whose metadata this project's Kotlin 1.9.10 compiler cannot read.
    implementation("com.prof18.rssparser:rssparser:6.0.6")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.material:material-icons-extended:1.7.0")

    // Mudita Mindful Design Framework
    implementation("com.mudita:MMD:1.0.0")

    testImplementation("junit:junit:4.13.2")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
