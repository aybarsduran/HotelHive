package com.example.hotelhive;
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.hotelhive.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private var isLoading = true
    private lateinit var loadingProgressBar: ProgressBar

    companion object {
        private const val REQUEST_IMAGE_SELECTION = 1001
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        requireActivity().title = "Profile"
        isLoading = true
        binding.profilePictureImageView.visibility = View.GONE
        binding.nameTextView.visibility = View.GONE
        loadingProgressBar = binding.loadingProgressBar

        //get the user
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // User is signed in
            val email = user.email

            val databaseRef = FirebaseDatabase.getInstance().reference
            val userId = user.uid
            val profilePictureRef = databaseRef.child("users").child(userId).child("profilePicture")
            profilePictureRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val base64Image = snapshot.getValue(String::class.java)
                    if (base64Image != null) {
                        val decodedImage = decodeBase64ToBitmap(base64Image)
                        binding.profilePictureImageView.setImageBitmap(decodedImage)
                    }
                    isLoading = false
                    showProfileViews()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database read error
                    Log.e("DatabaseError", error.message)
                    // Set the default picture as a fallback
                    binding.profilePictureImageView.setImageResource(R.drawable.defaultprofileimage)
                    isLoading = false
                    showProfileViews()
                }
            })

            val nameRef = databaseRef.child("users").child(userId).child("name")

            nameRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.getValue(String::class.java)
                    if (!name.isNullOrEmpty()) {
                        binding.nameTextView.text = name
                    } else {
                        // Use the email address as a fallback if name is not available
                        binding.nameTextView.text = email
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database read error
                    Log.e("DatabaseError", error.message)
                    // Use the email address as a fallback
                    binding.nameTextView.text = email
                }
            })

        }
        binding.profilePictureImageView.setOnClickListener {
            // Launch image selection intent
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_SELECTION)
        }
        binding.logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }
        binding.manageAccountButton.setOnClickListener {
            val intent = Intent(requireContext(), ManageAccountActivity::class.java)
            startActivity(intent)
        }

        binding.settingsButton.setOnClickListener {
            Toast.makeText(requireContext(), "This feature will be implemented soon.", Toast.LENGTH_SHORT).show()
        }
        binding.ratingsButton.setOnClickListener {
            Toast.makeText(requireContext(), "This feature will be implemented soon.", Toast.LENGTH_SHORT).show()
        }



        return view
    }
    override fun onResume() {
        super.onResume()

        // Reload the user's data from the Firebase Realtime Database
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val email = user.email
            val databaseRef = FirebaseDatabase.getInstance().reference
            val userId = user.uid

            val profilePictureRef = databaseRef.child("users").child(userId).child("profilePicture")
            profilePictureRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val base64Image = snapshot.getValue(String::class.java)
                    if (base64Image != null) {
                        val decodedImage = decodeBase64ToBitmap(base64Image)
                        binding.profilePictureImageView.setImageBitmap(decodedImage)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database read error
                    Log.e("DatabaseError", error.message)
                    // Set the default picture as a fallback
                    binding.profilePictureImageView.setImageResource(R.drawable.defaultprofileimage)
                }
            })

            val nameRef = databaseRef.child("users").child(userId).child("name")
            nameRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.getValue(String::class.java)
                    if (!name.isNullOrEmpty()) {
                        binding.nameTextView.text = name
                    } else {
                        // Use the email address as a fallback if name is not available
                        binding.nameTextView.text = email
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database read error
                    Log.e("DatabaseError", error.message)
                    // Use the email address as a fallback
                    binding.nameTextView.text = email
                }
            })
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_SELECTION && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            // Process the selected image
            if (selectedImageUri != null) {
                uploadProfilePicture(selectedImageUri)
                binding.profilePictureImageView.setImageURI(selectedImageUri)
            }
        }
    }
    private fun uploadProfilePicture(imageUri: Uri) {
        val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val data = baos.toByteArray()

        val base64Image = Base64.encodeToString(data, Base64.DEFAULT)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // Get the user ID
            val userId = user.uid

            // Update the user's profile picture in Firebase Realtime Database
            val databaseRef = FirebaseDatabase.getInstance().reference
            databaseRef.child("users").child(userId).child("profilePicture").setValue(base64Image)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Profile picture uploaded successfully
                    } else {
                        // Handle the upload failure
                    }
                }
        }
    }
    private fun decodeBase64ToBitmap(base64Image: String): Bitmap {
        val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
    private fun logout() {
        // Sign out the current user
        FirebaseAuth.getInstance().signOut()

        // Redirect the user to the login screen or any other desired screen
        // You can use intents or navigation components depending on your app's setup
        // For example, using intents:
        val intent = Intent(requireContext(), StartActivity::class.java)
        startActivity(intent)

        // Finish the current activity to prevent the user from going back to the profile screen
        requireActivity().finish()
    }
    private fun showLogoutConfirmationDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                // Call the logout function
                logout()
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            })

        // Create and show the dialog
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun showProfileViews() {
        if (isLoading) {
            // Show the progress indicator
            loadingProgressBar.visibility = View.VISIBLE
            binding.profilePictureImageView.visibility = View.GONE
            binding.nameTextView.visibility = View.GONE
        } else {
            // Data has been loaded, show the profile picture and name views
            loadingProgressBar.visibility = View.GONE
            binding.profilePictureImageView.visibility = View.VISIBLE
            binding.nameTextView.visibility = View.VISIBLE
        }
    }
}