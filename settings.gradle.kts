pluginManagement {
    repositories {
        mavenCentral()
        google()
        mavenLocal()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "PrimitiveScript"

