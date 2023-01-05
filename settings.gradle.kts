pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "Notable"

include("app")
include("features:editNote")
include("features:notesList")
include("libraries:coreCommon")
include("libraries:coreNote")