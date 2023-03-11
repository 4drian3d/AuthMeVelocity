plugins {
    alias(libs.plugins.blossom)
    id("authmevelocity.spotless")
}

repositories {
    mavenCentral()
    maven("https://repo.alessiodp.com/releases/")
}

dependencies {
    compileOnly(libs.configurate.hocon)
    compileOnly(libs.libby.core)

    testImplementation(libs.configurate.hocon)
    testImplementation(platform("org.junit:junit-bom:5.9.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.assertj)
}

blossom {
    replaceTokenIn("src/main/java/io/github/_4drian3d/authmevelocity/common/Constants.java")
    replaceToken("{version}", project.version)
    replaceToken("{description}", project.description)
    replaceToken("{configurate}", libs.versions.configurate.get())
    replaceToken("{geantyref}", libs.versions.geantyref.get())
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "failed")
        }
    }
}
