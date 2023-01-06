
plugins {
    `kotlin-dsl`
}

group = "com.dangerfield.notable.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:${libs.versions.detekt.get()}")
    implementation("com.google.firebase:firebase-admin:9.1.1")
    implementation("com.google.gms:google-services:4.3.14")
}

gradlePlugin {
    plugins {
        register("androidApplicationCompose") {
            id = "notable.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidApplication") {
            id = "notable.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "notable.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "notable.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidFeature") {
            id = "notable.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }

        register("androidTest") {
            id = "notable.android.test"
            implementationClass = "AndroidTestConventionPlugin"
        }
        register("androidHilt") {
            id = "notable.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }

        register("androidDetekt") {
            id = "notable.android.detekt"
            implementationClass = "AndroidDetektConventionPlugin"
        }

        register("androidCheckstyle") {
            id = "notable.android.checkstyle"
            implementationClass = "AndroidCheckstyleConventionPlugin"
        }
    }
}
