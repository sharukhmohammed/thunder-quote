package com.sharukh.thunderquote.ui.base

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sharukh.thunderquote.R
import com.sharukh.thunderquote.navigation.Screen

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean = false,
    val badgeCount: Int? = null,
    val screen: Screen
)

@Composable
fun AppBottomAppBar(hasDaily: Boolean = false, onChangeItem: (Screen) -> Unit) {
    val items = listOf(
        BottomNavigationItem(
            title = stringResource(id = R.string.daily),
            selectedIcon = Icons.Default.Home,
            unselectedIcon = Icons.Outlined.Home,
            screen = Screen.QuoteDetail,
        ),
        BottomNavigationItem(
            title = stringResource(id = R.string.quotes),
            selectedIcon = Icons.AutoMirrored.Default.List,
            unselectedIcon = Icons.AutoMirrored.Default.List,
            screen = Screen.QuoteList(false)
        ),
        BottomNavigationItem(
            title = stringResource(id = R.string.share),
            selectedIcon = Icons.Default.Favorite,
            unselectedIcon = Icons.Outlined.Favorite,
            screen = Screen.QuoteList(true)
        ),
        BottomNavigationItem(
            title = stringResource(id = R.string.refresh),
            selectedIcon = Icons.Default.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            screen = Screen.Settings
        ),
    )
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItemIndex == index,
                onClick = {
                    selectedItemIndex = index
                    onChangeItem(item.screen)
                },
                label = {
                    Text(text = item.title)
                },
                alwaysShowLabel = true,
                icon = {
                    BadgedBox(
                        badge = {
                            if (item.badgeCount != null) {
                                Badge {
                                    Text(text = item.badgeCount.toString())
                                }
                            } else if (item.hasNews) {
                                Badge()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (index == selectedItemIndex) {
                                item.selectedIcon
                            } else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    }
                }
            )
        }
    }
}

@Preview
@Composable
private fun AppBottomAppBarPreview() {
    AppBottomAppBar(){

    }
}