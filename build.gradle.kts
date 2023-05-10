plugins {
    id "com.github.johnrengelman.shadow" version "8.1.1"
    id "java"
    id "application"
    id "base"
    id "org.jetbrains.kotlin.jvm" version "1.8.21"
}

project.ext.lwjglVersion = "3.3.1"
project.ext.jomlVersion = "1.10.5"

group "org.crafter"
version "Continuous-Build"

final String releaseVersion = "v0.0.2 - Pre-Alpha"
final String[] natives = ["natives-linux", "natives-macos", "natives-windows"]

repositories {
    mavenCentral()
}

dependencies {

    testImplementation "org.junit.jupiter:junit-jupiter-api:5.8.1"
    testRuntimeOnly "org.junit.jupiter:junit-jupiter-engine:5.8.1"

    implementation platform("org.lwjgl:lwjgl-bom:$lwjglVersion")

    implementation "org.lwjgl:lwjgl"
    implementation "org.lwjgl:lwjgl-assimp"
    implementation "org.lwjgl:lwjgl-glfw"
    implementation "org.lwjgl:lwjgl-openal"
    implementation "org.lwjgl:lwjgl-opengl"
    implementation "org.lwjgl:lwjgl-stb"

    implementation "com.fasterxml.jackson.core:jackson-core:2.14.2"
    implementation "com.fasterxml.jackson.core:jackson-databind:2.14.2"
    implementation "com.fasterxml.jackson.core:jackson-annotations:2.14.2"

    implementation "party.iroiro.luajava:luajava:3.4.0"
    implementation "party.iroiro.luajava:luajit:3.4.0"
    runtimeOnly "party.iroiro.luajava:luajit-platform:3.4.0:natives-desktop"

    implementation "org.apache.commons:commons-lang3:3.12.0"

    implementation "org.openjdk.nashorn:nashorn-core:15.4"


    for (OS in natives) {
        runtimeOnly "org.lwjgl:lwjgl::$OS"
        runtimeOnly "org.lwjgl:lwjgl-assimp::$OS"
        runtimeOnly "org.lwjgl:lwjgl-glfw::$OS"
        runtimeOnly "org.lwjgl:lwjgl-openal::$OS"
        runtimeOnly "org.lwjgl:lwjgl-opengl::$OS"
        runtimeOnly "org.lwjgl:lwjgl-stb::$OS"
    }

    implementation "org.joml:joml:${jomlVersion}"
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

test {
    useJUnitPlatform()
}

application {
    mainClassName = "org.crafter.Main"
}

jar {
    manifest {
        attributes(
                "Main-Class": "org.crafter.Main"
        )
    }
}

// This is deprecated, but there"s no alternative I can find
gradle.taskGraph.afterTask {
    Task task,TaskState state ->
        if (task.name == "startShadowScripts") {
            println("ShadowJar: Compiling and jarifying game!")
            if (state.failure) {
                println "$task.name FAILED"
            } else {
                println "$task.name SUCCESSFULL"
            }
        } else if (task.name == "compileJava") {
            if (!compileJava.state.upToDate) {
                for (OS in natives) {
                    println("CompileJava: Adding $OS natives")
                }
            }
        }
}

shadowJar {
    mainClassName = "org.crafter.Main"
    archiveBaseName.set("Crafter")
    archiveClassifier.set("")
    archiveVersion.set("")
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}

// This runs the runShadowScripts then packs up the full game so users can run it easily, but only if fullGame flag given
final String[] dirs = ["fonts", "shaders", "textures"]

tasks.register("packageFullGame", Zip)  {

    println("Zipping full build of " + releaseVersion + "!")

    // This is just called "crafter" so people can simply drop the folder into the existing one and replace contents
    final String baseFolder = "crafter"

    archiveFileName = "CrafterLatestRelease.zip"

    // All game dirs
    for (directory in dirs) {
        println("Zipping folder: $directory")
        from("$directory/") {
            include "*"
            into("$baseFolder/$directory")
        }
    }
    // Now put the jar in
    from("build/libs") {
        println("Zipping: Jar File")
        include "*"
        into("$baseFolder")
    }

    doLast {
        for (thisFile in fileTree("build/distributions").filter { it.isFile() }.files.name) {
            if (thisFile != "CrafterLatestRelease.zip") {
                project.delete("build/distributions/" + thisFile)
                println("Zipping: deleted $thisFile")
            }
        }
        println("Zipping: Release folder cleaned!")
    }
}
kotlin {
    jvmToolchain(11)
}