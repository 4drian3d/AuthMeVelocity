plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.runvelocity)
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
    maven("https://maven.elytrium.net/repo/")
}

dependencies {
    compileOnly(libs.velocity.api)
    compileOnly(libs.velocity.proxy)
    annotationProcessor(libs.velocity.api)

    compileOnly(libs.miniplaceholders)
    compileOnly(libs.fastlogin.velocity)
    compileOnly(libs.vpacketevents)
    compileOnly(libs.luckperms)

    implementation(projects.authmevelocityCommon)
    implementation(projects.authmevelocityApiVelocity)

    implementation(libs.libby.velocity)
    implementation(libs.bstats.velocity)

    testImplementation(platform("org.junit:junit-bom:5.9.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.assertj)
}

tasks {
    shadowJar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        archiveBaseName.set("AuthMeVelocity-Velocity")
        archiveClassifier.set("")

        relocate("org.bstats", "io.github._4drian3d.authmevelocity.libs.bstats")
        relocate("net.byteflux.libby", "io.github._4drian3d.authmevelocity.libs.libby")
        relocate("org.spongepowered", "io.github._4drian3d.authmevelocity.libs.sponge")
        relocate("io.leangen.geantyref", "io.github._4drian3d.authmevelocity.libs.geantyref")
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
