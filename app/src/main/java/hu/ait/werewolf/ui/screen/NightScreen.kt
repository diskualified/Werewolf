package hu.ait.werewolf.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun NightScreen(
    toDay : () -> Unit = {}
) {
    Column() {
        Button(onClick = { toDay() }) {
            Text("Wake up")
        }
    }
}