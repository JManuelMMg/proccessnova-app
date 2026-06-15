package com.axtarget.processnova.data.local.dao

import androidx.room.*
import com.axtarget.processnova.data.models.OfflineSale
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {

    @Query("SELECT * FROM offline_sales WHERE synced = 0 ORDER BY createdAt ASC")
    suspend fun getPendingSales(): List<OfflineSale>

    @Query("SELECT * FROM offline_sales ORDER BY createdAt DESC")
    fun getAllSales(): Flow<List<OfflineSale>>

    @Insert
    suspend fun insert(sale: OfflineSale): Long

    @Update
    suspend fun update(sale: OfflineSale)

    @Delete
    suspend fun delete(sale: OfflineSale)

    @Query("UPDATE offline_sales SET synced = 1 WHERE localId = :id")
    suspend fun markAsSynced(id: Long)

    @Query("DELETE FROM offline_sales WHERE synced = 1")
    suspend fun deleteSynced()
}

