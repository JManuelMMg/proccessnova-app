package com.axtarget.processnova.data.api.services

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthService {

    @FormUrlEncoded
    @POST("login/")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("csrfmiddlewaretoken") csrfToken: String
    ): Response<ResponseBody>

    @POST("logout/")
    suspend fun logout(): Response<Unit>

    @FormUrlEncoded
    @POST("register/")
    suspend fun register(
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password1") password1: String,
        @Field("password2") password2: String,
        @Field("organization_name") organizationName: String,
        @Field("rfc") rfc: String,
        @Field("razon_social") razonSocial: String,
        @Field("regimen_fiscal") regimenFiscal: String,
        @Field("codigo_postal") codigoPostal: String,
        @Field("branch_name") branchName: String,
        @Field("csrfmiddlewaretoken") csrfToken: String
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("password-reset/")
    suspend fun requestPasswordReset(
        @Field("email") email: String,
        @Field("csrfmiddlewaretoken") csrfToken: String
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("password-change/")
    suspend fun changePassword(
        @Field("old_password") oldPassword: String,
        @Field("new_password1") newPassword1: String,
        @Field("new_password2") newPassword2: String,
        @Field("csrfmiddlewaretoken") csrfToken: String
    ): Response<ResponseBody>

    @GET("profile/")
    suspend fun getProfile(): Response<String>
}
