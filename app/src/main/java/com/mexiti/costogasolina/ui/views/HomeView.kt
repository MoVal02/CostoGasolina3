package com.mexiti.costogasolina.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mexiti.costogasolina.model.GasolineTransaction
import com.mexiti.costogasolina.ui.components.MainTitle
import com.mexiti.costogasolina.viewmodel.GasolineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    navController: NavController,
    viewModel: GasolineViewModel,
    modifier: Modifier = Modifier
) {
    // Observa los registros y actualiza el historial al detectar cambios
    val transactions by viewModel.transaction.collectAsState()

    // Construye la pantalla principal del historial
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    MainTitle(title = "Historial de Cargas")
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },

        // Botón flotante para abrir el formulario de registro
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("AddView")
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar Carga"
                )
            }
        }
    ) { innerPadding ->

        // Muestra un mensaje cuando no existen registros
        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay registros de gasolina")
            }

        } else {

            // Presenta todos los registros mediante una lista desplazable
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = transactions,
                    key = { it.id }
                ) { item ->

                    // Construye una tarjeta para cada carga de gasolina
                    TransactionCard(
                        transaction = item,
                        onEditClick = { id ->
                            navController.navigate("EditView/$id")
                        },
                        onDeleteClick = { id ->
                            navController.navigate("DeleteView/$id")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionCard(
    transaction: GasolineTransaction,
    onEditClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Calcula el costo total de la carga incluyendo la propina
    val total = transaction.pricePerLiter * transaction.liters +
            transaction.tip

    // Tarjeta que contiene los datos de un registro
    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // Muestra la fecha y el monto total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = transaction.date,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "$" + String.format("%.2f", total),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Muestra el precio por litro y la cantidad cargada
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Precio/L: $${transaction.pricePerLiter}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Litros: ${transaction.liters} L",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Muestra la información adicional cuando corresponde
            if (transaction.tip > 0 || transaction.kilometers > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // Muestra la propina registrada
                    if (transaction.tip > 0) {
                        Text(
                            text = "Propina: $${transaction.tip}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    // Muestra el kilometraje registrado
                    if (transaction.kilometers > 0) {
                        Text(
                            text = "Km: ${transaction.kilometers} km",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            // Contiene las acciones para modificar o eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                // Abre la pantalla de edición del registro
                IconButton(
                    onClick = {
                        onEditClick(transaction.id)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar Registro",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Abre la pantalla de confirmación de eliminación
                IconButton(
                    onClick = {
                        onDeleteClick(transaction.id)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar Registro",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
