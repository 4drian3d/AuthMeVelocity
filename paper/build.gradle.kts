import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    maven("https://jitpack.io")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://repo.alessiodp.com/releases/")
}

dependencies {
    compileOnly(project(":authmevelocity-common"))
    compileOnly(project(":authmevelocity-api-paper"))
    compileOnly("io.papermc.paper:paper-api:1.19.1-R0.1-SNAPSHOT")
    compileOnly("fr.xephi:authme:5.6.0-SNAPSHOT")
    compileOnly("com.github.4drian3d:MiniPlaceholders:1.1.1")
    shadow("net.byteflux:libby-bukkit:1.1.5")
}

bukkit {
    name = "AuthMeVelocity"
    main = "me.adrianed.authmevelocity.paper.AuthMeVelocityPaper"
    apiVersion = "1.13"
    website = "https://github.com/4drian3d/AuthMeVelocity"
    authors = listOf("xQuickGlare", "4drian3d")
    softDepend = listOf("MiniPlaceholders")
    depend = listOf("AuthMe")
    version = "4.0.0"
}

tasks {
    shadowJar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        relocate("net.byteflux.libby", "me.adrianed.authmevelocity.libs.libby")
        configurations = listOf(project.configurations.shadow.get())
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()

        options.release.set(17)
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))
