package kz.maltabu.app.maltabukz.network


import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Observable
import kz.maltabu.app.maltabukz.BuildConfig
import kz.maltabu.app.maltabukz.network.models.response.*
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

open class Repository{

    private var mainService: Service? = null

    private constructor (language: String){
        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.addInterceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder()
                .addHeader("x-locale", language)
                .addHeader("Accept", "application/json")
                .method(original.method(), original.body())
            chain.proceed(builder.build())
        }.connectTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).readTimeout(90, TimeUnit.SECONDS)
        val gson = GsonBuilder().setLenient().create()
        val rxAdapter = RxJava2CallAdapterFactory.create()
        mainService = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(clientBuilder.build())
            .addCallAdapterFactory(rxAdapter)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create<Service>(Service::class.java)
    }

    private constructor (token : String, language: String){
        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.addInterceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder()
                .addHeader("x-locale", language)
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .method(original.method(), original.body())
            chain.proceed(builder.build())
        }.connectTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).readTimeout(90, TimeUnit.SECONDS)
        val gson = GsonBuilder().setLenient().create()
        val rxAdapter = RxJava2CallAdapterFactory.create()
        mainService = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(clientBuilder.build())
            .addCallAdapterFactory(rxAdapter)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create<Service>(Service::class.java)
    }

    companion object {
        @JvmStatic
        fun newInstance(language: String): Repository{
            return Repository(language)
        }

        @JvmStatic
        fun newInstance(token : String,language: String): Repository{
            return Repository(token, language)
        }
    }

    fun getCategories(): Observable<Response<ResponseCategories>> {
        return mainService!!.getCategories()
    }

    fun getAds(page: Int? = null, order: String? = null, category: Int? = null, region: Int? = null, word: String? = null, price_to:Int? = null, price_from:Int? = null,
               exchange:Int? = null, image_required: Boolean? = null, favorite: Boolean? = null, city: Int? = null): Observable<Response<ResponseAds>>{
        return mainService!!.getAds(page=page, order = order, category = category, region = region, word = word, price_from = price_from, price_to = price_to,
            exchange = exchange, image_required = image_required, favorite = favorite, city = city)
    }

    fun getRegions(): Observable<Response<ResponseRegion>>{
        return mainService!!.getRegions()
    }

    fun getAdBuId(id: Int): Observable<Response<ResponseAd>>{
        return mainService!!.getAdById(id)
    }

    fun getAmountType(): Observable<Response<List<AmountType>>>{
        return mainService!!.getAmountType()
    }

//    fun newAd(body: NewAdBody): Observable<Response<MessageStatus>>{
//        return mainService!!.newAd(title = body.title!!,main_phone =  body.main_phone!!, category_id = body.category_id!!, region_id = body.region_id!!, city_id = body.city_id!!,
//            amount_id = body.amountId!!, amount = body.amount, email = body.email, description = body.description, phones = body.phones,  primary_image_id = body.primary_image_id)
//        return secondService!!.newAd(body.title!!,body.main_phone!!, body.category_id!!, body.region_id!!, body.amountId!!,
//           body.city_id!!, body.amount!!,body.email, body.description, body.phones, body.image_ids!!.toIntArray(), body.primary_image_id!!)
//    }

    fun postAdPhoto(file: MultipartBody.Part): Observable<Response<Int>>{
        return mainService!!.postAdPhoto(file)
    }

    fun register(name: String, email:String, phone:String, password:String, passwordConf: String):Observable<Response<ResponseRegister>>{
        return mainService!!.register(name = name,email = email,phone=phone,password= password,password_confirmation = passwordConf)
    }

    fun social(email:String, providerName:String, userId: String):Observable<Response<ResponseRegister>>{
        return mainService!!.socail_auth(email = email, provider_name = providerName, provider_id = userId)
    }

    fun login(email:String, password:String):Observable<Response<ResponseAuth>>{
        return mainService!!.login(email, password)
    }

    fun getHotAds(): Observable<Response<ResponseAds>>{
        return mainService!!.getHotAd()
    }

    fun getNews(page:Int): Observable<Response<ResponseNews>> {
        return mainService!!.getNews(page)
    }

    fun resetPassword(login: String): Observable<Response<ResponseEmpty>> {
        return mainService!!.resetPassword(login)
    }

    fun getBanner(): Observable<Response<ResponseBanner>> {
        return mainService!!.getBanners()
    }

    fun getMyAds(page:Int): Observable<Response<ResponseAds>> {
        return mainService!!.getMyAds(page)
    }

    fun contests(phone:String, first:String, last:String, sur:String, region:Int, city:Int)
            : Observable<Response<ResponseContest>> {
        return mainService!!.contest(phone,first,last,sur,region,city)
    }
}