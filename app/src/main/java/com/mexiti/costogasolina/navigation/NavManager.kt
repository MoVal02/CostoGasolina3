package com.mexiti.costogasolina.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mexiti.costogasolina.room.GasolineDao
import com.mexiti.costogasolina.ui.views.AddView
import com.mexiti.costogasolina.ui.views.HomeView
import com.mexiti.costogasolina.ui.views.EditView
import com.mexiti.costogasolina.ui.views.DeleteView
import com.mexiti.costogasolina.viewmodel.GasolineViewModel

@Composable
fun NavManager(dao: GasolineDao) {

    // Controla la navegación entre las pantallas
    val navController = rememberNavController()

    // Crea el ViewModel y le proporciona acceso al DAO de Room
    val viewModel: GasolineViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                GasolineViewModel(dao)
            }
        }
    )

    // Define la pantalla inicial y las rutas disponibles
    NavHost(
        navController = navController,
        startDestination = "Home"
    ) {

        // Muestra el historial de cargas de gasolina
        composable(route = "Home") {
            HomeView(navController, viewModel)
        }

        // Abre el formulario para agregar una nueva carga
        composable(route = "AddView") {
            AddView(navController, viewModel)
        }

        // Define la ruta para modificar un registro específico
        composable(
            route = "EditView/{transactionId}",
            arguments = listOf(
                navArgument("transactionId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            // Recupera el ID del registro que se desea editar
            val transactionId =
                backStackEntry.arguments?.getInt("transactionId") ?: 0

            // Abre la pantalla de edición con el registro seleccionado
            EditView(
                transactionId = transactionId,
                navController = navController,
                viewModel = viewModel
            )
        }

        // Define la ruta para eliminar un registro específico
        composable(
            route = "DeleteView/{transactionId}",
            arguments = listOf(
                navArgument("transactionId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            // Recupera el ID del registro que se desea eliminar
            val transactionId =
                backStackEntry.arguments?.getInt("transactionId") ?: 0

            // Abre la pantalla de confirmación de eliminación
            DeleteView(
                transactionId = transactionId,
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}



