package hu.ait.werewolf.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

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

        query.get().addOnSuccessListener {
            Log.d("role", "here")
            for (document in it.documents) {
                Log.d("role2", document.getString("role")!!)
                role = document.getString("role")!!
            }

        }
//    val currRoleState =
//        nightScreenViewModel.getRole().collectAsState(initial = NightScreenUIState.Init)
    Column() {
//        if (currRoleState.value is NightScreenUIState.Success) {
//            Text((currRoleState.value as NightScreenUIState.Success).role!!)
//        }
        Text(role)
        when (role) {
            "Villager" ->
            "Robber" ->
        }

        Button(onClick = { toDay() }) {
            Text("Wake up")
        }
    }
}

@Composable
fun VillagerCard() {

}

@Composable
fun WerewolfCard() {

}