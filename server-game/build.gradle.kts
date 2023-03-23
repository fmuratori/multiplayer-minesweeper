plugins {
    application
    id("io.vertx.vertx-plugin") version "1.1.1"
    jacoco
}


repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
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

tasks.withType<Test> {
    jacoco {
        enabled = true
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}
tasks.jacocoTestReport {
    dependsOn(tasks.test)
}
jacoco {
    toolVersion = "0.8.8"
    reportsDirectory.set(layout.buildDirectory.dir("customJacocoReportDir"))
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}
