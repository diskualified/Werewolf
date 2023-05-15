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
    data class Success2(val maxUser: String) : DayScreenUIState

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
        val collection = FirebaseFirestore.getInstance().collection("votes")
        val query = collection.whereEqualTo("name", email)
        query.get().addOnSuccessListener {
            if (it.documents.size == 0) {
                val vote = Vote(
                    name = email,
                    count = "1"
                )
                collection.add(vote)
            } else {
                for (document in it.documents) {
                    val tmp = document.getString("count")!!.toInt() + 1
                    collection.document(document.id).update("count", tmp.toString())
                }
            }
        }
    }

    fun countVotes(): Int {
        var count = 0
        val collection = FirebaseFirestore.getInstance().collection("votes")
        collection.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val documentData = document.data
                    count += documentData["count"] as Int
                }
            }
            .addOnFailureListener { e ->
                // Error occurred while retrieving the collection
            }
        return count
    }

    fun findMaxVotes() =
        callbackFlow {
            val snapshotListener =
                FirebaseFirestore.getInstance().collection("votes")
                    .addSnapshotListener() { snapshot, e ->
                        val response = if (snapshot != null) {
                            val voteList = snapshot.toObjects(Vote::class.java)
                            var maxCount = 0
                            var maxUser = ""
                            voteList.forEachIndexed { index, vote ->
                                val tmp = vote.count.toInt()
                                if (tmp > maxCount) {
                                    maxCount = tmp
                                    maxUser = vote.name
                                }
                            }
                            DayScreenUIState.Success2(
                                maxUser
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

//        var maxUser = ""
//        var maxCount = 0
//        val collection = FirebaseFirestore.getInstance().collection("votes")
//        val snapshotListener =
//        collection.get()
//            .addOnSuccessListener { querySnapshot ->
//                for (document in querySnapshot) {
//                    val documentData = document.data
//                    val tmp = documentData["count"] as Int
//                    if (tmp > maxCount) {
//                        maxCount = tmp
//                        maxUser = documentData["name"] as String
//                    }
//                }
//            }
//            .addOnFailureListener { e ->
//                // Error occurred while retrieving the collection
//            }
//        return maxUser
//    }

    fun getResult(user : String): String {
        var role = ""
        val collection = FirebaseFirestore.getInstance().collection("players")
        val query = collection.whereEqualTo("name", user)
        query.get().addOnSuccessListener {
            for (document in it.documents) {
                role = document.getString("role")!!
            }
        }
        if (role == "Werewolf") {
            return "Villagers Win"
        } else {
            return "Werewolves Win"
        }
    }

}