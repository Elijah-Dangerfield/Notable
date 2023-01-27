plugins {
    id("notable.android.library")
}

android {
    namespace = "com.dangerfield.core.notesapi"
}

dependencies {
    implementation(libs.kotlinx.coroutines)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.google.truth)
}
