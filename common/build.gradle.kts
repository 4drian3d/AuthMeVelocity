plugins {
    alias(libs.plugins.blossom)
    id("authmevelocity.spotless")
}

repositories {
    maven("https://repo.alessiodp.com/releases/")
}

dependencies {
    compileOnly(libs.configurate.hocon)
    compileOnly(libs.libby.core)

    testImplementation(libs.configurate.hocon)
    testImplementation(platform("org.junit:junit-bom:5.9.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.assertj)
}

sourceSets {
    main {
        blossom {
            javaSources {
                property("version", project.version.toString())
                property("configurate", libs.versions.configurate.get())
                property("geantyref", libs.versions.geantyref.get())
            }
        }
    }
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "failed")
        }
    }
}
