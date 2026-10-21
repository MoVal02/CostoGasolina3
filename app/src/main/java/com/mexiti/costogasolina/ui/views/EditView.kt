package com.mexiti.costogasolina.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mexiti.costogasolina.model.GasolineTransaction
import com.mexiti.costogasolina.viewmodel.GasolineViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditView(
    transactionId: Int,
    navController: NavController,
    viewModel: GasolineViewModel,
    modifier: Modifier = Modifier
) {
    // Almacena temporalmente el registro que se desea modificar
    var transaction by remember(transactionId) {
        mutableStateOf<GasolineTransaction?>(null)
    }

    // Indica si los datos todavía se están cargando
    var isLoading by remember(transactionId) {
        mutableStateOf(true)
    }

    // Recupera desde Room el registro seleccionado
    LaunchedEffect(transactionId) {
        isLoading = true
        transaction = viewModel.getTransactionById(transactionId)
        isLoading = false
    }

    // Permite ejecutar acciones relacionadas con la interfaz
    val scope = rememberCoroutineScope()

    // Controla los mensajes informativos mostrados al usuario
    val snackbarHostState = remember { SnackbarHostState() }

    // Construye la estructura principal de la pantalla
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Editar Registro") },
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
                        MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { innerPadding ->

        // Muestra un indicador mientras se consulta el registro
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

            // Muestra el formulario con los datos actuales
            EditContent(
                transaction = transaction!!,
                modifier = Modifier.padding(innerPadding),
                onUpdateTransaction = { updatedItem ->

                    // Envía los cambios al ViewModel
                    viewModel.updateTransaction(updatedItem)

                    // Muestra un mensaje y regresa al historial
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            "Registro actualizado correctamente"
                        )
                        navController.popBackStack()
                    }
                }
            )

        } else {

            // Informa cuando el registro solicitado no existe
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Registro no encontrado")
            }
        }
    }
}

@Composable
fun EditContent(
    transaction: GasolineTransaction,
    onUpdateTransaction: (GasolineTransaction) -> Unit,
    modifier: Modifier = Modifier
) {
    // Inicializa los campos con los valores guardados
    var precio by remember(transaction.id) {
        mutableStateOf(transaction.pricePerLiter.toString())
    }

    var litros by remember(transaction.id) {
        mutableStateOf(transaction.liters.toString())
    }

    var propina by remember(transaction.id) {
        mutableStateOf(transaction.tip.toString())
    }

    var kilometros by remember(transaction.id) {
        mutableStateOf(transaction.kilometers.toString())
    }

    // Convierte los valores escritos a números decimales
    val precioNumero = precio.toDoubleOrNull()
    val litrosNumero = litros.toDoubleOrNull()
    val propinaNumero = propina.toDoubleOrNull()
    val kilometrosNumero = kilometros.toDoubleOrNull()

    // Verifica que los datos ingresados sean válidos
    val formularioValido =
        precioNumero != null && precioNumero > 0 &&
                litrosNumero != null && litrosNumero > 0 &&
                propinaNumero != null && propinaNumero >= 0 &&
                kilometrosNumero != null && kilometrosNumero >= 0

    // Calcula automáticamente el importe de la carga
    val total =
        (precioNumero ?: 0.0) * (litrosNumero ?: 0.0) +
                (propinaNumero ?: 0.0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Contenedor de los campos que pueden modificarse
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Modificar datos de la carga",
                    style = MaterialTheme.typography.titleMedium
                )

                // Campo para modificar el precio por litro
                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("Precio por litro") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Campo para modificar la cantidad de litros
                OutlinedTextField(
                    value = litros,
                    onValueChange = { litros = it },
                    label = { Text("Cantidad de litros") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Campo para modificar la propina
                OutlinedTextField(
                    value = propina,
                    onValueChange = { propina = it },
                    label = { Text("Propina") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Campo para modificar el kilometraje
                OutlinedTextField(
                    value = kilometros,
                    onValueChange = { kilometros = it },
                    label = { Text("Kilómetros") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }

        // Muestra el monto total actualizado
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Monto total",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "$" + String.format("%.2f", total),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Guarda los cambios cuando los valores sean válidos
        Button(
            onClick = {
                if (formularioValido) {

                    // Conserva el ID y la fecha del registro original
                    val registroActualizado = transaction.copy(
                        pricePerLiter = precioNumero!!,
                        liters = litrosNumero!!,
                        tip = propinaNumero!!,
                        kilometers = kilometrosNumero!!
                    )

                    onUpdateTransaction(registroActualizado)
                }
            },
            enabled = formularioValido,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Cambios")
        }
    }
}



