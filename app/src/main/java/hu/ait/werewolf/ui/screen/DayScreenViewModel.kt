package hu.ait.werewolf.ui.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import hu.ait.werewolf.data.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow


sealed interface DayScreenUIState {
    object Init : DayScreenUIState

    data class Success(val playerNames: List<String>) : DayScreenUIState
    data class Error(val error: String?) : DayScreenUIState
}

class DayScreenViewModel : ViewModel() {

    var currentUser: String
    var currentUserId: String

    init {
        currentUser = Firebase.auth.currentUser!!.email!!
        currentUserId = Firebase.auth.currentUser!!.uid
    }

    fun playerList() = callbackFlow {
        val snapshotListener =
            FirebaseFirestore.getInstance().collection("players")
                .addSnapshotListener() { snapshot, e ->
                    val response = if (snapshot != null) {
                        val playerList = snapshot.toObjects(Player::class.java)
                        val playerNames = mutableListOf<String>()
                        playerList.forEachIndexed { index, player ->
                            playerNames.add(player.name)
                        }
                        DayScreenUIState.Success(
                            playerNames
                        )
                    } else {
                        DayScreenUIState.Error(e?.message.toString())
                    }

                    trySend(response)
                }
        awaitClose {
            snapshotListener.remove()
        }
    }
}