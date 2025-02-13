plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.idmark"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.idmark"
        minSdk = 24
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
        viewBinding = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.biometric.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // ML Kit Face Detection Library
    implementation(libs.face.detection)

    // Google Play Services ML Kit Face Detection
    implementation(libs.play.services.mlkit.face.detection)

    // CameraX for live face scanning (Optional but recommended)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // Glide for loading images (Optional)
    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    // TensorFlow Lite (Optional if you want advanced ML features)
    implementation(libs.tensorflow.lite)

    // Fingerprint
    implementation(libs.androidx.biometric.ktx)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.biometric.v120alpha05)




}