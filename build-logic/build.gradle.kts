plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.plugin.shadow)
    implementation(libs.build.indra.common)
    implementation(libs.build.indra.spotless)
    implementation("com.vanniktech.maven.publish:com.vanniktech.maven.publish.gradle.plugin:0.36.0")
}

repositories {
    gradlePluginPortal()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
