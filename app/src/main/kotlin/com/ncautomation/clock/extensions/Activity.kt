package com.ncautomation.clock.extensions

import com.ncautomation.clock.BuildConfig
import com.ncautomation.commons.activities.BaseSimpleActivity
import com.ncautomation.commons.dialogs.PermissionRequiredDialog
import com.ncautomation.commons.extensions.canUseFullScreenIntent
import com.ncautomation.commons.extensions.openFullScreenIntentSettings
import com.ncautomation.commons.extensions.openNotificationSettings

fun BaseSimpleActivity.handleFullScreenNotificationsPermission(
    notificationsCallback: (granted: Boolean) -> Unit,
) {
    handleNotificationPermission { granted ->
        if (granted) {
            if (canUseFullScreenIntent()) {
                notificationsCallback(true)
            } else {
                PermissionRequiredDialog(
                    activity = this,
                    textId = com.ncautomation.commons.R.string.allow_full_screen_notifications_reminders,
                    positiveActionCallback = {
                        openFullScreenIntentSettings(BuildConfig.APPLICATION_ID)
                    },
                    negativeActionCallback = {
                        notificationsCallback(false)
                    }
                )
            }
        } else {
            PermissionRequiredDialog(
                activity = this,
                textId = com.ncautomation.commons.R.string.allow_notifications_reminders,
                positiveActionCallback = {
                    openNotificationSettings()
                },
                negativeActionCallback = {
                    notificationsCallback(false)
                }
            )
        }
    }
}
