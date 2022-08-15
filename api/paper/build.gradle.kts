plugins {
    `maven-publish`
}

java {
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.1-R0.1-SNAPSHOT")
}

tasks {
    javadoc {
        options.encoding = Charsets.UTF_8.name()
        (options as StandardJavadocDocletOptions).links(
            "https://jd.adventure.kyori.net/api/4.11.0/",
            "https://jd.adventure.kyori.net/text-minimessage/4.11.0/",
            "https://jd.papermc.io/paper/1.19/"
        )
    }  
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group as String
            artifactId = project.name
            version = project.version as String
            from(components["java"])
        }
    }
}
