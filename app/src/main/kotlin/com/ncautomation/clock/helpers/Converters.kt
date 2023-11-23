package com.ncautomation.clock.helpers

import androidx.room.TypeConverter
import com.ncautomation.clock.extensions.gson.gson
import com.ncautomation.clock.models.StateWrapper
import com.ncautomation.clock.models.TimerState

class Converters {

    @TypeConverter
    fun jsonToTimerState(value: String): TimerState {
        return try {
            gson.fromJson(value, StateWrapper::class.java).state
        } catch (e: Exception) {
            TimerState.Idle
        }
    }

    @TypeConverter
    fun timerStateToJson(state: TimerState) = gson.toJson(StateWrapper(state))
}
