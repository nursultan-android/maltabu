package kz.maltabu.app.maltabukz.ui.fragment.newAd

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bartoszlipinski.viewpropertyobjectanimator.ViewPropertyObjectAnimator
import com.esafirm.imagepicker.features.ImagePicker
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.redmadrobot.inputmask.MaskedTextChangedListener
import com.redmadrobot.inputmask.MaskedTextChangedListener.Companion.installOn
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_new_ad.*
import kotlinx.android.synthetic.main.dialog_choose_catalog.*
import kotlinx.android.synthetic.main.dialog_choose_image.*
import kotlinx.android.synthetic.main.dialog_sort.*
import kotlinx.android.synthetic.main.fragment_new_ad.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.model.NewAdBody
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.models.response.*
import kz.maltabu.app.maltabukz.ui.activity.BaseActivity
import kz.maltabu.app.maltabukz.ui.activity.NewAdActivity
import kz.maltabu.app.maltabukz.ui.adapter.RegionAdapter
import kz.maltabu.app.maltabukz.utils.FormatHelper
import kz.maltabu.app.maltabukz.utils.customEnum.Status
import kz.maltabu.app.maltabukz.vm.NewAdViewModel
import me.shaohui.advancedluban.Luban
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import org.jetbrains.anko.image
import org.json.JSONObject
import org.koin.android.ext.android.inject
import java.io.File

class NewAdFragment() : Fragment(), RegionAdapter.ChooseRegion{

    private val REQUEST_CODE_CAMERA = 1
    private val REQUEST_CODE_GALLERY = 2
    private val array = arrayOf(false, false, false, false, false, false, false, false)
    private val arrayPhones = ArrayList<String>()
    private var imgArray : Array<ImageView>? = null
    private var totalImageCount : Int=0
    private lateinit var filesArr : Array<File?>
    private lateinit var sortDialog: Dialog
    private lateinit var body: NewAdBody
    private lateinit var viewModel: NewAdViewModel
    private lateinit var interstitialAd: InterstitialAd
    private var cities: List<City>? = null
    private var categoryId=0

    private val formatHelper: FormatHelper by inject()

    companion object {
        fun newInstance(categoryId: Int) : NewAdFragment{
            val fragment = NewAdFragment()
            val bundle = Bundle()
            bundle.putInt("categoryId", categoryId)
            fragment.arguments=bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_ad, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, NewAdViewModel.ViewModelFactory(Paper.book().read((activity!! as BaseActivity).enum.LANG, (activity!! as BaseActivity).enum.KAZAKH))).
            get(NewAdViewModel::class.java)
        sortDialog = Dialog(activity!!)
        createAd()
        body= NewAdBody()
        filesArr = arrayOf(null,null,null,null,null,null,null,null)
    }

    private fun createAd(){

        try {
            com.google.android.gms.ads.MobileAds.initialize(activity!!) {}
            interstitialAd = InterstitialAd(activity!!)
            interstitialAd.adUnitId = "ca-app-pub-8576417478026387/4564102090"
            interstitialAd.loadAd(AdRequest.Builder().build())
        } catch (e:Exception){
            Log.d("TAGg", e.message)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryId = arguments!!.getInt("categoryId")
        viewModel.mainResponse().observe(viewLifecycleOwner, Observer {
            consumeResponse(it)
        })

        viewModel.getAmountResponse().observe(viewLifecycleOwner, Observer {
            consumeResponseAmount(it)
        })

        viewModel.getAdResponse().observe(viewLifecycleOwner, Observer {
            consumeResponseAd(it)
        })

        viewModel.getImageResponse().observe(viewLifecycleOwner, Observer {
            consumeResponseImage(it)
        })
        arrayPhones.add("")
        arrayPhones.add("")
        arrayPhones.add("")
        setImageArray()
        setListeners()
        setPhoneMask()
        setArrowButtonColor()
        setUser()
        setPriceMask()
    }

    private fun consumeResponseImage(response: ApiResponse) {
        when (response.status) {
            Status.LOADING -> {}
            Status.SUCCESS -> {
                Log.d("TAGg", response.data!!.body().toString())
                if(body.image_ids!=null) {
                    body.image_ids!!.add(response.data!!.body() as Int)
                } else {
                    body.image_ids = ArrayList()
                    body.image_ids!!.add(response.data!!.body() as Int)
                }
                if(totalImageCount==body.image_ids!!.size){
                    viewModel.newAdOld(body)
                }
            }
            Status.ERROR -> {
                Log.d("TAGg", response.error.toString())
            }
            Status.THROWABLE -> {
                Log.d("TAGg", response.throwabl.toString())
            }
        }
    }

    private fun consumeResponseAd(response: Response) {
        hideLoader()
        Log.d("TAGg", response.code().toString())
        try {
            val json = JSONObject(response.body()!!.string())
            if (json.getString("status") == "success") {
                Log.d("TAGg", "OK")
                (activity as NewAdActivity).clearBackStack()
                val fragmentSuccess = SuccessFragment()
                fragmentSuccess.setAd(interstitialAd)
                (activity as NewAdActivity).setFragment(fragmentSuccess)
            } else {
                Log.d("TAGg", "not OK")
            }
        } catch (e: Exception){}
    }

    private fun consumeResponseAmount(response: ApiResponse) {
        when (response.status) {
            Status.LOADING -> {}
            Status.SUCCESS -> {
                renderAmountResponse(response.data!!.body() as List<AmountType>)
            }
            Status.ERROR -> {
                Log.d("TAGg", response.error.toString())
            }
            Status.THROWABLE -> {
                Log.d("TAGg", response.throwabl.toString())
            }
        }
    }

    private fun renderAmountResponse(list: List<AmountType>) {
        Log.d("TAGg", list.size.toString())
    }

    private fun setListeners(){
        choose_photo.setOnClickListener {
            showPhotoDialog()
        }
        button_region.setOnClickListener {
            viewModel.getRegions()
        }
//        button_city.setOnClickListener {
//            if(cities!=null){
//                showCitiesDialog(cities!!)
//            }
//        }
        choose_price.setOnClickListener {
            showPriceDialog()
        }
        add_phone.setOnClickListener {
            if(linear_phone_2.isVisible){
                linear_phone_3.visibility=View.VISIBLE
            } else {
                linear_phone_2.visibility = View.VISIBLE
            }
        }
        close_2_phone.setOnClickListener {
            linear_phone_2.visibility = View.GONE
            editText_phone_2.text.clear()
            arrayPhones[1]=""
        }
        close_3_phone.setOnClickListener {
            linear_phone_3.visibility = View.GONE
            editText_phone_3.text.clear()
            arrayPhones[2]=""
        }
        button_new_ad.setOnClickListener {
            postAd()
        }
    }

    private fun consumeResponse(response: ApiResponse) {
        when (response.status) {
            Status.LOADING -> {
                showLoader()
            }
            Status.SUCCESS -> {
                hideLoader()
                renderResponse(response.data!!.body() as ResponseRegion)
            }
            Status.ERROR -> {
                hideLoader()
                Log.d("TAGg", response.error.toString())
            }
            Status.THROWABLE -> {
                hideLoader()
                Log.d("TAGg", response.throwabl.toString())
            }
        }
    }

    private fun renderResponse(response: ResponseRegion) {
        showRegionsDialog(response.regions)
    }

    private fun showRegionsDialog(regions: List<Region>) {
        sortDialog.setContentView(R.layout.dialog_choose_region)
        val adapter = RegionAdapter(this)
        adapter.setData(regions)
        sortDialog.catalogs_recycler.adapter = adapter
        sortDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        sortDialog.show()
    }

    private fun setImageArray() {
        imgArray = arrayOf(img1, img2, img3, img4, img5, img6, img7, img8)
        setCloseButtonListeneres(imgArray!!)
    }

    private fun setCloseButtonListeneres(arrayImg: Array<ImageView>) {
        close_1.setOnClickListener {
            arrayImg[0].image=null
            array[0]=false
            choose_photo.isEnabled=true
            filesArr[0]=null
        }
        close_2.setOnClickListener {
            arrayImg[1].image=null
            array[1]=false
            choose_photo.isEnabled=true
            filesArr[1]=null
        }
        close_3.setOnClickListener {
            arrayImg[2].image=null
            array[2]=false
            choose_photo.isEnabled=true
            filesArr[2]=null
        }
        close_4.setOnClickListener {
            arrayImg[3].image=null
            array[3]=false
            choose_photo.isEnabled=true
            filesArr[3]=null
        }
        close_5.setOnClickListener {
            arrayImg[4].image=null
            array[4]=false
            choose_photo.isEnabled=true
            filesArr[4]=null
        }
        close_6.setOnClickListener {
            arrayImg[5].image=null
            array[5]=false
            choose_photo.isEnabled=true
            filesArr[5]=null
        }
        close_7.setOnClickListener {
            arrayImg[6].image=null
            array[6]=false
            choose_photo.isEnabled=true
            filesArr[6]=null
        }
        close_8.setOnClickListener {
            arrayImg[7].image=null
            array[7]=false
            choose_photo.isEnabled=true
            filesArr[7]=null
        }
    }

    private fun setPriceMask(){
//        val disposable = RxTextView.textChanges(editText_price)
//                .subscribe {
//
//                    if(it.toString()!=null && it.toString().isNotEmpty()) {
//                        var data  = it.toString().replace(".", "").replace(",", "").replace(" ", "")
//                        var result = ""
//                        if(data.length>3){
//                            val intCount = data.length.div(3)
//                            var intMod = data.length-(intCount*3)
//                            if(intMod!=0){
//                                result = data.substring(0, intMod)+" "+addSpace(data.substring(intMod, data.length))
//                            } else {
//                                result = addSpace(data)
//                            }
//                            editText_price.text.replace(0,0,"$result ₸")
//                        } else {
//                            editText_price.text.replace(0,0,"$data ₸")
//                        }
//                    }
//                }
//        editText_price.addTextChangedListener(object: TextWatcher{
//            override fun afterTextChanged(s: Editable?) {
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                if(s.toString()!=null && s.toString().isNotEmpty()) {
//                    var data = s.toString().replace(".", "").replace(",", "").replace(" ", "").replace("₸", "")
//                    var result = ""
//                    if(data.length>3){
//                        val intCount = data.length.div(3)
//                        var intMod = data.length-(intCount*3)
//                        if(intMod!=0){
//                            result = data.substring(0, intMod)+" "+addSpace(data.substring(intMod, data.length))
//                        } else {
//                            result = addSpace(data)
//                        }
//                        editText_price.text.replace(0,0,"$result ₸")
//                    } else {
//                        editText_price.text.replace(0,0,"$data ₸")
//                    }
//                }
//            }
//
//        })
    }

    private fun addSpace(string: String): String{
        var index = 0
        var result = ""
        var iteration = string.length/3
        for (i in 0 until iteration){
            result+=string.substring(index, index+3)+" "
            index+=3
        }
        return result
    }

    private fun showPhotoDialog() {
        sortDialog.setContentView(R.layout.dialog_choose_image)
        sortDialog.picker_camera.setOnClickListener {
            uploadPhoto(REQUEST_CODE_CAMERA)
            sortDialog.dismiss()
        }
        sortDialog.picker_gallery.setOnClickListener {
            uploadPhoto(REQUEST_CODE_GALLERY)
            sortDialog.dismiss()
        }
        sortDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        sortDialog.show()
    }

    private fun uploadPhoto(code: Int) {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.INTERNET)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.INTERNET), 3000)
        }
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.MEDIA_CONTENT_CONTROL)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.MEDIA_CONTENT_CONTROL), 3600)
        }
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 2000)
        }
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 200)
        }
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 20)
        }
        ImagePicker
            .create(this)
            .limit(checkArrLimit(array))
            .start(code)
    }

    private fun compressImgFile(file: File){
        Luban.compress(context, file)
            .putGear(Luban.CUSTOM_GEAR)
            .setMaxSize(1600)
            .launch(object : me.shaohui.advancedluban.OnCompressListener{
                override fun onSuccess(file2: File?) {
                    if(imgArray!=null) {
                        val freeIndex = checkArr(array)
                        val bmImg = BitmapFactory.decodeFile(file2!!.path)
                        imgArray!![freeIndex].setImageBitmap(bmImg)
                        filesArr[freeIndex]=file2
                        array[freeIndex]=true
                    }
                    if(checkArr(array)==-1){
                        choose_photo.isEnabled=false
                    }
                }

                override fun onError(e: Throwable?) {}

                override fun onStart() {}
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val unmaskedRequestCode = requestCode and 0x0000ffff
            if (unmaskedRequestCode == REQUEST_CODE_CAMERA || unmaskedRequestCode == REQUEST_CODE_GALLERY) {
                val images = ImagePicker.getImages(data)
                first_four_photo.visibility= View.VISIBLE
                second_four_photo.visibility= View.VISIBLE
                for(i in 0 until images.size){
                    val fileMult = File(images[i].path)
                    compressImgFile(fileMult)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun checkArr(arr: Array<Boolean>): Int{
        for(i in arr.indices) {
            if (!arr[i]){
                return i
            }
        }
        return -1
    }

    private fun checkStrArr(arr: ArrayList<String>): Int{
        for(i in arr.indices) {
            if (arr[i].isEmpty()){
                return i
            }
        }
        return -1
    }

    private fun checkArrLimit(arr: Array<Boolean>): Int{
        var limit = 0
        for(i in arr.indices) {
            if (!arr[i]){
                limit++
            }
        }
        return limit
    }

    private fun showPriceDialog() {
        sortDialog.setContentView(R.layout.dialog_sort)
        sortDialog.newAds.text=resources.getString(R.string.sell)
        sortDialog.cheap.text=resources.getString(R.string.radioB2)
        sortDialog.newAds.setOnClickListener {
            body.amountId=1
            editText_price.visibility =View.VISIBLE
            choose_price.text=resources.getString(R.string.sell)
            editText_price.isEnabled=true
            editText_price.setText("")
            editText_price.hint = resources.getString(R.string.price)
            sortDialog.dismiss()
        }
        sortDialog.cheap.setOnClickListener {
            body.amountId=3
            editText_price.visibility =View.VISIBLE
            editText_price.hint = resources.getString(R.string.radioB2)
            editText_price.isEnabled=false
            sortDialog.dismiss()
        }
        sortDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        sortDialog.show()
    }

    private fun setPhoneMask(){
        val listener =
            installOn(
                editText_phone_1,
                "+7-[000]-[000]-[00]-[00]",
                object : MaskedTextChangedListener.ValueListener {
                    override fun onTextChanged(maskFilled: Boolean, extractedValue: String, formattedValue: String) {
                        if(maskFilled) {
                            body.main_phone=formattedValue.replace("+","")
                            arrayPhones[0]=formattedValue.replace("+","")
                        }
                    }
                }
            )
        val listener2 =
            installOn(
                editText_phone_2,
                "+7-[000]-[000]-[00]-[00]",
                object : MaskedTextChangedListener.ValueListener {
                    override fun onTextChanged(maskFilled: Boolean, extractedValue: String, formattedValue: String) {
                        if(maskFilled) {
                            arrayPhones[1]=formattedValue.replace("+","")
                        }
                    }
                }
            )
        val listener3 =
            installOn(
                editText_phone_3,
                "+7-[000]-[000]-[00]-[00]",
                object : MaskedTextChangedListener.ValueListener {
                    override fun onTextChanged(maskFilled: Boolean, extractedValue: String, formattedValue: String) {
                        if(maskFilled) {
                            arrayPhones[2]=formattedValue.replace("+","")
                        }
                    }
                }
            )
        editText_phone_1.hint = listener.placeholder()
        editText_phone_2.hint = listener2.placeholder()
        editText_phone_3.hint = listener3.placeholder()
    }

    private fun postAd(){
        addToBody()
        Log.d("TAGg", "city:${body.city_id.toString()}  region:${body.region_id.toString()}")
        if (validateForm()){
            showLoader()
            sendImages()
        }
    }

    private fun sendImages() {
        for (file in filesArr) {
            if(file!=null) {
                totalImageCount++
                val part = MultipartBody.Part.createFormData("file", file.name, RequestBody.create(MediaType.parse("image/*"), file))
                viewModel.postImage(part)
            }
        }
        if(totalImageCount==0){
            viewModel.newAdOld(body)
        }
    }

    private fun addToBody(){
        body.title=editText_title.text.toString()
        body.category_id=categoryId
        if(body.amountId==1) {
            if(editText_price.text.toString().isEmpty()){
                Toast.makeText(activity, resources.getString(R.string.priceValid), Toast.LENGTH_SHORT).show()
            } else {
                body.amount = makeNumber(editText_price.text.toString())
            }
        } else {
            body.amount = null
        }
        if(cities!=null && button_city.text.toString()!=null && button_city.text.toString().isNotEmpty()){
            val city = formatHelper.getCityByName(cities!!, button_city.text.toString())
            chooseCity(city)
        }
        if(editText_desc.text.toString().isNotEmpty()){
            body.description=editText_desc.text.toString()
        }
        if(editText_email.text.toString().isNotEmpty()){
            body.email=editText_email.text.toString()
        }
        if(arrayPhones!=null){
            for (i in 0 until arrayPhones.size){
                if(arrayPhones[i]!=null && arrayPhones[i].isNotEmpty()){
                    if (body.phones!=null) {
                        body.phones!!.add(arrayPhones[i])
                    } else {
                        body.phones=ArrayList()
                        body.phones!!.add(arrayPhones[i])
                    }
                    Log.d("TAGg", arrayPhones[i])
                }
            }
        }
    }

    private fun makeNumber(string: String): Int {
        val result = string.replace(" ", "").replace(".", "").replace(",", "")
        return result.toInt()
    }

    private fun setcityAdapter(){
        val citiesNameList= ArrayList<String>()
        for(i in 0 until cities!!.size){
            citiesNameList.add(cities!![i].name)
        }
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(activity!!, android.R.layout.simple_list_item_1, citiesNameList)
        button_city.setAdapter(adapter)
    }

    override fun chooseRegion(region: Region) {
        button_region.text=region.name
        button_city.visibility=View.VISIBLE
        textView_city.visibility=View.VISIBLE
        if(region.cities!=null) {
            this.cities = region.cities
            body.region_id=null
            body.city_id=null
            setcityAdapter()
            button_city.isEnabled=true
            button_city.setText("")
        } else {
            button_city.setText(region.name)
            button_city.isEnabled=false
            this.cities = null
            body.region_id=region.id
            body.city_id=region.id
        }
        sortDialog.dismiss()
    }

    private fun chooseCity(city: City) {
        button_city.setText(city.name)
        body.region_id=city.region_id
        body.city_id=city.id
    }

    private fun validateForm():Boolean{
        if(editText_title.text.isEmpty()){
            Toast.makeText(activity, resources.getString(R.string.titleValid), Toast.LENGTH_SHORT).show()
            ViewPropertyObjectAnimator.animate(nested_scroll).scrollY(editText_title.scrollY).start()
            return false
        } else {
            if(body.amountId==null || (body.amountId==1 && editText_price.text.isEmpty())){
                Toast.makeText(activity, resources.getString(R.string.priceValid), Toast.LENGTH_SHORT).show()
                ViewPropertyObjectAnimator.animate(nested_scroll).scrollY(choose_price.scrollY).start()
                return false
            } else {
                if(body.region_id==null || body.city_id==null || body.region_id==0 || body.city_id==0){
                    Toast.makeText(activity, resources.getString(R.string.regionValid), Toast.LENGTH_SHORT).show()
                    ViewPropertyObjectAnimator.animate(nested_scroll).scrollY(button_region.scrollY).start()
                    return false
                } else {
                    if(body.main_phone==null){
                        Toast.makeText(activity, resources.getString(R.string.phone), Toast.LENGTH_SHORT).show()
                        return false
                    }
                }
            }
        }
        return true
    }

    private fun setArrowButtonColor(){
        activity!!.back_arrow.setOnClickListener {
            activity!!.onBackPressed()
        }
    }

    private fun showLoader(){
        (activity as NewAdActivity).showLoader()
    }

    private fun hideLoader(){
        (activity as NewAdActivity).hideLoader()
    }

    private fun setUser(){
        val user: User? = Paper.book().read<User>((activity!! as BaseActivity).enum.USER, null)
        if(user!=null){
            if(user.phone!=null && user.phone.isNotEmpty()){
                editText_phone_1.setText(user.phone)
                editText_phone_1.isEnabled=false
                editText_phone_1.setOnClickListener {}
            }
        }
        if(user!=null){
            if(user.email!=null && user.email.isNotEmpty()){
                editText_email.setText(user.email)
                editText_email.isEnabled=false
            }
        }
    }
}
