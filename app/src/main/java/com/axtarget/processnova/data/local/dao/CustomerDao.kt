package com.axtarget.processnova.data.local.dao

import androidx.room.*
import com.axtarget.processnova.data.models.CachedCustomer
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {

    @Query("SELECT * FROM cached_customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<CachedCustomer>>

    @Query("SELECT * FROM cached_customers WHERE id = :id")
    suspend fun getCustomerById(id: Int): CachedCustomer?

    @Query("SELECT * FROM cached_customers WHERE name LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%'")
    fun searchCustomers(query: String): Flow<List<CachedCustomer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(customers: List<CachedCustomer>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customer: CachedCustomer)

    @Query("DELETE FROM cached_customers")
    suspend fun clearAll()
}

