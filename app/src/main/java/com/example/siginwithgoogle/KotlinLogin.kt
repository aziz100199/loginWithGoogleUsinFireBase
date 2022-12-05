package com.example.siginwithgoogle

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.siginwithgoogle.databinding.ActivityKotlinLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class KotlinLogin : AppCompatActivity() {
    private var binding: ActivityKotlinLoginBinding? = null

    // Initialize variables
    var googleSignInClient: GoogleSignInClient? = null
    var firebaseAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_kotlin_login)


        // Initialize sign in options
        // the client-id is copied form
        // google-services.json file
        // Initialize sign in options
        // the client-id is copied form
        // google-services.json file
        val googleSignInOptions = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()


        // Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(
            this, googleSignInOptions
        )


        binding?.btSignIn?.setOnClickListener {
            // Initialize sign in intent
            val intent = googleSignInClient!!.signInIntent
            // Start activity for result
            startActivityForResult(intent, 100)
        }


        // Initialize firebase auth
        // Initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        // Initialize firebase user
        // Initialize firebase user
        val firebaseUser = firebaseAuth!!.currentUser
        // Check condition
        // Check condition
        if (firebaseUser != null) {
            // When user already sign in
            // redirect to profile activity
            startActivity(
                Intent(this, SecondActivty::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check condition
        if (requestCode == 100) {
            // When request code is equal to 100
            // Initialize task
            val signInAccountTask: Task<GoogleSignInAccount> = GoogleSignIn
                .getSignedInAccountFromIntent(data)

            // check condition
            if (signInAccountTask.isSuccessful()) {
                // When google sign in successful
                // Initialize string
                val s = "Google sign in successful"
                // Display Toast
                displayToast(s)
                // Initialize sign in account
                try {
                    // Initialize sign in account
                    val googleSignInAccount: GoogleSignInAccount = signInAccountTask
                        .getResult(ApiException::class.java)
                    // Check condition
                    if (googleSignInAccount != null) {
                        // When sign in account is not equal to null
                        // Initialize auth credential
                        val authCredential = GoogleAuthProvider
                            .getCredential(
                                googleSignInAccount.idToken, null
                            )
                        // Check credential
                        firebaseAuth!!.signInWithCredential(authCredential)
                            .addOnCompleteListener(
                                this
                            ) { task ->
                                // Check condition
                                if (task.isSuccessful) {
                                    // When task is successful
                                    // Redirect to profile activity
                                    startActivity(
                                        Intent(this, SecondActivty::class.java)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    )
                                    // Display Toast
                                    displayToast("Firebase authentication successful")
                                } else {
                                    // When task is unsuccessful
                                    // Display Toast
                                    displayToast(
                                        "Authentication Failed :" + task.exception
                                    )
                                }
                            }
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun displayToast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


}