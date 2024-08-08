package io.violabs.plugins.publicPublishing.githubMaven

/**
 * Repository configuration for publishing to Github Packages
 *
 * @property username The username to use for authentication
 * @property password The password to use for authentication
 * @property usernameLookup A pair of property name and environment variable name to use for looking up the username
 */
open class ViolabsLibraryRepository(
    var username: String? = null,
    var password: String? = null,
    var usernameLookup: LookupGroup? = null,
    var passwordLookup: LookupGroup? = null
)
