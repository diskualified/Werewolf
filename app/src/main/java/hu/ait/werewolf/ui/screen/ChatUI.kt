package hu.ait.werewolf.ui.screen
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ait.werewolf.data.Post


@Composable
fun ChatUI(
    currentUserId: String,
    chatViewModel: ChatViewModel = viewModel(),
    writePostViewModel: WritePostViewModel = viewModel(),
    isChatUIExpanded: Boolean,
    onChatUIHeaderClick: () -> Unit
) {

    var postListState = chatViewModel.postsList().collectAsState(initial = ChatUIState.Init)
    val textFieldValue = remember { mutableStateOf(TextFieldValue("")) }


    Scaffold(
        topBar = {
            // add clickable modifier to the top bar
            MessagesTopAppBar(switch = { onChatUIHeaderClick() })
        },
        bottomBar = {
            // check if the content should be visible
            if(isChatUIExpanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = textFieldValue.value,
                        onValueChange = { textFieldValue.value = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(text = "Type a message...") },
                        singleLine = true,
                    )
                    Button(
                        onClick = {
                            writePostViewModel.uploadPost("", textFieldValue.value.text)
                            textFieldValue.value = TextFieldValue("") // Reset the TextField after sending
                        },
                        modifier = Modifier
                            .padding(start = 8.dp)
                    ) {
                        Text("Send")
                    }
                }
            }
        },
        content = {
            // check if the content should be visible
            if(isChatUIExpanded) {
                Column(modifier = Modifier.padding(top=70.dp, bottom = 70.dp)) {
                    if (postListState.value == ChatUIState.Init) {
                        Text("initializing")
                    } else if (postListState.value is ChatUIState.Success) {
                        LazyColumn {
                            items((postListState.value as ChatUIState.Success).postList.sortedBy { it.post.time }) {
                                MessageCard(
                                    currentUserId,
                                    post = it.post,
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesTopAppBar(switch : () -> Unit) {
    TopAppBar(
        modifier = Modifier.clickable { switch() },
        title = { Text(text = "Messages", color = Color.White) },
        colors = topAppBarColors(
            containerColor = Color.LightGray
        )
    )
}

@Composable
fun MessageCard(
    currentUserId : String,
    post: Post,
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = when { // 2
            currentUserId == post.uid -> Alignment.End
            else -> Alignment.Start
        },
    ) {
        Card(
            modifier = Modifier.widthIn(max = 340.dp),
            shape = cardShapeFor(post.body, currentUserId == post.uid),
            colors = when {
                currentUserId == post.uid -> CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                )
                else -> CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                )
            },
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = post.body
            )
        }
        Text(
            // 4
            text = post.author,
            fontSize = 12.sp,
        )
    }
}

@Composable
fun cardShapeFor(message: String, isMine: Boolean): Shape {
    val roundedCorners = RoundedCornerShape(16.dp)
    return when {
        isMine -> roundedCorners.copy(bottomEnd = CornerSize(0))
        else -> roundedCorners.copy(bottomStart = CornerSize(0))
    }
}
