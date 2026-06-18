package com.axtarget.processnova.data.api.services

import com.axtarget.processnova.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface FinanceService {

    @GET("finance/api/summary/")
    suspend fun getSummary(): Response<FinanceSummary>

    @GET("finance/api/accounts/")
    suspend fun getAccounts(): Response<List<Account>>

    @GET("finance/api/accounts/{id}/")
    suspend fun getAccount(@Path("id") id: Int): Response<Account>

    @GET("finance/api/transactions/")
    suspend fun getTransactions(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): Response<List<Transaction>>

    @POST("finance/api/transactions/")
    suspend fun createTransaction(@Body transaction: Transaction): Response<Transaction>

    @GET("finance/api/incomes/")
    suspend fun getIncome(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): Response<List<Transaction>>

    @POST("finance/api/incomes/")
    suspend fun createIncome(@Body transaction: Transaction): Response<Transaction>

    @GET("finance/api/expenses/")
    suspend fun getExpenses(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): Response<List<Transaction>>

    @POST("finance/api/expenses/")
    suspend fun createExpense(@Body transaction: Transaction): Response<Transaction>

    @GET("finance/api/invoices/")
    suspend fun getInvoices(): Response<List<Invoice>>

    @GET("finance/api/invoices/{id}/")
    suspend fun getInvoice(@Path("id") id: Int): Response<Invoice>
}

