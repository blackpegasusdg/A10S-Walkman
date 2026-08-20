plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.dip.a10swalkman"

    compileSdk = 35

    defaultConfig {
        applicationId = "com.dip.a10swalkman"

        minSdk = 23
        targetSdk = 35

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }
    configurations.all {
        resolutionStrategy {
            force("androidx.browser:browser:1.8.0")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }

        debug {
            isMinifyEnabled = false
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

    // =========================
    // ANDROIDX
    // =========================

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")


    // =========================
    // COMPOSE
    // =========================

    implementation(platform("androidx.compose:compose-bom:2025.04.01"))

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")

    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")


    // =========================
    // NAVIGATION
    // =========================

    implementation("androidx.navigation:navigation-compose:2.9.0")


    // =========================
    // SUPABASE
    // =========================

    implementation(platform("io.github.jan-tennert.supabase:bom:3.2.1"))

    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:postgrest-kt:3.2.4")
    implementation("io.github.jan-tennert.supabase:auth-kt:3.2.4")
    implementation("io.ktor:ktor-client-android:3.1.3")
    implementation("io.github.jan-tennert.supabase:realtime-kt")


    // =========================
    // KTOR
    // =========================

    implementation("io.ktor:ktor-client-android:3.1.3")


    // =========================
    // TESTING
    // =========================

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")

    androidTestImplementation(
        platform("androidx.compose:compose-bom:2025.04.01")
    )

    androidTestImplementation(
        "androidx.compose.ui:ui-test-junit4"
    )


    // =========================
    // DEBUG
    // =========================

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}