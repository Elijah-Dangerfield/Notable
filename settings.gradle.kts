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
include("core:common")
include("core:notes")
include("core:notes:local")
include("core:notesApi")
include("features:auth")
include("features:editNote")
include("features:notesList")
include("core:notes:remote")