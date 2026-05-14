plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose) // Hỗ trợ Jetpack Compose
    id("com.google.gms.google-services") // Bắt buộc cho Firebase
}

android {
    namespace = "com.example.movieapplication"
    compileSdk = 36 // Cập nhật theo bản mới nhất bạn cung cấp

    defaultConfig {
        applicationId = "com.example.movieapplication"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // --- Firebase (Sử dụng BOM để quản lý version) ---
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-auth") // Đăng nhập
    implementation("com.google.firebase:firebase-firestore") // Cơ sở dữ liệu

    // --- AI Integration (Google Gemini) ---
    // Thêm thư viện này để hiện thực hóa các tính năng AI bạn đã đưa vào CV
    implementation("com.google.ai.client.generativeai:generativeai:0.7.0")

    // --- Jetpack Compose (Sử dụng BOM) ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3) // Material Design 3
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // --- Retrofit & Networking ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // --- Image Loading (Glide) ---
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // --- Media Player (ExoPlayer) ---
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")

    // --- Architecture Components (Lifecycle, ViewModel) ---
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // --- UI Legacy (Nếu bạn vẫn dùng XML/RecyclerView kèm Compose) ---
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // --- Unit Testing & Mocking ---
    testImplementation(libs.junit)
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("kotlinx.coroutines:coroutines-test:1.7.1")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // --- Parceler ---
    implementation("org.parceler:parceler-api:1.1.13")
    annotationProcessor("org.parceler:parceler:1.1.13")

    debugImplementation(libs.androidx.compose.ui.tooling)
}