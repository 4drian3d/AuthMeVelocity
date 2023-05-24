plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.runpaper)
    id("authmevelocity.spotless")
}

repositories {
    maven("https://repo.codemc.org/repository/maven-public/") {
        mavenContent {
            includeGroup("fr.xephi")
        }
    }
    maven("https://repo.alessiodp.com/releases/"){
        mavenContent {
            includeGroup("net.byteflux")
        }
    }
}

dependencies {
    compileOnly(libs.paper)
    compileOnly(libs.authme)

    compileOnly(libs.miniplaceholders)

    implementation(projects.authmevelocityCommon)
    implementation(projects.authmevelocityApiPaper)

    implementation(libs.libby.bukkit)
    implementation(libs.libby.paper)
}

tasks {
    shadowJar {
        archiveBaseName.set("AuthMeVelocity-Paper")
        archiveClassifier.set("")

        relocate("net.byteflux.libby", "io.github._4drian3d.authmevelocity.libs.libby")
        relocate("org.spongepowered", "io.github._4drian3d.authmevelocity.libs.sponge")
        relocate("io.leangen.geantyref", "io.github._4drian3d.authmevelocity.libs.geantyref")
    }
    build {
        dependsOn(shadowJar)
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    runServer {
        minecraftVersion("1.19.4")
    }
    processResources {
        filesMatching(listOf("paper-plugin.yml", "plugin.yml")) {
            expand("version" to project.version)
        }
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))
