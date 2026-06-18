package com.axtarget.processnova.data.api.services

import com.axtarget.processnova.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface InventoryService {

    @GET("inventory/api/")
    suspend fun getProducts(): Response<List<Product>>

    @GET("inventory/api/products/{id}/")
    suspend fun getProduct(@Path("id") id: Int): Response<Product>

    @POST("inventory/api/quick-create/")
    suspend fun quickCreate(@Body request: QuickCreateRequest): Response<Product>

    @POST("inventory/api/add-stock/")
    suspend fun addStock(@Body request: AddStockRequest): Response<AddStockResponse>

    @POST("inventory/api/create-category/")
    suspend fun createCategory(@Body request: CreateCategoryRequest): Response<Category>

    @GET("inventory/api/categories/")
    suspend fun getCategories(): Response<List<Category>>

    @GET("inventory/api/movements/")
    suspend fun getStockMovements(): Response<List<StockMovement>>

    @GET("inventory/api/suppliers/")
    suspend fun getSuppliers(): Response<List<Supplier>>

    @GET("inventory/api/purchase-orders/")
    suspend fun getPurchaseOrders(): Response<List<PurchaseOrder>>

    @PUT("inventory/api/products/{id}/")
    suspend fun updateProduct(@Path("id") id: Int, @Body product: Product): Response<Product>

    @DELETE("inventory/api/products/{id}/")
    suspend fun deleteProduct(@Path("id") id: Int): Response<Unit>
}

data class AddStockResponse(
    val success: Boolean = false,
    val newStock: Int = 0,
    val message: String = ""
)

