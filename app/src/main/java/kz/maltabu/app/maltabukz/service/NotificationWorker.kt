package kz.maltabu.app.maltabukz.service

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.paperdb.Paper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kz.maltabu.app.maltabukz.model.NewAdBody
import kz.maltabu.app.maltabukz.model.PostBody
import kz.maltabu.app.maltabukz.network.Repository
import kz.maltabu.app.maltabukz.utils.customEnum.EnumsClass
import okhttp3.*
import java.io.File


class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams){
    private val WORK_RESULT = "work_result"
    private val disposable = CompositeDisposable()
    private var totalImageCount : Int=0
    private var imageIds: ArrayList<Int> = ArrayList()
    private lateinit var language:String

    override fun doWork(): Result {
        val taskData: Data = inputData
        val taskDataString = taskData.getString(EnumsClass().MESSAGE_STATUS)
        language = Paper.book().read<String>(EnumsClass().LANG, EnumsClass().KAZAKH)
        showNotification("WorkManager", taskDataString ?: "Message has been Sent")
        sendNewAdImages()
        val outputData: Data = Data.Builder().putString(WORK_RESULT, "Jobs Finished").build()
        return Result.success(outputData)
    }

    private fun showNotification(task: String, desc: String) {
        val manager= applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "task_channel"
        val channelName = "task_name"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
        val builder = NotificationCompat.Builder(applicationContext, channelId).setContentTitle("Фото өңдеу…").setSmallIcon(R.drawable.stat_sys_download).build()
        manager.notify(1, builder)
    }

    private fun sendNewAdImages(){
        val images = Paper.book().read<List<File>>("imgFiles")
        imageIds= ArrayList()
        if(images!=null && images.isNotEmpty()) {
            totalImageCount = images.size
            Log.d("TAGg", "size = ${images.size.toString()}")
            for (file in images) {
                if (file != null) {
                    val part = MultipartBody.Part.createFormData("file", file.name, RequestBody.create(MediaType.parse("image/*"), file))
                    disposable.add(
                        Repository.newInstance(language).postAdPhoto(part)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                { result ->
                                    if (result.code() == 200) {
                                        imageIds.add(result.body() as Int)
                                        checkAndSendAdBody()
                                    }
                                },
                                { throwable -> Log.d("TAGg", throwable.message) }
                            ))
                }
            }
        } else {
            val adBody = Paper.book().read<NewAdBody>(EnumsClass().ADBODY)
            Log.d("TAGg", "noImg")
            newAdOld(adBody)
        }

    }

    private fun checkAndSendAdBody() {
        val adBody = Paper.book().read<NewAdBody>(EnumsClass().ADBODY)
        if(imageIds.size==totalImageCount){
            adBody.image_ids=imageIds
            newAdOld(adBody)
        }
    }

    private fun newAdOld(bodyAd: NewAdBody){
        val manager= applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val observable = createRequest(bodyAd)
        disposable.add(observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    Log.d("TAGg", "added")
                    manager.cancel(1)
                },
                { throwable ->  Log.d("TAGg", throwable.message!!)}
            ))
    }

    private fun createRequest(bodyAd: NewAdBody): Observable<Response> {
        val client = OkHttpClient()
        val body = PostBody(bodyAd).create()
        val request = Request.Builder()
            .url("https://maltabu.kz/api/v2/advertisements")
            .addHeader("Content-Type", "multipart/form-data")
            .addHeader("x-locale", language)
            .post(body)
            .build()

        Log.d("TAGg", "noImg req1")
        return Observable.fromCallable {
            client.newCall(request).execute()
        }
    }
}