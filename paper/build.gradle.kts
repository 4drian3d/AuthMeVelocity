plugins {
    alias(libs.plugins.pluginyml.bukkit)
    alias(libs.plugins.shadow)
}

repositories {
    maven("https://jitpack.io")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://repo.alessiodp.com/releases/")
}

dependencies {
    compileOnly(project(":authmevelocity-common"))
    compileOnly(project(":authmevelocity-api-paper"))
    compileOnly(libs.paper)
    compileOnly(libs.authme)
    compileOnly(libs.miniplaceholders)
    shadow(libs.libby.bukkit)
}

bukkit {
    name = "AuthMeVelocity"
    main = "me.adrianed.authmevelocity.paper.AuthMeVelocityPlugin"
    apiVersion = "1.13"
    website = "https://github.com/4drian3d/AuthMeVelocity"
    description = project.description as String
    authors = listOf("xQuickGlare", "4drian3d")
    softDepend = listOf("MiniPlaceholders")
    depend = listOf("AuthMe")
    version = project.version as String
}

tasks {
    shadowJar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        relocate("net.byteflux.libby", "me.adrianed.authmevelocity.libs.libby")
        configurations = listOf(project.configurations.shadow.get())
    }
}

