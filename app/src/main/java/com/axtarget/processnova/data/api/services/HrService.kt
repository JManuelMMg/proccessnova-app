package com.axtarget.processnova.data.api.services

import com.axtarget.processnova.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface HrService {

    @GET("hr/api/employees/")
    suspend fun getEmployees(): Response<List<Employee>>

    @GET("hr/api/employees/{id}/")
    suspend fun getEmployee(@Path("id") id: Int): Response<Employee>

    @POST("hr/api/employees/")
    suspend fun createEmployee(@Body employee: Employee): Response<Employee>

    @PUT("hr/api/employees/{id}/")
    suspend fun updateEmployee(@Path("id") id: Int, @Body employee: Employee): Response<Employee>

    @POST("hr/api/checkin/")
    suspend fun checkIn(@Body request: CheckInRequest): Response<CheckInResponse>

    @POST("hr/api/checkout/")
    suspend fun checkOut(@Body request: CheckOutRequest): Response<CheckInResponse>

    @GET("hr/api/attendance/")
    suspend fun getAttendance(
        @Query("employee_id") employeeId: Int? = null,
        @Query("month") month: String? = null
    ): Response<List<AttendanceRecord>>

    @GET("hr/api/payroll/")
    suspend fun getPayroll(
        @Query("period") period: String? = null
    ): Response<List<PayrollEntry>>

    @GET("hr/api/departments/")
    suspend fun getDepartments(): Response<List<Department>>
}

data class CheckInRequest(
    val employee_id: Int
)

data class CheckOutRequest(
    val employee_id: Int
)

data class CheckInResponse(
    val success: Boolean = false,
    val message: String = "",
    val timestamp: String = ""
)

