package com.axtarget.processnova.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.axtarget.processnova.core.Constants
import com.axtarget.processnova.data.local.dao.CustomerDao
import com.axtarget.processnova.data.local.dao.ProductDao
import com.axtarget.processnova.data.local.dao.SaleDao
import com.axtarget.processnova.data.models.CachedCustomer
import com.axtarget.processnova.data.models.CachedProduct
import com.axtarget.processnova.data.models.OfflineSale

/**
 * Base de datos Room para almacenamiento offline.
 * Cachea productos, clientes y ventas pendientes de sincronización.
 */
@Database(
    entities = [CachedProduct::class, CachedCustomer::class, OfflineSale::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun customerDao(): CustomerDao
    abstract fun saleDao(): SaleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    Constants.DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

