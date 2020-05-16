package kz.maltabu.app.maltabukz.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kz.maltabu.app.maltabukz.BuildConfig
import kz.maltabu.app.maltabukz.model.NewAdBody
import kz.maltabu.app.maltabukz.model.PostBody
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.Repository
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response


class NewAdViewModel(private var language: String) : ViewModel() {
    private val disposable = CompositeDisposable()
    private val response = MutableLiveData<ApiResponse>()
    private val amountResponse = MutableLiveData<ApiResponse>()
    private val adResponse = MutableLiveData<Response>()
    private val imageResponse = MutableLiveData<ApiResponse>()

    fun mainResponse(): MutableLiveData<ApiResponse> {
        return response
    }

    fun getAmountResponse(): MutableLiveData<ApiResponse> {
        return amountResponse
    }

    fun getAdResponse(): MutableLiveData<Response> {
        return adResponse
    }

    fun getImageResponse(): MutableLiveData<ApiResponse> {
        return imageResponse
    }

    fun getRegions(){
        disposable.add(
            Repository.newInstance(language).getRegions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { response.value= ApiResponse.loading()}
                .subscribe(
                    { result ->
                        if(result.code()==200){
                            response.value= ApiResponse.success(result)
                        } else {
                            response.value= ApiResponse.error(result)
                        }
                    },
                    { throwable ->  response.value= ApiResponse.throwable(throwable)}
                ))
    }

    fun getAmountTypes(){
        disposable.add(
            Repository.newInstance(language).getAmountType()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { amountResponse.value= ApiResponse.loading()}
                .subscribe(
                    { result ->
                        if(result.code()==200){
                            amountResponse.value= ApiResponse.success(result)
                        } else {
                            amountResponse.value= ApiResponse.error(result)
                        }
                    },
                    { throwable ->  amountResponse.value= ApiResponse.throwable(throwable)}
                ))
    }

    fun postImage(file: MultipartBody.Part){
        disposable.add(
            Repository.newInstance(language).postAdPhoto(file)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { imageResponse.value= ApiResponse.loading()}
                .subscribe(
                    { result ->
                        if(result.code()==200){
                            imageResponse.value= ApiResponse.success(result)
                        } else {
                            imageResponse.value= ApiResponse.error(result)
                        }
                    },
                    { throwable ->  imageResponse.value= ApiResponse.throwable(throwable)}
                ))
    }

    fun newAdOld(bodyAd: NewAdBody){
        val observable = createRequest(bodyAd)
        disposable.add(observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                { result -> adResponse.value=result },
                { throwable ->  Log.d("TAGg", throwable.message!!)}
            ))
    }

    private fun createRequest(bodyAd: NewAdBody): Observable<Response> {
        val client = OkHttpClient()
        val body = PostBody(bodyAd).create()
        val request = Request.Builder()
            .url(BuildConfig.BASE_URL+"/api/v2/advertisements")
            .addHeader("Content-Type", "multipart/form-data")
            .addHeader("x-locale", language)
            .post(body)
            .build()

        return Observable.fromCallable {
            client.newCall(request).execute()
        }
    }

    class ViewModelFactory(private val language: String): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return NewAdViewModel(language) as T
        }
    }
}
