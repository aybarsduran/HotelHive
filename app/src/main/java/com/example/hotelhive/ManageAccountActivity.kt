package com.example.hotelhive

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hotelhive.databinding.ActivityManageAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class ManageAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageAccountBinding
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Edit Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val email = user.email
            binding.emailTextView.text = email
            userId = user.uid

            // Retrieve the user's information from the Firebase Realtime Database
            val databaseRef = FirebaseDatabase.getInstance().reference
            val userRef = databaseRef.child("users").child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userInfo = snapshot.getValue(UserInfo::class.java)
                        if (userInfo != null) {
                            // Update the layout fields with the retrieved data
                            binding.nameEditLayout.editText?.setText(userInfo.name)
                            binding.surnameEditLayout.editText?.setText(userInfo.surname)
                            binding.phoneEditText.setText(userInfo.phoneNumber)
                            binding.birthdateEditText.setText(userInfo.birthDate)
                            val genderPosition = getGenderPosition(userInfo.gender)
                            binding.genderSpinner.setSelection(genderPosition)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database read error
                    Log.e("DatabaseError", error.message)
                }
            })
        }

        binding.birthdateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        binding.saveButton.setOnClickListener {
            // Retrieve the values from the input fields
            val name = binding.nameEditLayout.editText?.text.toString()
            val surname = binding.surnameEditLayout.editText?.text.toString()
            val phoneNumber = binding.phoneEditText.text.toString()
            val birthDate = binding.birthdateEditText.text.toString()
            val gender = binding.genderSpinner.selectedItem.toString()

            // Get a reference to the Firebase Realtime Database and the current user's node
            val databaseRef = FirebaseDatabase.getInstance().reference
            val userRef = databaseRef.child("users").child(userId)

            // Retrieve the profile picture value from the database
            userRef.child("profilePicture").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val profilePicture = snapshot.getValue(String::class.java)

                    // Create a new UserInfo object with the updated profile picture value
                    val updatedUserInfo = UserInfo(name, surname, phoneNumber, birthDate, gender, profilePicture ?: "")

                    // Save the user's information to the database
                    userRef.setValue(updatedUserInfo)
                        .addOnSuccessListener {
                            Toast.makeText(this@ManageAccountActivity, "Information saved successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@ManageAccountActivity, "Failed to save information", Toast.LENGTH_SHORT).show()
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database read error
                    Log.e("DatabaseError", error.message)
                }
            })
            finish()
        }

    }
    private fun showDatePickerDialog() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format(
                    Locale.getDefault(),
                    "%02d-%02d-%04d",
                    selectedDay,
                    selectedMonth + 1,
                    selectedYear
                )
                binding.birthdateEditText.setText(formattedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }
    private fun getGenderPosition(gender: String): Int {
        val genderOptions = resources.getStringArray(R.array.gender_options)
        return genderOptions.indexOf(gender)
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}