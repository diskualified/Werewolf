package hu.ait.werewolf.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import hu.ait.werewolf.data.Player
import hu.ait.werewolf.data.Role

@Composable
fun NightScreen(
    toDay : () -> Unit = {},
    nightScreenViewModel: NightScreenViewModel = viewModel()
) {
    val currentUserId = Firebase.auth.currentUser!!.uid
    var role by remember {
        mutableStateOf("")
    }
    val collection = FirebaseFirestore.getInstance().collection("players")
    val query = collection.whereEqualTo("id", currentUserId)
    val playerListState = nightScreenViewModel.activePlayersList().collectAsState(initial = NightScreenUIState.Init)
    var playersList by remember { mutableStateOf<List<Player>>(emptyList())}


    if (playerListState.value==NightScreenUIState.Init) {
        Text("initializing")
    } else if (playerListState.value is NightScreenUIState.Success) {
        playersList = (playerListState.value as NightScreenUIState.Success).playerList
    }

    query.get().addOnSuccessListener {
        for (document in it.documents) {
            role = document.getString("role")!!
        }
    }

    Column {
        Text(role)
        when (role) {
            "Villager" -> VillagerCard()
            "Werewolf" -> WerewolfCard()
            "Troublemaker" -> TroublemakerCard(currentUserId, playersList)
        }

        Button(onClick = { toDay() }) {
            Text("Wake up")
        }
    }
}

@Composable
fun VillagerCard() {
    Text("Sleep, you are a villager")
}

@Composable
fun WerewolfCard() {
    var wolves by remember {
        mutableStateOf(mutableListOf<String>())
    }
    var wolvesLoaded by remember {
        mutableStateOf(false)
    }
    val collection = FirebaseFirestore.getInstance().collection("players")
    val query = collection.whereEqualTo("role", "Werewolf")

    query.get().addOnSuccessListener {
        for (document in it.documents) {
            wolves.add(document.getString("name")!!)
        }
        wolvesLoaded = true
    }
    if (wolvesLoaded) {
        Column() {
            Text("Werewolf Team:")
            LazyColumn() {
                items(wolves) {
                    Text(it)
                }
            }
        }
    }
}

@Composable
fun TroublemakerCard(playerId: String, playersList: List<Player>) {

    var swappedPlayers by remember { mutableStateOf<List<Player>>(emptyList())}
    var errorState by rememberSaveable { mutableStateOf(false) }
    val errorText by rememberSaveable {
        mutableStateOf(
            "Please select 2 players to swap"
        )
    }
    var swapped by rememberSaveable {
        mutableStateOf(false)
    }

    Column() {
        Text(text = "You are troublemaker. Choose the players whose roles you'd like to swap.")
        LazyColumn() {
            items(playersList) {
                if (playerId != it.id) {
                    var checked by remember {
                        mutableStateOf(false)
                    }
                    Row() {
                        Text(text = "Select ${it.name}", modifier = Modifier.align(Alignment.CenterVertically))
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { checkedState ->
                                swappedPlayers = if (checkedState) {
                                    swappedPlayers + it
                                } else {
                                    swappedPlayers - it
                                }
                                checked = checkedState
                                Log.d("swappedPlayers", swappedPlayers.toString())
                            }
                        )
                    }
                }
            }
        }

        Button(onClick = {
            errorState = (swappedPlayers.size != 2)
            if (!errorState) {
                Role.Troublemaker.swap(swappedPlayers[0], swappedPlayers[1])
            }
            swapped = true
            Log.d("error state", errorState.toString())
        }) {
            Text("Swap")
        }
        if (errorState) {
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp),
            )
        }
        if (swapped){
            Text("Successfully swapped roles")
        }
        Text("Wake up once you have swapped")
    }
}