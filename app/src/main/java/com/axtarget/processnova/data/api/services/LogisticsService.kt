package com.axtarget.processnova.data.api.services

import com.axtarget.processnova.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface LogisticsService {

    @GET("logistics/api/orders/")
    suspend fun getOrders(): Response<List<ShipmentOrder>>

    @GET("logistics/api/orders/{id}/")
    suspend fun getOrder(@Path("id") id: Int): Response<ShipmentOrder>

    @PUT("logistics/api/orders/{id}/")
    suspend fun updateOrderStatus(
        @Path("id") id: Int,
        @Body request: UpdateOrderRequest
    ): Response<ShipmentOrder>

    @GET("logistics/api/vehicles/")
    suspend fun getVehicles(): Response<List<Vehicle>>

    @GET("logistics/api/vehicles/{id}/")
    suspend fun getVehicle(@Path("id") id: Int): Response<Vehicle>
}

data class UpdateOrderRequest(
    val status: String,
    val notes: String? = null
)

