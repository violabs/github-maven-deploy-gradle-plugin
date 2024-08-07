import java.util.*

// Load properties from the root project's gradle.properties
val rootProperties = Properties().apply {
    file("${rootDir.parentFile}/gradle.properties").inputStream().use { load(it) }
}

// Make properties available as project properties
rootProperties.forEach { (key, value) ->
    project.extra.set(key.toString(), value)
}

plugins {
    `kotlin-dsl`
    `maven-publish`
    `java-gradle-plugin`
    java
}

group = "io.violabs.neo.kortex" // update
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.yaml:snakeyaml:2.0")
    implementation(gradleApi())

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.3")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("io.violabs.geordi:unit-sim:1.0.6")
}

gradlePlugin {
    plugins {
        create("") { // plugin name
            id = "" // id like `io.violabs.neo.kortex.plugins.initializer`
            implementationClass = "" // like `io.violabs.neo.kortex.plugins.initializer.InitializerPlugin`
        }
    }
}

val secretPropsFile = project.rootProject.file("secret.properties") // update to your secret file under `buildSrc`
val ext = project.extensions.extraProperties
if (secretPropsFile.exists()) {
    secretPropsFile.reader().use {
        Properties().apply { load(it) }
    }.onEach { (name, value) ->
        ext[name.toString()] = value
    }
    project.logger.log(LogLevel.LIFECYCLE, "Secrets loaded from file: $ext")
}

val publishingUrl: String by project.ext

if (publishingUrl.isNotBlank()) {
    publishing {
        val publicationRepoName: String by project.ext
        val secretFileUsernameKey: String by project.ext
        val secretFilePasswordKey: String by project.ext
        val envUsernameKey: String by project.ext
        val envPasswordKey: String by project.ext
        val publicationName: String by project.ext

        repositories {
            maven {
                name = publicationRepoName
                url = uri(publishingUrl)
                credentials {
                    username = project.findProperty(secretFileUsernameKey) as String? ?: System.getenv(envUsernameKey)
                    password = project.findProperty(secretFilePasswordKey) as String? ?: System.getenv(envPasswordKey)
                }
            }
        }
        publications {
            create<MavenPublication>(publicationName) {
                from(components["java"])
                groupId = "io.violabs.neo.kortex" // update to group id
                artifactId = "plugins-" // update to artifact id
                version = project.version.toString()
            }
        }
    }
}