@file:Suppress("UnstableApiUsage")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "authmevelocity-parent"

listOf("common", "paper", "velocity").forEach {
    include("authmevelocity-$it")
    project(":authmevelocity-$it").projectDir = file(it)
}

listOf("paper", "velocity").forEach {
    include("authmevelocity-api-$it")
    project(":authmevelocity-api-$it").projectDir = file("api/$it")
}

pluginManagement {
    @Suppress("UnstableApiUsage")
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://repo.william278.net/velocity/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://repo.codemc.org/repository/maven-public/") {
            mavenContent {
                includeGroup("com.github.games647")
                includeGroup("fr.xephi")
            }
        }
        maven("https://repo.alessiodp.com/releases/") {
            mavenContent {
                includeGroup("net.byteflux")
            }
        }
    }
}
