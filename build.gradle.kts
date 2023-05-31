@file:Suppress("DEPRECATION")

import org.lwjgl.Lwjgl
import org.lwjgl.Lwjgl.Module.*
import org.lwjgl.lwjgl

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm") version "1.8.21"
    id("application")
    id("base")
    id("org.lwjgl.plugin") version "0.0.34"
}

val group = "org.crafter"
val version = "Continuous-Build"

val releaseVersion = "v0.0.2 - Pre-Alpha"
val natives = arrayOf("natives-linux", "natives-macos", "natives-windows")

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    lwjgl {
        implementation(core, assimp, glfw, opengl, opengl, stb)
        implementation(Lwjgl.Addons.`joml 1_10_5`)
    }

    implementation("com.fasterxml.jackson.core:jackson-core:2.14.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.14.2")

    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.openjdk.nashorn:nashorn-core:15.4")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("org.crafter.MainKt")
}

tasks.jar {
    manifest {
        // This is a hashmap! I'm writing this comment in case you reading this are learning from this project!
        attributes["Main-Class"] = "org.crafter.MainKt"
    }
}

// This is deprecated, but there's no alternative I can find
@Suppress("Deprecated")
gradle.taskGraph.afterTask {
    val name = this.name

    if (name == "startShadowScripts") {
        println("ShadowJar: Compiling and jarifying game!")
        if (this.state.failure != null) {
            println("$name FAILED")
        } else {
            println("$name SUCCESSFUL")
        }
    } else if (name == "compileJava") {
        if (!tasks.compileJava.isPresent) {
            natives.forEach {
                println("CompileJava: Adding $it natives")
            }
        }
    }
}

tasks.shadowJar {
    project.setProperty("mainClassName", "org.crafter.MainKt")
    archiveBaseName.set("Crafter")
    archiveClassifier.set("")
    archiveVersion.set("")
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}

// This runs the runShadowScripts then packs up the full game so users can run it easily, but only if fullGame flag given
val dirs = arrayOf("fonts", "shaders", "textures")

tasks.register<Zip>("packageFullGame") {

    println("Zipping full build of $releaseVersion!")

    // This is just called "crafter" so people can simply drop the folder into the existing one and replace contents
    val baseFolder = "crafter"

//    val archiveFileName = "CrafterLatestRelease.zip"

    // All game dirs
    for (directory in dirs) {
        println("Zipping folder: $directory")
        from("$directory/") {
            include("*")
            into("$baseFolder/$directory")
        }
    }
    // Now put the jar in
    from("build/libs") {
        println("Zipping: Jar File")
        include("*")
        into(baseFolder)
    }

    doLast {
        for (thisFile in fileTree("build/distributions")
            .filter { it.isFile }.files) {
            if (!thisFile.equals("CrafterLatestRelease.zip")) {
                project.delete("build/distributions/$thisFile")
                println("Zipping: deleted $thisFile")
            }
        }
        println("Zipping: Release folder cleaned!")
    }
}
kotlin {
    jvmToolchain(17)
}