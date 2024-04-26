/*
 * Copyright 2021-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.oktaidxintegration

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.okta.authfoundation.client.OidcClient
import com.okta.authfoundation.client.OidcConfiguration
import com.okta.idx.kotlin.client.InteractionCodeFlow
import com.okta.idx.kotlin.client.InteractionCodeFlow.Companion.createInteractionCodeFlow
import com.okta.idx.kotlin.dto.IdxRecoverCapability
import com.okta.idx.kotlin.dto.IdxRemediation
import com.okta.idx.kotlin.dto.IdxRemediation.Type.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl.Companion.toHttpUrl

suspend fun InteractionCodeFlow.getRemediation(type: IdxRemediation.Type): IdxRemediation {
    return this.resume().getOrThrow().remediations.find { it.type == type }!!
}

suspend fun InteractionCodeFlow.proceedAndGetRemediation(remediation: IdxRemediation, type: IdxRemediation.Type): IdxRemediation {
    val idxResponse = this.proceed(remediation).getOrThrow()
    return idxResponse.remediations.find { it.type == type}!!
}
class SessionTokenViewModel : ViewModel() {

    private val oidcConfiguration = OidcConfiguration(
        clientId = BuildConfig.CLIENT_ID,
        defaultScope = "openid email profile offline_access",
    )
    private val client = OidcClient.createFromDiscoveryUrl(
        oidcConfiguration,
        "${BuildConfig.ISSUER}/.well-known/openid-configuration".toHttpUrl(),
    )

    private val flow by lazy {
        runBlocking {
            val clientResult = client.createInteractionCodeFlow(BuildConfig.REDIRECT_URI)
            clientResult.getOrThrow()
        }
    }

    private var refreshToken: String? = null

    fun login(username: String, password: String) {

        viewModelScope.launch(Dispatchers.IO) {

            val identifyRemediation = flow.getRemediation(IDENTIFY)
            identifyRemediation.let {
                it.form["identifier"]?.value = username
            }

            val passwordRemediation = flow.proceedAndGetRemediation(identifyRemediation, CHALLENGE_AUTHENTICATOR)
            passwordRemediation.let {
                it.form["credentials.passcode"]?.value = password
            }

            val triggerOtpRemediation = flow.proceedAndGetRemediation(passwordRemediation, AUTHENTICATOR_VERIFICATION_DATA)
            triggerOtpRemediation.let {
                it.form["authenticator.methodType"]?.value = "sms"
            }

            flow.proceed(triggerOtpRemediation)
        }
    }

    fun submitOtp(otp: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val confirmOtpRemediation = flow.getRemediation(CHALLENGE_AUTHENTICATOR)
            confirmOtpRemediation.let {
                it.form["credentials.passcode"]?.value = otp
            }

            val response = flow.proceed(confirmOtpRemediation).getOrThrow()
            val tokens = flow.exchangeInteractionCodeForTokens(response.remediations.find { it.type == ISSUE }!!).getOrThrow()
            tokens.refreshToken?.let {
                refreshToken = it
            }
        }
    }

    fun useRefreshToken() {
        viewModelScope.launch(Dispatchers.IO) {
            refreshToken?.let {
                val newToken = client.refreshToken(it).getOrThrow().idToken
                Log.i("demo", newToken!!)
            }
        }
    }

    fun forgetPassword(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Start an interaction with Okta
            val identifyRemediation = flow.getRemediation(IDENTIFY)

            // Provide the email to know the next steps available
            identifyRemediation.let {
                it.form["identifier"]?.value = email
            }
            val idxResponse = flow.proceed(identifyRemediation).getOrThrow()

            // Chose the password authenticator
            val selectAuthAuthRemediation = idxResponse.remediations.find { it.type == SELECT_AUTHENTICATOR_AUTHENTICATE }!!
            selectAuthAuthRemediation.let { remediation ->
                val authenticatorField = remediation.form.visibleFields.find { it.name == "authenticator" }!!
                authenticatorField.selectedOption = authenticatorField.options?.find { it.label == "password" }
            }
            val idxResponse2 = flow.proceed(selectAuthAuthRemediation).getOrThrow()

            // Chose the recover option for the password
            val recoverRemediation = idxResponse2.authenticators[0].capabilities.get<IdxRecoverCapability>()?.remediation!!
            val idxResponse3 = flow.proceed(recoverRemediation).getOrThrow()

            // Chose the email option to recover the password
            val verifyRemediation = idxResponse3.remediations.find { it.type == AUTHENTICATOR_VERIFICATION_DATA }!!
            verifyRemediation.let {
                it.form["authenticator.methodType"]?.value = "email"
            }
            flow.proceed(verifyRemediation).getOrThrow()
        }
    }
}