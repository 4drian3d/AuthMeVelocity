plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.runvelocity)
    id("authmevelocity.spotless")
}

dependencies {
    compileOnly(libs.velocity.api)
    compileOnly(libs.velocity.proxy)
    annotationProcessor(libs.velocity.api)

    compileOnly(libs.miniplaceholders)
    compileOnly(libs.fastlogin.velocity) {
        isTransitive = false
    }
    compileOnly(libs.vpacketevents)
    compileOnly(libs.luckperms)

    implementation(projects.authmevelocityCommon)
    implementation(projects.authmevelocityApiVelocity)

    implementation(libs.bstats.velocity)

    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.assertj)
}

tasks {
    shadowJar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        archiveBaseName.set("AuthMeVelocity-Velocity")
        archiveClassifier.set("")

        relocate("org.bstats", "io.github._4drian3d.authmevelocity.libs.bstats")
    }
    build {
        dependsOn(shadowJar)
    }
    runVelocity {
        velocityVersion(libs.versions.velocity.get())
    }
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "failed")
        }
    }
}
