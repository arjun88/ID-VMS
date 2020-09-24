package com.idbsoftek.vms.setup.login

interface TokenRefreshable {
    fun onTokenRefresh(responseCode: Int, token: String)
}