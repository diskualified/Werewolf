package hu.ait.werewolf

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import hu.ait.werewolf.navigation.NavGraph
import hu.ait.werewolf.ui.theme.WerewolfTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WerewolfTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph()
                }
            }
        }
    }

//    override fun onPause() {
//        val collection = FirebaseFirestore.getInstance().collection("activeUsers")
//        val query = collection.whereEqualTo("email", Firebase.auth.currentUser!!.email!!)
//        query.get().addOnSuccessListener {
//            for (document in it.documents) {
//                collection.document(document.id).delete()
//            }
//        }
//            .addOnSuccessListener { Log.d("s", "DocumentSnapshot successfully deleted!") }
//        Firebase.auth.signOut()
//        super.onPause()
//    }
}
