#!/usr/bin/env kotlin

@file:Repository("https://repo1.maven.org/maven2")
@file:DependsOn("com.google.auth:google-auth-library-oauth2-http:1.14.0")
@file:DependsOn("com.google.apis:google-api-services-drive:v3-rev197-1.25.0")

import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.ServiceAccountCredentials
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

// These file ids can be found in the url of the sharable google drive links to these files
data class FileInfo(val id: String, val pathToStore: String)

val firebaseGoogleServiceJson =
    FileInfo(
        id = "1blmGvAxS6jc7foqgjIzH8DU5m4ZOJsY0",
        pathToStore = "app/google-services.json"
    )

val firebaseServiceAccountKeyJson =
    FileInfo(
        id = "1lx6au_L4eI85Bgpd2kX8vWKWmyciB7yS",
        pathToStore = "app/service-account-key.json"
    )

val fileInfoList = listOf(
    firebaseGoogleServiceJson,
    firebaseServiceAccountKeyJson,
)

val red = "\u001b[31m"
val green = "\u001b[32m"
val reset = "\u001b[0m"

fun printRed(text: String) {
    println(red + text + reset)
}

fun printGreen(text: String) {
    println(green + text + reset)
}

val isHelpCall = args.isNotEmpty() && (args[0] == "-h" || args[0].contains("help"))
if (isHelpCall) {
    printRed(
        """
        This script collects every required secret file from the google drive 
        a service account key file. You must either pass in the path to the drive-service-account-key.json
        file or have it installed in the root directory. 
        
        You can download the drive-service-account-key.json file from the following link: 
        https://drive.google.com/file/d/1-fC0Sq04tmB2TcYHyL5W4vX-fsbtq1g4/view?usp=share_link
        
        Usage: ./get_secret_files.main.kts [option]
        option: path to drive-service-account-key.json file used to access google drive
        """.trimIndent()
    )

    @Suppress("TooGenericExceptionThrown")
    throw Exception("See Message Above")
}

val serviceAccountKeyPath = args.getOrNull(0) ?: run {
    if (File("drive-service-account-key.json").isFile) return@run "drive-service-account-key.json"
    printRed(
        """
        You must either pass in the path to the sdrive-service-account-key.json
        file or have it installed in the root directory. 
        
        You can download the drive-service-account-key.json file from the following link: 
        https://drive.google.com/file/d/1-fC0Sq04tmB2TcYHyL5W4vX-fsbtq1g4/view?usp=share_link
        """.trimIndent()
    )
    @Suppress("TooGenericExceptionThrown")
    throw Exception("No drive-service-account-key.json file found")
}

fun getFiles() {
    val file = File(serviceAccountKeyPath)
    val credentials = ServiceAccountCredentials.fromStream(file.inputStream())
    val scopedCredentials = credentials.createScoped(listOf(DriveScopes.DRIVE))

    val transport: HttpTransport = NetHttpTransport()
    val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()

    val drive = Drive.Builder(transport, jsonFactory, HttpCredentialsAdapter(scopedCredentials)).build()
    var successfulFetches = 0
    fileInfoList.forEach {
        val driveFile = drive.files().get(it.id).execute()
        File(it.pathToStore).let { file ->
            if (!file.isFile) file.createNewFile()
        }

        if (driveFile.mimeType == "application/json") {
            val outputStream = ByteArrayOutputStream()
            drive.files().get(it.id).executeMediaAndDownloadTo(outputStream)
            val outputFile = FileOutputStream(it.pathToStore)
            outputStream.writeTo(outputFile)
        }

        successfulFetches += 1
    }

    if (successfulFetches == fileInfoList.size) {
        printGreen("Downloaded all secret files")
    } else {
        printRed("DID NOT Download all secret files")
        @Suppress("TooGenericExceptionThrown")
        throw Exception("See Message Above")
    }
}

getFiles()
