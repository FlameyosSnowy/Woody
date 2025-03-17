plugins {
    id("java")
}

group = "me.flame.woody"
version = "1.0.0"

repositories {
    mavenCentral()
    // papermc repo

    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // paper api
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.test {
    useJUnitPlatform()
}