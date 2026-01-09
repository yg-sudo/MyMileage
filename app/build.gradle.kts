/*
 * MyMileage – Your Smart Vehicle Mileage Tracker
 * Copyright (C) 2025  Yojit Ghadi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.yg.mileage"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.yg.mileage"
        minSdk = 26
        targetSdk = 36
        versionCode = 5
        versionName = "2.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

signingConfigs {
        create("release") {
            val keystoreFile = System.getenv("APP_UPLOAD_KEYSTORE_FILE")
            val keystorePassword = System.getenv("APP_UPLOAD_STORE_PASSWORD")
            val keyAlias = System.getenv("APP_UPLOAD_KEY_ALIAS")
            val keyPassword = System.getenv("APP_UPLOAD_KEY_PASSWORD")

            if (keystoreFile != null && keystorePassword != null) {
                storeFile = file(keystoreFile)
                storePassword = keystorePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            }
        }
    }

    buildTypes {
        release {
            // This line causes the error if the block above is missing
            signingConfig = signingConfigs.getByName("release")

            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            javaParameters.set(true)
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15" // Compatible with Kotlin 1.9.20
    }
    packaging {
        resources {
            resources.excludes.add("META-INF/*")
        }
    }
}


dependencies {
    /** Coil */
    implementation(libs.coil.compose)
    /** Core Android & Kotlin */
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.androidx.material3.window.size.class1)
    implementation(libs.material3.adaptive)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.ui.text)
    implementation(libs.androidx.compose.foundation.layout)
    /** Room */
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    /** Vico */
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)
    /** Navigation */
    implementation(libs.androidx.navigation.compose)
    /** Google Services */
    implementation(libs.play.services.auth)
    implementation(libs.google.api.services.drive)
    implementation(libs.google.api.client.android)
    implementation(libs.google.oauth.client.jetty)
    implementation(libs.gson)
    implementation(libs.androidx.adaptive) // Adaptive
    /** Credential Manager */
    implementation(libs.androidx.credential)
    implementation(libs.androidx.credential.play.services.auth)
    implementation(libs.googleid)
    /** Testing */
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    // Room
    androidTestImplementation(libs.androidx.room.testing)
    /** Debug */
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    /** Firebase */
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.ui.auth)
    implementation(libs.firebase.analytics)
    /** Play Integrity */
    implementation(libs.integrity)
    /** Fonts */
    implementation(libs.androidx.ui.text.google.fonts)
    /** CameraX */
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    // ML Kit Text Recognition for Latin script (on-device)
    implementation(libs.play.services.mlkit.text.recognition)
    // Accompanist Permissions for handling runtime camera permissions
    implementation(libs.accompanist.permissions)
    /** Play in-app updates */
    implementation(libs.app.update)
    implementation(libs.app.update.ktx)
    /** Ads */
    implementation(libs.firebase.admob.ads)
    /** Widgets */
    implementation(libs.androidx.glance)
    implementation(libs.androidx.glance.material3)
}
