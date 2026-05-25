package com.honeypie.launcher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo

/**
 * Fetches and caches installed launchable apps from PackageManager.
 * Apps are loaded once at startup and cached in memory.
 * Call refresh() when apps are installed/uninstalled.
 */
class AppRepository(private val context: Context) {

    @Volatile
    private var cachedApps: List<AppInfo> = emptyList()

    /**
     * Returns the cached app list. Call loadApps() first to populate.
     */
    fun getApps(): List<AppInfo> = cachedApps

    /**
     * Queries PackageManager for all launchable apps, sorts alphabetically,
     * and caches the result. This is fast enough to run on the main thread
     * for typical app counts (<200 apps).
     */
    fun loadApps() {
        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos: List<ResolveInfo> = pm.queryIntentActivities(mainIntent, 0)
        val ownPackage = context.packageName

        val apps = ArrayList<AppInfo>(resolveInfos.size)
        for (ri in resolveInfos) {
            val pkgName = ri.activityInfo.packageName
            // Skip our own launcher from the list
            if (pkgName == ownPackage) continue

            apps.add(
                AppInfo(
                    label = ri.loadLabel(pm).toString(),
                    packageName = pkgName,
                    icon = ri.loadIcon(pm)
                )
            )
        }

        // Sort alphabetically, case-insensitive
        apps.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.label })
        cachedApps = apps
    }

    /**
     * Refreshes the cached app list. Call when receiving
     * ACTION_PACKAGE_ADDED or ACTION_PACKAGE_REMOVED broadcasts.
     */
    fun refresh() {
        loadApps()
    }
}
