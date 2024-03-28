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
//
//include (":models-cnn52")
//project(":models-cnn52").projectDir = File(rootDir, "../sdk/lunaid/cnn52")
//
//include (":models-cnn59")
//project(":models-cnn59").projectDir = File(rootDir, "../sdk/lunaid/cnn59")
//
//include (":models-oslm")
//project(":models-oslm").projectDir = File(rootDir, "../sdk/lunaid/oslm")
//
//include (":models-mask")
//project(":models-mask").projectDir = File(rootDir, "../sdk/lunaid/mask")
//
//include (":models-glasses")
//project(":models-glasses").projectDir = File(rootDir, "../sdk/lunaid/glasses-arm")
