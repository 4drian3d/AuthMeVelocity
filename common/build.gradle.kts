plugins {
    alias(libs.plugins.ideaext)
    alias(libs.plugins.blossom)
    id("authmevelocity.spotless")
}

dependencies {
    compileOnly(libs.configurate.hocon)
    compileOnly(libs.libby.core)

    testImplementation(libs.configurate.hocon)
    testImplementation(platform("org.junit:junit-bom:5.10.1"))
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
