plugins {
    `maven-publish`
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
