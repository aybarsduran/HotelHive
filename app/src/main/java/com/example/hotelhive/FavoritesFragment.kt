package com.example.hotelhive

import SpaceItemDecoration
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hotelhive.databinding.FragmentFavoritesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FavoritesFragment : Fragment(), FavoritesAdapter.OnFavoriteClickListener {

    private lateinit var binding: FragmentFavoritesBinding

    private lateinit var favoritesRecyclerView: RecyclerView
    private lateinit var favoritesAdapter: FavoritesAdapter
    private val favoriteHotels: MutableList<Hotel> = mutableListOf()

    private lateinit var favoritesRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        val view = binding.root
        requireActivity().title = "Favorites"

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance()
        favoritesRef = database.getReference("favorites/$userId")

        favoritesRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get the list of favorite hotels
                val updatedFavoriteHotels = dataSnapshot.children.mapNotNull { it.getValue(Hotel::class.java) }
                // Update the list of favorite hotels
                favoriteHotels.clear()
                favoriteHotels.addAll(updatedFavoriteHotels)
                // Notify the adapter about the updated data
                favoritesAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        })

        favoritesRecyclerView = binding.favoritesRecyclerView

        // Set up the adapter for the RecyclerView
        favoritesAdapter = FavoritesAdapter(favoriteHotels, this)
        favoritesRecyclerView.adapter = favoritesAdapter
        favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val spacing = resources.getDimensionPixelSize(R.dimen.item_spacing)
        favoritesRecyclerView.addItemDecoration(SpaceItemDecoration(spacing))

        return view
    }

    override fun onFavoriteClick(position: Int) {
        // Get the clicked hotel
        val hotel = favoriteHotels[position]

        // Remove the hotel from favorites in Firebase
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance()
        val favoritesRef = database.getReference("favorites/$userId")
        favoritesRef.child(hotel.id).removeValue()

        // Remove the hotel from the local list of favorite hotels
        favoriteHotels.removeAt(position)
        favoritesAdapter.notifyItemRemoved(position)
    }

    override fun onHotelClick(hotel: Hotel) {
        // Otelin detaylarını göstermek için HotelDetailActivity'yi aç
        val intent = Intent(requireContext(), HotelDetailActivity::class.java)
        intent.putExtra("hotelId", hotel.id)
        intent.putExtra("hotelName", hotel.name)
        intent.putExtra("hotelRegion", hotel.region)
        intent.putExtra("hotelPrice", hotel.priceInfo)
        intent.putExtra("hotelnumday", hotel.numberOfDays)
        intent.putExtra("hotelrev", hotel.reviewScore)
        intent.putExtra("hotelTotalReview", hotel.totalReviewCount)
        intent.putExtra("imageurl",hotel.imageUrl)
        startActivity(intent)
    }
}
