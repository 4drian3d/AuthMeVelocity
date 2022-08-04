rootProject.name = "authmevelocity-parent"

listOf("common", "paper", "velocity").forEach {
    include("authmevelocity-$it")
    project(":authmevelocity-$it").projectDir = file(it)
}