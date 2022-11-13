/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java application project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/6.9.3/userguide/building_java_projects.html
 */

plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
    id("io.vertx.vertx-plugin") version "1.1.1"

}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use JUnit test framework.
    testImplementation("junit:junit:4.13")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:29.0-jre")

//    implementation("io.vertx:vertx-core:4.3.4")
    implementation("io.vertx:vertx-web:4.3.4")
}

vertx {
    mainVerticle = "multiplayer.minesweeper.App"
}

application {
    // Define the main class for the application.
    mainClass.set("multiplayer.minesweeper.App")

}