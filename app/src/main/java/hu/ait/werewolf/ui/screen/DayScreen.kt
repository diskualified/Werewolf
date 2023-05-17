package hu.ait.werewolf.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun DayScreen(
    onReset: () -> Unit = {},
    dayScreenViewModel:DayScreenViewModel = viewModel()
) {
    // This state will track whether the ChatUI is expanded or collapsed
    var isChatUIExpanded by remember { mutableStateOf(false) }

    val voteState = dayScreenViewModel.findMaxVotes().collectAsState(initial = DayScreenUIState.Init)
    val playerListState =
        dayScreenViewModel.playerList().collectAsState(initial = DayScreenUIState.Init)
    var selectedOption = remember { mutableStateOf("") }

    val currCount = dayScreenViewModel.countVotes().collectAsState(initial = DayScreenUIState.Init)
//    var currCount by remember { mutableStateOf(0) }
    val collection = FirebaseFirestore.getInstance().collection("players")
    var playerCount by remember {
        mutableStateOf(0)
    }
    var res by remember {
        mutableStateOf("")
    }
    var voted by remember {
        mutableStateOf(false)
    }


    Scaffold { contentPadding ->
        // Screen content
        Column(modifier = Modifier.padding(contentPadding)) {
            Column(modifier = Modifier.weight(if (isChatUIExpanded) 0.05f else 1.0f)) {
                Text("Username: ${dayScreenViewModel.currentUser}")
                if (playerListState.value == DayScreenUIState.Init) {
                    Text("initializing")
                } else if (playerListState.value is DayScreenUIState.Success) {
                    playerCount =
                        (playerListState.value as DayScreenUIState.Success).playerNames.size
                    LazyColumn() {
                        items((playerListState.value as DayScreenUIState.Success).playerNames) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedOption.value == it,
                                    onClick = { selectedOption.value = it },
                                )
                                Text(text = it)
                            }
                        }
                    }

                    if (!voted) Button(
                        onClick = {
//                            currCount = 1
//                            collection.get()
//                                .addOnSuccessListener { querySnapshot ->
//                                    for (document in querySnapshot) {
//                                        val documentData = document.data
////                                        currCount += (documentData["votes"] as String).toInt()
//                                    }
//                                }
                            dayScreenViewModel.addVote(selectedOption.value)
                            voted = true
                        }
                    ) {
                        Text("Vote")
                    }
                }

                if (currCount.value is DayScreenUIState.Success2) {
                    Text("Vote Count: ${(currCount.value as DayScreenUIState.Success2).res}")
                }
                Button(
                    enabled = currCount.value is DayScreenUIState.Success2 && (currCount.value as DayScreenUIState.Success2).res.toInt() == playerCount,
                    onClick = {
                    if (voteState.value is DayScreenUIState.Success2) {
                        res = (voteState.value as DayScreenUIState.Success2).res
                        Log.d("res", res)
                    }
                }) {
                    Text("Reveal Winner when Vote Count is ${playerCount}")
                }
                if (res != "") {
                    Text(res)
                }
                Button(onClick = {
                    dayScreenViewModel.deletePlayers()
                    onReset()
                }) {
                    Text("New Game")
                }
            }

            Box(modifier = Modifier.weight(if (isChatUIExpanded) 1f else 0.05f)) {
                ChatUI(
                    currentUserId = dayScreenViewModel.currentUserId,
                    // Pass the isChatUIExpanded state to the ChatUI function
                    isChatUIExpanded = isChatUIExpanded
                ) { isChatUIExpanded = !isChatUIExpanded }
            }
        }
    }
}
