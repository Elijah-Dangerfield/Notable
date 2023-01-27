plugins {
    id("notable.android.library")
}

android {
    namespace = "com.dangerfield.core.navigation"
}

dependencies {
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.google.truth)
}
