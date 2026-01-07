plugins {
    alias(libs.plugins.ideaext)
    alias(libs.plugins.blossom)
    //id("authmevelocity.spotless")
}

dependencies {
    compileOnly(libs.configurate.hocon)
    compileOnly(libs.libby.core)

    testImplementation(libs.configurate.hocon)
    testImplementation(platform("org.junit:junit-bom:6.0.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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
