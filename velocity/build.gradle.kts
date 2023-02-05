plugins {
    alias(libs.plugins.shadow)
}

repositories {
    maven("https://repo.codemc.org/repository/maven-public/") {
        mavenContent {
            includeGroup("com.github.games647")
        }
    }
    maven("https://repo.alessiodp.com/releases/"){
        mavenContent {
            includeGroup("net.byteflux")
        }
    }
    maven("https://jitpack.io") {
        mavenContent {
            includeGroup("com.github.4drian3d")
        }
    }
}

dependencies {
    compileOnly(projects.authmevelocityCommon)
    compileOnly(projects.authmevelocityApiVelocity)
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

