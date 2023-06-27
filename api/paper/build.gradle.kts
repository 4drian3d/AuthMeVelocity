plugins {
    id("authmevelocity.publishing")
    id("authmevelocity.spotless")
    id("authmevelocity.java")
}

dependencies {
    compileOnly(libs.paper)
}

tasks {
    javadoc {
        (options as StandardJavadocDocletOptions).run {
            encoding = Charsets.UTF_8.name()
            links(
                "https://jd.advntr.dev/api/4.12.0/",
                "https://jd.advntr.dev/text-minimessage/4.12.0/",
                "https://jd.papermc.io/paper/1.19/"
            )
        }
    }
}
