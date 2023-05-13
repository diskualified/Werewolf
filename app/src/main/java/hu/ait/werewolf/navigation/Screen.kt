package hu.ait.werewolf.navigation

// sealed: all in one place, no subclass
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Main : Screen("main")
    object WritePost : Screen("writepost")
    object Night : Screen("night")
    object Day : Screen("day")
}
