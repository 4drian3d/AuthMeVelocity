plugins {
    alias(libs.plugins.pluginyml.bukkit)
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
}

bukkit {
    name = "AuthMeVelocity"
    main = "io.github._4drian3d.authmevelocity.paper.AuthMeVelocityPlugin"
    apiVersion = "1.13"
    website = project.property("url") as String
    description = project.description as String
    authors = listOf("xQuickGlare", "4drian3d")
    softDepend = listOf("MiniPlaceholders")
    depend = listOf("AuthMe")
    version = project.version as String
}

tasks {
    shadowJar {
        archiveFileName.set("AuthMeVelocity-Paper-${project.version}.jar")
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
        minecraftVersion("1.19.3")
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))
