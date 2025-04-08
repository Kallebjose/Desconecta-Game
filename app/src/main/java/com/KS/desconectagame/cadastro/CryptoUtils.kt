package com.KS.desconectagame.util

import java.security.MessageDigest

object CryptoUtils {
    fun gerarHashSHA256(senha: String): String {
        val bytes = senha.toByteArray()
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(bytes)
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
