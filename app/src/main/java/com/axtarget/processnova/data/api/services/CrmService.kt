package com.axtarget.processnova.data.api.services

import com.axtarget.processnova.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface CrmService {

    @GET("crm/api/customers/")
    suspend fun getCustomers(): Response<List<Customer>>

    @GET("crm/api/customers/{id}/")
    suspend fun getCustomer(@Path("id") id: Int): Response<Customer>

    @POST("crm/api/customers/")
    suspend fun createCustomer(@Body customer: Customer): Response<Customer>

    @PUT("crm/api/customers/{id}/")
    suspend fun updateCustomer(@Path("id") id: Int, @Body customer: Customer): Response<Customer>

    @GET("crm/api/leads/")
    suspend fun getLeads(): Response<List<Lead>>

    @POST("crm/api/leads/")
    suspend fun createLead(@Body lead: Lead): Response<Lead>

    @PUT("crm/api/leads/{id}/")
    suspend fun updateLead(@Path("id") id: Int, @Body lead: Lead): Response<Lead>

    @GET("crm/api/opportunities/")
    suspend fun getOpportunities(): Response<List<Opportunity>>

    @POST("crm/api/opportunities/")
    suspend fun createOpportunity(@Body opportunity: Opportunity): Response<Opportunity>

    @PUT("crm/api/opportunities/{id}/")
    suspend fun updateOpportunity(@Path("id") id: Int, @Body opportunity: Opportunity): Response<Opportunity>

    @GET("crm/api/campaigns/")
    suspend fun getCampaigns(): Response<List<Campaign>>

    @POST("crm/api/campaigns/")
    suspend fun createCampaign(@Body campaign: Campaign): Response<Campaign>

    @GET("crm/api/interactions/")
    suspend fun getInteractions(@Query("customer_id") customerId: Int? = null): Response<List<Interaction>>

    @POST("crm/api/interactions/")
    suspend fun createInteraction(@Body interaction: Interaction): Response<Interaction>
}

