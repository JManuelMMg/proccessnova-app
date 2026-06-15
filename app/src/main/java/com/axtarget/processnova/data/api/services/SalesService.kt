package com.axtarget.processnova.data.api.services

import com.axtarget.processnova.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface SalesService {

    @GET("sales/api/get-cart/")
    suspend fun getCart(): Response<Cart>

    @POST("sales/api/add-to-cart/")
    suspend fun addToCart(@Body request: AddToCartRequest): Response<Cart>

    @POST("sales/api/update-item-quantity/")
    suspend fun updateItemQuantity(@Body request: UpdateCartItemRequest): Response<Cart>

    @POST("sales/api/remove-from-cart/")
    suspend fun removeFromCart(@Body request: RemoveFromCartRequest): Response<Cart>

    @POST("sales/api/clear-cart/")
    suspend fun clearCart(): Response<Cart>

    @POST("sales/api/checkout/")
    suspend fun checkout(@Body request: CheckoutRequest): Response<CheckoutResponse>

    @POST("sales/api/scan-product/")
    suspend fun scanProduct(@Body request: ScanProductRequest): Response<Product>

    @GET("sales/api/products-cache/")
    suspend fun getProductsCache(): Response<List<Product>>

    @POST("sales/api/sync-offline/")
    suspend fun syncOffline(@Body sales: List<OfflineSaleSync>): Response<SyncResponse>

    @POST("sales/api/switch-branch/")
    suspend fun switchBranch(@Body request: SwitchBranchRequest): Response<BranchResponse>

    @GET("sales/api/history/")
    suspend fun getSalesHistory(): Response<List<SaleDetail>>

    @GET("sales/api/history/{id}/")
    suspend fun getSaleDetail(@Path("id") id: Int): Response<SaleDetail>

    @POST("sales/api/cancel/{id}/")
    suspend fun cancelSale(@Path("id") id: Int): Response<CancelResponse>
}

data class OfflineSaleSync(
    val items: String,
    val total: Double,
    val customerId: Int?,
    val saleType: String,
    val payments: String,
    val createdAt: Long
)

data class SyncResponse(
    val success: Boolean = false,
    val synced: Int = 0,
    val failed: Int = 0,
    val message: String = ""
)

data class BranchResponse(
    val success: Boolean = false,
    val branch_name: String = ""
)

data class CancelResponse(
    val success: Boolean = false,
    val message: String = ""
)

