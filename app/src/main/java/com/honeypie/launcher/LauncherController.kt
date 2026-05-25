package com.honeypie.launcher

import android.content.ActivityNotFoundException
import android.content.Context
import android.util.Log
import android.widget.Toast

/**
 * Handles launching apps via intent. Single responsibility — no UI logic.
 */
object LauncherController {

    private const val TAG = "LauncherController"

    /**
     * Launches the app with the given package name.
     * Falls back to a toast if the app can't be found.
     */
    fun launchApp(context: Context, packageName: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Log.e(TAG, "App not found: $packageName", e)
                Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.w(TAG, "No launch intent for: $packageName")
            Toast.makeText(context, "Cannot launch app", Toast.LENGTH_SHORT).show()
        }
    }
}
