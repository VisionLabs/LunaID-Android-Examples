plugins {
    id("com.android.application")
    id("kotlin-android")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "ai.visionlabs.examples.camera"
    compileSdk = 33

    defaultConfig {
        applicationId = "ai.visionlabs.examples.camera"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
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
        viewBinding = true
    }
    lint {
        // disable after https://issuetracker.google.com/issues/169249668 fix
        disable += "NullSafeMutableLiveData"
    }
    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = true
        }
    }
}

dependencies {
    implementation ("androidx.core:core-ktx:1.9.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.8.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")

    implementation ("androidx.fragment:fragment-ktx:1.6.1")
    // NOTE: specify both to avoid possible build errors
    implementation ("androidx.lifecycle:lifecycle-viewmodel:2.5.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")

    implementation("ai.visionlabs.lunaid:core:1.7.0-alpha1@aar")
    implementation("ai.visionlabs.lunaid:cnn52:1.6.0@aar")
    implementation("ai.visionlabs.lunaid:cnn59:1.6.0@aar")

//    implementation(project(":lunaCore"))
//    implementation(project(":models-cnn52"))
//    implementation(project(":models-cnn59"))
//    implementation(project(":models-oslm"))
}
