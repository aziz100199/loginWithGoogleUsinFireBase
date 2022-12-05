package com.example.siginwithgoogle

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.siginwithgoogle.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var auth = FirebaseAuth.getInstance()
    private var signInRequest: BeginSignInRequest? = null
    private  var oneTapClient: SignInClient? = null

    // ...
    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
    private var showOneTapUI = true

    // ...
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding?.btSignIn?.setOnClickListener {
        beginSignIn()
        }
        signInWithGoogle()


    }

    private fun beginSignIn() {

        oneTapClient = Identity.getSignInClient(this)
        signInRequest?.let {
            oneTapClient!!.beginSignIn(it)
                .addOnSuccessListener(this) { result ->
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender, REQ_ONE_TAP,
                            null, 0, 0, 0, null)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.d("CouldNotStart", "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener(this) { e ->
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Log.d("CouldNotStart", e.localizedMessage)
                }
        }

    }

    private fun signInWithGoogle() {


        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient?.getSignInCredentialFromIntent(data)
                    val idToken = credential?.googleIdToken
                    val username = credential?.id
                    val password = credential?.password
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with your backend.
                            Log.d("tokenid", "Got ID token.")
                        }
                        password != null -> {
                            // Got a saved username and password. Use them to authenticate
                            // with your backend.
                            Log.d("password", "Got password.")
                        }
                        else -> {
                            // Shouldn't happen.
                            Log.d("Shouldn't", "No ID token or password!")
                        }
                    }
                } catch (e: ApiException) {
                    when (requestCode) {
                        REQ_ONE_TAP -> {
                            try {
                                // ...
                            } catch (e: ApiException) {
                                when (e.statusCode) {
                                    CommonStatusCodes.CANCELED -> {
                                        Log.d("CANCELED", "One-tap dialog was closed.")
                                        // Don't re-prompt the user.
                                        showOneTapUI = false
                                    }
                                    CommonStatusCodes.NETWORK_ERROR -> {
                                        Log.d("NETWORK_ERROR", "One-tap encountered a network error.")
                                        // Try again or just ignore.
                                    }
                                    else -> {
                                        Log.d("credentialNotGet", "Couldn't get credential from result." +
                                                " (${e.localizedMessage})")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}