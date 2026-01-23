plugins {
    id("java")
}

group = "org.tact"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation(files("libs/HytaleServer.jar"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    destinationDirectory.set(file("C:\\Users\\Raphael\\AppData\\Roaming\\Hytale\\UserData\\Mods"))
}