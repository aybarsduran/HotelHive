package com.example.hotelhive


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ImagePagerAdapter(
    fragmentManager: FragmentManager,
    private var images: List<ImageHotel>
) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int = images.size

    override fun getItem(position: Int): Fragment {
        return ImageFragment.newInstance(images[position].imageUrlEach) // Assuming you have this method in your ImageFragment
    }

    // Call this function when you have fetched images and want to update the ViewPager
    fun updateImages(newImages: List<ImageHotel>) {
        this.images = newImages
    }

    // If you want to show image descriptions in your TabLayout, you can override this
    // Assuming ImageHotel has a 'description' property
    override fun getPageTitle(position: Int): CharSequence? {
        return "description"
    }
}
