package com.ncautomation.clock.dialogs

import com.ncautomation.clock.activities.SimpleActivity
import com.ncautomation.clock.adapters.SelectTimeZonesAdapter
import com.ncautomation.clock.databinding.DialogSelectTimeZonesBinding
import com.ncautomation.clock.extensions.config
import com.ncautomation.clock.helpers.getAllTimeZones
import com.ncautomation.commons.extensions.getAlertDialogBuilder
import com.ncautomation.commons.extensions.setupDialogStuff

class AddTimeZonesDialog(val activity: SimpleActivity, private val callback: () -> Unit) {
    private val binding = DialogSelectTimeZonesBinding.inflate(activity.layoutInflater)

    init {
        binding.selectTimeZonesList.adapter = SelectTimeZonesAdapter(activity, getAllTimeZones())

        activity.getAlertDialogBuilder()
            .setPositiveButton(com.ncautomation.commons.R.string.ok) { dialog, which -> dialogConfirmed() }
            .setNegativeButton(com.ncautomation.commons.R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this)
            }
    }

    private fun dialogConfirmed() {
        val adapter = binding.selectTimeZonesList.adapter as? SelectTimeZonesAdapter
        val selectedTimeZones = adapter?.selectedKeys?.map { it.toString() }?.toHashSet() ?: LinkedHashSet()
        activity.config.selectedTimeZones = selectedTimeZones
        callback()
    }
}
