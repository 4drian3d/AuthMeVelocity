plugins {
    id("net.kyori.indra.publishing.sonatype") version "3.0.1"
}

allprojects {
    apply<JavaPlugin>()

    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

indraSonatype {
    useAlternateSonatypeOSSHost("s01")
}
