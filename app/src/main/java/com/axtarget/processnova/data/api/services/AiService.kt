package com.axtarget.processnova.data.api.services

import com.axtarget.processnova.data.models.AiAnalysisRequest
import com.axtarget.processnova.data.models.AiChatRequest
import com.axtarget.processnova.data.models.AiChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AiService {

    @POST("ai/api/chat/")
    suspend fun chat(@Body request: AiChatRequest): Response<AiChatResponse>

    @POST("ai/api/inventory/analyze/")
    suspend fun analyzeInventory(@Body request: AiAnalysisRequest): Response<AiChatResponse>

    @POST("ai/api/crm/analyze/")
    suspend fun analyzeCrm(@Body request: AiAnalysisRequest): Response<AiChatResponse>

    @POST("ai/api/finance/analyze/")
    suspend fun analyzeFinance(@Body request: AiAnalysisRequest): Response<AiChatResponse>

    @POST("ai/api/inventory/prices/")
    suspend fun suggestPrices(@Body request: AiAnalysisRequest): Response<AiChatResponse>

    @POST("ai/api/hr/analyze/")
    suspend fun analyzeHr(@Body request: AiAnalysisRequest): Response<AiChatResponse>

    @POST("ai/api/logistics/analyze/")
    suspend fun analyzeLogistics(@Body request: AiAnalysisRequest): Response<AiChatResponse>
}

