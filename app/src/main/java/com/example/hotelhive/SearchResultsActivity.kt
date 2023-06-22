package com.example.hotelhive

import SpaceItemDecoration
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hotelhive.databinding.ActivitySearchResultsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class SearchResultsActivity : AppCompatActivity(), SearchResultsAdapter.OnHotelClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SearchResultsAdapter
    private lateinit var binding: ActivitySearchResultsBinding
    private val resultHotels: MutableList<Hotel> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val location = intent.getStringExtra("location")
        val numOfAdults = intent.getIntExtra("adultsCount",1)
        val numOfKids= intent.getIntExtra("childsCount",2)
        val checkInDay= intent.getIntExtra("checkInDay",0)
        val checkInMonth= intent.getIntExtra("checkInMonth",0)
        val checkInYear= intent.getIntExtra("checkInYear",0)
        val checkOutDay= intent.getIntExtra("checkOutDay",0)
        val checkOutMonth= intent.getIntExtra("checkOutMonth",0)
        val checkOutYear= intent.getIntExtra("checkOutYear",0)
        val checkOutDateString= intent.getStringExtra("checkOutDate")
        val checkInDateString= intent.getStringExtra("checkInDate")

        val parseFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // assuming your date strings are in this format
        val checkInDate: Date = parseFormat.parse(checkInDateString)
        val checkOutDate: Date = parseFormat.parse(checkOutDateString)
        val displayFormat = SimpleDateFormat("d MMMM", Locale.getDefault())
        val checkInFormatted = displayFormat.format(checkInDate)
        val checkOutFormatted = displayFormat.format(checkOutDate)

        supportActionBar?.title = "$location $checkInFormatted - $checkOutFormatted"

        // Add a back button to the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)




        // Initialize RecyclerView
        recyclerView = binding.searchResultsRecyclerView
        adapter = SearchResultsAdapter()
        adapter.setOnHotelClickListener(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        val spacing = resources.getDimensionPixelSize(R.dimen.item_spacing)
        recyclerView.addItemDecoration(SpaceItemDecoration(spacing))


        val testRapidAPI= testRapidAPI()
        // Add some test favorite hotels

        binding.progressBar.visibility = View.VISIBLE

        if (location != null) {
            GlobalScope.launch {
                val gelocationId = testRapidAPI.performLocationSearch(location)

                println(gelocationId)
                val hotelList: List<Hotel> = testRapidAPI.performHotelSearch(
                    gelocationId,
                    numOfAdults,
                    checkInDay,
                    checkInMonth,
                    checkInYear,
                    checkOutDay,
                    checkOutMonth,
                    checkOutYear
                )
                resultHotels.addAll(hotelList)
                println(hotelList.size)

                withContext(Dispatchers.Main){
                    binding.progressBar.visibility = View.GONE
                    adapter.setSearchResults(resultHotels)
                }
            }

        }


    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
    override fun onHotelClick(hotel: Hotel) {
        val intent = Intent(this, HotelDetailActivity::class.java)
        intent.putExtra("hotelId", hotel.id)
        intent.putExtra("hotelName",hotel.name)
        intent.putExtra("hotelRegion", hotel.region)
        intent.putExtra("hotelPrice",hotel.priceInfo)
        intent.putExtra("hotelnumday", hotel.numberOfDays)
        intent.putExtra("hotelrev",hotel.reviewScore)
        intent.putExtra("hotelTotalReview", hotel.totalReviewCount)
        intent.putExtra("imageurl",hotel.imageUrl)

        startActivity(intent)

    }

}
