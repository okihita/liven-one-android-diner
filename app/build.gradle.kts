plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.liven.diner"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.liven.diner"
        minSdk = 24
        //noinspection OldTargetApi
        targetSdk = 35
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

    // Core AndroidX Libraries - Fundamental utilities and compatibility layers
    implementation(libs.androidx.core.ktx) // Kotlin extensions for AndroidX Core
    implementation(libs.androidx.appcompat) // Provides app compatibility for older Android versions

    // UI Libraries - Material Design Components & Jetpack Compose
    implementation(libs.material) // Material Design components for views
    implementation(libs.androidx.activity.compose) // Integration for Jetpack Compose in Activities
    implementation(platform(libs.androidx.compose.bom)) // Bill of Materials for Jetpack Compose, manages versions of Compose libraries
    implementation(libs.androidx.ui) // Core Jetpack Compose UI library
    implementation(libs.androidx.ui.graphics) // Jetpack Compose graphics library
    implementation(libs.androidx.ui.tooling.preview) // Jetpack Compose tooling for previews in Android Studio
    implementation(libs.androidx.material3) // Material Design 3 components for Jetpack Compose
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material.icons.extended)


    // Lifecycle Libraries - Managing lifecycle-aware components
    implementation(libs.androidx.lifecycle.runtime.ktx) // Lifecycle runtime Kotlin extensions

    // Unit Testing Libraries
    testImplementation(libs.junit) // JUnit for local unit tests

    // Android Instrumented Testing Libraries
    androidTestImplementation(libs.androidx.junit) // AndroidX Test extensions for JUnit
    androidTestImplementation(libs.androidx.espresso.core) // Espresso for UI testing
    androidTestImplementation(platform(libs.androidx.compose.bom)) // BOM for Compose test dependencies
    androidTestImplementation(libs.androidx.ui.test.junit4) // Jetpack Compose testing utilities for JUnit4

    // Debugging and Tooling Libraries
    debugImplementation(libs.androidx.ui.tooling) // Jetpack Compose tooling, e.g., Layout Inspector
    debugImplementation(libs.androidx.ui.test.manifest) // Test manifest utilities for Jetpack Compose
}