package hu.ait.werewolf.data

import com.google.firebase.firestore.FirebaseFirestore

public val allRolesStr = listOf("villager", "werewolf", "robber", "troublemaker")
public val allRoles = listOf(Role.Villager, Role.Werewolf, Role.Robber, Role.Troublemaker)
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

enum class Team {
    Villagers,
    Werewolves
}

data class Player(
    val id: String = "",
    val name: String = "",
    var role: String = "",
    var votes: String = "0"
)

data class Message(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val content: String = "",
    val timestamp: Long = 0L
)

enum class GameState {
    SETUP, NIGHT, DAY, VOTE, GAME_OVER
}

data class NightAction(val playerId: Int, val targetPlayerId: Int? = null, val targetPlayerId2: Int? = null)
