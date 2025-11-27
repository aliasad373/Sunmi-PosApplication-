plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.mindcrafts.posapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mindcrafts.posapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 5
        versionName = "1.4"

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
    buildFeatures {
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    //sunmi libs
    implementation ("com.sunmi:printerlibrary:1.0.15")
    implementation ("org.bouncycastle:bcpkix-jdk15on:1.70") // 请检查最新版本
    //
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.intuit.sdp:sdp-android:1.1.1")
    implementation ("com.google.code.gson:gson:2.11.0")

    implementation ("androidx.room:room-runtime:2.6.1");
    annotationProcessor ("androidx.room:room-compiler:2.6.1");

    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0");
    // Gson converter for JSON parsing
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0");
    // Optional: Logging interceptor for debugging
    implementation ("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11");

    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    implementation ("androidx.work:work-runtime:2.9.0")
}