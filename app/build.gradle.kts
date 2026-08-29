import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.gms.google.services)
    id("androidx.navigation.safeargs.kotlin")
    kotlin("plugin.serialization") version "2.3.20"

}

val secretsFile = rootProject.file("secrets.properties")
val secretsProperties = Properties().apply {
    if (secretsFile.exists()) {
        load(FileInputStream(secretsFile))
    }
}

android {
    namespace = "com.samedtevin.bagcilarapp"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.samedtevin.bagcilarapp"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["GOOGLE_MAPS_API_KEY"] = secretsProperties.getProperty("GOOGLE_MAPS_API_KEY", "")

    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // ViewBinding
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.firebase.ai)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:34.17.0"))

    // Firebase App Check - local development
    implementation("com.google.firebase:firebase-appcheck-debug")

    // Navigation &  Views/Fragments Integration
    val nav_version = "2.9.8"

    implementation ("androidx.navigation:navigation-fragment:$nav_version")
    implementation ("androidx.navigation:navigation-ui:$nav_version")

    // Worm Dots Indicator
    implementation("com.tbuonomo:dotsindicator:5.1.1")

    // DataStore
    implementation ("androidx.datastore:datastore-preferences:1.1.6")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")

    // Glide
    implementation("com.github.bumptech.glide:glide:5.0.5")

    implementation("io.noties.markwon:core:4.6.2")

    implementation(libs.play.services.maps)

}