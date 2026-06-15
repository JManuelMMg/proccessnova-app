package com.axtarget.processnova.data.api.services

import com.axtarget.processnova.data.models.AppNotification
import retrofit2.Response
import retrofit2.http.*

interface NotificationService {

    @GET("notifications/api/list/")
    suspend fun getNotifications(): Response<List<AppNotification>>

    @POST("notifications/api/mark-read/{id}/")
    suspend fun markAsRead(@Path("id") id: Int): Response<Unit>

    @POST("notifications/api/mark-all-read/")
    suspend fun markAllAsRead(): Response<Unit>

    @GET("notifications/api/unread-count/")
    suspend fun getUnreadCount(): Response<UnreadCountResponse>
}

data class UnreadCountResponse(
    val count: Int = 0
)

