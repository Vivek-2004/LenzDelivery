package com.fitting.lenzdelivery.network

import com.fitting.lenzdelivery.models.AssignDeliveryReqBody
import com.fitting.lenzdelivery.models.AssignPickupReqBody
import com.fitting.lenzdelivery.models.ChangeWorkingStatus
import com.fitting.lenzdelivery.models.FcmToken
import com.fitting.lenzdelivery.models.LogInRider
import com.fitting.lenzdelivery.models.LogInRiderResponse
import com.fitting.lenzdelivery.models.OtpCode
import com.fitting.lenzdelivery.models.PatchCompleteTransit
import com.fitting.lenzdelivery.models.RiderDetails
import com.fitting.lenzdelivery.models.RiderOrder
import com.fitting.lenzdelivery.models.SignUpRider
import com.fitting.lenzdelivery.models.VerifyAdminOtp
import com.fitting.lenzdelivery.models.VerifyAdminPickupOtpReqBody
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
    .connectTimeout(45, TimeUnit.SECONDS)
    .readTimeout(45, TimeUnit.SECONDS)
    .writeTimeout(45, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl("https://lenzshop.duckdns.org/api/")
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val deliveryService: ApiService = retrofit.create(ApiService::class.java)

const val lenzApiKey = "a99ed202v335i6d3v763e447k4417f8b"
interface ApiService {

    @Headers("lenz-api-key: $lenzApiKey")
    @GET("riders/order-history/")
    suspend fun getOrders(): List<RiderOrder>

    @Headers("lenz-api-key: $lenzApiKey")
    @GET("riders/{riderId}/details")
    suspend fun getRiderDetails(
        @Path("riderId") riderId: String
    ): RiderDetails

    @Headers("lenz-api-key: $lenzApiKey")
    @PUT("riders/{riderId}/edit-working-status")
    suspend fun editWorkingStatus(
        @Path("riderId") riderId: Int,
        @Body newStatus: ChangeWorkingStatus
    ): Response<ResponseBody>

    @Headers("lenz-api-key: $lenzApiKey")
    @POST("riders/login")
    suspend fun riderLogIn(
        @Body loginBody: LogInRider
    ): LogInRiderResponse

    @Headers("lenz-api-key: $lenzApiKey")
    @POST("riders/signup")
    suspend fun riderSignUp(
        @Body signupBody: SignUpRider
    ): Response<ResponseBody>

    @Headers("lenz-api-key: $lenzApiKey")
    @PATCH("orders/{groupOrderId}/accept-pickup")
    suspend fun assignPickupRider(
        @Path("groupOrderId") groupOrderId: String,
        @Body pickupRiderId: AssignPickupReqBody
    ): Response<ResponseBody>

    @Headers("lenz-api-key: $lenzApiKey")
    @POST("orders/assign-rider")
    suspend fun assignDeliveryRider(
        @Body deliveryReqBody: AssignDeliveryReqBody
    ): Response<ResponseBody>

    @Headers("lenz-api-key: $lenzApiKey")
    @POST("orders/{groupOrderId}/verify-pickup-otp")
    suspend fun verifyPickupOtp(
        @Path("groupOrderId") groupOrderId: String,
        @Body otpCode: OtpCode
    ): Response<ResponseBody>

    @Headers("lenz-api-key: $lenzApiKey")
    @POST("orders/{groupOrderId}/verify-admin-otp")
    suspend fun verifyAdminOtp(
        @Path("groupOrderId") groupOrderId: String,
        @Body body: VerifyAdminOtp
    ): Response<ResponseBody>

    @Headers("lenz-api-key: $lenzApiKey")
    @POST("orders/{orderKey}/verify-admin-pickup-otp")
    suspend fun verifyAdminPickupOtp(
        @Path("orderKey") orderKey: String,
        @Body body: VerifyAdminPickupOtpReqBody
    ): Response<ResponseBody>

    @Headers("lenz-api-key: $lenzApiKey")
    @POST("orders/{groupOrderId}/verify-delivery-otp")
    suspend fun verifyShopDropOtp(
        @Path("groupOrderId") groupOrderId: String,
        @Body body: VerifyAdminPickupOtpReqBody
    ): Response<ResponseBody>

    @Headers("lenz-api-key: $lenzApiKey")
    @PATCH("orders/{orderKey}/complete-transit")
    suspend fun patchCompleteTransit(
        @Path("orderKey") orderKey: String,
        @Body riderId: PatchCompleteTransit
    ): Response<ResponseBody>

    @Headers("lenz-api-key: $lenzApiKey")
    @POST("riders/update-fcm-token")
    suspend fun updateFcmToken(
        @Body reqBody: FcmToken
    ): Response<ResponseBody>
}