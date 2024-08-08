package io.violabs.plugins.publicPublishing.githubMaven

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction


open class GithubMavenPublishTask : DefaultTask() {
    @get:Input
    var projectName: String? = null

    @get:Input
    var artifactId: String? = null

    @TaskAction
    fun publishToGithubPackages() {
        val artifactId: String =
            artifactId ?: throw MissingPropertyException.ArtifactId(projectName ?: "unknown")

        logger.lifecycle("Publishing $artifactId to GitHub Packages for $projectName")
    }
}
