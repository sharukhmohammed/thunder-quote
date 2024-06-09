@file:OptIn(ExperimentalMaterial3Api::class)

package com.sharukh.thunderquote.home.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api

class HomeActivity : ComponentActivity(), HomeScreenActions {
    private val viewModel by viewModels<HomeViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreenActivity(viewModel, this)
        }
    }

    override fun onClickRefresh() = viewModel.refresh()
    override fun onClickMore() = Unit
    override fun onClickFavorite() = Unit
    override fun onClickShare() = Unit
}



