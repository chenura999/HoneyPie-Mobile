package com.honeypie.launcher

import android.graphics.drawable.Drawable

/**
 * Lightweight data class representing an installed app.
 * No Parcelable, no serialization — pure in-memory object.
 */
data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable
)
