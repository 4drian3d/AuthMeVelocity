plugins {
    alias(libs.plugins.pluginyml.bukkit)
    alias(libs.plugins.shadow)
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
    maven("https://jitpack.io") {
        mavenContent {
            includeGroup("com.github.4drian3d")
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
        //duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        archiveFileName.set("AuthMeVelocity-Paper-${project.version}.jar")
        archiveClassifier.set("")
        relocate("net.byteflux.libby", "io.github._4drian3d.authmevelocity.libs.libby")
    }

    build {
        dependsOn(shadowJar)
    }
}

tasks.compileJava {
    options.encoding = Charsets.UTF_8.name()
    options.release.set(17)
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))
