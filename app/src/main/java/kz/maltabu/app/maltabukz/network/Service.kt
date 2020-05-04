package kz.maltabu.app.maltabukz.network

import io.reactivex.Observable
import kz.maltabu.app.maltabukz.network.models.response.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface Service {

    @GET("api/v2/categories")
    fun getCategories(): Observable<Response<ResponseCategories>>

    @GET("api/v2/advertisements")
    fun getAds(@Query("page") page: Int?=null, @Query("order") order: String?=null, @Query("word") word: String?=null, @Query("category") category: Int?=null,
               @Query("region") region: Int?=null, @Query("price_to") price_to: Int?=null, @Query("price_from") price_from: Int?=null, @Query("city") city: Int?=null,
               @Query("exchange") exchange: Int?=null, @Query("image_required") image_required: Boolean?=null, @Query("favorite") favorite: Boolean?=null)
            : Observable<Response<ResponseAds>>

    @GET("/api/v2/location/region")
    fun getRegions(): Observable<Response<ResponseRegion>>

    @GET("/api/v2/advertisement/{id}")
    fun getAdById(@Path("id")id: Int): Observable<Response<ResponseAd>>

    @GET("/api/v2/amount-type")
    fun getAmountType(): Observable<Response<List<AmountType>>>

    @FormUrlEncoded
    @POST("/api/v2/advertisements")
    fun newAd(@Field("title") title: String, @Field("main_phone") main_phone: String, @Field("category_id") category_id: Int, @Field("region_id") region_id: Int,
              @Field("amount_id") amount_id: Int, @Field("city_id") city_id: Int, @Field("amount") amount: Int?=null, @Field("email") email: String?=null,
              @Field("description") description: String?=null, @Field("phones") phones: ArrayList<String>?=null, @Field("image_ids[0]") image_ids: Int? = 48,
              @Field("primary_image_id") primary_image_id: Int?):Observable<Response<MessageStatus>>

    @Multipart
    @POST("/api/v2/advertisement/image/upload")
    fun postAdPhoto(@Part img: MultipartBody.Part):Observable<Response<Int>>

    @FormUrlEncoded
    @POST("/api/v2/register")
    fun register(@Field("name") name: String, @Field("email") email: String, @Field("phone") phone: String, @Field("password") password: String,
                 @Field("password_confirmation") password_confirmation: String):Observable<Response<ResponseRegister>>

    @FormUrlEncoded
    @POST("/api/v2/social-auth")
    fun socail_auth(@Field("provider_name") provider_name: String, @Field("provider_id") provider_id: String, @Field("email") email: String)
            :Observable<Response<ResponseRegister>>

    @FormUrlEncoded
    @POST("/api/v2/login")
    fun login(@Field("identity") email: String, @Field("password") password: String):Observable<Response<ResponseAuth>>

    @FormUrlEncoded
    @POST("/api/v2/reset-password")
    fun resetPassword(@Field("identity") email: String):Observable<Response<ResponseEmpty>>

    @GET("/api/v2/advertisements-hot")
    fun getHotAd(): Observable<Response<ResponseAds>>

    @GET("/api/v2/news")
    fun getNews(@Query("page") page: Int):Observable<Response<ResponseNews>>

}