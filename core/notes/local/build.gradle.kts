
plugins {
    id("notable.android.library")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.dangerfield.core.notes.local"
}

dependencies {
    implementation(libs.room)
    kapt(libs.room.compiler)
    implementation(libs.kotlinx.coroutines)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.google.truth)
}
