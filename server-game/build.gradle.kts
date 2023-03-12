plugins {
    application
    id("io.vertx.vertx-plugin") version "1.1.1"

}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    implementation("io.vertx:vertx-core:4.3.4")
    implementation("io.vertx:vertx-web:4.3.4")

    implementation("com.corundumstudio.socketio:netty-socketio:1.7.19")
}

vertx {
    mainVerticle = "multiplayer.minesweeper.Main"
}

application {
    mainClass.set("multiplayer.minesweeper.Main")

}

tasks.test {
    useJUnitPlatform()
}