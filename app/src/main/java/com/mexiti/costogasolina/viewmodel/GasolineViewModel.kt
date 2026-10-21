package com.mexiti.costogasolina.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mexiti.costogasolina.model.GasolineTransaction
import com.mexiti.costogasolina.room.GasolineDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Administra los datos y las operaciones de las cargas de gasolina
class GasolineViewModel(private val gasolineDao: GasolineDao): ViewModel() {

    // Mantiene actualizada la lista de registros obtenidos de Room
    val transaction: StateFlow<List<GasolineTransaction>> =
        gasolineDao.getAllTransactions()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    // Crea y guarda una nueva carga de gasolina
    fun addTransaction(
        price: Double,
        liters: Double,
        tip: Double,
        date: String,
        kilometers: Double
    ) {
        viewModelScope.launch {

            // Construye el registro con los datos ingresados
            val newTransaction = GasolineTransaction(
                pricePerLiter = price,
                liters = liters,
                tip = tip,
                date = date,
                kilometers = kilometers
            )

            // Inserta el registro en la base de datos
            gasolineDao.insertTransaction(newTransaction)
        }
    }

    // Consulta un registro mediante su ID
    suspend fun getTransactionById(id: Int): GasolineTransaction? {
        return gasolineDao.getTransactionById(id)
    }

    // Actualiza un registro utilizando una corrutina
    fun updateTransaction(transaction: GasolineTransaction) {
        viewModelScope.launch {
            gasolineDao.updateTransaction(transaction)
        }
    }

    // Elimina un registro utilizando una corrutina
    fun deleteTransaction(transaction: GasolineTransaction) {
        viewModelScope.launch {
            gasolineDao.deleteTransaction(transaction)
        }
    }
}

