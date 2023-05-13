package hu.ait.werewolf.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import hu.ait.werewolf.data.GameState
import hu.ait.werewolf.data.Role

class WerewolfServer {
    // This is just a mock implementation, you need to replace it with actual server logic
    private val _playerRole = MutableLiveData<Role>()
    val playerRole: LiveData<Role> = _playerRole

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    private val _gameState = MutableLiveData<GameState>()
    val gameState: LiveData<GameState> = _gameState

    fun listenToPlayerRole(roomId: String, playerId: String, onRoleUpdate: (Role) -> Unit) {
        // Replace this mock implementation with actual server logic
        _playerRole.observeForever { role ->
            onRoleUpdate(role)
        }
    }

    fun listenToMessages(roomId: String, onMessagesUpdate: (List<Message>) -> Unit) {
        // Replace this mock implementation with actual server logic
        _messages.observeForever { messages ->
            onMessagesUpdate(messages)
        }
    }

    fun listenToGameState(roomId: String, onGameStateUpdate: (GameState) -> Unit) {
        // Replace this mock implementation with actual server logic
        _gameState.observeForever { gameState ->
            onGameStateUpdate(gameState)
        }
    }
}
