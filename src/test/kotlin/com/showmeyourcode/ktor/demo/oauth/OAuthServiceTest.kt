package com.showmeyourcode.ktor.demo.oauth

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class OAuthServiceTest {

    @Test
    fun `should successfully decode a token WHEN the token is valid`() {
        val service = OAuthService()
        val clientId = "CLIENT_ID"

        val token = service.generateToken(clientId, "CLIENT_SECRET", "123abc", "authorization_code")

        val decodedToken = service.decodeToken(token.accessToken)

        assertEquals("JWT", decodedToken.type)
        assertEquals("HS512", decodedToken.algorithm)

        assertEquals(clientId, decodedToken.getClaim(OAuthService.CLAIM_ID).asString())
        assertEquals(OAuthService.CLAIM_SUBJECT_VALUE, decodedToken.getClaim("sub").asString())
        assertEquals(OAuthService.CLAIM_ISSUER_VALUE, decodedToken.getClaim("iss").asString())

        assertNotNull(decodedToken.getClaim(OAuthService.CLAIM_SCOPE).toString())
        assertNotNull(decodedToken.getClaim("exp").toString())
        assertNotNull(decodedToken.getClaim("iat").toString())
    }

    @Test
    fun `should not verify an invalid token and throw TokenExpiredException WHEN the token expired`() {
        val expiredToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBdXRoZW50aWNhdGlvbiIsImF1ZCI6Imt0b3ItYXBwbGljYXRpb24taWQiLCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJpc3MiOiJodHRwczovL3Nob3dtZXlvdXJjb2RlLnNlcnZlci5leGFtcGxlLmNvbSIsImlkIjoiIiwiZXhwIjoxNjkyNzE2MzEyLCJpYXQiOjE2OTI3MTI3MTJ9.9eOsdW5cuB-sCeAc6kyZMUY5BJgsvXl9sJDECiwvhw_TTjnKmIVuY01XbsJWOrphs2Yl49BvsUrSIYp67KgkWA"
        val service = OAuthService()

        assertFailsWith<com.auth0.jwt.exceptions.TokenExpiredException>(
            block = {
                service.decodeToken(expiredToken)
            }
        )
    }

    @Test
    fun `should decode an invalid token and throw InvalidClaimException WHEN the issuer does not match`() {
        val invalidToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBdXRoZW50aWNhdGlvbiIsInNjb3BlIjoib3BlbmlkIHByb2ZpbGUgZW1haWwiLCJpc3MiOiJzaG93bWV5b3VyY29kZS1URVNUIiwibmlja25hbWUiOiJteS1uaWNrIiwiaWQiOjEsImV4cCI6MTY4NzA4NjE2OSwiaWF0IjoxNjg2OTk5NzY5fQ.KSscgjyLy7xfUW4XcTd_rDBWjfaN4vAtGS92EgYC9PtrYCwHlBDhBOAqCJvk4Im1kxeX0kaStRQjGr7sKPQq3A"
        val service = OAuthService()

        assertFailsWith<com.auth0.jwt.exceptions.InvalidClaimException>(
            block = {
                service.decodeToken(invalidToken)
            }
        )
    }
}
