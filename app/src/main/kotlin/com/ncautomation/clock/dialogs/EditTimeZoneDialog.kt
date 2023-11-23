package com.ncautomation.clock.dialogs

import com.ncautomation.clock.activities.SimpleActivity
import com.ncautomation.clock.databinding.DialogEditTimeZoneBinding
import com.ncautomation.clock.extensions.config
import com.ncautomation.clock.extensions.getEditedTimeZonesMap
import com.ncautomation.clock.extensions.getModifiedTimeZoneTitle
import com.ncautomation.clock.helpers.EDITED_TIME_ZONE_SEPARATOR
import com.ncautomation.clock.helpers.getDefaultTimeZoneTitle
import com.ncautomation.clock.models.MyTimeZone
import com.ncautomation.commons.extensions.getAlertDialogBuilder
import com.ncautomation.commons.extensions.setupDialogStuff
import com.ncautomation.commons.extensions.showKeyboard
import com.ncautomation.commons.extensions.value

class EditTimeZoneDialog(val activity: SimpleActivity, val myTimeZone: MyTimeZone, val callback: () -> Unit) {

    init {
        val binding = DialogEditTimeZoneBinding.inflate(activity.layoutInflater).apply {
            editTimeZoneTitle.setText(activity.getModifiedTimeZoneTitle(myTimeZone.id))
            editTimeZoneLabel.setText(getDefaultTimeZoneTitle(myTimeZone.id))
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(com.ncautomation.commons.R.string.ok) { dialog, which -> dialogConfirmed(binding.editTimeZoneTitle.value) }
            .setNegativeButton(com.ncautomation.commons.R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this) { alertDialog ->
                    alertDialog.showKeyboard(binding.editTimeZoneTitle)
                }
            }
    }

    private fun dialogConfirmed(newTitle: String) {
        val editedTitlesMap = activity.getEditedTimeZonesMap()

        if (newTitle.isEmpty()) {
            editedTitlesMap.remove(myTimeZone.id)
        } else {
            editedTitlesMap[myTimeZone.id] = newTitle
        }

        val newTitlesSet = HashSet<String>()
        for ((key, value) in editedTitlesMap) {
            newTitlesSet.add("$key$EDITED_TIME_ZONE_SEPARATOR$value")
        }

        activity.config.editedTimeZoneTitles = newTitlesSet
        callback()
    }
}
