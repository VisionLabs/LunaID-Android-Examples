plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "ai.visionlabs.examples.platformapi"
    compileSdk = 33

    defaultConfig {
        applicationId = "ai.visionlabs.examples.platformapi"
        minSdk = 23
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    androidResources {
        ignoreAssetsPatterns.addAll(
            listOf(
                "!glasses_estimation_flwr_arm.plan",
                "!glasses_estimation_flwr_cpu.plan",
                "!mask_clf_v3_arm.plan",
                "!mask_clf_v3_cpu.plan",
                "!oslm_v4_model_1_arm.plan",
                "!oslm_v4_model_1_cpu.plan",
                "!oslm_v4_model_2_arm.plan",
                "!oslm_v4_model_2_cpu.plan",
                "!cnn59m_arm.plan",
                "!cnn59m_cpu.plan",
                "!cnndescriptor_59.conf",
            )
        )
    }

}

dependencies {

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("androidx.core:core-ktx:1.9.0")


    implementation("ai.visionlabs.lunaid:core:1.3.2@aar")
//    implementation(project(":lunaCore"))

}