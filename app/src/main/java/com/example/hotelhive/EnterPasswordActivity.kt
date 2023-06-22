package com.example.hotelhive

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hotelhive.databinding.ActivityEnterPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class EnterPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEnterPasswordBinding
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnterPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra("email")

        // Concatenate the email with the existing text
        val informationText = "Please enter your password for $email"
        binding.informationEmailText.text = informationText

        mAuth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener {
            val password = binding.passwordEditText.text.toString()

            // Call the login method
            if (email != null) {
                login(email, password)
            }
        }

    }
    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login success, user is authenticated
                    val user = mAuth.currentUser
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Login failed
                    Toast.makeText(
                        this,
                        "Login failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}