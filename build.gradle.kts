allprojects {
    apply<JavaPlugin>()

    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}
