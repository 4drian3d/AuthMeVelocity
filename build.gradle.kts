import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

allprojects {
    apply(plugin = "java")
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    shadow(project(":authmevelocity-common"))
    shadow(project(":authmevelocity-api-paper"))
    shadow(project(":authmevelocity-api-velocity"))
    shadow(project(":authmevelocity-velocity"))
    shadow(project(":authmevelocity-paper"))
}

tasks {
    shadowJar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        archiveFileName.set("AuthMeVelocity.jar")
        configurations = listOf(project.configurations.shadow.get())
    }

    build {
        dependsOn(shadowJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()

        options.release.set(17)
    }
}