plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    maven("https://jitpack.io")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://repo.alessiodp.com/releases/")
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.1.2-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.1.2-SNAPSHOT")
    compileOnly("com.github.4drian3d:MiniPlaceholders:1.1.1")
    compileOnly("com.github.games647:fastlogin.velocity:1.11-SNAPSHOT")
    shadow("net.byteflux:libby-velocity:1.1.5")
    compileOnly(project(":authmevelocity-common"))
    compileOnly(project(":authmevelocity-api-velocity"))
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()

        options.release.set(17)
    }

    build {
        dependsOn(shadowJar)
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))
