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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

data class SecondScreen(val greetings: String) : Screen {
    @Composable
    override fun Content() {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("First screen")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Greetings: $greetings}")
            SecondScreenButton()
        }
    }

    @Composable
    private fun SecondScreenButton() {
        val navigator = LocalNavigator.currentOrThrow

        Button(
            onClick = {
                navigator.pop()
            }
        ) {
            Text("Go Back")
        }
    }
}
