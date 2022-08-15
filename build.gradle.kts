import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    maven("https://repo.alessiodp.com/releases/")
}

allprojects {
    apply<JavaPlugin>()
    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    tasks.compileJava {
        options.encoding = Charsets.UTF_8.name()

        options.release.set(17)
    }

    java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

dependencies {
    shadow(project(":authmevelocity-common", "shadow"))
    shadow(project(":authmevelocity-api-paper"))
    shadow(project(":authmevelocity-api-velocity"))
    shadow(project(":authmevelocity-velocity", "shadow"))
    shadow(project(":authmevelocity-paper", "shadow"))
}

tasks {
    shadowJar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        archiveFileName.set("AuthMeVelocity.jar")
        configurations = listOf(project.configurations.shadow.get())
        exclude("net/byteflux/libby/**/")
        exclude("org/bstats/**/")
    }

    build {
        dependsOn(shadowJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()

        options.release.set(17)
    }
}

