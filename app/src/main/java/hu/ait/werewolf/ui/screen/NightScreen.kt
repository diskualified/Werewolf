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

        query.get().addOnSuccessListener {
            for (document in it.documents) {
                role = document.getString("role")!!
            }

        }

    Column {
        Text(role)
        when (role) {
            "Villager" -> VillagerCard()
            "Werewolf" -> {
                Text("hi")
                WerewolfCard()
            }
            "Troublemaker" -> TroublemakerCard()
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
        LazyColumn() {
            items(wolves) {
                Text(it)
            }
        }
    }
}

@Composable
fun TroublemakerCard() {

}
