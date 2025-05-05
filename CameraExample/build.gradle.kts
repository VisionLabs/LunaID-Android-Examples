import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import com.android.build.gradle.internal.api.BaseVariantOutputImpl

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
        externalNativeBuild {
            cmake {
                cppFlags.add("-v")
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    applicationVariants.configureEach {
        outputs.configureEach {
            val filter = (this as? BaseVariantOutputImpl)?.getFilter(com.android.build.OutputFile.ABI) ?: "universal"
            (this as? ApkVariantOutputImpl)?.outputFileName = "${rootProject.name}_${System.currentTimeMillis()}_${filter}_${buildType.name}.apk"
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
    implementation("androidx.activity:activity:1.10.0")
    implementation("androidx.camera:camera-core:1.5.0-alpha05")
    implementation("androidx.camera:camera-camera2:1.5.0-alpha05")
    implementation("androidx.camera:camera-lifecycle:1.5.0-alpha05")
    implementation("androidx.camera:camera-video:1.5.0-alpha05")
    implementation("androidx.camera:camera-view:1.5.0-alpha05")
    implementation ("androidx.core:core-ktx:1.15.0")
    implementation ("androidx.appcompat:appcompat:1.7.0")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")

    implementation ("androidx.fragment:fragment-ktx:1.8.5")
    // NOTE: specify both to avoid possible build errors
    implementation ("androidx.lifecycle:lifecycle-viewmodel:2.8.7")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")

    implementation("ai.visionlabs.lunaid:core:$sdkVersion@aar")
    implementation("ai.visionlabs.lunaid:common-arm:$sdkVersion@aar")
//    implementation("ai.visionlabs.lunaid:cnn52-arm:$sdkVersion@aar")
    implementation("ai.visionlabs.lunaid:cnn59-arm:$sdkVersion@aar")
    implementation("ai.visionlabs.lunaid:mask-arm:$sdkVersion@aar")
    implementation("ai.visionlabs.lunaid:mouthestimator-arm:$sdkVersion@aar")
    implementation("ai.visionlabs.lunaid:mouthestimator-arm:$sdkVersion@aar")

    implementation("ai.visionlabs.lunaid:glasses-arm:$sdkVersion@aar")
    implementation("ai.visionlabs.lunaid:security:$sdkVersion@aar")
}
