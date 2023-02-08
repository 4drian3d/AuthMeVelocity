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

    publishReleasesTo("maven central", "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
    publishSnapshotsTo("sonatype snapshots", "https://s01.oss.sonatype.org/content/repositories/snapshots/")

    configurePublications {
        artifactId = project.name

        from(components["java"])

        pom {
            developers {
                developer {
                    id.set("4drian3d")
                    name.set("Adrian Gonzales")
                    email.set("adriangonzalesval@gmail.com")
                }
            }
        }
    }
}
