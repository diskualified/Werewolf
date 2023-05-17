package hu.ait.werewolf.data

import com.google.firebase.firestore.FirebaseFirestore

public val allRolesStr = listOf("villager", "werewolf", "robber", "troublemaker", "seer")
sealed class Role {
    // Add role-specific actions and state management

    object Villager : Role() {
        // Villager-specific actions
    }
    object Werewolf : Role() {
        // Werewolf-specific actions
    }
//    object Seer : Role() {
//        // Seer-specific actions
//    }
    object Robber : Role() {
        // Robber-specific actions
    }
    object Troublemaker : Role() {
        // Troublemaker-specific actions
        fun swap(player1: Player, player2: Player) {
            val role1 = player1.role
            val collection = FirebaseFirestore.getInstance().collection("players")
            val query = collection.whereEqualTo("id", player1.id)
            query.get().addOnSuccessListener {
                for (document in it.documents) {
                    collection.document(document.id).update("role", player2.role)
                }
            }
            val query2 = collection.whereEqualTo("id", player2.id)
            query2.get().addOnSuccessListener {
                for (document in it.documents) {
                    collection.document(document.id).update("role", role1)
                }
            }
        }


    }
}


data class Player(
    val id: String = "",
    val name: String = "",
    var role: String = "",
    var votes: String = "0"
)
