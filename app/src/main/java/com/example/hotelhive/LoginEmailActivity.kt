package com.example.hotelhive

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.example.hotelhive.databinding.ActivityLoginEmailBinding
import com.google.firebase.auth.FirebaseAuth

class LoginEmailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.continueEmail.setOnClickListener {
            val email = binding.emailEditText.text.toString()

            // Validate email format
            if (!isEmailValid(email)) {
                binding.emailEditText.error = "Invalid email format"
                binding.emailEditText.requestFocus()
                return@setOnClickListener
            }
            // check
            isUserRegistered(email) { isRegistered ->
                if (isRegistered) {
                    // password
                    val intent = Intent(this, EnterPasswordActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                } else {
                    // register
                    val intent = Intent(this, RegisterActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                }
            }
        }
    }

    private fun isUserRegistered(email: String, callback: (Boolean) -> Unit) {
        val auth = FirebaseAuth.getInstance()

        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    val isUserRegistered = signInMethods != null && signInMethods.isNotEmpty()
                    callback(isUserRegistered)
                } else {
                    // Hata sirasinda yapilacak islemelr
                    callback(false)
                }
            }
    }
    private fun isEmailValid(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }
}
