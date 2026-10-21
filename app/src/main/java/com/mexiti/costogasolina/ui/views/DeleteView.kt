package com.mexiti.costogasolina.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mexiti.costogasolina.model.GasolineTransaction
import com.mexiti.costogasolina.viewmodel.GasolineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteView(
    transactionId: Int,
    navController: NavController,
    viewModel: GasolineViewModel,
    modifier: Modifier = Modifier
) {
    // Guarda el registro que se desea eliminar
    var transaction by remember(transactionId) {
        mutableStateOf<GasolineTransaction?>(null)
    }

    // Controla el estado de carga de los datos
    var isLoading by remember(transactionId) {
        mutableStateOf(true)
    }

    // Consulta el registro seleccionado mediante su ID
    LaunchedEffect(transactionId) {
        isLoading = true
        transaction = viewModel.getTransactionById(transactionId)
        isLoading = false
    }

    // Construye la pantalla de eliminación
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Eliminar Registro") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor =
                        MaterialTheme.colorScheme.errorContainer
                )
            )
        }
    ) { innerPadding ->

        // Muestra un indicador mientras se recupera el registro
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        } else if (transaction != null) {

            // Presenta la confirmación antes de eliminar
            DeleteContent(
                transaction = transaction!!,
                onConfirmDelete = { itemToDelete ->

                    // Solicita eliminar el registro mediante el ViewModel
                    viewModel.deleteTransaction(itemToDelete)

                    // Regresa a la pantalla del historial
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                },
                modifier = Modifier.padding(innerPadding)
            )

        } else {

            // Muestra un mensaje si el registro ya no existe
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("El registro no existe o ya fue eliminado")
            }
        }
    }
}

@Composable
fun DeleteContent(
    transaction: GasolineTransaction,
    onConfirmDelete: (GasolineTransaction) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Calcula el importe total del registro
    val total =
        transaction.pricePerLiter * transaction.liters +
                transaction.tip

    // Evita ejecutar repetidamente la acción de eliminación
    var deleting by remember(transaction.id) {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Muestra el icono de advertencia
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Advertencia",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "¿Está seguro de eliminar este registro?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )

        // Muestra la información del registro seleccionado
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = "Fecha: ${transaction.date}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Precio/Litro: $${transaction.pricePerLiter}"
                )

                Text(
                    text = "Litros: ${transaction.liters} L"
                )

                // Muestra la propina únicamente si es mayor que cero
                if (transaction.tip > 0) {
                    Text(
                        text = "Propina: $${transaction.tip}"
                    )
                }

                // Muestra los kilómetros si fueron registrados
                if (transaction.kilometers > 0) {
                    Text(
                        text = "Kilómetros: ${transaction.kilometers} km"
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Muestra el monto correspondiente a la carga
                Text(
                    text = "Monto total: $" +
                            String.format("%.2f", total),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Botones para cancelar o confirmar la eliminación
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Regresa al historial sin eliminar el registro
            OutlinedButton(
                onClick = onCancel,
                enabled = !deleting,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }

            // Confirma la eliminación del registro seleccionado
            Button(
                onClick = {
                    deleting = true
                    onConfirmDelete(transaction)
                },
                enabled = !deleting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Eliminar")
            }
        }
    }
}



