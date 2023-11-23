package com.ncautomation.clock.dialogs

import com.ncautomation.clock.R
import com.ncautomation.clock.databinding.DialogChangeAlarmSortBinding
import com.ncautomation.clock.extensions.config
import com.ncautomation.clock.helpers.SORT_BY_ALARM_TIME
import com.ncautomation.clock.helpers.SORT_BY_CREATION_ORDER
import com.ncautomation.clock.helpers.SORT_BY_DATE_AND_TIME
import com.ncautomation.commons.activities.BaseSimpleActivity
import com.ncautomation.commons.extensions.getAlertDialogBuilder
import com.ncautomation.commons.extensions.setupDialogStuff

class ChangeAlarmSortDialog(val activity: BaseSimpleActivity, val callback: () -> Unit) {
    private val binding = DialogChangeAlarmSortBinding.inflate(activity.layoutInflater).apply {
        val activeRadioButton = when (activity.config.alarmSort) {
            SORT_BY_ALARM_TIME -> sortingDialogRadioAlarmTime
            SORT_BY_DATE_AND_TIME -> sortingDialogRadioDayAndTime
            else -> sortingDialogRadioCreationOrder
        }
        activeRadioButton.isChecked = true
    }

    init {
        activity.getAlertDialogBuilder()
            .setPositiveButton(com.ncautomation.commons.R.string.ok) { _, _ -> dialogConfirmed() }
            .setNegativeButton(com.ncautomation.commons.R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this, com.ncautomation.commons.R.string.sort_by)
            }
    }

    private fun dialogConfirmed() {
        val sort = when (binding.sortingDialogRadioSorting.checkedRadioButtonId) {
            R.id.sorting_dialog_radio_alarm_time -> SORT_BY_ALARM_TIME
            R.id.sorting_dialog_radio_day_and_time -> SORT_BY_DATE_AND_TIME
            else -> SORT_BY_CREATION_ORDER
        }

        activity.config.alarmSort = sort
        callback()
    }
}
