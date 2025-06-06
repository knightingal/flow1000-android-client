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
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
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
    }
}

rootProject.name = "flow1000-client"
include(":app" )
// Include the host app project. Assumed existing content.
// Replace "flutter_module" with whatever package_name you supplied when you ran:
// `$ flutter create -t module [package_name]
val filePath = settingsDir.toString() + "/flutter_module/.android/include_flutter.groovy"
apply(from = File(filePath))
 