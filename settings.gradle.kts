pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "oppslag-soknad"
include("app")

dependencyResolutionManagement {
    versionCatalogs {
        create("kelvinLibs") {
            from("no.nav.aap.kelvin:version-catalog:2.0.173")
        }
    }
    @Suppress("UnstableApiUsage")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    @Suppress("UnstableApiUsage")
    repositories {
        exclusiveContent {
            forRepository {
                maven("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
            }
            filter {
                includeGroupByRegex("no\\.nav\\..*")
            }
        }
        mavenCentral()
    }
}