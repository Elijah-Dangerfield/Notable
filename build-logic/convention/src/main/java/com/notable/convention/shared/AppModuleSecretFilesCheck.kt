package com.notable.convention.shared

import org.gradle.api.Project
import java.io.File
import java.lang.System.getenv

internal fun Project.checkForAppModuleSecretFiles() {

    val secretFilesNeeded = listOf(
        File("${project.projectDir}/google-services.json"),
        File("${project.projectDir}/service-account-key.json")
    )

    val missingServiceAccountMessage = "\n\n\n\n" + """
                    //////////////////////////////// ACTION NEEDED /////////////////////////////////////
                    
                    A drive-service-account_key.json is needed to download secret files from google drive and get this 
                    project running.  Please download the drive-service_account-key.json here: 
                    https://drive.google.com/file/d/1-fC0Sq04tmB2TcYHyL5W4vX-fsbtq1g4/view?usp=share_link
                    
                    You can save the file either in the root directory of this project (for temporary usage) 
                    or in your home directory (for continual development)
                    
                    if you wish to save the file in your home directory make sure to export the path
                    
                    copy the path and add the following line to your ~/.bashrc 
                    or ~/.zshrc (depending on your set up)
                    
                    export NOTABLE_SERVICE_KEY_PATH="{INSERT_PATH}"
  
                    there will be an offline only flavor that can be ran alternatively
    """.trimIndent()

    fun isAppMissingSecretFiles(): Boolean = secretFilesNeeded.any { !it.isFile }

    fun installSecretFiles() {
        val driveServiceKeyPath =
            getenv("NOTABLE_SERVICE_KEY_PATH") ?: "${project.rootDir}/drive-service-account-key.json"

        if (!File(driveServiceKeyPath).isFile) {
            printRed(missingServiceAccountMessage)
            @Suppress("UseCheckOrError")
            throw IllegalStateException(missingServiceAccountMessage)
        } else {
            val result = ProcessBuilder("./scripts/get_secret_files.main.kts", driveServiceKeyPath)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
                .waitFor()

            @Suppress("TooGenericExceptionThrown")
            if (result != 0) {

                @Suppress("TooGenericExceptionThrown")
                throw Exception(
                    """
                   Failed to run ./scripts/get_secret_files.main.kts with input "$driveServiceKeyPath"
                   Please see AppModuleSecretFilesCheck.kt
                    """.trimIndent()
                )
            } else {
                printGreen("Finished downloading all secret files")
            }
        }
    }

    if (isAppMissingSecretFiles()) {
        printRed(
            "MISSING SECRET FILES FOR PROJECT ${project.name.toUpperCase()}. " +
                "\nUPDATING ALL SECRET FILES."
        )
        installSecretFiles()
    } else {
        printGreen("Project ${project.name } has all Secret files needed.")
    }
}
