plugins {
    id("notable.android.library")
}

android {
    namespace = "com.dangerfield.core.common"
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.google.truth)
}
