package screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import model.FirstScreenModel
import screens.tabs.TabScreen

class FirstScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { FirstScreenModel() }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(screenModel.screenTitle)
            FirstScreenButton(screenModel)
            Spacer(modifier = Modifier.height(16.dp))
            TabScreenButton()
        }
    }

    @Composable
    private fun FirstScreenButton(screenModel: FirstScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        Button(
            onClick = {
                navigator.push(SecondScreen(screenModel.greetings))
                // infix call
                // navigator push SecondScreen("Hello from First Screen")
            }
        ) {
            Text(screenModel.buttonText)
        }
    }

    @Composable
    private fun TabScreenButton() {
        val navigator = LocalNavigator.currentOrThrow

        Button(
            onClick = {
                navigator.push(TabScreen())
            }
        ) {
            Text("Tabs")
        }
    }
}
