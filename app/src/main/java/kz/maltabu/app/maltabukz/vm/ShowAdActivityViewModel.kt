package kz.maltabu.app.maltabukz.vm

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.Repository
import kz.maltabu.app.maltabukz.ui.activity.OnModerate

class ShowAdActivityViewModel(private var language: String) : ViewModel() {
    private val disposable = CompositeDisposable()
    private val response = MutableLiveData<ApiResponse>()
    private val smsResponse = MutableLiveData<ApiResponse>()
    private val codeResponse = MutableLiveData<ApiResponse>()

    fun mainResponse(): MutableLiveData<ApiResponse> {
        return response
    }

    fun getSmsResponse(): MutableLiveData<ApiResponse> {
        return smsResponse
    }

    fun getCodeResponse(): MutableLiveData<ApiResponse> {
        return codeResponse
    }

    fun getAdById(id:Int, listener: OnModerate){
        disposable.add(
            Repository.newInstance(language).getAdBuId(id)
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
                {
                    listener.adOnMOderate()
                }
            ))
    }

    fun sendSms(phone: String){
        disposable.add(
            Repository.newInstance(language).sendSms(phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { smsResponse.value= ApiResponse.loading()}
                .subscribe(
                    { result ->
                        if(result.code()==200){
                            smsResponse.value= ApiResponse.success(result)
                        } else {
                            smsResponse.value= ApiResponse.error(result)
                        }
                    },
                    {
                        smsResponse.value= ApiResponse.throwable(it)
                    }
                ))
    }

    fun sendCode(phone: String, code: String, type: String, id: Int){
        disposable.add(
            Repository.newInstance(language).sendCode(phone, code, type, id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { codeResponse.value= ApiResponse.loading()}
                .subscribe(
                    { result ->
                        if(result.code()==200){
                            codeResponse.value= ApiResponse.success(result)
                        } else {
                            codeResponse.value= ApiResponse.error(result)
                        }
                    },
                    {
                        codeResponse.value= ApiResponse.throwable(it)
                    }
                ))
    }

    class ViewModelFactory(private val language: String): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ShowAdActivityViewModel(language) as T
        }
    }
}