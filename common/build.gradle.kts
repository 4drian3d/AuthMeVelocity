plugins {
    id("net.kyori.blossom") version "1.3.1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    maven("https://repo.alessiodp.com/releases/")
}

dependencies {
    compileOnly("org.spongepowered:configurate-hocon:4.1.2")
    compileOnly("org.slf4j:slf4j-api:1.7.36")
    compileOnly("net.byteflux:libby-core:1.1.5")
    compileOnly("net.kyori:adventure-api:4.11.0")
}

tasks {
    shadowJar {
        relocate("net.byteflux.libby", "me.adrianed.authmevelocity.libs.libby")
        relocate("org.spongepowered", "me.adrianed.authmevelocity.libs.sponge")
        relocate("io.leangen.geantyref", "me.adrianed.authmevelocity.libs.geantyref")
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()

        options.release.set(17)
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

blossom {
    replaceTokenIn("src/main/java/me/adrianed/authmevelocity/common/Constants.java")
    replaceToken("{version}", project.version)
    replaceToken("{description}", project.description)
}