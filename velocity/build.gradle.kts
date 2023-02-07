plugins {
    alias(libs.plugins.shadow)
    id("authmevelocity.spotless")
}

repositories {
    maven("https://repo.codemc.org/repository/maven-public/") {
        mavenContent {
            includeGroup("com.github.games647")
        }
    }
    maven("https://repo.alessiodp.com/releases/") {
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
    compileOnly(libs.velocity)
    annotationProcessor(libs.velocity)

    compileOnly(libs.miniplaceholders)
    compileOnly(libs.fastlogin.velocity)

    implementation(projects.authmevelocityCommon)
    implementation(projects.authmevelocityApiVelocity)

    implementation(libs.libby.velocity)
    implementation(libs.bstats.velocity)
}

tasks {
    shadowJar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        archiveFileName.set("AuthMeVelocity-Velocity-${project.version}.jar")
        archiveClassifier.set("")

        relocate("net.byteflux.libby", "io.github._4drian3d.authmevelocity.libs.libby")
        relocate("org.bstats", "io.github._4drian3d.authmevelocity.libs.bstats")
    }

    build {
        dependsOn(shadowJar)
    }
}

