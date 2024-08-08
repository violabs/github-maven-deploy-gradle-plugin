import java.util.*

// Load properties from the root project's gradle.properties
val rootProperties = Properties().apply {
    rootProject.file("gradle.properties").inputStream().use { load(it) }
}

// Make properties available as project properties
rootProperties.forEach { (key, value) ->
    project.extra.set(key.toString(), value)
}

plugins {
    `kotlin-dsl`
    `maven-publish`
    `java-gradle-plugin`
    alias(libs.plugins.kover)
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka)
    kotlin("jvm") version libs.versions.kotlin.get()
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0" apply false
}

group = "io.violabs.plugins.publishing"
version = "0.0.1"


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


repositories {
    val libraryRepoUrl: String by project.ext
    val publicationRepoName: String by project.ext
    val secretFileUsernameKey: String by project.ext
    val secretFilePasswordKey: String by project.ext
    val envUsernameKey: String by project.ext
    val envPasswordKey: String by project.ext
    val publicationName: String by project.ext

    mavenCentral()
    repositories {
        maven {
            name = publicationRepoName
            url = uri("https://maven.pkg.github.com/violabs/neo-kortex")
            credentials {
                username = project.findProperty(secretFileUsernameKey) as String? ?: System.getenv(envUsernameKey)
                password = project.findProperty(secretFilePasswordKey) as String? ?: System.getenv(envPasswordKey)
            }
        }
    }
}

dependencies {
    implementation(libs.snakeYaml)
    implementation(libs.logging)
    implementation(libs.detekt)
    implementation(libs.dokka)

    testImplementation(libs.bundles.standardTesting)
}

gradlePlugin {
    plugins {
        create("githubMavenPublishPlugin") {
            id = "io.violabs.plugins.public.publishing.github-maven"
            implementationClass = "io.violabs.plugins.publicPublishing.githubMaven.GithubMavenPublishPlugin"
        }
    }
}

publishing {
    val publicationRepoName: String by project.ext
    val secretFileUsernameKey: String by project.ext
    val secretFilePasswordKey: String by project.ext
    val envUsernameKey: String by project.ext
    val envPasswordKey: String by project.ext
    val publicationName: String by project.ext
    val pluginPublishingRepoUrl: String by project.ext

    repositories {
        maven {
            name = publicationRepoName
            url = uri(pluginPublishingRepoUrl)
            credentials {
                username = project.findProperty(secretFileUsernameKey) as String? ?: System.getenv(envUsernameKey)
                password = project.findProperty(secretFilePasswordKey) as String? ?: System.getenv(envPasswordKey)
            }
        }
    }
    publications {
        register<MavenPublication>(publicationName) {
            from(components["java"])
            groupId = "io.violabs.plugins.public.publishing"
            artifactId = "github-maven-publish"
            version = project.version.toString()
        }
    }
}