plugins {
    id("notable.android.feature")
}

android {
    namespace = "com.dangerfield.auth"
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.google.truth)
    // implementation(getModule("libraries:coreCommon"))
}
