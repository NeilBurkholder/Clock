package com.ncautomation.clock.adapters

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ncautomation.clock.fragments.AlarmFragment
import com.ncautomation.clock.fragments.ClockFragment
import com.ncautomation.clock.fragments.StopwatchFragment
import com.ncautomation.clock.fragments.TimerFragment
import com.ncautomation.clock.helpers.*
import com.ncautomation.commons.models.AlarmSound

class ViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    private val fragments = HashMap<Int, Fragment>()

    override fun getItem(position: Int): Fragment {
        return getFragment(position)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position)
        if (fragment is Fragment) {
            fragments[position] = fragment
        }
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, item: Any) {
        fragments.remove(position)
        super.destroyItem(container, position, item)
    }

    override fun getCount() = TABS_COUNT

    private fun getFragment(position: Int) = when (position) {
        0 -> ClockFragment()
        1 -> AlarmFragment()
        2 -> StopwatchFragment()
        3 -> TimerFragment()
        else -> throw RuntimeException("Trying to fetch unknown fragment id $position")
    }

    fun showAlarmSortDialog() {
        (fragments[TAB_ALARM] as? AlarmFragment)?.showSortingDialog()
    }

    fun updateClockTabAlarm() {
        (fragments[TAB_CLOCK] as? ClockFragment)?.updateAlarm()
    }

    fun updateAlarmTabAlarmSound(alarmSound: AlarmSound) {
        (fragments[TAB_ALARM] as? AlarmFragment)?.updateAlarmSound(alarmSound)
    }

    fun updateTimerTabAlarmSound(alarmSound: AlarmSound) {
        (fragments[TAB_TIMER] as? TimerFragment)?.updateAlarmSound(alarmSound)
    }

    fun updateTimerPosition(timerId: Int) {
        (fragments[TAB_TIMER] as? TimerFragment)?.updatePosition(timerId)
    }

    fun startStopWatch() {
        (fragments[TAB_STOPWATCH] as? StopwatchFragment)?.startStopWatch()
    }
}
