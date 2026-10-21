package com.mexiti.costogasolina.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mexiti.costogasolina.model.GasolineTransaction
import kotlinx.coroutines.flow.Flow

// Define las operaciones disponibles para la base de datos Room
@Dao
interface GasolineDao {

    // Inserta una nueva carga de gasolina en la base de datos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: GasolineTransaction)

    // Obtiene todos los registros ordenados por fecha descendente
    @Query("SELECT * FROM gasoline_transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<GasolineTransaction>>

    // Busca un registro específico mediante su identificador
    @Query("SELECT * FROM gasoline_transactions WHERE id = :id")
    suspend fun getTransactionById(id: Int): GasolineTransaction?

    // Actualiza la información de un registro existente
    @Update
    suspend fun updateTransaction(transaction: GasolineTransaction)

    // Elimina un registro seleccionado de la base de datos
    @Delete
    suspend fun deleteTransaction(transaction: GasolineTransaction)
}

