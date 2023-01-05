import com.notable.convention.shared.getModule

plugins {
    id("notable.android.library")
}

android {
    namespace = "com.dangerfield.corenote"
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.google.truth)
    //implementation(getModule("libraries:coreCommon"))
}
