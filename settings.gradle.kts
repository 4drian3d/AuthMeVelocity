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
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}