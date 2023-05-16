package hu.ait.werewolf.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hu.ait.werewolf.ui.screen.*

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(Screen.Main.route)
            })
        }
        composable(Screen.Main.route) {
            MainScreen(
                // remove first arg (moved to day screen)
                onWriteNewPostClick = {
                navController.navigate(Screen.WritePost.route)
            },
            onLogout = {
                navController.navigate(Screen.Login.route)
            },
            toNight = {
                navController.navigate(Screen.Night.route)
            })
        }
        composable(Screen.WritePost.route) {
            WritePostScreen(onWritePostSuccess = {
                navController.popBackStack(Screen.Day.route, false)
            })
        }
        composable(Screen.Night.route) {
            NightScreen(toDay = {
                navController.navigate(Screen.Day.route)
            })
        }
        composable(Screen.Day.route) {
            DayScreen(
                onWriteNewPostClick = {
                    navController.navigate(Screen.WritePost.route)
                },
                onReset = {
                    navController.navigate((Screen.Main.route))
                }
            )
        }
    }
}