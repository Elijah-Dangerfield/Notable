plugins {
    id("notable.android.library")
}

android {
    namespace = "com.dangerfield.core.notes"
}

dependencies {
    implementation(project(":core:notes:local"))
    implementation(project(":core:notes:remote"))
    implementation(project(":core:notesApi"))

    implementation(libs.kotlinx.coroutines)
    implementation(libs.javax.inject)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.google.truth)
}
