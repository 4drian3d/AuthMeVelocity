plugins {
    id("net.kyori.indra.publishing")
}

indra {
    javaVersions {
        testWith().add(17)
    }

    github("4drian3d", "AuthMeVelocity") {
        ci(true)
    }

    gpl3OrLaterLicense()

    configurePublications {
        groupId = project.group as String
        artifactId = project.name.replaceFirst("authmevelocity-", "")
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
