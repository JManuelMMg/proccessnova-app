package com.axtarget.processnova.data.api.services

import com.axtarget.processnova.data.models.DashboardData
import retrofit2.Response
import retrofit2.http.GET

interface DashboardService {

    @GET("api/dashboard/")
    suspend fun getDashboard(): Response<DashboardData>

    @GET("api/dashboard/sales-chart/")
    suspend fun getSalesChart(): Response<List<com.axtarget.processnova.data.models.SalesChartPoint>>

    @GET("api/dashboard/recent-sales/")
    suspend fun getRecentSales(): Response<List<com.axtarget.processnova.data.models.SaleSummary>>
}

