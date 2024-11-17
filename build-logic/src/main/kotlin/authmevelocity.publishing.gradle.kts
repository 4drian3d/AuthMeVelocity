plugins {
    `java-library`
    `maven-publish`
    signing
}

java {
    withSourcesJar()
    withJavadocJar()
}

//publishing {
//    publications {
//        create<MavenPublication>("mavenJava") {
//            repositories {
//                maven {
//                    credentials {
//                        username = property("sonatypeTokenUsername")?.toString() ?: ""
//                        password = property("sonatypeTokenPassword")?.toString() ?: ""
//                    }
//
//                    val central = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
//                    val snapshots = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
//
//                    if (project.version.toString().endsWith("SNAPSHOT")) {
//                        name = "SonatypeSnapshots"
//                        setUrl(snapshots)
//                    } else {
//                        name = "OSSRH"
//                        setUrl(central)
//                    }
//                }
//            }
//            from(components["java"])
//            pom {
//                url.set("https://github.com/4drian3d/AuthMeVelocity")
//                licenses {
//                    license {
//                        name.set("GNU General Public License version 3 or later")
//                        url.set("https://opensource.org/licenses/GPL-3.0")
//                    }
//                }
//                scm {
//                    connection.set("scm:git:https://github.com/4drian3d/AuthMeVelocity.git")
//                    developerConnection.set("scm:git:ssh://git@github.com/4drian3d/AuthMeVelocity.git")
//                    url.set("https://github.com/4drian3d/AuthMeVelocity")
//                }
//                developers {
//                    developer {
//                        id.set("4drian3d")
//                        name.set("Adrian Gonzales")
//                        email.set("adriangonzalesval@gmail.com")
//                    }
//                }
//                issueManagement {
//                    name.set("GitHub")
//                    url.set("https://github.com/4drian3d/AuthMeVelocity/issues")
//                }
//                ciManagement {
//                    name.set("GitHub Actions")
//                    url.set("https://github.com/4drian3d/AuthMeVelocity/actions")
//                }
//                name.set(project.name)
//                description.set(project.description)
//                url.set("https://github.com/4drian3d/AuthMeVelocity")
//            }
//        }
//    }
//}
//
//signing {
//    useGpgCmd()
//    sign(configurations.archives.get())
//    sign(publishing.publications["mavenJava"])
//}

