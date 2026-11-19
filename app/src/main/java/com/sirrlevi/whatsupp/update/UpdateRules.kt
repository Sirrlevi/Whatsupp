package com.sirrlevi.whatsupp.update

import java.security.MessageDigest

/** Which GitHub request failed; the same HTTP status means different things at each step. */
enum class Stage { REPOSITORY, RELEASE, FILE }

/** Plain, Android-free update rules so they can be unit tested on the JVM. */
object UpdateRules {
    const val OWNER = "Sirrlevi"
    const val REPOSITORY = "Whatsupp"

    /** Rolling release that the CI keeps pointing at the newest build. */
    const val RELEASE_TAG = "latest-apk"
    const val METADATA_ASSET = "whatsupp-update.json"
    const val APK_ASSET = "Whatsupp.apk"

    fun isNewer(installedVersionCode: Long, latestVersionCode: Long): Boolean =
        latestVersionCode > installedVersionCode

    fun sameHash(first: String, second: String): Boolean =
        first.trim().equals(second.trim(), ignoreCase = true)

    fun sha256Hex(bytes: ByteArray): String =
        MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }

    fun formatSize(bytes: Long): String = when {
        bytes <= 0L -> ""
        bytes < 1024L * 1024L -> "${bytes / 1024L} KB"
        else -> "%.1f MB".format(bytes / (1024.0 * 1024.0))
    }

    /**
     * Turns a GitHub error into one sentence that says what to change. [githubMessage] is GitHub's own
     * explanation (the "message" field of its JSON error), appended so nothing is hidden.
     */
    fun errorMessage(status: Int, stage: Stage, githubMessage: String = ""): String {
        val text = when {
            status == 401 ->
                "GitHub says this token isn't valid. It may have expired, been revoked, or been pasted with a character missing. Create a new token and paste it again."
            status == 404 && stage == Stage.REPOSITORY ->
                "This token can't see $OWNER/$REPOSITORY. When creating the token, set Resource owner to $OWNER and Repository access to Only select repositories, then choose $REPOSITORY."
            status == 404 && stage == Stage.RELEASE ->
                "The token works, but there is no \"$RELEASE_TAG\" release yet. It appears once a build has finished on main."
            status == 404 ->
                "GitHub couldn't find the update file. A new build may still be uploading; try again in a minute."
            status == 403 && githubMessage.contains("rate limit", ignoreCase = true) ->
                "GitHub's request limit was reached. Try again in a few minutes."
            status == 403 ->
                "The token can see the repository but isn't allowed to read its releases. Edit the token and set Repository permissions > Contents to Read-only."
            else -> "GitHub update request failed ($status)."
        }
        val extra = githubMessage.trim().take(160)
        return if (extra.isEmpty()) text else "$text (GitHub says: $extra)"
    }
}
