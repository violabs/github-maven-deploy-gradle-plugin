plugins {
    kotlin("jvm") version "2.0.0-Beta5"
}

group = "io.violabs.plugin-template"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

allprojects {
    repositories {
        mavenCentral()
    }

    tasks.withType<Test>  {
        useJUnitPlatform()
    }
}


subprojects {
    apply {
        plugin("kotlin")
        plugin("org.jetbrains.kotlin.jvm")
    }

    dependencies {
        // LOGGING
        implementation("io.github.microutils:kotlin-logging:4.0.0-beta-2")
    }

}

kotlin {
    jvmToolchain(21)
}
