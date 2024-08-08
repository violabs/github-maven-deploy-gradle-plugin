package io.violabs.plugins.publicPublishing.githubMaven

import org.gradle.api.Plugin
import org.gradle.api.Project

val ENABLED_PUBLISHING_TASKS = listOf(
    "publishGprPublicationToGitHubPackagesRepository",
    "generateMetadataFileForGprPublication",
    "generatePomFileForGprPublication",
    "publishToMavenLocal"
)

open class GithubMavenPublishPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Create and register the extension
        val extension = project.extensions.create("publishGithubMaven", GithubMavenPublishExtension::class.java)

        project.pluginManager.apply("maven-publish")

        if (!extension.enableAllTasks.get()) {
            project
                .tasks
                .filter { it.group == "publishing" }
                .filter { it.name !in ENABLED_PUBLISHING_TASKS }
                .forEach { it.enabled = false }
        }

        project.tasks.register("publishToGithubPackages", GithubMavenPublishTask::class.java) {
            project.tasks.findByName("dokkaJavadoc")?.apply { dependsOn("dokkaJavadoc") }

            projectName = project.name
            artifactId = extension.artifactId
            finalizedBy("publishGprPublicationToGitHubPackagesRepository")
        }

        project.tasks.findByName("dokkaJavadoc")?.finalizedBy("publishToGithubPackages")

        project.afterEvaluate {
            project
                .rootProject
                .processSecretsFromFile(extension.secretFileLocation ?: "secret.properties")
            project.configurePublishToGithubPackages(extension)
        }
    }
}
