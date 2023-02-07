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