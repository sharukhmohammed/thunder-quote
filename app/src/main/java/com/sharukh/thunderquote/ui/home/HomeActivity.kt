@file:OptIn(ExperimentalMaterial3Api::class)

package com.sharukh.thunderquote.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api

class HomeActivity : ComponentActivity() {

    companion object {
        private const val INPUT_ID = "INPUT_ID"

        fun quoteIntent(context: Context, id: Int) =
            Intent(context, HomeActivity::class.java).apply {
                putExtra(INPUT_ID, id)
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

    }

    private val viewModel by viewModels<HomeViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.w("HomeActivity:", "Created Activity")
        handleIntent(intent)
        setContent {
            HomeActivityScreen(viewModel)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (intent.hasExtra(INPUT_ID)) {
            val id = intent.getIntExtra(INPUT_ID, -1)
            Log.w("HomeActivity", "New Intent with ID: $id")
        }
    }
}



