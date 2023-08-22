package com.showmeyourcode.ktor.demo.constant

object RoutingConstant {
    private const val API_V1_PREFIX = "/api/v1"
    const val DEFAULT = "/"
    const val METRICS = "/metrics"
    const val HEALTH = "/health"
    const val API_USERS = "$API_V1_PREFIX/users"
    const val COUNT = "$API_USERS/count"
    private const val OAUTH_PREFIX = "$API_V1_PREFIX/oauth2"
    const val OAUTH_AUTHORIZE = "$OAUTH_PREFIX/authorize"
    const val OAUTH = "$OAUTH_PREFIX/token"
    const val OAUTH_STATUS = "$OAUTH/status"
    const val OAUTH_REVOKE = "$API_V1_PREFIX/oauth2/revoke"
}
