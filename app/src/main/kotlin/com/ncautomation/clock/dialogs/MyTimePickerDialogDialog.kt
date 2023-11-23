package com.ncautomation.clock.dialogs

import com.ncautomation.clock.activities.SimpleActivity
import com.ncautomation.clock.databinding.DialogMyTimePickerBinding
import com.ncautomation.commons.extensions.getAlertDialogBuilder
import com.ncautomation.commons.extensions.getProperTextColor
import com.ncautomation.commons.extensions.setupDialogStuff

class MyTimePickerDialogDialog(val activity: SimpleActivity, val initialSeconds: Int, val callback: (result: Int) -> Unit) {
    private val binding = DialogMyTimePickerBinding.inflate(activity.layoutInflater)

    init {
        binding.apply {
            val textColor = activity.getProperTextColor()
            arrayOf(myTimePickerHours, myTimePickerMinutes, myTimePickerSeconds).forEach {
                it.textColor = textColor
                it.selectedTextColor = textColor
                it.dividerColor = textColor
            }

            myTimePickerHours.value = initialSeconds / 3600
            myTimePickerMinutes.value = (initialSeconds) / 60 % 60
            myTimePickerSeconds.value = initialSeconds % 60
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(com.ncautomation.commons.R.string.ok) { dialog, which -> dialogConfirmed() }
            .setNegativeButton(com.ncautomation.commons.R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this)
            }
    }

    private fun dialogConfirmed() {
        binding.apply {
            val hours = myTimePickerHours.value
            val minutes = myTimePickerMinutes.value
            val seconds = myTimePickerSeconds.value
            callback(hours * 3600 + minutes * 60 + seconds)
        }
    }
}
