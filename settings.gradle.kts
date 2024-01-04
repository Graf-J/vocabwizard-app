pluginManagement {
    repositories {
        google()
        jcenter()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://jitpack.io") // Corrected syntax for Kotlin DSL
        google()
        jcenter()
    }
}

rootProject.name = "vocab-wizard-app"
include(":app")
 