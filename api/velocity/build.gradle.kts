plugins {
    `java-library`
    id("authmevelocity.publishing")
    id("authmevelocity.spotless")
}

dependencies {
    compileOnly(libs.velocity)
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks {
    javadoc {
        (options as? StandardJavadocDocletOptions)?.run{
            encoding = Charsets.UTF_8.name()
            links(
                "https://jd.adventure.kyori.net/api/4.12.0/",
                "https://jd.adventure.kyori.net/text-minimessage/4.12.0/",
                "https://jd.papermc.io/velocity/3.0.0/"
            )
        }
    }
}
