pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        ivy {
            url = java.net.URI.create("https://download.visionlabs.ru/")
            patternLayout {
                artifact ("releases/lunaid-[artifact]-[revision].[ext]")
                setM2compatible(false)
            }
            credentials {
                username = getLocalProperty("vl.login") as String
                password = getLocalProperty("vl.pass") as String
            }
            metadataSources { artifact() }
        }
    }
}

fun getLocalProperty(key: String, file: String = "local.properties"): Any {
    val file = File(rootProject.projectDir, file)
    val properties = java.util.Properties()
    val localProperties = file
    if (localProperties.isFile) {
        java.io.InputStreamReader(java.io.FileInputStream(localProperties), Charsets.UTF_8).use { reader ->
            properties.load(reader)
        }
    } else error("File from not found: '$file'")

    if (!properties.containsKey(key)) {
        error("Key not found '$key' in file '$file'")
    }
    return properties.getProperty(key)
}


rootProject.name = "Luna ID Examples"

include(":CameraExample")
//include(":PlatformAPIExample")

include(":lunaCore")
project(":lunaCore").projectDir = File(rootDir, "../sdk/lunaid/core")

include(":models-common")
project(":models-common").projectDir = File(rootDir, "../sdk/lunaid/common-arm")

include (":models-cnn59")
project(":models-cnn59").projectDir = File(rootDir, "../sdk/lunaid/cnn59-arm")

include (":models-cnn60")
project(":models-cnn60").projectDir = File(rootDir, "../sdk/lunaid/cnn60-arm")

include (":models-oslm")
project(":models-oslm").projectDir = File(rootDir, "../sdk/lunaid/oslm-arm")

include (":models-mask")
project(":models-mask").projectDir = File(rootDir, "../sdk/lunaid/mask-arm")

include (":models-mouthestimator")
project(":models-mouthestimator").projectDir = File(rootDir, "../sdk/lunaid/mouthestimator-arm")

include (":models-glasses")
project(":models-glasses").projectDir = File(rootDir, "../sdk/lunaid/glasses-arm")

include (":security")
project(":security").projectDir = File(rootDir, "../sdk/lunaid/security")
