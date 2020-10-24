package com.hms.demo.googleauth

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import net.openid.appauth.*
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val RC_AUTH =100
    private val TAG="MainAcivity"
    val CLIENT_ID="284284823111-8eiq4kjt9nmnqjce0ptj2ebct6c04vhv.apps.googleusercontent.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gbtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val serviceConfig= AuthorizationServiceConfiguration(
            Uri.parse("https://accounts.google.com/o/oauth2/auth"), // authorization endpoint
            Uri.parse("https://oauth2.googleapis.com/token")); // token endpoint
        val authRequestBuilder = AuthorizationRequest.Builder(
            serviceConfig,  // the authorization service configuration
            CLIENT_ID,  // the client ID, typically pre-registered and static
            ResponseTypeValues.CODE,  //
            Uri.parse("$packageName:/oauth2redirect")
        ) // the redirect URI to which the auth response is sent
        authRequestBuilder.setScope("openid email profile")
        val authRequest=authRequestBuilder.build()
        val authService = AuthorizationService(this)
        val authIntent = authService.getAuthorizationRequestIntent(authRequest)
        startActivityForResult(authIntent, RC_AUTH)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==RC_AUTH){
            handleGoogleSignIn(data)
        }
    }

    private fun handleGoogleSignIn(data: Intent?) {
        val response = data?.let { AuthorizationResponse.fromIntent(it) }
        val ex = data?.let { AuthorizationException.fromIntent(it) }
        val authState = AuthState(response, ex)
        response?.apply {
            val service = AuthorizationService(this@MainActivity)
            service.performTokenRequest(
                createTokenExchangeRequest()
            ) { tokenResponse, exception ->
                service.dispose()
                exception?.let {
                    Log.e(TAG, "Token Exchange failed", it)
                }
                tokenResponse?.let {
                    authState.update(tokenResponse, exception)
                    val accessToken = it.accessToken
                    val idToken = it.idToken
                    Log.i(TAG, "IdToken: $idToken")
                    accessToken?.apply { InfoRequestAsync(this).execute() }
                }

            }
        }

    }

}