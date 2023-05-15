package hu.ait.werewolf.data

import java.util.Date

data class Post(
    var time: Date = Date(),
    var uid: String = "",
    var author: String = "",
    var title: String = "",
    var body: String = "",
    var imgUrl: String = ""
) {
    // Add a public no-argument constructor
    constructor() : this(Date(), "", "", "")
}

data class PostWithId(
    val postId: String,
    val post: Post
)

data class availableRole(
    var uid: String = "",
    var role: String = ""
)

data class activeUser(
    var uid: String = "",
    var email: String = ""
)