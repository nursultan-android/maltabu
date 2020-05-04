package kz.maltabu.app.maltabukz.utils.web

import android.util.Log
import androidx.lifecycle.MutableLiveData
import org.jsoup.Jsoup
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.schedulers.Schedulers
import org.jsoup.nodes.Document

class VersionChecker {
    private val disposable = CompositeDisposable()
    private val data = MutableLiveData <String>()

    fun getData():MutableLiveData<String>{
        return data
    }

    private fun createObservable(): Observable<Document>?{
        var observable:Observable<Document>?=null
        try {
            observable = Observable.fromCallable {
                Jsoup.connect("https://play.google.com/store/apps/details?id=kz.maltabu.app.maltabukz")
                    .timeout(30000)
                    .ignoreHttpErrors(true)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
            }
        } catch (e: OnErrorNotImplementedException){
            Log.d("TAGg", e.message)
        }
        return observable!!
    }

    fun getVersion(){
        val request = createObservable()
        if(request!=null) {
            disposable.add(request
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        run {
                            try {
                                data.value = result.select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                                        .first().toString()
                            } catch (e: Exception) {
                                Log.d("TAGg", e.message)
                            }
                        }
                    },
                    { throwable ->  Log.d("TAGg", throwable.message)}
                ))
        }
    }

}