plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.runpaper)
    id("authmevelocity.spotless")
}

dependencies {
    compileOnly(libs.paper)
    compileOnly(libs.authme)

    compileOnly(libs.miniplaceholders)

    implementation(projects.authmevelocityCommon)
    implementation(projects.authmevelocityApiPaper)

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
    runServer {
        minecraftVersion("1.20.4")
    }
    processResources {
        filesMatching("paper-plugin.yml") {
            expand("version" to project.version)
        }
    }
}
