package hu.ait.werewolf.ui.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import hu.ait.werewolf.data.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow


sealed interface ChatUIState {
    object Init : ChatUIState

    data class Success(val postList: List<PostWithId>) : ChatUIState
    data class Error(val error: String?) : ChatUIState
}

class ChatViewModel : ViewModel() {

//    var currentUser: String = Firebase.auth.currentUser!!.email!!
//    var currentUserId: String = Firebase.auth.currentUser!!.uid


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
                        ChatUIState.Success(
                            postWithIdList
                        )
                    } else {
                        ChatUIState.Error(e?.message.toString())
                    }

                    trySend(response)
                }
        awaitClose {
            snapshotListener.remove()
        }
    }


}
