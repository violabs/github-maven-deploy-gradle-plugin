package io.violabs.plugins.publicPublishing.githubMaven

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

open class GithubMavenPublishExtension @Inject constructor(objectFactory: ObjectFactory) {
    var artifactId: String? = null
    var enableAllTasks: Property<Boolean> = objectFactory.property(Boolean::class.java).convention(false)
    var organization: String? = null
    var projectName: String? = null
    var secretFileLocation: String? = null
    var groupOverride: String? = null
    var libraryRepository: ViolabsLibraryRepository = ViolabsLibraryRepository()
    var repoName: String? = null
}
