package com.ncautomation.clock.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ncautomation.clock.activities.MainActivity
import com.ncautomation.clock.activities.SimpleActivity
import com.ncautomation.clock.adapters.AlarmsAdapter
import com.ncautomation.clock.databinding.FragmentAlarmBinding
import com.ncautomation.clock.dialogs.ChangeAlarmSortDialog
import com.ncautomation.clock.dialogs.EditAlarmDialog
import com.ncautomation.clock.extensions.*
import com.ncautomation.clock.helpers.*
import com.ncautomation.clock.interfaces.ToggleAlarmInterface
import com.ncautomation.clock.models.Alarm
import com.ncautomation.clock.models.AlarmEvent
import com.ncautomation.commons.extensions.getProperBackgroundColor
import com.ncautomation.commons.extensions.getProperTextColor
import com.ncautomation.commons.extensions.toast
import com.ncautomation.commons.extensions.updateTextColors
import com.ncautomation.commons.helpers.SORT_BY_DATE_CREATED
import com.ncautomation.commons.helpers.ensureBackgroundThread
import com.ncautomation.commons.models.AlarmSound
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AlarmFragment : Fragment(), ToggleAlarmInterface {
    private var alarms = ArrayList<Alarm>()
    private var currentEditAlarmDialog: EditAlarmDialog? = null

    private lateinit var binding: FragmentAlarmBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        setupViews()
    }

    fun showSortingDialog() {
        ChangeAlarmSortDialog(activity as SimpleActivity) {
            setupAlarms()
        }
    }

    private fun setupViews() {
        binding.apply {
            requireContext().updateTextColors(alarmFragment)
            alarmFab.setOnClickListener {
                val newAlarm = root.context.createNewAlarm(DEFAULT_ALARM_MINUTES, 0)
                newAlarm.isEnabled = true
                newAlarm.days = getTomorrowBit()
                openEditAlarm(newAlarm)
            }
        }

        setupAlarms()
    }

    private fun setupAlarms() {
        alarms = context?.dbHelper?.getAlarms() ?: return

        when (requireContext().config.alarmSort) {
            SORT_BY_ALARM_TIME -> alarms.sortBy { it.timeInMinutes }
            SORT_BY_DATE_CREATED -> alarms.sortBy { it.id }
            SORT_BY_DATE_AND_TIME -> alarms.sortWith(compareBy<Alarm> {
                requireContext().firstDayOrder(it.days)
            }.thenBy {
                it.timeInMinutes
            })
        }
        context?.getEnabledAlarms { enabledAlarms ->
            if (enabledAlarms.isNullOrEmpty()) {
                val removedAlarms = mutableListOf<Alarm>()
                alarms.forEach {
                    if (it.days == TODAY_BIT && it.isEnabled && it.timeInMinutes <= getCurrentDayMinutes()) {
                        it.isEnabled = false
                        ensureBackgroundThread {
                            if (it.oneShot) {
                                it.isEnabled = false
                                context?.dbHelper?.deleteAlarms(arrayListOf(it))
                                removedAlarms.add(it)
                            } else {
                                context?.dbHelper?.updateAlarmEnabledState(it.id, false)
                            }
                        }
                    }
                }
                alarms.removeAll(removedAlarms)
            }
        }

        val currAdapter = binding.alarmsList.adapter
        if (currAdapter == null) {
            AlarmsAdapter(activity as SimpleActivity, alarms, this, binding.alarmsList) {
                openEditAlarm(it as Alarm)
            }.apply {
                binding.alarmsList.adapter = this
            }
        } else {
            (currAdapter as AlarmsAdapter).apply {
                updatePrimaryColor()
                updateBackgroundColor(requireContext().getProperBackgroundColor())
                updateTextColor(requireContext().getProperTextColor())
                updateItems(this@AlarmFragment.alarms)
            }
        }
    }

    private fun openEditAlarm(alarm: Alarm) {
        currentEditAlarmDialog = EditAlarmDialog(activity as SimpleActivity, alarm) {
            alarm.id = it
            currentEditAlarmDialog = null
            setupAlarms()
            checkAlarmState(alarm)
        }
    }

    override fun alarmToggled(id: Int, isEnabled: Boolean) {
        (activity as SimpleActivity).handleFullScreenNotificationsPermission { granted ->
            if (granted) {
                if (requireContext().dbHelper.updateAlarmEnabledState(id, isEnabled)) {
                    val alarm = alarms.firstOrNull { it.id == id } ?: return@handleFullScreenNotificationsPermission
                    alarm.isEnabled = isEnabled
                    checkAlarmState(alarm)
                    if (!alarm.isEnabled && alarm.oneShot) {
                        requireContext().dbHelper.deleteAlarms(arrayListOf(alarm))
                        setupAlarms()
                    }
                } else {
                    requireActivity().toast(com.ncautomation.commons.R.string.unknown_error_occurred)
                }
                requireContext().updateWidgets()
            } else {
                setupAlarms()
            }
        }
    }

    private fun checkAlarmState(alarm: Alarm) {
        if (alarm.isEnabled) {
            context?.scheduleNextAlarm(alarm, true)
        } else {
            context?.cancelAlarmClock(alarm)
        }
        (activity as? MainActivity)?.updateClockTabAlarm()
    }

    fun updateAlarmSound(alarmSound: AlarmSound) {
        currentEditAlarmDialog?.updateSelectedAlarmSound(alarmSound)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AlarmEvent.Refresh) {
        setupAlarms()
    }
}
