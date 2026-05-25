package com.honeypie.launcher

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Main launcher activity — the home screen.
 *
 * Wires together:
 * - AppRepository (data source)
 * - AppAdapter (RecyclerView display)
 * - SearchManager (in-memory filtering)
 * - LauncherController (app launching)
 *
 * Listens for package install/uninstall broadcasts to keep the list fresh.
 */
class MainActivity : Activity() {

    private lateinit var repository: AppRepository
    private lateinit var adapter: AppAdapter
    private lateinit var searchEditText: EditText

    // BroadcastReceiver for app install/uninstall events
    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            repository.refresh()
            updateList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize repository and load apps
        repository = AppRepository(applicationContext)
        repository.loadApps()

        // Set up adapter with click handler
        adapter = AppAdapter { app ->
            LauncherController.launchApp(this, app.packageName)
        }

        // Configure RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.appsRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator = null // Disable animations for speed

        // Submit initial app list
        adapter.submitList(repository.getApps())

        // Set up search
        searchEditText = findViewById(R.id.searchEditText)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateList()
            }
        })

        // Register package change receiver
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }
        registerReceiver(packageReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(packageReceiver)
    }

    /**
     * Handle back press — clear search if active, otherwise do nothing
     * (we're the home screen, nowhere to go back to).
     */
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (searchEditText.text.isNotEmpty()) {
            searchEditText.text.clear()
            searchEditText.clearFocus()
        }
        // Don't call super — launcher should not exit
    }

    /**
     * When resuming (e.g., returning from an app), clear search focus
     * so the keyboard doesn't pop up.
     */
    override fun onResume() {
        super.onResume()
        searchEditText.clearFocus()
    }

    /**
     * Filters the app list based on the current search query
     * and submits the result to the adapter.
     */
    private fun updateList() {
        val query = searchEditText.text.toString()
        val filtered = SearchManager.filter(repository.getApps(), query)
        adapter.submitList(filtered)
    }
}
