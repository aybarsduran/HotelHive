package com.example.hotelhive


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.hotelhive.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val profileStartFragment = ProfileFragment()
        replaceFragment(profileStartFragment)

        mAuth = FirebaseAuth.getInstance()

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profile -> {
                    val profileFragment = ProfileFragment()
                    replaceFragment(profileFragment)
                }
                R.id.favorites -> {
                    val favoritesFragment = FavoritesFragment()
                    replaceFragment(favoritesFragment)
                }
                R.id.search -> {
                    val searchFragment = SearchFragment()
                    replaceFragment(searchFragment)
                }
                R.id.reservations -> {
                    val reservationsFragment = ReservationsFragment()
                    replaceFragment(reservationsFragment)
                }
            }
            true
        }

    }


    override fun onStart() {
        super.onStart()
        val user = mAuth.currentUser
        if (user == null) {
            startActivity(Intent(this, StartActivity::class.java))
            finish()
        }
    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }

}