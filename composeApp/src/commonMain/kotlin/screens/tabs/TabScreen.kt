package screens.tabs

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator

class TabScreen : Screen {
    @Composable
    override fun Content() {
        TabNavigator(FirstTab) {
            Scaffold(
                bottomBar = {
                    BottomNavigation {
                        TabNavigationItem(FirstTab)
                        TabNavigationItem(SecondTab)
                    }
                }
            ) {
                CurrentTab()
            }
        }
    }

    @Composable
    private fun RowScope.TabNavigationItem(tab: Tab) {
        val tabNavigator = LocalTabNavigator.current

        BottomNavigationItem(
            selected = tabNavigator.current == tab,
            onClick = { tabNavigator.current = tab },
            icon = {
                tab.options.icon?.let {
                    Icon(painter = it, contentDescription = tab.options.title)
                }
            }
        )
    }
}