package com.example.synoptrack.core.utils

object IdentityUtils {

    private const val ALLOWED_CHARS = "0123456789abcdefghijklmnopqrstuvwxyz"

    /**
     * Generates a random 4-character alphanumeric hash for the discriminator.
     * Example: "9uwu"
     */
    fun generateDiscriminator(): String {
        return (1..4)
            .map { ALLOWED_CHARS.random() }
            .joinToString("")
    }

    /**
     * Generates a unique Invite Code based on the user's identity.
     * Format: username#discriminator@4RandomChars
     * Example: kartia#9uwu@572a
     */
    fun generateInviteCode(username: String, discriminator: String): String {
        val randomSuffix = (1..4)
            .map { ALLOWED_CHARS.random() }
            .joinToString("")
        // Ensure username is clean (no spaces, lowercase)
        val cleanName = username.replace(" ", "").lowercase()
        return "$cleanName#$discriminator@$randomSuffix"
    }

    /**
     * Validates if the discriminator is valid (4 alphanumeric chars).
     */
    fun isValidDiscriminator(discriminator: String): Boolean {
        return discriminator.length == 4 && discriminator.all { it.isLetterOrDigit() }
    }
}
