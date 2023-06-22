package com.example.hotelhive

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SearchResultsAdapter : RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>() {
    private val searchResults: MutableList<Hotel> = mutableListOf()
    private var onHotelClickListener: OnHotelClickListener? = null
    private lateinit var favoritesRef: DatabaseReference

    init {
        val database = FirebaseDatabase.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        favoritesRef = database.getReference("favorites/$userId")
    }

    // Create the ViewHolder
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Declare and initialize the views in the item layout
        val hotelImageView: ImageView = view.findViewById(R.id.hotelImageView)
        val hotelNameTextView: TextView = view.findViewById(R.id.hotelNameTextView)
        val priceInfoTextView: TextView = view.findViewById(R.id.priceInfoTextView)
        val favoriteButton: ImageButton = view.findViewById(R.id.favoriteButton)
        val hotelRegionNameTextView : TextView = view.findViewById(R.id.regionNameTextView)
        val reviewScoreTextView: TextView = view.findViewById(R.id.reviewScoreTextView)
        val totalReviewsTextView: TextView = view.findViewById(R.id.totalReviewsTextView)
        val priceDay: TextView = view.findViewById(R.id.priceInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the item layout and create the ViewHolder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_searchresult_hotel, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind the data to the views in each item
        val hotel = searchResults[position]
        Glide.with(holder.itemView.context)
            .load(hotel.imageUrl)
            .into(holder.hotelImageView)
        holder.hotelNameTextView.text = hotel.name
        holder.priceInfoTextView.text = hotel.priceInfo
        holder.hotelRegionNameTextView.text= hotel.region
        holder.priceDay.text = "Price for ${hotel.numberOfDays} ${if (hotel.numberOfDays > 1) "days" else "day"} taxes and fees included"

        if (hotel.totalReviewCount == 0.toString()) {
            holder.reviewScoreTextView.text = ""
            holder.totalReviewsTextView.text = "(No reviews yet)"
        } else {
            holder.reviewScoreTextView.text = "${hotel.reviewScore} / 10"
            holder.totalReviewsTextView.text = "(total ${hotel.totalReviewCount})"
        }

        holder.itemView.setOnClickListener {
            onHotelClickListener?.onHotelClick(hotel)
        }


        holder.favoriteButton.setOnClickListener {
            hotel.isFavorite = !hotel.isFavorite
            if (hotel.isFavorite) {
                favoritesRef.child(hotel.id).setValue(hotel)
            } else {
                favoritesRef.child(hotel.id).removeValue()
            }
            notifyDataSetChanged()
        }
        if (hotel.isFavorite) {
            holder.favoriteButton.setImageResource(R.drawable.icon_button_favorite)
        } else {
            holder.favoriteButton.setImageResource(R.drawable.icon_favorite_border)
        }
    }

    override fun getItemCount(): Int {
        return searchResults.size
    }

    fun setSearchResults(results: List<Hotel>) {
        searchResults.clear()
        searchResults.addAll(results)
        notifyDataSetChanged()
    }

    interface OnHotelClickListener {
        fun onHotelClick(hotel: Hotel)
    }

    fun setOnHotelClickListener(listener: OnHotelClickListener) {
        onHotelClickListener = listener
    }
}
