@file:Suppress("DEPRECATION")

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java")
    id("application")
    id("base")
    id("org.jetbrains.kotlin.jvm") version "1.8.21"
}

val lwjglVersion = "3.3.1"
val jomlVersion = "1.10.5"

val group = "org.crafter.engine"
val version = "Continuous-Build"

val releaseVersion = "v0.0.2 - Pre-Alpha"
val natives = arrayOf("natives-linux", "natives-macos", "natives-windows")

repositories {
    mavenCentral()
}

dependencies {

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    implementation("org.lwjgl:lwjgl")
    implementation("org.lwjgl:lwjgl-assimp")
    implementation("org.lwjgl:lwjgl-glfw")
    implementation("org.lwjgl:lwjgl-openal")
    implementation("org.lwjgl:lwjgl-opengl")
    implementation("org.lwjgl:lwjgl-stb")

    implementation("com.fasterxml.jackson.core:jackson-core:2.14.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.14.2")

    implementation("org.apache.commons:commons-lang3:3.12.0")

    implementation("org.openjdk.nashorn:nashorn-core:15.4")


    natives.forEach{
        runtimeOnly("org.lwjgl:lwjgl::$it")
        runtimeOnly("org.lwjgl:lwjgl-assimp::$it")
        runtimeOnly("org.lwjgl:lwjgl-glfw::$it")
        runtimeOnly("org.lwjgl:lwjgl-openal::$it")
        runtimeOnly("org.lwjgl:lwjgl-opengl::$it")
        runtimeOnly("org.lwjgl:lwjgl-stb::$it")
    }

    implementation("org.joml:joml:${jomlVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}


tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("org.crafter.engine.MainKt")
}

tasks.jar {
    manifest {
        // This is a hashmap! I'm writing this comment in case you reading this are learning from this project!
        attributes["Main-Class"] = "org.crafter.engine.MainKt"
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
    project.setProperty("mainClassName", "org.crafter.engine.MainKt")
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
