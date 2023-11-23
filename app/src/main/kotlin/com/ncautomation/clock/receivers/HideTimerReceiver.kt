package com.ncautomation.clock.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ncautomation.clock.extensions.hideTimerNotification
import com.ncautomation.clock.helpers.INVALID_TIMER_ID
import com.ncautomation.clock.helpers.TIMER_ID
import com.ncautomation.clock.models.TimerEvent
import org.greenrobot.eventbus.EventBus

class HideTimerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val timerId = intent.getIntExtra(TIMER_ID, INVALID_TIMER_ID)
        context.hideTimerNotification(timerId)
        EventBus.getDefault().post(TimerEvent.Reset(timerId))
    }
}
