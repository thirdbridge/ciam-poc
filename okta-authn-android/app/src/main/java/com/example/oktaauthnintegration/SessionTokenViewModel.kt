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
package com.example.oktaauthnintegration

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.okta.authfoundation.client.OidcClientResult
import com.okta.authfoundationbootstrap.CredentialBootstrap
import com.okta.authn.sdk.client.AuthenticationClients
import com.okta.authn.sdk.resource.AuthenticationRequest
import com.okta.authn.sdk.resource.VerifyPassCodeFactorRequest
import com.okta.oauth2.SessionTokenFlow.Companion.createSessionTokenFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SessionTokenViewModel : ViewModel() {
    private var _factorId = ""
    private var _stateToken = ""
    private val _client = AuthenticationClients.builder()
        .setOrgUrl(BuildConfig.ORG_URL)
        .build()

    private var refreshToken: String? = null;

    fun login(username: String, password: String) {

        Thread {
            val request = _client.instantiate(AuthenticationRequest::class.java)
                .setUsername(username)
                .setPassword(password.toCharArray())


            val response = _client.authenticate(request, null)
            val factor = response.factors[0]

            _factorId = factor.id
            _stateToken = response.stateToken

            _client.challengeFactor(factor.id, response.stateToken, null)
        }.start()
    }

    fun submitOtp(otp: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val request =
                _client.instantiate(VerifyPassCodeFactorRequest::class.java)
                    .setPassCode(otp)
                    .setStateToken(_stateToken)

            val response = _client.verifyFactor(_factorId, request, null)
            Log.i("demo", response.sessionToken)
            val sessionTokenFlow = CredentialBootstrap.oidcClient.createSessionTokenFlow()

            when (val result = sessionTokenFlow.start(response.sessionToken, "com.example.oktaauthnintegration:/login")) {
                is OidcClientResult.Error -> {
                    Log.e("demo", result.exception.toString())
                }
                is OidcClientResult.Success -> {
                    Log.i("demo", "Success: ${result.result.idToken}")
                    Log.i("demo", "Success: ${result.result.refreshToken}")
                    refreshToken = result.result.refreshToken
                }
            }
        }
    }


    fun useRefreshToken() {
        viewModelScope.launch(Dispatchers.IO) {
            refreshToken?.let {
                val newToken = CredentialBootstrap.oidcClient.refreshToken(it).getOrThrow().idToken
                Log.i("demo", newToken!!)
            }
        }
    }
}