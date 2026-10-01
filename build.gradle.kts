plugins {
    base
}

for (taskName in listOf("clean", "build", "assemble", "check")) {
    tasks.named(taskName) {
        dependsOn(subprojects.map { it.path + ":$taskName" })
    }
}

tasks.named("check") {
    dependsOn(gradle.includedBuild("build-logic").task(":check"))
}
