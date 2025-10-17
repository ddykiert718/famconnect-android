package com.dsolutions.famconnect.util

import java.security.MessageDigest

object SecurityUtils {

    /**
     * Hashes a given PIN using the SHA-256 algorithm.
     * @param pin The plain text PIN to hash.
     * @return The hexadecimal string representation of the hash.
     */
    fun hashPin(pin: String): String {
        // Wir verwenden einen sicheren, etablierten Hashing-Algorithmus.
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(pin.toByteArray(Charsets.UTF_8))

        // Konvertiere das Byte-Array in einen hexadezimalen String.
        return hashBytes.fold("") { str, it -> str + "%02x".format(it) }
    }
}