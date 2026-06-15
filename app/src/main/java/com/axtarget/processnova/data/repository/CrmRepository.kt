package com.axtarget.processnova.data.repository

import com.axtarget.processnova.core.Result
import com.axtarget.processnova.data.api.ApiClient
import com.axtarget.processnova.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CrmRepository {

    suspend fun getCustomers(): Result<List<Customer>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.crmService.getCustomers()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Error al obtener clientes", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun getCustomer(id: Int): Result<Customer> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.crmService.getCustomer(id)
            if (response.isSuccessful) {
                response.body()?.let { Result.Success(it) }
                    ?: Result.Error("Cliente no encontrado")
            } else {
                Result.Error("Error al obtener cliente", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun createCustomer(customer: Customer): Result<Customer> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.crmService.createCustomer(customer)
            if (response.isSuccessful) {
                response.body()?.let { Result.Success(it) }
                    ?: Result.Error("Error al crear cliente")
            } else {
                Result.Error("Error al crear cliente", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun updateCustomer(id: Int, customer: Customer): Result<Customer> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.crmService.updateCustomer(id, customer)
            if (response.isSuccessful) {
                response.body()?.let { Result.Success(it) }
                    ?: Result.Error("Error al actualizar cliente")
            } else {
                Result.Error("Error al actualizar cliente", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun getLeads(): Result<List<Lead>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.crmService.getLeads()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Error al obtener leads", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun createLead(lead: Lead): Result<Lead> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.crmService.createLead(lead)
            if (response.isSuccessful) {
                response.body()?.let { Result.Success(it) }
                    ?: Result.Error("Error al crear lead")
            } else {
                Result.Error("Error al crear lead", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun updateLead(id: Int, lead: Lead): Result<Lead> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.crmService.updateLead(id, lead)
            if (response.isSuccessful) {
                response.body()?.let { Result.Success(it) }
                    ?: Result.Error("Error al actualizar lead")
            } else {
                Result.Error("Error al actualizar lead", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun getOpportunities(): Result<List<Opportunity>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.crmService.getOpportunities()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Error al obtener oportunidades", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun createOpportunity(opportunity: Opportunity): Result<Opportunity> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.crmService.createOpportunity(opportunity)
            if (response.isSuccessful) {
                response.body()?.let { Result.Success(it) }
                    ?: Result.Error("Error al crear oportunidad")
            } else {
                Result.Error("Error al crear oportunidad", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun updateOpportunity(id: Int, opportunity: Opportunity): Result<Opportunity> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.crmService.updateOpportunity(id, opportunity)
            if (response.isSuccessful) {
                response.body()?.let { Result.Success(it) }
                    ?: Result.Error("Error al actualizar oportunidad")
            } else {
                Result.Error("Error al actualizar oportunidad", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun getCampaigns(): Result<List<Campaign>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.crmService.getCampaigns()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Error al obtener campañas", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun getInteractions(customerId: Int?): Result<List<Interaction>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.crmService.getInteractions(customerId)
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Error al obtener interacciones", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun createInteraction(interaction: Interaction): Result<Interaction> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.crmService.createInteraction(interaction)
            if (response.isSuccessful) {
                response.body()?.let { Result.Success(it) }
                    ?: Result.Error("Error al crear interacción")
            } else {
                Result.Error("Error al crear interacción", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    private fun handleException(e: Exception): String {
        return when (e) {
            is java.net.SocketTimeoutException -> "Conexión lenta, verifica tu internet"
            is java.net.UnknownHostException -> "Sin conexión a internet"
            is java.io.IOException -> "Error de conexión"
            else -> "Error inesperado: ${e.message}"
        }
    }
}

