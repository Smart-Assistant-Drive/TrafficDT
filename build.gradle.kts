plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization").version("2.1.20")
    alias(libs.plugins.ktor)
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.smartassistantdrive.trafficdt.Main")
}

dependencies {
    implementation("io.github.wldt:wldt-core:0.4.0")
    implementation ("io.github.wldt:http-digital-adapter:0.2")
    implementation("io.github.wldt:mqtt-digital-adapter:0.1.2")
    implementation("io.github.wldt:mqtt-physical-adapter:0.1.2")
    // SnakeYAML YAML parser
    implementation("org.yaml:snakeyaml:2.0")

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.ktor.server.customer.negotiation)
    implementation(libs.ktor.client.customer.negotiation)
    implementation(libs.ktor.server.customer.json)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)

    // https://mvnrepository.com/artifact/org.eclipse.paho/org.eclipse.paho.client.mqttv3
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.jar {
    manifest {
        attributes(
            mapOf("Implementation-Title" to project.name, "Implementation-Version" to project.version)
        )
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(19)
}