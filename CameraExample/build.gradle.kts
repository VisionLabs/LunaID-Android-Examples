val sdkVersion: String = DepVersions.sdkVersion

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "ai.visionlabs.examples.camera"
    compileSdk = DepVersions.compileSdkVersion
    buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "ai.visionlabs.examples.camera"
        minSdk = DepVersions.minSdkVersion
        targetSdk = DepVersions.targetSdkVersion
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "SDK_VERSION", "\"$sdkVersion\"")

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

    implementation("ai.visionlabs.lunaid:core:$sdkVersion@aar")
    implementation("ai.visionlabs.lunaid:common-arm:$sdkVersion@aar")
    implementation("ai.visionlabs.lunaid:cnn52-arm:$sdkVersion@aar")
    implementation("ai.visionlabs.lunaid:cnn59-arm:$sdkVersion@aar")
    implementation("ai.visionlabs.lunaid:mask-arm:$sdkVersion@aar")
    implementation("ai.visionlabs.lunaid:glasses-arm:$sdkVersion@aar")
//    implementation("ai.visionlabs.lunaid:oslm-arm:$sdkVersion@aar")

//    implementation("ai.visionlabs.lunaid:common-x86:$sdkVersion@aar")
//    implementation("ai.visionlabs.lunaid:cnn52-x86:$sdkVersion@aar")
//    implementation("ai.visionlabs.lunaid:cnn59-x86:$sdkVersion@aar")
//    implementation("ai.visionlabs.lunaid:mask-x86:$sdkVersion@aar")
//    implementation("ai.visionlabs.lunaid:oslm-x86:$sdkVersion@aar")
//    implementation("ai.visionlabs.lunaid:glasses-x86:$sdkVersion@aar")

//    implementation(files("libs/lunaid-core-9f7183c7.aar"))
//    implementation(files("libs/lunaid-cnn52-arm-9f7183c7.aar"))
//    implementation(files("libs/lunaid-cnn59-arm-9f7183c7.aar"))
//    implementation(files("libs/lunaid-common-arm-9f7183c7.aar"))
//    implementation(files("libs/lunaid-glasses-arm-9f7183c7.aar"))
//    implementation(files("libs/lunaid-mask-arm-9f7183c7.aar"))
//    implementation(files("libs/lunaid-oslm-arm-9f7183c7.aar"))

//    implementation(project(":lunaCore"))
//    implementation(project(":models-cnn52"))
//    implementation(project(":models-cnn59"))
//    implementation(project(":models-common"))
//    implementation(project(":models-glasses"))
//    implementation(project(":models-mask"))
//    implementation(project(":models-oslm"))


}
