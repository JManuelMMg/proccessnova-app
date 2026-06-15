package com.axtarget.processnova.data.repository

import com.axtarget.processnova.core.Result
import com.axtarget.processnova.data.api.ApiClient
import com.axtarget.processnova.data.api.services.*
import com.axtarget.processnova.data.local.dao.ProductDao
import com.axtarget.processnova.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class InventoryRepository(
    private val productDao: ProductDao
) {

    suspend fun getProducts(): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.inventoryService.getProducts()
            if (response.isSuccessful) {
                val products = response.body() ?: emptyList()
                // Cachear en Room
                productDao.insertAll(products.map { it.toCached() })
                Result.Success(products)
            } else {
                Result.Error("Error al obtener productos: ${response.code()}", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    fun searchProducts(query: String): Flow<List<CachedProduct>> {
        return productDao.searchProducts(query)
    }

    fun getAllCachedProducts(): Flow<List<CachedProduct>> {
        return productDao.getAllProducts()
    }

    suspend fun getProduct(id: Int): Result<Product> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.inventoryService.getProduct(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Producto no encontrado")
            } else {
                Result.Error("Error al obtener producto", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun quickCreate(request: QuickCreateRequest): Result<Product> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.inventoryService.quickCreate(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Error al crear producto")
            } else {
                Result.Error("Error al crear producto: ${response.code()}", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun addStock(request: AddStockRequest): Result<AddStockResponse> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.inventoryService.addStock(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Error al agregar stock")
            } else {
                Result.Error("Error al agregar stock", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun getCategories(): Result<List<Category>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.inventoryService.getCategories()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Error al obtener categorías", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun createCategory(request: CreateCategoryRequest): Result<Category> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.inventoryService.createCategory(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Error al crear categoría")
            } else {
                Result.Error("Error al crear categoría", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun getStockMovements(): Result<List<StockMovement>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.inventoryService.getStockMovements()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Error al obtener movimientos", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun getSuppliers(): Result<List<Supplier>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.inventoryService.getSuppliers()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Error al obtener proveedores", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun getPurchaseOrders(): Result<List<PurchaseOrder>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.inventoryService.getPurchaseOrders()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Error al obtener órdenes de compra", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun updateProduct(id: Int, product: Product): Result<Product> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.inventoryService.updateProduct(id, product)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Error al actualizar producto")
            } else {
                Result.Error("Error al actualizar producto", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    private fun Product.toCached() = CachedProduct(
        id = id,
        name = name,
        sku = sku,
        barcode = barcode,
        salePrice = salePrice,
        currentStock = currentStock,
        category = category,
        imageUrl = imageUrl
    )

    private fun handleException(e: Exception): String {
        return when (e) {
            is java.net.SocketTimeoutException -> "Conexión lenta, verifica tu internet"
            is java.net.UnknownHostException -> "Sin conexión a internet"
            is java.io.IOException -> "Error de conexión"
            else -> "Error inesperado: ${e.message}"
        }
    }
}
