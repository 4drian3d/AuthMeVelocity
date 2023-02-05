plugins {
    `java-library`
    alias(libs.plugins.indra)
    id("authmevelocity.publishing")
}

java {
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    compileOnly(libs.paper)
}

tasks {
    javadoc {
        (options as StandardJavadocDocletOptions).run {
            encoding = Charsets.UTF_8.name()
            links(
                "https://jd.adventure.kyori.net/api/4.11.0/",
                "https://jd.adventure.kyori.net/text-minimessage/4.11.0/",
                "https://jd.papermc.io/paper/1.19/"
            )
        }
    }  
}
