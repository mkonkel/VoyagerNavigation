package screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import model.FirstScreenModel

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
}
