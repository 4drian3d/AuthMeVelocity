plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://repo.alessiodp.com/releases/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly(project(":authmevelocity-common"))
    compileOnly(project(":authmevelocity-api-velocity"))
    compileOnly("com.velocitypowered:velocity-api:3.1.2-SNAPSHOT")
    compileOnly("com.github.4drian3d:MiniPlaceholders:1.3.1")
    compileOnly("com.github.games647:fastlogin.velocity:1.12-SNAPSHOT")
    shadow("net.byteflux:libby-velocity:1.1.5")
    shadow("org.bstats:bstats-velocity:3.0.0")
    annotationProcessor("com.velocitypowered:velocity-api:3.1.2-SNAPSHOT")
}

tasks {
    shadowJar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        relocate("net.byteflux.libby", "me.adrianed.authmevelocity.libs.libby")
        relocate("org.bstats", "me.adrianed.authmevelocity.libs.bstats")
        configurations = listOf(project.configurations.shadow.get())
    }
}

