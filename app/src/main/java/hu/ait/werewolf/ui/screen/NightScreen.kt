package hu.ait.werewolf.ui.screen

import hu.ait.werewolf.R
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            "Seer" -> SeerCard(playerId = currentUserId, playersList = playersList)
        }

        Button(onClick = { toDay() }) {
            Text("Wake up")
        }
    }
}

@Composable
fun VillagerCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val image: Painter = painterResource(R.drawable.villager )
        Image(
            painter = image,
            contentDescription = "Villager Image",
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Sleep, you are a villager",
            style = TextStyle(fontSize = 20.sp)
        )
    }
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Werewolf Team:",
                style = TextStyle(
                    fontSize = 24.sp,
                    color = Color.Red
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                items(wolves) {
                    Text(
                        text = it,
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            val image: Painter = painterResource(R.drawable.werewolf )
            Image(
                painter = image,
                contentDescription = "Werewolf Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
    }
}

@Composable
fun SeerCard(playerId: String, playersList: List<Player>) {
    var selectedRole by remember { mutableStateOf("") }
    var selectedPlayerName by remember { mutableStateOf("") }
    var revealState by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "You are the seer. Choose the player whose roles you'd like to see.",
            style = TextStyle(
                fontSize = 20.sp,
                color = Color.Blue
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            items(playersList) {
                if (playerId != it.id) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "Select ${it.name}",
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        RadioButton(
                            selected = selectedRole == it.role,
                            onClick = {
                                if (!revealState) {
                                    selectedRole = it.role
                                    selectedPlayerName = it.name
                                }
                            },
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        val image: Painter = painterResource(R.drawable.seer)
        Image(
            painter = image,
            contentDescription = "Seer Image",
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (selectedRole != "") {
                    revealState = true
                }
            }
        ) {
            Text(text = "Reveal Role")
        }

        if (revealState) {
            Text(
                text = "$selectedPlayerName's role is $selectedRole!",
                style = TextStyle(
                    fontSize = 20.sp,
                    color = Color.Red
                )
            )
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "You are troublemaker. Choose the players whose roles you'd like to swap.",
            style = TextStyle(
                fontSize = 20.sp,
                color = Color.Black
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            items(playersList) {
                if (playerId != it.id) {
                    var checked by remember {
                        mutableStateOf(false)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "Select ${it.name}",
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { checkedState ->
                                swappedPlayers = if (checkedState) {
                                    swappedPlayers + it
                                } else {
                                    swappedPlayers - it
                                }
                                checked = checkedState
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        val image: Painter = painterResource(R.drawable.troublemaker )
        Image(
            painter = image,
            contentDescription = "Troublemaker Image",
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (!swapped) Button(onClick = {
            errorState = (swappedPlayers.size != 2)
            if (!errorState) {
                Role.Troublemaker.swap(swappedPlayers[0], swappedPlayers[1])
            }
            swapped = true
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