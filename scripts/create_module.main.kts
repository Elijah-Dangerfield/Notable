#!/usr/bin/env kotlin

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.regex.Pattern

val red = "\u001b[31m"
val green = "\u001b[32m"
val reset = "\u001b[0m"

fun printRed(text: String) {
    println(red + text + reset)
}

fun printGreen(text: String) {
    println(green + text + reset)
}


fun main() {

    if (checkForHelpCall()) return

    val moduleType = args.getOrNull(0) ?: run {
        print("Enter module type (\"library\" or \"feature\"): ")
        readLine()!!.lowercase()
    }

    if (moduleType != "library" && moduleType != "feature") {
        printRed("Error: Invalid module type. Must be \"library\" or \"feature\"")
        return
    }

    val moduleName = args.getOrNull(1) ?: run {
        print("Enter module name (in camelCase): ")
        readLine()!!
    }

    val (baseDir, newDir) = createDirectory(moduleType, moduleName)

    createPackage(moduleName, newDir)

    updateSettingGradleFile(baseDir, moduleName)

    updateGradleBuildFile(moduleType, newDir, moduleName)

   printGreen("""
       Success! 
       The $moduleType module "$moduleName" was created. 
       Please make sure to update the readme.
   """.trimIndent())
}

fun updateSettingGradleFile(baseDir: String, moduleName: String) {
    val includeLine = "include(\"$baseDir:$moduleName\")"

    val includePattern = Pattern.compile("include\\(\"[^\\)]+\"\\)")
    val settingsFile = File("settings.gradle.kts")
    val settingsLines = settingsFile.readLines().toMutableList()

    if (!settingsLines.contains(includeLine)) {
        settingsLines += includeLine
    }

    val indexOfFirstInclude = settingsLines.indexOfFirst { it.matches(includePattern.toRegex()) }
    val indexOfLastInclude = settingsLines.indexOfLast { it.matches(includePattern.toRegex()) }

    settingsLines.subList(indexOfFirstInclude, indexOfLastInclude).sort()

    settingsFile.writeText(settingsLines.joinToString("\n"))
}


fun createPackage(moduleName: String, newDir: String) {
    val packageName =  "com.dangerfield.${moduleName.lowercase()}"

    val mainDir = File("$newDir/src/main/java/$packageName")
    mainDir.mkdirs()

    val testDir = File("$newDir/src/test/java/$packageName")
    testDir.mkdirs()

}

fun createDirectory(moduleType: String, moduleName: String): Pair<String, String> {
    val baseDir = if (moduleType == "library") "libraries" else "features"
    val exampleDir = "example"
    val newDir = "$baseDir/$moduleName"

    File(exampleDir).copyRecursively(File(newDir), overwrite = true)

    return Pair(baseDir, newDir)
}

fun updateGradleBuildFile(moduleType: String, newDir: String, moduleName: String) {
    val buildFile = if (moduleType == "library") {
        val currentBuildFile = File("$newDir/librarybuild.gradle.kts")
        val newBuildFile = File("$newDir/build.gradle.kts.kts")
        val fileToDelete = File("$newDir/featurebuild.gradle.kts")

        currentBuildFile.renameTo(newBuildFile)
        fileToDelete.delete()
        newBuildFile
    } else {
        val currentBuildFile = File("$newDir/featurebuild.gradle.kts")
        val newBuildFile = File("$newDir/build.gradle.kts.kts")
        val fileToDelete = File("$newDir/librarybuild.gradle.kts")

        currentBuildFile.renameTo(newBuildFile)
        fileToDelete.delete()
        newBuildFile
    }

    val reader = BufferedReader(FileReader(buildFile))
    val modifiedLines = mutableListOf<String>()

    var line = reader.readLine()
    while (line != null) {
        if (line.contains("namespace = \"com.dangerfield.example\"")) {
            line = line.replace("com.dangerfield.example", "com.dangerfield.${moduleName.lowercase()}")
        }
        modifiedLines.add(line)
        line = reader.readLine()
    }
    reader.close()

    // Write the modified lines back to the file
    val writer = BufferedWriter(FileWriter(buildFile))
    for (modifiedLine in modifiedLines) {
        writer.write(modifiedLine)
        writer.newLine()
    }
    writer.close()
}

fun checkForHelpCall(): Boolean {
    val isHelpCall = args.isNotEmpty() && (args[0] == "-h" || args[0].contains("help"))
    if (isHelpCall) {
        @Suppress("MaxLineLength")
        printGreen(
            """
               This script creates a new module according to our module structure. 
               
               
               Usage: ./create_module.main.kts [options]
               option module-type - the type of the module to create: "library" or "feature" 
               option module-name - The camelCase name of the module to create
               
    """.trimIndent()
        )
    }

    return isHelpCall
}

main()
