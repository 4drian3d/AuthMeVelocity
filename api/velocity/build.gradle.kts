plugins {
    `java-library`
    alias(libs.plugins.indra)
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
        artifactId = "api-velocity"
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
