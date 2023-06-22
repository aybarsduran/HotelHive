package com.example.hotelhive

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.hotelhive.databinding.ActivityLocationDialogBinding

class LocationDialogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val saveButton = binding.saveButton
        saveButton.setOnClickListener {
            val location = binding.locationInputEditText.text.toString()
            val resultIntent = Intent().apply {
                putExtra("location", location)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }


    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }



}