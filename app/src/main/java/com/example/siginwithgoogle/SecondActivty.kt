package com.example.siginwithgoogle

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.siginwithgoogle.databinding.ActivitySecondActivtyBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth


class SecondActivty : AppCompatActivity() {
    private var binding: ActivitySecondActivtyBinding? = null
    var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_second_activty)


        // Initialize sign in client
        val googleSignInClient = GoogleSignIn.getClient(
            this, GoogleSignInOptions.DEFAULT_SIGN_IN
        );

        binding?.logout?.setOnClickListener {


            // Sign out from google
            // Sign out from google
            googleSignInClient.signOut().addOnCompleteListener { task ->
                // Check condition
                if (task.isSuccessful) {
                    // When task is successful
                    // Sign out from firebase
                    firebaseAuth.signOut()

                    // Display Toast
                    Toast.makeText(applicationContext, "Logout successful", Toast.LENGTH_SHORT)
                        .show()

                    // Finish activity
                    finish()
                }
            }


        }


    }
}