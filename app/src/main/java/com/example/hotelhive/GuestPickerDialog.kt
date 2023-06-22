package com.example.hotelhive

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class GuestPickerDialog : BottomSheetDialogFragment() {
    private var adultsCount = 1
    private var childsCount = 0
    private lateinit var listener: GuestPickerListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.apply {
            requestFeature(Window.FEATURE_NO_TITLE)
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_guest_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adultsMinusButton: ImageButton = view.findViewById(R.id.adultsMinButton)
        val adultsPlusButton: ImageButton = view.findViewById(R.id.adultsPlusButton)
        val adultsCountText: TextView = view.findViewById(R.id.adultsCountText)

        val childsMinusButton: ImageButton = view.findViewById(R.id.childsMinButton)
        val childsPlusButton: ImageButton = view.findViewById(R.id.childsPlusButton)
        val childsCountText: TextView = view.findViewById(R.id.childsCountText)
        val closeButton: ImageButton = view.findViewById(R.id.closeButton)
        val confirmButton: MaterialButton = view.findViewById(R.id.confirmButton)

        confirmButton.setOnClickListener {
            listener.onGuestCountsSelected(adultsCount, childsCount)
            dismiss()
        }

        closeButton.setOnClickListener {
            dismiss()
        }

        adultsMinusButton.setOnClickListener {
            if (adultsCount > 1) {
                adultsCount--
                adultsCountText.text = adultsCount.toString()
            }
        }

        adultsPlusButton.setOnClickListener {
            if (adultsCount < 2) {
                adultsCount++
                adultsCountText.text = adultsCount.toString()
            }
        }

        childsMinusButton.setOnClickListener {
            if (childsCount > 0) {
                childsCount--
                childsCountText.text = childsCount.toString()
            }
        }

        childsPlusButton.setOnClickListener {
            if (childsCount < 2) {
                childsCount++
                childsCountText.text = childsCount.toString()
            }
        }
    }
    fun setGuestPickerListener(listener: SearchFragment) {
        this.listener = listener
    }
    interface GuestPickerListener {
        fun onGuestCountsSelected(adultsCount: Int, childsCount: Int)
    }

        // Handle your dialog content and functionality here

}
