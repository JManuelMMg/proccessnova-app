package com.axtarget.processnova.data.repository

import com.axtarget.processnova.core.Result
import com.axtarget.processnova.core.getHttpErrorMessage
import com.axtarget.processnova.data.api.ApiClient
import com.axtarget.processnova.data.api.services.*
import com.axtarget.processnova.data.local.dao.SaleDao
import com.axtarget.processnova.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SalesRepository(
    private val saleDao: SaleDao
) {

    suspend fun getCart(): Result<Cart> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.salesService.getCart()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: Cart())
            } else {
                Result.Error(getHttpErrorMessage(response.code(), "Error al obtener carrito"), response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun addToCart(request: AddToCartRequest): Result<Cart> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.salesService.addToCart(request)
            if (response.isSuccessful) {
                Result.Success(response.body() ?: Cart())
            } else {
                Result.Error(getHttpErrorMessage(response.code(), "Error al agregar al carrito"), response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun updateItemQuantity(request: UpdateCartItemRequest): Result<Cart> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.salesService.updateItemQuantity(request)
            if (response.isSuccessful) {
                Result.Success(response.body() ?: Cart())
            } else {
                Result.Error(getHttpErrorMessage(response.code(), "Error al actualizar cantidad"), response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun removeFromCart(request: RemoveFromCartRequest): Result<Cart> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.salesService.removeFromCart(request)
            if (response.isSuccessful) {
                Result.Success(response.body() ?: Cart())
            } else {
                Result.Error(getHttpErrorMessage(response.code(), "Error al eliminar del carrito"), response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun clearCart(): Result<Cart> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.salesService.clearCart()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: Cart())
            } else {
                Result.Error(getHttpErrorMessage(response.code(), "Error al limpiar carrito"), response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun checkout(request: CheckoutRequest): Result<CheckoutResponse> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.salesService.checkout(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Error en el cobro")
            } else {
                Result.Error(getHttpErrorMessage(response.code(), "Error en el cobro"), response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun scanProduct(request: ScanProductRequest): Result<Product> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.salesService.scanProduct(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Producto no encontrado")
            } else {
                Result.Error(getHttpErrorMessage(response.code(), "Producto no encontrado"), response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun getProductsCache(): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.salesService.getProductsCache()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error(getHttpErrorMessage(response.code(), "Error al obtener productos"), response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun syncOffline(sales: List<OfflineSale>): Result<SyncResponse> = withContext(Dispatchers.IO) {
        try {
            val syncData = sales.map {
                OfflineSaleSync(
                    items = it.items,
                    total = it.total,
                    customerId = it.customerId,
                    saleType = it.saleType,
                    payments = it.payments,
                    createdAt = it.createdAt
                )
            }
            val response = ApiClient.salesService.syncOffline(syncData)
            if (response.isSuccessful) {
                // Marcar como sincronizadas
                sales.forEach { saleDao.markAsSynced(it.localId) }
                Result.Success(response.body() ?: SyncResponse())
            } else {
                Result.Error(getHttpErrorMessage(response.code(), "Error al sincronizar"), response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun getSalesHistory(): Result<List<SaleDetail>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.salesService.getSalesHistory()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error(getHttpErrorMessage(response.code(), "Error al obtener historial"), response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun getSaleDetail(id: Int): Result<SaleDetail> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.salesService.getSaleDetail(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Venta no encontrada")
            } else {
                Result.Error(getHttpErrorMessage(response.code(), "Error al obtener venta"), response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun cancelSale(id: Int): Result<CancelResponse> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.salesService.cancelSale(id)
            if (response.isSuccessful) {
                Result.Success(response.body() ?: CancelResponse())
            } else {
                Result.Error(getHttpErrorMessage(response.code(), "Error al cancelar venta"), response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    // Offline sales
    suspend fun saveOfflineSale(sale: OfflineSale): Long = withContext(Dispatchers.IO) {
        saleDao.insert(sale)
    }

    suspend fun getPendingOfflineSales(): List<OfflineSale> = withContext(Dispatchers.IO) {
        saleDao.getPendingSales()
    }

    fun getAllOfflineSales(): Flow<List<OfflineSale>> = saleDao.getAllSales()

    private fun handleException(e: Exception): String {
        android.util.Log.e("SalesRepo", "Error de red", e)
        return when (e) {
            is java.net.SocketTimeoutException -> "Servidor lento (Render despertando), reintenta en un momento"
            is java.net.UnknownHostException -> "Sin conexión a internet"
            is java.net.ConnectException -> "No se pudo conectar al servidor. Reintentando..."
            is java.io.IOException -> "Error de conexión: ${e.message}"
            else -> "Error inesperado: ${e.message}"
        }
    }
}