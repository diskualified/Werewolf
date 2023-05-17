package hu.ait.werewolf.ui.screen

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import hu.ait.werewolf.data.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


sealed interface DayScreenUIState {
    object Init : DayScreenUIState

    data class Success(val playerNames: List<String>) : DayScreenUIState
    data class Success2(val res: String) : DayScreenUIState

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

    fun addVote(email : String) {
        val collection = FirebaseFirestore.getInstance().collection("players")
        val query = collection.whereEqualTo("name", email)
        query.get().addOnSuccessListener {
            for (document in it.documents) {
                val tmp = document.getString("votes")!!.toInt() + 1
                collection.document(document.id).update("votes", tmp.toString())
            }
        }
    }

    fun countVotes() =
        callbackFlow {
            val snapshotListener =
                FirebaseFirestore.getInstance().collection("players")
                    .addSnapshotListener() { snapshot, e ->
                        val response = if (snapshot != null) {
                            val voteList = snapshot.toObjects(Player::class.java)
                            var voteCount = 0
                            voteList.forEachIndexed { index, player ->
                                voteCount += player.votes.toInt()
                            }
                            DayScreenUIState.Success2(
                                voteCount.toString()
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



    fun findMaxVotes() =
        callbackFlow {
            val snapshotListener =
                FirebaseFirestore.getInstance().collection("players")
                    .addSnapshotListener() { snapshot, e ->
                        val response = if (snapshot != null) {
                            val voteList = snapshot.toObjects(Player::class.java)
                            var maxCount = 0
                            var maxUser = ""
                            var res = ""
                            voteList.forEachIndexed { index, player ->
                                val tmp = player.votes.toInt()
                                if (tmp > maxCount) {
                                    maxCount = tmp
                                    maxUser = player.name
                                }
                            }
                            voteList.forEach { player ->
                                if (player.name == maxUser) {
                                    if (player.role == "Werewolf") {
                                        res = "Villagers Win"
                                    } else {
                                        res = "Werewolves Win"
                                    }
                                }
                            }
                            DayScreenUIState.Success2(
                                res
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

    fun deletePlayers() {
        val collection = FirebaseFirestore.getInstance().collection("players")
        collection.get().addOnSuccessListener {
            for (document in it.documents) {
                collection.document(document.id).delete()
            }
        }
        val collection2 = FirebaseFirestore.getInstance().collection("roles")
        collection2.get().addOnSuccessListener {
            for (document in it.documents) {
                collection2.document(document.id).delete()
            }
        }
        val collection3 = FirebaseFirestore.getInstance().collection("posts")
        collection3.get().addOnSuccessListener {
            for (document in it.documents) {
                collection3.document(document.id).delete()
            }
        }
    }
}
