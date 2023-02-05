plugins {
    alias(libs.plugins.blossom)
    // Required to relocate packages
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
    maven("https://repo.alessiodp.com/releases/")
}

dependencies {
    compileOnly(libs.configurate.hocon)
    compileOnly(libs.libby.core)
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
    replaceToken("{configurate}", libs.versions.configurate.get())
    replaceToken("{geantyref}", libs.versions.geantyref.get())
}