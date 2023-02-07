plugins {
    alias(libs.plugins.blossom)
    // Required to relocate packages
    alias(libs.plugins.shadow)
    id("authmevelocity.spotless")
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
        relocate("net.byteflux.libby", "io.github._4drian3d.authmevelocity.libs.libby")
        relocate("org.spongepowered", "io.github._4drian3d.authmevelocity.libs.sponge")
        relocate("io.leangen.geantyref", "io.github._4drian3d.authmevelocity.libs.geantyref")
    }
}

blossom {
    replaceTokenIn("src/main/java/me/adrianed/authmevelocity/common/Constants.java")
    replaceToken("{version}", project.version)
    replaceToken("{description}", project.description)
    replaceToken("{configurate}", libs.versions.configurate.get())
    replaceToken("{geantyref}", libs.versions.geantyref.get())
}