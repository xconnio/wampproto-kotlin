plugins {
    kotlin("jvm") version "1.9.23"
}

group = "io.xconn"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-cbor:2.14.2")
    implementation("org.msgpack:jackson-dataformat-msgpack:0.9.8")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
