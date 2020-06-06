package kz.maltabu.app.maltabukz.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kz.maltabu.app.maltabukz.model.QueryPaginationModel
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.Repository

class ContestViewModel(private val language: String) : ViewModel() {
    private val disposable = CompositeDisposable()
    private val response = MutableLiveData<ApiResponse>()
    private val regionsResponse = MutableLiveData<ApiResponse>()

    fun mainResponse(): MutableLiveData<ApiResponse> {
        return response
    }

    fun mainRegionsResponse(): MutableLiveData<ApiResponse> {
        return regionsResponse
    }

    fun doContest(phone:String, first:String, last:String, sur:String, region:Int, city:Int){
        disposable.add(
            Repository.newInstance(language).contests(phone,first, last, sur, region, city)
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

    fun getRegions(){
        disposable.add(
            Repository.newInstance(language).getRegions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { regionsResponse.value= ApiResponse.loading()}
                .subscribe(
                    { result ->
                        if(result.code()==200){
                            regionsResponse.value= ApiResponse.success(result)
                        } else {
                            regionsResponse.value= ApiResponse.error(result)
                        }
                    },
                    { throwable ->  regionsResponse.value= ApiResponse.throwable(throwable)}
                ))
    }

    class ViewModelFactory(private val language: String): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ContestViewModel(language) as T
        }
    }
}
