package com.example.hotelhive

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FavoritesAdapter(
    private val favoriteHotels: MutableList<Hotel>,
    private val listener: OnFavoriteClickListener
) : RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_searchresult_hotel, parent, false)
        return FavoriteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val hotel = favoriteHotels[position]
        holder.bind(hotel)
    }

    override fun getItemCount(): Int {
        return favoriteHotels.size
    }

    interface OnFavoriteClickListener {
        fun onFavoriteClick(position: Int)
        fun onHotelClick(hotel: Hotel)
    }

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Declare and initialize the views in the item layout
        private val hotelImageView: ImageView = itemView.findViewById(R.id.hotelImageView)
        private val hotelNameTextView: TextView = itemView.findViewById(R.id.hotelNameTextView)
        private val priceInfoTextView: TextView = itemView.findViewById(R.id.priceInfoTextView)
        private val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
        private val hotelRegionNameTextView : TextView = itemView.findViewById(R.id.regionNameTextView)
        private val reviewScoreTextView: TextView = itemView.findViewById(R.id.reviewScoreTextView)
        private val totalReviewsTextView: TextView = itemView.findViewById(R.id.totalReviewsTextView)
        private val priceDay: TextView = itemView.findViewById(R.id.priceInfo)

        fun bind(hotel: Hotel) {
            // Set the hotel information to the views
            hotelNameTextView.text = hotel.name
            priceInfoTextView.text = hotel.priceInfo
            hotelRegionNameTextView.text = hotel.region
            Glide.with(itemView.context)
                .load(hotel.imageUrl)
                .into(hotelImageView)

            priceDay.text = "Price for ${hotel.numberOfDays} ${if (hotel.numberOfDays > 1) "days" else "day"} taxes and fees included"

            if (hotel.totalReviewCount == 0.toString()) {
                reviewScoreTextView.text = ""
                totalReviewsTextView.text = "(No reviews yet)"
            } else {
                reviewScoreTextView.text = "${hotel.reviewScore} / 10"
                totalReviewsTextView.text = "(total ${hotel.totalReviewCount})"
            }


            // Always show the filled favorite icon for items in the favorites list
            favoriteButton.setImageResource(R.drawable.icon_button_favorite)

            // Set up click listeners or other operations on the item if needed
            favoriteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onFavoriteClick(position)
                }
            }
            itemView.setOnClickListener {
                listener.onHotelClick(hotel)
            }
        }
    }
}
