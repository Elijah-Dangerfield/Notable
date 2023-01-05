import com.notable.convention.shared.getModule

plugins {
    id("notable.android.feature")
}

android {
    namespace = "com.dangerfield.editnote"
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.google.truth)
    //implementation(getModule("libraries:coreCommon"))
}
