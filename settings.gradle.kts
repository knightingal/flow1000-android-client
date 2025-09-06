pluginManagement {
    repositories {
        maven {
            url=uri ("https://maven.aliyun.com/repository/public/")
        }
        maven{
            url=uri ("https://maven.aliyun.com/repository/central")
        }
        maven{
            url=uri ("https://maven.aliyun.com/repository/gradle-plugin")
        }
        maven{
            url=uri ("https://storage.googleapis.com/download.flutter.io")
        }
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    val storageUrl: String = System.getenv("FLUTTER_STORAGE_BASE_URL") ?: "https://storage.googleapis.com"
    repositories {
        maven {
            url=uri ("https://maven.aliyun.com/repository/public/")
        }
        maven{
            url=uri ("https://maven.aliyun.com/repository/central")
        }
        maven{
            url=uri ("https://maven.aliyun.com/repository/gradle-plugin")
        }
        maven{
            url=uri ("https://storage.googleapis.com/download.flutter.io")
        }
        google()
        mavenCentral()
        maven("$storageUrl/download.flutter.io")
    }
}

rootProject.name = "flow1000-client"
include(":app")

val filePath = settingsDir.parentFile.toString() + "/flutter_module/.android/include_flutter.groovy"
apply(from = File(filePath))