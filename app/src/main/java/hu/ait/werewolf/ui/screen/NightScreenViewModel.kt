package hu.ait.werewolf.ui.screen

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import hu.ait.werewolf.data.Player
import hu.ait.werewolf.data.Post
import hu.ait.werewolf.data.PostWithId
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

sealed interface NightScreenUIState {
    object Init : NightScreenUIState

    data class Success(val role : String?) : NightScreenUIState
    data class Error(val error: String?) : NightScreenUIState
}
class NightScreenViewModel : ViewModel() {

    var currentUser: String
    var currentUserId: String

    init {
        currentUser = Firebase.auth.currentUser!!.email!!
        currentUserId = Firebase.auth.currentUser!!.uid
    }

}

