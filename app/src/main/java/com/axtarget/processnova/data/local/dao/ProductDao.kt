package com.axtarget.processnova.data.local.dao

import androidx.room.*
import com.axtarget.processnova.data.models.CachedProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM cached_products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<CachedProduct>>

    @Query("SELECT * FROM cached_products WHERE id = :id")
    suspend fun getProductById(id: Int): CachedProduct?

    @Query("SELECT * FROM cached_products WHERE barcode = :barcode LIMIT 1")
    suspend fun getProductByBarcode(barcode: String): CachedProduct?

    @Query("SELECT * FROM cached_products WHERE name LIKE '%' || :query || '%' OR sku LIKE '%' || :query || '%' OR barcode LIKE '%' || :query || '%'")
    fun searchProducts(query: String): Flow<List<CachedProduct>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<CachedProduct>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: CachedProduct)

    @Query("DELETE FROM cached_products")
    suspend fun clearAll()
}

