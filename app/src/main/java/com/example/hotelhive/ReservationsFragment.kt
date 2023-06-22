package com.example.hotelhive

import SpaceItemDecoration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hotelhive.databinding.FragmentReservationsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ReservationsFragment : Fragment(), ReservationsAdapter.OnReserveClickListener {

    private lateinit var binding: FragmentReservationsBinding
    private lateinit var reservationsRecyclerView: RecyclerView
    private lateinit var reservationsAdapter: ReservationsAdapter
    private lateinit var reservationsRef: DatabaseReference
    private lateinit var noReservationsTextView: TextView

    private val activeHotels: MutableList<Hotel> = mutableListOf()
    private val cancelledHotels: MutableList<Hotel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReservationsBinding.inflate(inflater, container, false)
        val view = binding.root
        requireActivity().title = "Reservations"
        noReservationsTextView = binding.noReservationsTextView

        reservationsRecyclerView = binding.reservationsRecyclerView
        reservationsAdapter = ReservationsAdapter(activeHotels, this)
        reservationsRecyclerView.adapter = reservationsAdapter
        reservationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val spacing = resources.getDimensionPixelSize(R.dimen.item_spacing)
        reservationsRecyclerView.addItemDecoration(SpaceItemDecoration(spacing))


        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance()
        reservationsRef = database.getReference("reservations/$userId")

        reservationsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val updatedReserveHotels = dataSnapshot.children.mapNotNull { it.getValue(Hotel::class.java) }
                activeHotels.clear()
                activeHotels.addAll(updatedReserveHotels)
                reservationsAdapter.notifyDataSetChanged()
                if (activeHotels.isEmpty()) {
                    noReservationsTextView.visibility = View.VISIBLE
                    reservationsRecyclerView.visibility = View.GONE
                } else {
                    noReservationsTextView.visibility = View.GONE
                    reservationsRecyclerView.visibility = View.VISIBLE
                }

            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        })

        return view
    }



    override fun onReserveClick(position: Int) {
        val hotel = activeHotels[position]

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Cancel Reservation")
        builder.setMessage("Are you sure you want to cancel this reservation?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            // Remove the hotel from active reservations
            hotel.isReserved = false
            activeHotels.removeAt(position)
            reservationsAdapter.updateData(activeHotels)

            // Remove the hotel from reservations in Firebase
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val database = FirebaseDatabase.getInstance()
            reservationsRef = database.getReference("reservations/$userId")
            reservationsRef.child(hotel.id).removeValue()

            // Add it to cancelled hotels in Firebase
            updateCancelledHotelsInDatabase(hotel)

            dialog.dismiss()
            Toast.makeText(requireContext(), "Reservation canceled successfully", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }


    override fun onHotelClick(hotel: Hotel) {
        TODO("Not yet implemented")
    }


    private fun updateCancelledHotelsInDatabase(hotel: Hotel) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance()
        val cancelledHotelsRef = database.getReference("cancelledHotels/$userId")
        // Set the hotel data under the generated key
        cancelledHotelsRef.setValue(hotel)
    }

}