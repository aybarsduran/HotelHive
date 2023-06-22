package com.example.hotelhive

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.hotelhive.databinding.ActivityHotelDetailBinding
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HotelDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHotelDetailBinding
    private val resultImages: MutableList<ImageHotel> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHotelDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)





        val hotelId = intent.getStringExtra("hotelId")
        val hotelName = intent.getStringExtra("hotelName")
        val hotelRegion = intent.getStringExtra("hotelRegion")
        val hotelPrice = intent.getStringExtra("hotelPrice")
        val hotelNumDay = intent.getIntExtra("hotelnumday",1)
        val hotelRev = intent.getStringExtra("hotelrev")
        val hotelTotalRev= intent.getStringExtra("hotelTotalReview")
        println("HotelID" + hotelId)



        binding.hotelNameText.text = hotelName
        binding.priceInfoText.text = hotelPrice
        binding.regionNameTextView.text = hotelRegion

        binding.priceInfoDays.text = "Price for ${hotelNumDay} ${if (hotelNumDay > 1) "days" else "day"} taxes and fees included"

        if (hotelTotalRev == 0.toString()) {
            binding.reviewScoreTextView.text = ""
            binding.totalReviewsText.text = "(No reviews yet)"
        } else {
            binding.reviewScoreTextView.text = "${hotelRev} / 10"
            binding.totalReviewsText.text= "(total ${hotelTotalRev})"
        }

        binding.bookButton.setOnClickListener {
            showConfirmationDialog()
        }






        // Add a back button to the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val viewPager: ViewPager = binding.imageViewPager
        val tabLayout: TabLayout = binding.imageTabLayout

        // Create an adapter for the ViewPager
        // Create an adapter for the ViewPager
        val adapter = ImagePagerAdapter(supportFragmentManager, resultImages)


        // Set the adapter on the ViewPager
        viewPager.adapter = adapter

        // Connect the TabLayout with the ViewPager
        tabLayout.setupWithViewPager(viewPager)

        val imageProgressBar: ProgressBar = binding.imageProgressBar
        imageProgressBar.visibility = View.VISIBLE //



        val testRapidAPI= testRapidAPI()
        if (hotelId!= null) {
            GlobalScope.launch {


                println(hotelId)
                val imageUrlList: List<ImageHotel> = testRapidAPI.fetchHotelImages(hotelId)

                resultImages.addAll(imageUrlList)
                println(imageUrlList.size)

                withContext(Dispatchers.Main){
                    adapter.updateImages(resultImages) // assuming your adapter has a method to update images
                    adapter.notifyDataSetChanged()
                    imageProgressBar.visibility = View.GONE

                }
            }

        }


    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmation")
        builder.setMessage("Are you sure you want to book this hotel?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            bookHotel()
            Toast.makeText(this, "Successfully booked this hotel", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
    private fun bookHotel() {

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance()


        val hotelId = intent.getStringExtra("hotelId")
        val hotelName = intent.getStringExtra("hotelName")
        val hotelRegion = intent.getStringExtra("hotelRegion")
        val hotelPrice = intent.getStringExtra("hotelPrice")
        val hotelNumDay = intent.getIntExtra("hotelnumday", 1)
        val hotelRev = intent.getStringExtra("hotelrev")
        val hotelTotalRev = intent.getStringExtra("hotelTotalReview")
        val imageurl = intent.getStringExtra("imageurl")

        // Create a new Hotel object
        val hotel = Hotel(
            hotelId!!,
            hotelName!!,
            hotelRegion!!,
            imageurl!!,
            hotelPrice!!,
            false,
            true,
            hotelRev!!,
            hotelTotalRev!!,
            hotelNumDay
        )



        if (userId != null) {
            val reservationsRef = database.getReference("reservations/$userId")
            reservationsRef.child(hotelId).setValue(hotel)
        }


    }


}