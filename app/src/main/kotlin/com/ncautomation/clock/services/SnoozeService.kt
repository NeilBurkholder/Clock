package com.ncautomation.clock.services

import android.app.IntentService
import android.content.Intent
import com.ncautomation.clock.extensions.config
import com.ncautomation.clock.extensions.dbHelper
import com.ncautomation.clock.extensions.hideNotification
import com.ncautomation.clock.extensions.setupAlarmClock
import com.ncautomation.clock.helpers.ALARM_ID
import com.ncautomation.commons.helpers.MINUTE_SECONDS

class SnoozeService : IntentService("Snooze") {
    override fun onHandleIntent(intent: Intent?) {
        val id = intent!!.getIntExtra(ALARM_ID, -1)
        val alarm = dbHelper.getAlarmWithId(id) ?: return
        hideNotification(id)
        setupAlarmClock(alarm, config.snoozeTime * MINUTE_SECONDS)
    }
}
