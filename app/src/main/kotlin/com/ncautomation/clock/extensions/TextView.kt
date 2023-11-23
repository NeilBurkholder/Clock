package com.ncautomation.clock.extensions

import android.widget.TextView
import com.ncautomation.commons.extensions.applyColorFilter

fun TextView.colorCompoundDrawable(color: Int) {
    compoundDrawables.filterNotNull().forEach { drawable ->
        drawable.applyColorFilter(color)
        setCompoundDrawables(drawable, null, null, null)
    }
}
