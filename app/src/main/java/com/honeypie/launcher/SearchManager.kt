package com.honeypie.launcher

/**
 * In-memory search filter for the cached app list.
 * Pure filtering — no database, no indexing, no debounce needed.
 */
object SearchManager {

    /**
     * Filters the app list by label using case-insensitive contains match.
     * Returns the full list if query is blank.
     */
    fun filter(apps: List<AppInfo>, query: String): List<AppInfo> {
        if (query.isBlank()) return apps

        val result = ArrayList<AppInfo>(apps.size / 4) // reasonable initial capacity
        for (app in apps) {
            if (app.label.contains(query, ignoreCase = true)) {
                result.add(app)
            }
        }
        return result
    }
}
