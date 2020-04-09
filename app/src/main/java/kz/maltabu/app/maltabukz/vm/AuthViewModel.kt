package kz.maltabu.app.maltabukz.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kz.maltabu.app.maltabukz.model.RegisterBody
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.Repository

class AuthViewModel(private val language: String) : ViewModel() {
    private val disposable = CompositeDisposable()
    private val response = MutableLiveData<ApiResponse>()
    private val socialResponse = MutableLiveData<ApiResponse>()

    fun mainResponse(): MutableLiveData<ApiResponse> {
        return response
    }

    fun socResponse(): MutableLiveData<ApiResponse> {
        return socialResponse
    }

    fun socail(email: String, userId: String, provider: String){
        disposable.add(
            Repository.newInstance(language).social(email, userId=userId, providerName = provider)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { socialResponse.value= ApiResponse.loading()}
                .subscribe(
                    { result ->
                        if(result.code()==200){
                            socialResponse.value= ApiResponse.success(result)
                        } else {
                            socialResponse.value= ApiResponse.error(result)
                        }
                    },
                    { throwable ->  socialResponse.value= ApiResponse.throwable(throwable)}
                ))
    }

    fun login(email: String, password: String){
        disposable.add(
            Repository.newInstance(language).login(email,password)
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

    class ViewModelFactory(private val language: String): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AuthViewModel(language) as T
        }
    }
}
