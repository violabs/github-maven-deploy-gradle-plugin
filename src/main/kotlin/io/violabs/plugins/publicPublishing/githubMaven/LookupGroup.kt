package io.violabs.plugins.publicPublishing.githubMaven

data class LookupGroup(
    val propertyLookup: PropertyLookup,
    val envLookup: EnvLookup
) {
    companion object {
        fun of(propertyLookup: String, envLookup: String) =
            LookupGroup(PropertyLookup(propertyLookup), EnvLookup(envLookup))
    }
}
