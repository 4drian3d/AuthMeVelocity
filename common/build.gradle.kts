plugins {
    id("net.kyori.blossom") version "1.3.1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenCentral()
    maven("https://repo.alessiodp.com/releases/")
}

dependencies {
    compileOnly("org.spongepowered:configurate-hocon:4.1.2")
    compileOnly("org.slf4j:slf4j-api:2.0.1")
    compileOnly("net.byteflux:libby-core:1.1.5")
    compileOnly("net.kyori:adventure-api:4.11.0")
}

tasks {
    shadowJar {
        relocate("net.byteflux.libby", "me.adrianed.authmevelocity.libs.libby")
        relocate("org.spongepowered", "me.adrianed.authmevelocity.libs.sponge")
        relocate("io.leangen.geantyref", "me.adrianed.authmevelocity.libs.geantyref")
    }
}

blossom {
    replaceTokenIn("src/main/java/me/adrianed/authmevelocity/common/Constants.java")
    replaceToken("{version}", project.version)
    replaceToken("{description}", project.description)
}