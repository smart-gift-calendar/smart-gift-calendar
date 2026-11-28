package com.smartgiftcalendar.app.data.local

import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Salted SHA-256 password hashing for local demo accounts.
 *
 * Every account stores its own random salt next to the derived hash, so two accounts with the
 * same password do not share a hash. [verify] compares digests in constant time.
 */
object PasswordHasher {

    private const val ALGORITHM = "SHA-256"
    private const val SALT_BYTES = 16

    /** A salt and the hash derived from a password with that salt, both lowercase hex. */
    data class HashedPassword(val salt: String, val hash: String)

    /** Creates a fresh random salt, as lowercase hex. */
    fun generateSalt(): String {
        val bytes = ByteArray(SALT_BYTES)
        SecureRandom().nextBytes(bytes)
        return bytes.toHex()
    }

    /** Hashes [password] with a new random salt. */
    fun hash(password: String): HashedPassword {
        val salt = generateSalt()
        return HashedPassword(salt = salt, hash = hash(password, salt))
    }

    /** Hashes [password] with an existing [salt]. */
    fun hash(password: String, salt: String): String {
        val digest = MessageDigest.getInstance(ALGORITHM)
        digest.update(salt.toByteArray(Charsets.UTF_8))
        return digest.digest(password.toByteArray(Charsets.UTF_8)).toHex()
    }

    /** Returns true when [password] reproduces [expectedHash] for [salt]. */
    fun verify(password: String, salt: String, expectedHash: String): Boolean {
        val actual = hash(password, salt).toByteArray(Charsets.UTF_8)
        val expected = expectedHash.lowercase().toByteArray(Charsets.UTF_8)
        return MessageDigest.isEqual(actual, expected)
    }

    private fun ByteArray.toHex(): String = joinToString(separator = "") { byte ->
        "%02x".format(byte)
    }
}
