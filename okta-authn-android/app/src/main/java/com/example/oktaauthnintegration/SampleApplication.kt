package com.example.oktaauthnintegration

import android.app.Application
import com.okta.authfoundation.client.OidcClient
import com.okta.authfoundation.client.OidcConfiguration
import com.okta.authfoundation.credential.CredentialDataSource.Companion.createCredentialDataSource
import com.okta.authfoundationbootstrap.CredentialBootstrap
import okhttp3.HttpUrl.Companion.toHttpUrl

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val oidcConfiguration = OidcConfiguration(
            clientId = BuildConfig.CLIENT_ID,
            defaultScope = "openid email profile offline_access",
        )
        val client = OidcClient.createFromDiscoveryUrl(
            oidcConfiguration,
            "${BuildConfig.ORG_URL}/oauth2/${BuildConfig.AUTH_SERVER_ID}/.well-known/openid-configuration".toHttpUrl(),
        )
        CredentialBootstrap.initialize(client.createCredentialDataSource(baseContext))
    }
}
