package io.violabs.plugins.publicPublishing.githubMaven

import org.gradle.api.Project

open class MissingPropertyException(projectName: String, propertyName: String) :
    IllegalStateException("$projectName is missing the required property $propertyName"){

    class ArtifactId(projectName: String) : MissingPropertyException(projectName, "artifactId") {
        constructor(project: Project) : this(project.name)
    }
}
