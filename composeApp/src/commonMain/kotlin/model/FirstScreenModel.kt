package model

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FirstScreenModel : ScreenModel {
    val screenTitle = "First screen"
    val buttonText = "Second Screen"
    val greetings = "Hello from First Screen"

    val countDownText = mutableStateOf<String>("0")

    init {
        screenModelScope.launch {
            for (i in 10 downTo 0) {
                countDownText.value = i.toString()
                delay(1000)
            }
        }
    }
}