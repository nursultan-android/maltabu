package kz.maltabu.app.maltabukz.ui.fragment

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
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bartoszlipinski.viewpropertyobjectanimator.ViewPropertyObjectAnimator
import com.esafirm.imagepicker.features.ImagePicker
import com.redmadrobot.inputmask.MaskedTextChangedListener
import com.redmadrobot.inputmask.MaskedTextChangedListener.Companion.installOn
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_new_ad.*
import kotlinx.android.synthetic.main.dialog_choose_catalog.*
import kotlinx.android.synthetic.main.dialog_choose_image.*
import kotlinx.android.synthetic.main.dialog_sort.*
import kotlinx.android.synthetic.main.fragment_new_ad.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.model.NewAdBody
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.models.response.*
import kz.maltabu.app.maltabukz.ui.activity.NewAdActivity
import kz.maltabu.app.maltabukz.ui.adapter.CityAdapter
import kz.maltabu.app.maltabukz.ui.adapter.RegionAdapter
import kz.maltabu.app.maltabukz.utils.customEnum.ApiLangEnum
import kz.maltabu.app.maltabukz.utils.customEnum.Keys
import kz.maltabu.app.maltabukz.utils.customEnum.Status
import kz.maltabu.app.maltabukz.vm.NewAdViewModel
import me.shaohui.advancedluban.Luban
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import org.jetbrains.anko.image
import org.json.JSONObject
import java.io.File

class NewAdFragment(val categoryId: Int) : Fragment(), RegionAdapter.ChooseRegion, CityAdapter.ChooseCity{

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
    private var cities: List<City>? = null

    companion object {
        fun newInstance(categoryId: Int) = NewAdFragment(categoryId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_ad, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, NewAdViewModel.ViewModelFactory(Paper.book().read(Keys.LANG.constantKey, ApiLangEnum.KAZAKH.constantKey))).
            get(NewAdViewModel::class.java)
        sortDialog = Dialog(activity!!)
        body= NewAdBody()
        filesArr = arrayOf(null,null,null,null,null,null,null,null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                (activity as NewAdActivity).setFragment(SuccessFragment.newInstance())
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
        button_city.setOnClickListener {
            if(cities!=null){
                showCitiesDialog(cities!!)
            }
        }
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
        sortDialog.setContentView(R.layout.dialog_choose_catalog)
        val adapter = RegionAdapter(this)
        adapter.setData(regions)
        sortDialog.catalogs_recycler.adapter = adapter
        sortDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        sortDialog.show()
    }

    private fun showCitiesDialog(cities: List<City>) {
        sortDialog.setContentView(R.layout.dialog_choose_catalog)
        val adapter = CityAdapter(this)
        adapter.setData(cities)
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
            .setMaxSize(2400)
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
            editText_price.setHint(resources.getString(R.string.price))
            sortDialog.dismiss()
        }
        sortDialog.cheap.setOnClickListener {
            body.amountId=3
            editText_price.visibility =View.VISIBLE
            editText_price.setText(resources.getString(R.string.radioB2))
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
                body.amount = editText_price.text.toString().toInt()
            }
        }
        else {
            body.amount = null
        }
        if(editText_desc.text.toString().isNotEmpty()){
            body.description=editText_desc.text.toString()
        }
        if(editText_email.text.toString().isNotEmpty()){
            body.email=editText_email.text.toString()
        }
    }

    override fun chooseRegion(region: Region) {
        button_region.text=region.name
        button_city.visibility=View.VISIBLE
        textView_city.visibility=View.VISIBLE
        if(region.cities!=null) {
            this.cities = region.cities
            body.region_id=null
            body.city_id=null
            button_city.text=resources.getString(R.string.chooseCity)
        } else {
            button_city.text=region.name
            body.region_id=region.id
            body.city_id=region.id
        }
        sortDialog.dismiss()
    }

    override fun chooseCity(city: City) {
        button_city.text=city.name
        body.region_id=city.region_id
        body.city_id=city.id
        sortDialog.dismiss()
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
                if(body.region_id==null || body.city_id==null){
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
        val user: User? = Paper.book().read<User>(Keys.USER.constantKey, null)
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
