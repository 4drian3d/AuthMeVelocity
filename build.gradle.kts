plugins {
    java
    alias(libs.plugins.shadow)
}

repositories {
    maven("https://repo.alessiodp.com/releases/") {
        mavenContent {
            includeGroup("net.byteflux")
        }
    }
}

allprojects {
    apply<JavaPlugin>()

    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    tasks.compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

dependencies {
    shadow(project(":authmevelocity-common", "shadow"))
    shadow(projects.authmevelocityApiPaper)
    shadow(projects.authmevelocityApiVelocity)
    shadow(project(":authmevelocity-velocity", "shadow"))
    shadow(project(":authmevelocity-paper", "shadow"))
}

tasks {
    shadowJar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        archiveFileName.set("AuthMeVelocity.jar")
        configurations = listOf(project.configurations.shadow.get())
        exclude("net/byteflux/libby/**/")
        exclude("org/bstats/**/")
    }

    build {
        dependsOn(shadowJar)
    }
}

