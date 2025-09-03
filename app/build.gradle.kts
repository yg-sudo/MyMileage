plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    id("com.google.gms.google-services")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.yg.mileage"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.yg.mileage"
        minSdk = 26
        targetSdk = 36
        versionCode = 4
        versionName = "2.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true   // AFTER: Enable code shrinking and obfuscation

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.5" // Compatible with Kotlin 1.9.20
    }
    packaging {
        resources {
            resources.excludes.add("META-INF/*")
        }
    }
}


dependencies {
    // Coil
    implementation(libs.coil.compose)
    // Core Android & Kotlin
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.androidx.material3.window.size.class1)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.ui.text)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    androidTestImplementation(libs.androidx.room.testing)
    // Navigation
    implementation(libs.androidx.navigation.compose)
    // Google Services
    implementation(libs.play.services.auth)
    implementation(libs.google.api.services.drive)
    implementation(libs.google.api.client.android)
    implementation(libs.google.oauth.client.jetty)
    implementation(libs.gson)
    implementation(libs.androidx.adaptive) // Adaptive
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    // Debug
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.ui.auth)
    implementation(libs.firebase.analytics)
    // Play Integrity
    implementation(libs.integrity)
    // Fonts
    implementation(libs.androidx.ui.text.google.fonts)
// or a version compatible with your Firebase BoM

}