package com.showmeyourcode.ktor.demo.user

import com.showmeyourcode.ktor.demo.configuration.appConfig
import io.ktor.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private const val DEFAULT_HASHING_ALGORITHM = "HmacSHA1"
private val hashKey = hex(appConfig.property("auth.secretKey").getString())
private val hmacKey = SecretKeySpec(hashKey, DEFAULT_HASHING_ALGORITHM)

private fun hash(password: String): String {
    val hmac = Mac.getInstance(DEFAULT_HASHING_ALGORITHM)
    hmac.init(hmacKey)
    return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
}

val hashFunction = { s: String -> hash(s) }
