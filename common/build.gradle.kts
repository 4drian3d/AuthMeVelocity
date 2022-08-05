plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    maven("https://repo.alessiodp.com/releases/")
}

dependencies {
    compileOnly("space.arim.dazzleconf:dazzleconf-core:1.3.0-M1")
    compileOnly("space.arim.dazzleconf:dazzleconf-ext-snakeyaml:1.3.0-M1")
    compileOnly("org.slf4j:slf4j-api:1.7.36")
    compileOnly("net.byteflux:libby-core:1.1.5")
}

tasks {
    shadowJar {
        relocate("net.byteflux.libby", "me.adrianed.authmevelocity.libs.libby")
        relocate("space.arim.dazzleconf", "me.adrianed.authmevelocity.libs.dazzleconf")
    }
}