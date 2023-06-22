package com.example.hotelhive

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hotelhive.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val email = intent.getStringExtra("email")

        mAuth = FirebaseAuth.getInstance()

        binding.createAndLoginButton.setOnClickListener(){
            if (email != null) {
                createUser(email)
            }
        }
    }

    private fun createUser(email:String) {
        val password = binding.passwordEditText.text.toString()
        val confirmPassword = binding.confirmPasswordEditText.text.toString()
        if(TextUtils.isEmpty(password)){
            binding.passwordEditText.error = "Password cannot be empty"
            binding.passwordEditText.requestFocus()
            return
        }
        else if(TextUtils.isEmpty(confirmPassword)){
            binding.confirmPasswordEditText.error = "Confirm password cannot be empty"
            binding.confirmPasswordEditText.requestFocus()
            return
        }
        else if (password.length < 8) {
            binding.passwordEditText.error = "Password must be at least 8 characters long"
            binding.passwordEditText.requestFocus()
            return
        }
        else if (!password.matches(".*\\d.*".toRegex())) {
            binding.passwordEditText.error = "Password must contain at least one number"
            binding.passwordEditText.requestFocus()
            return
        }
        else if (password != confirmPassword) {
            binding.confirmPasswordEditText.error = "Passwords do not match"
            binding.confirmPasswordEditText.requestFocus()
            return
        }

        else{
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Registration success, user is created
                        val user = mAuth.currentUser
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Registration failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

    }
}