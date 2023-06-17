package com.showmeyourcode.ktor.demo.auth

import com.showmeyourcode.ktor.demo.validUser
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class JwtServiceTest {

    @Test
    fun `should successfully verify a valid token`() {
        val service = JwtService()
        val token = service.generateAccessToken(validUser)

        val decodedToken = service.verifyToken(token)

        assertEquals("JWT", decodedToken.type)
        assertEquals("HS512", decodedToken.algorithm)

        assertEquals(validUser.id.toString(), decodedToken.getClaim(JwtService.CLAIM_ID).toString())
        assertEquals("\"${validUser.nick}\"", decodedToken.getClaim(JwtService.CLAIM_NICKNAME).toString())
        assertEquals("\"${JwtService.CLAIM_SUBJECT_VALUE}\"", decodedToken.getClaim("sub").toString())
        assertEquals("\"${JwtService.CLAIM_ISSUER_VALUE}\"", decodedToken.getClaim("iss").toString())

        assertNotNull(decodedToken.getClaim(JwtService.CLAIM_SCOPE).toString())
        assertNotNull(decodedToken.getClaim("exp").toString())
        assertNotNull(decodedToken.getClaim("iat").toString())
    }

    @Test
    fun `should not verify invalid token - TokenExpiredException`() {
        val expiredToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBdXRoZW50aWNhdGlvbiIsInNjb3BlIjoib3BlbmlkIHByb2ZpbGUgZW1haWwiLCJpc3MiOiJzaG93bWV5b3VyY29kZSIsIm5pY2tuYW1lIjoibXktbmljayIsImlkIjoxLCJleHAiOjE2ODY5OTk4OTksImlhdCI6MTY4Njk5OTg5OX0.AM0bNjB1gt1iQnPePcJm1hJPC6dwcLPOoK9rRDtfOZIB46IPXOfrnzesO0EOR_Mzg_POEKe9yCwNLfBJZls4UQ"
        val service = JwtService()

        assertFailsWith<com.auth0.jwt.exceptions.TokenExpiredException>(
            block = {
                service.verifyToken(expiredToken)
            }
        )
    }

    @Test
    fun `should not verify invalid token - InvalidClaimException`() {
        val expiredToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBdXRoZW50aWNhdGlvbiIsInNjb3BlIjoib3BlbmlkIHByb2ZpbGUgZW1haWwiLCJpc3MiOiJzaG93bWV5b3VyY29kZS1URVNUIiwibmlja25hbWUiOiJteS1uaWNrIiwiaWQiOjEsImV4cCI6MTY4NzA4NjE2OSwiaWF0IjoxNjg2OTk5NzY5fQ.KSscgjyLy7xfUW4XcTd_rDBWjfaN4vAtGS92EgYC9PtrYCwHlBDhBOAqCJvk4Im1kxeX0kaStRQjGr7sKPQq3A"
        val service = JwtService()

        assertFailsWith<com.auth0.jwt.exceptions.InvalidClaimException>(
            block = {
                service.verifyToken(expiredToken)
            }
        )
    }
}
