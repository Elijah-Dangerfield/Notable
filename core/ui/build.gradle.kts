plugins {
    id("notable.android.library")
}

android {
    namespace = "com.dangerfield.core.ui"
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.kotlinx.coroutines)
    implementation(libs.androidx.lifecycle.vm)
    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.google.truth)
}
