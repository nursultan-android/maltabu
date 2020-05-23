package kz.maltabu.app.maltabukz.model

import android.util.Log
import okhttp3.MultipartBody

class PostBody(private val bodyAd: NewAdBody) {

    fun create(): MultipartBody{
        val builderSecond = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("title", bodyAd.title)
            .addFormDataPart("main_phone", bodyAd.main_phone)
            .addFormDataPart("city_id", bodyAd.city_id.toString())
            .addFormDataPart("region_id", bodyAd.region_id.toString())
            .addFormDataPart("category_id", bodyAd.category_id.toString())
            .addFormDataPart("amount_id", bodyAd.amountId.toString())
        if(bodyAd.phones!=null && bodyAd.phones!!.size>0){
            builderSecond.addFormDataPart("phones[0]", bodyAd.main_phone)
            for (i in 0 until bodyAd.phones!!.size){
                builderSecond.addFormDataPart("phones[${(i+1).toString()}]", bodyAd.phones!![i])
                Log.d("TAGg", "phones "+bodyAd.phones!![i])
            }
        }
        if(bodyAd.image_ids!=null && bodyAd.image_ids!!.size>0){
            for (i in 0 until bodyAd.image_ids!!.size){
                builderSecond.addFormDataPart("image_ids[${i.toString()}]", bodyAd.image_ids!![i].toString())
            }
        }
        if(bodyAd.amount!=null){
            builderSecond.addFormDataPart("amount", bodyAd.amount.toString())
        }
        if(bodyAd.email!=null){
            builderSecond.addFormDataPart("email", bodyAd.email)
        }
        if(bodyAd.description!=null){
            builderSecond.addFormDataPart("description", bodyAd.description)
        }
        return builderSecond.build()
    }

    fun createForEdit(): MultipartBody{
        val builderSecond = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("title", bodyAd.title)
            .addFormDataPart("main_phone", bodyAd.main_phone)
            .addFormDataPart("advertisement_id", bodyAd.advertisementId.toString())
            .addFormDataPart("city_id", bodyAd.city_id.toString())
            .addFormDataPart("region_id", bodyAd.region_id.toString())
            .addFormDataPart("category_id", bodyAd.category_id.toString())
            .addFormDataPart("amount_id", bodyAd.amountId.toString())
        if(bodyAd.phones!=null && bodyAd.phones!!.size>0){
            builderSecond.addFormDataPart("phones[0]", bodyAd.main_phone)
            for (i in 0 until bodyAd.phones!!.size){
                builderSecond.addFormDataPart("phones[${(i+1).toString()}]", bodyAd.phones!![i])
                Log.d("TAGg", "phones "+bodyAd.phones!![i])
            }
        }
        if(bodyAd.image_ids!=null && bodyAd.image_ids!!.size>0){
            for (i in 0 until bodyAd.image_ids!!.size){
                builderSecond.addFormDataPart("image_ids[${i.toString()}]", bodyAd.image_ids!![i].toString())
            }
        }
        if(bodyAd.amount!=null){
            builderSecond.addFormDataPart("amount", bodyAd.amount.toString())
        }
        if(bodyAd.email!=null){
            builderSecond.addFormDataPart("email", bodyAd.email)
        }
        if(bodyAd.description!=null){
            builderSecond.addFormDataPart("description", bodyAd.description)
        }
        return builderSecond.build()
    }
}