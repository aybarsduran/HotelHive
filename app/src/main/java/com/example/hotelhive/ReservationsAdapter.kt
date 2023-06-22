package com.example.hotelhive

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ReservationsAdapter(
    private val reserveHotels: MutableList<Hotel>,
    private val listener: OnReserveClickListener,

) : RecyclerView.Adapter<ReservationsAdapter.ReservationsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reservation, parent, false)
        return ReservationsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReservationsAdapter.ReservationsViewHolder, position: Int) {
        val hotel = reserveHotels[position]

        holder.bind(hotel)
    }

    override fun getItemCount(): Int {
        return reserveHotels.size
    }

    interface OnReserveClickListener {
        fun onReserveClick(position: Int)
        fun onHotelClick(hotel: Hotel)
    }

    inner class ReservationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Declare and initialize the views in the item layout
        private val hotelImageView: ImageView = itemView.findViewById(R.id.hotelImageView)
        private val hotelNameTextView: TextView = itemView.findViewById(R.id.hotelNameTextView)
        private val priceInfoTextView: TextView = itemView.findViewById(R.id.priceInfoTextView)
        private val cancelButton: Button = itemView.findViewById(R.id.cancelReservationButton)
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




            // Set up click listeners or other operations on the item if needed
            cancelButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onReserveClick(position)
                }
            }
            itemView.setOnClickListener {
                listener.onHotelClick(hotel)
            }
        }
    }
    fun updateData(hotels: List<Hotel>) {
        reserveHotels.clear()
        reserveHotels.addAll(hotels)
        notifyDataSetChanged()
    }


}