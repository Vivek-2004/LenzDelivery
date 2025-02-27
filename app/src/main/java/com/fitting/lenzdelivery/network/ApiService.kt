package com.fitting.lenzdelivery.network

import com.fitting.lenzdelivery.models.AssignOrderReqBody
import com.fitting.lenzdelivery.models.ChangeWorkingStatus
import com.fitting.lenzdelivery.models.EditPhoneNumber
import com.fitting.lenzdelivery.models.LogInRider
import com.fitting.lenzdelivery.models.LogInRiderResponse
import com.fitting.lenzdelivery.models.RiderDetails
import com.fitting.lenzdelivery.models.RiderOrder
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl("https://lenz-backend.onrender.com/api/")
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val deliveryService: ApiService = retrofit.create(ApiService::class.java)

interface ApiService {
    @Headers("lenz-api-key: a99ed2023194a3356d37634474417f8b")
    @GET("riders/order-history/")
    suspend fun getOrders(): List<RiderOrder>

    @Headers("lenz-api-key: a99ed2023194a3356d37634474417f8b")
    @GET("riders/{riderId}/details")
    suspend fun getRiderDetails(
        @Path("riderId") riderId: String
    ): RiderDetails

    @Headers("lenz-api-key: a99ed2023194a3356d37634474417f8b")
    @PUT("riders/{riderId}/edit-phone-number")
    suspend fun editRiderPhone(
        @Path("riderId") riderId: Int,
        @Body newPhoneNumber: EditPhoneNumber
    )

    @Headers("lenz-api-key: a99ed2023194a3356d37634474417f8b")
    @PUT("riders/{riderId}/edit-working-status")
    suspend fun editWorkingStatus(
        @Path("riderId") riderId: Int,
        @Body newStatus: ChangeWorkingStatus
    ): Response<ResponseBody>

    @Headers("lenz-api-key: a99ed2023194a3356d37634474417f8b")
    @POST("riders/login")
    suspend fun logInRider(
        @Body loginBody: LogInRider
    ): LogInRiderResponse

    @Headers("lenz-api-key: a99ed2023194a3356d37634474417f8b")
    @PATCH("orders/{groupOrderId}/accept-pickup")
    suspend fun assignRider(
        @Path("groupOrderId") groupOrderId: String,
        @Body pickupRiderId: AssignOrderReqBody
    )
}