plugins {
    alias(libs.plugins.shadow)
}

repositories {
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://repo.alessiodp.com/releases/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly(project(":authmevelocity-common"))
    compileOnly(project(":authmevelocity-api-velocity"))
    compileOnly(libs.velocity)
    compileOnly(libs.miniplaceholders)
    compileOnly(libs.fastlogin.velocity)
    shadow(libs.libby.velocity)
    shadow(libs.bstats.velocity)
    annotationProcessor(libs.velocity)
}

tasks {
    shadowJar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        relocate("net.byteflux.libby", "me.adrianed.authmevelocity.libs.libby")
        relocate("org.bstats", "me.adrianed.authmevelocity.libs.bstats")
        configurations = listOf(project.configurations.shadow.get())
    }
}

