plugins {
    id("authmevelocity.publishing")
    id("authmevelocity.spotless")
}

dependencies {
    compileOnly(libs.velocity)
}

tasks {
    javadoc {
        (options as StandardJavadocDocletOptions).run{
            encoding = Charsets.UTF_8.name()
            links(
                "https://jd.advntr.dev/api/4.12.0/",
                "https://jd.advntr.dev/text-minimessage/4.12.0/",
                "https://jd.papermc.io/velocity/3.0.0/"
            )
        }
    }
}
