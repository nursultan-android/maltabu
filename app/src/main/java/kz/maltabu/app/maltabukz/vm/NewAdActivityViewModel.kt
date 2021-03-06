package kz.maltabu.app.maltabukz.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.Repository

class NewAdActivityViewModel(private var language: String) : ViewModel() {
    private val disposable = CompositeDisposable()
    private val response = MutableLiveData<ApiResponse>()

    fun mainResponse(): MutableLiveData<ApiResponse> {
        return response
    }

    fun setLang(lang : String){
        this.language = lang
    }

    fun getCategories(){
        disposable.add(
            Repository.newInstance(language).getCategories()
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
            return NewAdActivityViewModel(language) as T
        }
    }
}