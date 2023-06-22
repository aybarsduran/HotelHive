package com.example.hotelhive

import android.app.Activity
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hotelhive.databinding.FragmentSearchBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*

class SearchFragment : Fragment(), GuestPickerDialog.GuestPickerListener {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var locationButton: MaterialButton
    private lateinit var datepickerButton: MaterialButton
    private lateinit var guestpickerButton: MaterialButton
    private lateinit var searchButton: MaterialButton
    private var checkInDate: String? = null
    private var checkOutDate: String? = null
    private var adultsCount = 0
    private var childsCount = 0
    private var checkInDay=0
    private var checkInMonth=0
    private var checkInYear=0
    private var checkOutDay=0
    private var checkOutMonth=0
    private var checkOutYear=0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root

        requireActivity().title = "Search"

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        locationButton = binding.locationButton
        datepickerButton = binding.datepickerButton
        guestpickerButton = binding.guestpickerButton
        searchButton = binding.searchButton

        locationButton.setOnClickListener {
            val intent = Intent(requireContext(), LocationDialogActivity::class.java)
            startActivityForResult(intent, LOCATION_DIALOG_REQUEST_CODE)
        }

        datepickerButton.setOnClickListener {
            showDatePickerDialog()
        }
        guestpickerButton.setOnClickListener {
            val guestPickerDialog = GuestPickerDialog()
            guestPickerDialog.setGuestPickerListener(this)
            guestPickerDialog.show(parentFragmentManager, "GuestPickerDialog")
        }
        searchButton.setOnClickListener{
            val location = locationButton.text.toString()
            val intent = Intent(requireContext(), SearchResultsActivity::class.java)
            intent.putExtra("location", location)
            intent.putExtra("adultsCount", adultsCount)
            intent.putExtra("childsCount", childsCount)
            intent.putExtra("checkInDay", checkInDay)
            intent.putExtra("checkInMonth", checkInMonth)
            intent.putExtra("checkInYear", checkInYear)
            intent.putExtra("checkOutDay", checkOutDay)
            intent.putExtra("checkOutMonth", checkOutMonth)
            intent.putExtra("checkOutYear", checkOutYear)
            intent.putExtra("checkInDate", checkInDate)
            intent.putExtra("checkOutDate", checkOutDate)
            startActivity(intent)
        }


    }

    private fun showDatePickerDialog() {
        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select dates")
                .setSelection(
                    androidx.core.util.Pair(
                        MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds()
                    )
                )
                .build()
        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            // Get the selected start and end dates
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val startDate = selection.first
            val endDate = selection.second
            val formattedStartDate = dateFormat.format(startDate)
            val formattedEndDate = dateFormat.format(endDate)
            checkInDate=formattedStartDate
            checkOutDate=formattedEndDate


            val startDateCalendar = Calendar.getInstance()
            startDateCalendar.timeInMillis = startDate

            val endDateCalendar = Calendar.getInstance()
            endDateCalendar.timeInMillis = endDate

            checkInDay = startDateCalendar.get(Calendar.DAY_OF_MONTH)
            checkInMonth = startDateCalendar.get(Calendar.MONTH)
            checkInYear = startDateCalendar.get(Calendar.YEAR)

            checkOutDay = endDateCalendar.get(Calendar.DAY_OF_MONTH)
            checkOutMonth = endDateCalendar.get(Calendar.MONTH)
            checkOutYear = endDateCalendar.get(Calendar.YEAR)

            // Do something with the selected dates
            // For example, display them in a TextView
            val selectedDatesText = " $formattedStartDate - $formattedEndDate"
            datepickerButton.text = selectedDatesText



        }
        dateRangePicker.show(childFragmentManager, "dateRangePicker")
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_DIALOG_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val location = data?.getStringExtra("location")
            location?.let {
                locationButton.text = it
            }
        }
    }

    companion object {
        private const val LOCATION_DIALOG_REQUEST_CODE = 1
    }
    override fun onGuestCountsSelected(adultsCount: Int, childsCount: Int) {
        this.adultsCount = adultsCount
        this.childsCount = childsCount

        updateGuestPickerButtonText()
    }
    private fun updateGuestPickerButtonText() {
        val guestText = "$adultsCount Adults, $childsCount Kids"
        guestpickerButton.text = guestText
    }



}
