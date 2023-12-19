allprojects {
    apply<JavaPlugin>()

    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
}
