package hu.ait.werewolf.ui.screen

import android.util.Log
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import hu.ait.werewolf.data.*
import hu.ait.werewolf.ui.screen.WritePostViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlin.random.Random


sealed interface MainScreenUIState {
    object Init : MainScreenUIState

    data class Success(val postList: List<PostWithId>) : MainScreenUIState
    data class Success2(val roleList : List<String>) : MainScreenUIState
//    data class Success3(val roleList : List<Player>) : MainScreenUIState
    data class Error(val error: String?) : MainScreenUIState
}

class MainScreenViewModel : ViewModel() {

    var currentUser: String
    var currentUserId: String

    init {
        currentUser = Firebase.auth.currentUser!!.email!!
        currentUserId = Firebase.auth.currentUser!!.uid
    }

    fun postsList() = callbackFlow {
        val snapshotListener =
            FirebaseFirestore.getInstance().collection(WritePostViewModel.COLLECTION_POSTS)
                .addSnapshotListener() { snapshot, e ->
                    val response = if (snapshot != null) {
                        val postList = snapshot.toObjects(Post::class.java)
                        val postWithIdList = mutableListOf<PostWithId>()
                        postList.forEachIndexed { index, post ->
                            postWithIdList.add(PostWithId(snapshot.documents[index].id, post))
                        }
                        MainScreenUIState.Success(
                            postWithIdList
                        )
                    } else {
                        MainScreenUIState.Error(e?.message.toString())
                    }

                    trySend(response)
                }
        awaitClose {
            snapshotListener.remove()
        }
    }

    fun deletePost(postKey: String) {
        FirebaseFirestore.getInstance().collection(
            WritePostViewModel.COLLECTION_POSTS
        ).document(postKey).delete()
    }

    fun uploadRole(role: String) {
        var r = ""
        if (role.trim().lowercase() !in allRolesStr) {
            r = "villager"
        } else {
            r = role
        }
        val newRole = availableRole(
            uid = currentUserId,
            player = "None",
            role = r
        )
        val rolesCollection = FirebaseFirestore.getInstance().collection("roles")

        rolesCollection.add(newRole).addOnSuccessListener {
//            writePostUiState = WritePostUiState.PostUploadSuccess
            Log.d("Y", "yay")
        }.addOnFailureListener {
//            writePostUiState = WritePostUiState.ErrorDuringPostUpload(it.message)
            Log.d("N", "nay")
        }
    }

    fun rolesList() = callbackFlow {
        val snapshotListener =
            FirebaseFirestore.getInstance().collection("roles")
                .addSnapshotListener() { snapshot, e ->
                    val response = if (snapshot != null) {
                        val rolesList = snapshot.toObjects(availableRole::class.java)
                        val rolesStrList = mutableListOf<String>()
                        rolesList.forEachIndexed { index, role -> //snapshot.documents[index].id,
                            rolesStrList.add(role.role)
                        }
                        MainScreenUIState.Success2(
                            rolesStrList
                        )
                    } else {
                        MainScreenUIState.Error(e?.message.toString())
                    }

                    trySend(response)
                }
        awaitClose {
            snapshotListener.remove()
        }
    }

    fun assign() {
        val db = FirebaseFirestore.getInstance()
        db.collection("roles")
            .get().addOnSuccessListener { querySnapshot ->
                // Process the query snapshot
                val roleList = mutableListOf<String>()
                for (document in querySnapshot.documents) {
                    roleList.add(document.getString("role")!!)
                }

                db.collection("activeUsers")
                    .get().addOnSuccessListener { querySnapshot2 ->
                        // Process the query snapshot
                        val uidList = mutableListOf<String>()
                        val emailList = mutableListOf<String>()
                        for (doc in querySnapshot2.documents) {
                            Log.d("3", "hi")
                            uidList.add(doc.getString("uid")!!)
                            emailList.add(doc.getString("email")!!)
                        }

                        val n = roleList.size
                        if (n >= uidList.size) {
                            Log.d("n", "$n")
                            val array = IntArray(n)
                            for (i in 0 until n) {
                                array[i] = i
                            }
                            val arr = array.toMutableList()
                            arr.shuffle()

                            for (i in 0 until uidList.size) {
                                val player = Player(
                                    id = uidList[i],
                                    name = emailList[i],
                                    role = roleList[arr[i]]
                                )
                                FirebaseFirestore.getInstance().collection("players").add(player)
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Handle any errors that occurred
                        // ...
                    }

            }
            .addOnFailureListener { exception ->
                // Handle any errors that occurred
                // ...
            }
    }
    fun logout() {
        val collection = FirebaseFirestore.getInstance().collection("activeUsers")
        val query = collection.whereEqualTo("email", currentUser)
        query.get().addOnSuccessListener {
            for (document in it.documents) {
                collection.document(document.id).delete()
            }
        }
//            .addOnSuccessListener { Log.d("s", "DocumentSnapshot successfully deleted! ${currentUserId}") }
//            .addOnFailureListener { e -> Log.w("f", "Error deleting document", e) }
        Firebase.auth.signOut()
    }


}