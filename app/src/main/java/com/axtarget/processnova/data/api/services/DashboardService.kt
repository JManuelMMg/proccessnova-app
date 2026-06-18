package com.axtarget.processnova.data.api.services

import com.axtarget.processnova.data.models.DashboardData
import retrofit2.Response
import retrofit2.http.GET

interface DashboardService {

    @GET("dashboard/api/")
    suspend fun getDashboard(): Response<DashboardData>

    @GET("dashboard/api/sales-chart/")
    suspend fun getSalesChart(): Response<List<com.axtarget.processnova.data.models.SalesChartPoint>>

    @GET("dashboard/api/recent-sales/")
    suspend fun getRecentSales(): Response<List<com.axtarget.processnova.data.models.SaleSummary>>
}

