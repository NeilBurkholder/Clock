package com.ncautomation.clock.models

sealed interface AlarmEvent {
    object Refresh : AlarmEvent
}
