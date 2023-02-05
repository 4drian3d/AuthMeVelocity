plugins {
    `java-library`
    alias(libs.plugins.indra)
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

indra {
    javaVersions {
        testWith().add(17)
    }

    issues {
        url("https://github.com/4drian3d/AuthMeVelocity/issues")
        system("Github Issues")
    }

    github("4drian3d", "AuthMeVelocity") {
        ci(true)
    }

    gpl3OrLaterLicense()

    scm {
        connection("scm:git:git://github.com/4drian3d/AuthMeVelocity.git")
        developerConnection("scm:git:ssh://github.com/4drian3d/AuthMeVelocity.git")
        url("http://github.com/4drian3d/AuthMeVelocity/")
    }

    configurePublications {
        groupId = project.group as String
        artifactId = "api-paper"
        version = project.version as String
        from(components["java"])

        pom {
            developers {
                developer {
                    id.set("4drian3d")
                    name.set("Adrian")
                }
            }
        }
    }
}

