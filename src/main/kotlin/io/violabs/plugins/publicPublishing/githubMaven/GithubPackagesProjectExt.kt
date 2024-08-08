package io.violabs.plugins.publicPublishing.githubMaven

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register
import java.util.*

/**
 * Configures the project to publish to Github Packages.
 * It will get secrets from either a from the provider, secret.properties file, or environment variables.
 *
 * @param extension The GithubPackagesExtension that contains repo configurations
 */
fun Project.configurePublishToGithubPackages(extension: GithubMavenPublishExtension) {
    project.logger.lifecycle("Publishing to Github Packages")
    val repository: ViolabsLibraryRepository = extension.libraryRepository
    val injectedUsername = getInjectedValue(
        repository.username,
        repository.usernameLookup,
        project,
        LookupGroup.of("gpr.user", "GITHUB_ACTOR")
    )

    val injectedPassword = getInjectedValue(
        repository.password,
        repository.passwordLookup,
        project,
        LookupGroup.of("gpr.token", "GITHUB_TOKEN")
    )

    project.afterEvaluate {
        configurePublishingExtension(project, extension, injectedUsername, injectedPassword)
    }
}

private fun configurePublishingExtension(
    project: Project,
    extension: GithubMavenPublishExtension,
    injectedUsername: String,
    injectedPassword: String
) {
    val organization: String = extension.organization ?: "violabs"
    val projectName: String = extension.projectName ?: project.name
    val artifactId: String? = extension.artifactId

    project.extensions.configure(PublishingExtension::class.java) {
        repositories {
            maven {
                name = "GitHubPackages"
                url = project.uri("https://maven.pkg.github.com/$organization/$projectName")
                credentials {
                    username = injectedUsername
                    password = injectedPassword
                }
            }
        }

        publications {
            register<MavenPublication>("gpr") {
                from(project.components["java"])
                groupId = extension.groupOverride ?: project.group.toString()
                this.artifactId = artifactId
                version = project.version.toString()
            }
        }
    }
}

@Suppress("ReturnCount")
fun getInjectedValue(
    value: String?,
    lookupGroup: LookupGroup?,
    project: Project,
    defaultLookup: LookupGroup
): String {
    if (value != null) return value

    val lookupValue = lookupGroup?.let { (propertyLookup, envLookup) ->
        (project.findProperty(propertyLookup.value) as String?) ?: System.getenv(envLookup.value)
    }

    if (lookupValue != null) return lookupValue

    val defaultPropertyName = defaultLookup.propertyLookup.value
    val defaultEnvName = defaultLookup.envLookup.value

    return project.findProperty(defaultPropertyName) as String?
        ?: System.getenv(defaultEnvName)
        ?: throw IllegalArgumentException("Missing value for $defaultPropertyName or $defaultEnvName")
}

fun Project.processSecretsFromFile(
    secretPropertiesName: String = "secret.properties",
    systemProperties: Map<Ext.Key, Ext.SysPropName> = emptyMap()
) {
    val secretPropsFile = this.rootProject.file(secretPropertiesName)
    val ext = this.extensions.extraProperties
    if (secretPropsFile.exists()) {
        secretPropsFile.reader().use {
            Properties().apply { load(it) }
        }.onEach { (name, value) ->
            ext[name.toString()] = value
        }
    } else {
        systemProperties.forEach { (key, sysPropName) ->
            ext[key.value] = System.getProperty(sysPropName.value)
        }
    }
}

/**
 * Configures the project to publish to Github Packages.
 * It will get secrets from either a from the provider, secret.properties file, or environment variables.
 *
 * @param extension The GithubPackagesExtension that contains repo configurations
 */
fun <T : GithubMavenPublishExtension> Project.configurePublish(
    extension: T,
    repoName: String = "Github Packages",
    defaultUserLookup: LookupGroup = LookupGroup.of("gpr.user", "GITHUB_ACTOR"),
    defaultPasswordLookup: LookupGroup = LookupGroup.of("gpr.token", "GITHUB_TOKEN"),
    configurePublishingWithExtension: (Project, T, RepoCredentials) -> Unit
) {
    project.logger.lifecycle("Publishing to $repoName")
    val repository: ViolabsLibraryRepository = extension.libraryRepository
    val injectedUsername = getInjectedValue(
        repository.username,
        repository.usernameLookup,
        project,
        defaultUserLookup
    )

    val injectedPassword = getInjectedValue(
        repository.password,
        repository.passwordLookup,
        project,
        defaultPasswordLookup
    )

    val repoCredentials = RepoCredentials(injectedUsername, injectedPassword)

    project.afterEvaluate {
        configurePublishingWithExtension(project, extension, repoCredentials)
    }
}
