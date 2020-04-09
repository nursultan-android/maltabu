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

class RegViewModel(private val language: String) : ViewModel() {
    private val disposable = CompositeDisposable()
    private val response = MutableLiveData<ApiResponse>()

    fun mainResponse(): MutableLiveData<ApiResponse> {
        return response
    }

    fun register(body : RegisterBody){
        disposable.add(
            Repository.newInstance(language).register(body.name!!, email = body.email!!, phone = body.phone!!, password = body.password!!, passwordConf = body.confirm!!)
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
            return RegViewModel(language) as T
        }
    }
}
