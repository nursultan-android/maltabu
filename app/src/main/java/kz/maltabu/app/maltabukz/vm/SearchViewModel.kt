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

class SearchViewModel(private val language: String) : ViewModel() {
    private val disposable = CompositeDisposable()
    private val response = MutableLiveData<ApiResponse>()
    private val regionsResponse = MutableLiveData<ApiResponse>()
    private val categoryResponse = MutableLiveData<ApiResponse>()

    fun mainResponse(): MutableLiveData<ApiResponse> {
        return response
    }

    fun mainRegionsResponse(): MutableLiveData<ApiResponse> {
        return regionsResponse
    }

    fun mainCategoryResponse(): MutableLiveData<ApiResponse> {
        return categoryResponse
    }

    fun getAds(body : QueryPaginationModel){
        disposable.add(
            Repository.newInstance(language).getAds(page = body.page, order = body.order, category = body.category, region = body.region, word = body.word, price_from = body.price_from,
                price_to = body.price_to, exchange = body.exchange, image_required = body.image_required, favorite = body.favorite, city = body.city)
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

    fun getCategories(){
        disposable.add(
            Repository.newInstance(language).getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { categoryResponse.value= ApiResponse.loading()}
                .subscribe(
                    { result ->
                        if(result.code()==200){
                            categoryResponse.value= ApiResponse.success(result)
                        } else {
                            categoryResponse.value= ApiResponse.error(result)
                        }
                    },
                    { throwable ->  categoryResponse.value= ApiResponse.throwable(throwable)}
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
            return SearchViewModel(language) as T
        }
    }
}
