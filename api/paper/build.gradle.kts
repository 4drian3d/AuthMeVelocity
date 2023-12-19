plugins {
    id("authmevelocity.publishing")
    id("authmevelocity.spotless")
}

dependencies {
    compileOnly(libs.paper)
}

tasks {
    javadoc {
        (options as StandardJavadocDocletOptions).run {
            encoding = Charsets.UTF_8.name()
            links(
                "https://jd.advntr.dev/api/${libs.versions.adventure.get()}/",
                "https://jd.advntr.dev/text-minimessage/${libs.versions.adventure.get()}/",
                "https://jd.papermc.io/paper/1.20/"
            )
        }
    }  
}
