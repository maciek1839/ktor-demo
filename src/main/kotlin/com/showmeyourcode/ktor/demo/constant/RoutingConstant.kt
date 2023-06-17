package com.showmeyourcode.ktor.demo.constant

object RoutingConstant {
    private const val API_V1_PREFIX = "/api/v1"
    const val DEFAULT = "/"
    const val METRICS = "/metrics"
    const val HEALTH = "/health"
    const val API_USERS = "$API_V1_PREFIX/users"
    const val API_USERS_STATS = "$API_USERS/stats"
    const val LOGIN = "$API_USERS/login"
}
