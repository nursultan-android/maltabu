package kz.maltabu.app.maltabukz.ui.activity

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.gms.ads.MobileAds
import com.redmadrobot.inputmask.MaskedTextChangedListener
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_show_ad.*
import kotlinx.android.synthetic.main.activity_show_ad.callPhone
import kotlinx.android.synthetic.main.activity_show_ad.content_txt
import kotlinx.android.synthetic.main.activity_show_ad.dateTxt
import kotlinx.android.synthetic.main.activity_show_ad.finish
import kotlinx.android.synthetic.main.activity_show_ad.hot_lay
import kotlinx.android.synthetic.main.activity_show_ad.location_txt
import kotlinx.android.synthetic.main.activity_show_ad.my_template
import kotlinx.android.synthetic.main.activity_show_ad.pager
import kotlinx.android.synthetic.main.activity_show_ad.phones_txt
import kotlinx.android.synthetic.main.activity_show_ad.photos
import kotlinx.android.synthetic.main.activity_show_ad.price_txt
import kotlinx.android.synthetic.main.activity_show_ad.progress_bar_show_ad
import kotlinx.android.synthetic.main.activity_show_ad.title_txt
import kotlinx.android.synthetic.main.activity_show_ad.visitors_text
import kotlinx.android.synthetic.main.activity_show_ad_with_comments.*
import kotlinx.android.synthetic.main.dialog_input_code.*
import kotlinx.android.synthetic.main.dialog_input_phone.*
import kotlinx.android.synthetic.main.dialog_input_phone.auto_complete
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.models.comment.Comment
import kz.maltabu.app.maltabukz.network.models.response.Ad
import kz.maltabu.app.maltabukz.network.models.response.ResponseAd
import kz.maltabu.app.maltabukz.network.models.response.ResponseSuccess
import kz.maltabu.app.maltabukz.ui.adapter.CommentAdapter
import kz.maltabu.app.maltabukz.ui.adapter.ImagesAdapter
import kz.maltabu.app.maltabukz.ui.adapter.PhoneAdapter
import kz.maltabu.app.maltabukz.ui.fragment.showAd.ImageFragment
import kz.maltabu.app.maltabukz.ui.fragment.showAd.NoImageFragment
import kz.maltabu.app.maltabukz.ui.fragment.showAd.YandexAdFragment
import kz.maltabu.app.maltabukz.utils.CustomAnimator
import kz.maltabu.app.maltabukz.utils.FormatHelper
import kz.maltabu.app.maltabukz.utils.customEnum.Status
import kz.maltabu.app.maltabukz.utils.web.NetworkChecker
import kz.maltabu.app.maltabukz.utils.yandexAds.GoogleAdmobHelper
import kz.maltabu.app.maltabukz.vm.ShowAdActivityViewModel
import org.koin.android.ext.android.inject
import java.util.*


class ShowAdActivity : BaseActivity(), PhoneAdapter.MakeCall, OnModerate {

    private lateinit var viewModel: ShowAdActivityViewModel
    private lateinit var noAdDialog: Dialog
    private var commentsList = ArrayList<Comment>()
    lateinit var imagesIntent: Intent
    var current=0
    var size=0
    var PERMISSION_ALL = 1
    var PERMISSIONS =  Manifest.permission.CALL_PHONE
    var phoneNumber=""

    private val formatHelper: FormatHelper by inject()
    private val checker: NetworkChecker by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_ad_with_comments)
//        val data = intent.data
//        if(data!=null){
//            var slug = data.toString()
//            slug = slug.substring(slug.lastIndexOf("/") + 1)
//            Log.d("TAGg", slug)
//
//        }
        noAdDialog = Dialog(this)
        viewModel = ViewModelProviders.of(this, ShowAdActivityViewModel.ViewModelFactory(Paper.book().read(enum.LANG, enum.KAZAKH)))
            .get(ShowAdActivityViewModel::class.java)
        viewModel.mainResponse().observe(this, Observer { consumeResponse(it) })
        viewModel.getSmsResponse().observe(this, Observer { consumeSmsResponse(it) })
        viewModel.getCodeResponse().observe(this, Observer { consumeCodeResponse(it) })
        imagesIntent = Intent(this, ImageActivity::class.java)
        finish.setOnClickListener {
            CustomAnimator.animateHotViewLinear(it)
            finish()
        }
        loadGoogleAd()
    }

    override fun onResume() {
        super.onResume()
        val id = (intent.getSerializableExtra("ad") as Ad).id
        if(checker.isNetworkAvailable) {
            viewModel.getAdById(id, this)
        } else {
            setNoInternetView()
        }
    }

    private fun consumeCodeResponse(response: ApiResponse) {
        when (response.status) {
            Status.LOADING -> {
                showDialog()
            }
            Status.SUCCESS -> {
                hideDialog()
                val body = response.data!!.body() as ResponseSuccess
                if(body.status=="success"){
                    showSuccessfulPromotionDialog()
                }
            }
            Status.ERROR -> {
                hideDialog()
                Log.d("TAGg", "error")
                if(response.error!!.code()==404){
                    showModeratorDialog()
                }
            }
            Status.THROWABLE -> {
                Log.d("TAGg", "throw")
                hideDialog()
            }
        }
    }

    private fun consumeSmsResponse(response: ApiResponse) {
        when (response.status) {
            Status.LOADING -> {
                showDialog()
            }
            Status.SUCCESS -> {
                hideDialog()
                val body = response.data!!.body() as ResponseSuccess
                if(body.status=="success"){
                    showEnterCodeDialog()
                }
            }
            Status.ERROR -> {
                hideDialog()
                Log.d("TAGg", "error")
                if(response.error!!.code()==404){
                    showModeratorDialog()
                }
            }
            Status.THROWABLE -> {
                Log.d("TAGg", "throw")
                hideDialog()
            }
        }
    }

    override fun onBackPressed() {
        if(intent.data==null) {
            super.onBackPressed()
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun setListeners(ad: Ad) {

        if(ad.images!=null && ad.images.size>0) {
            size=ad.images.size
            val tapGestureDetector = GestureDetector(this, TapGestureListener(this, ad))
            pager.setOnTouchListener { _, event ->
                tapGestureDetector.onTouchEvent(event)
                false
            }
            pager.setOnPageChangeListener(object : OnPageChangeListener {
                override fun onPageSelected(position: Int) {
                    current = position
                    if (ad.images.size > 0) {
                        photos.text = ((position + 1).toString() + "/" + ad.images.size.toString())
                    }
                    if (position == ad.images.size) {
                        photos.visibility = View.GONE
                        finish.visibility = View.GONE
                    } else {
                        photos.visibility = View.VISIBLE
                        finish.visibility = View.VISIBLE
                    }
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                override fun onPageScrollStateChanged(state: Int) {}
            })
        }

        if(ad.phones!=null && ad.phones.size>0) {
            callPhone.setOnClickListener {
                makeDial(ad.phones[0])
            }
        }

        hot_lay.setOnClickListener {
            showHotPromoteDialog()
        }

        show_comments_button.setOnClickListener {
            val intent = Intent(this, CommentActivity::class.java)
            intent.putExtra("commentsList", commentsList)
            intent.putExtra("adId", ad.id)
            startActivity(intent)
        }
    }

    private fun makeDial(number:String) {
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, arrayOf(PERMISSIONS), PERMISSION_ALL)
        } else {
            number.replace("-", "")
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:+7$number")
            startActivity(callIntent)
        }
    }

    private fun setAdapter(ad: Ad) {
        val pagerAdapter = ImagesAdapter(supportFragmentManager, getImageFragments(ad))
        pager.adapter = pagerAdapter
        pager.offscreenPageLimit=9
        val phoneAdapter = PhoneAdapter(this, this)
        phoneAdapter.setData(ad.phones)
        phones_txt.adapter=phoneAdapter
    }

    private fun getImageFragments(ad: Ad): List<Fragment> {
        val fragments: MutableList<Fragment> = ArrayList()
        if (ad.images.size > 0) {
            for (i in 0 until ad.images.size) {
                fragments.add(ImageFragment.newInstance(i, ad.images[i]))
            }
            fragments.add(YandexAdFragment.newInstance())
        } else {
            fragments.add(0,
                NoImageFragment.newInstance())
        }
        return fragments
    }

    private fun consumeResponse(response: ApiResponse) {
        when (response.status) {
            Status.LOADING -> {
                showDialog()
            }
            Status.SUCCESS -> {
                hideDialog()
                renderResponse(response.data!!.body() as ResponseAd)
            }
            Status.ERROR -> {
                hideDialog()
                Log.d("TAGg", "error")
                if(response.error!!.code()==404){
                    showModeratorDialog()
                }
            }
            Status.THROWABLE -> {
                Log.d("TAGg", "throw")
                hideDialog()
            }
        }
    }

    private fun renderResponse(response: ResponseAd) {
        if(response.ad.title!=null) {
            title_txt.text = response.ad.title
            price_txt.text = formatHelper.setFormat(response.ad.currency, response.ad.amount)
            content_txt.text = response.ad.description
            dateTxt.text = response.ad.date
            if (response.ad.city != null) {
                if (response.ad.region != null) {
                    location_txt.text = "${response.ad.region}, ${response.ad.city}"
                } else {
                    location_txt.text = "${response.ad.city}"
                }
            }
            visitors_text.text = response.ad.visited.toString()
            if (response.ad.images.size > 0)
                photos.text = "1/" + response.ad.images.size.toString()
            setAdapter(response.ad)
            setListeners(response.ad)
            if(response.ad.comments.size>0) {
                commentsList.clear()
                commentsList.addAll(response.ad.comments)
                var commentsList = response.ad.comments
                if (response.ad.comments.size > 3) {
                    commentsList = response.ad.comments.take(3)
                }
                val adapterForComment = CommentAdapter(this)
                adapterForComment.setData(commentsList)
                comments_recycler.adapter=adapterForComment
            } else {
                show_comments_button.text= resources.getString(R.string.comments2)
            }
        } else {
            showModeratorDialog()
        }
    }

    private fun showDialog(){
        progress_bar_show_ad.visibility=View.VISIBLE
    }

    private fun hideDialog(){
        progress_bar_show_ad.visibility=View.GONE
    }

    private fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission!!) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    override fun makeCall(phone: String) {
        makeDial(phone)
    }

    class TapGestureListener(val context: Context, private val ad: Ad) : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            val adActivity = (context as ShowAdActivity)
            adActivity.imagesIntent.putExtra("pageCount", adActivity.size)
            adActivity.imagesIntent.putExtra("select", adActivity.current)
            adActivity.imagesIntent.putExtra("ad", ad)
            adActivity.startActivity(adActivity.imagesIntent)
            return super.onSingleTapConfirmed(e)
        }
    }

    private fun loadGoogleAd(){
        try {
            MobileAds.initialize(this) {}
            GoogleAdmobHelper(this, "ca-app-pub-8576417478026387/6427654586", my_template).loadAd()
        } catch (e:Exception){
            Log.d("TAGg", e.message)
        }
    }

    private fun showModeratorDialog(){
        noAdDialog.setContentView(R.layout.dialog_ad_moderate)
        noAdDialog.show()
        noAdDialog.setOnCancelListener {
            onBackPressed()
        }
    }

    private fun showHotPromoteDialog(){
        noAdDialog.setContentView(R.layout.dialog_input_phone)
        noAdDialog.send_sms_button.setOnClickListener {
            if(phoneNumber.isNotEmpty()){
                noAdDialog.dismiss()
                viewModel.sendSms(phoneNumber)
            }
        }
        val listener =
            MaskedTextChangedListener.installOn(
                noAdDialog.auto_complete,
                "+7-([000])-[000]-[00]-[00]",
                object : MaskedTextChangedListener.ValueListener {
                    override fun onTextChanged(
                        maskFilled: Boolean,
                        extractedValue: String,
                        formattedValue: String
                    ) {
                        phoneNumber = if (maskFilled) {
                            formatHelper.removeInvelidSymbols(formattedValue)
                        } else {
                            ""
                        }
                    }
                }
            )
        noAdDialog.setCanceledOnTouchOutside(false)
        noAdDialog.auto_complete.hint = listener.placeholder()
        noAdDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        noAdDialog.show()
    }

    private fun showEnterCodeDialog(){
        noAdDialog.setContentView(R.layout.dialog_input_code)
        noAdDialog.send_code_button.setOnClickListener {
            val code = noAdDialog.auto_complete.text.toString()
            val id = (intent.getSerializableExtra("ad") as Ad).id
            if(code.isNotEmpty() && code.length>5){
                noAdDialog.dismiss()
                viewModel.sendCode(phoneNumber, code, "hot", id)
            }
        }
        noAdDialog.setCanceledOnTouchOutside(false)
        noAdDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        noAdDialog.show()
    }

    private fun showSuccessfulPromotionDialog(){
        noAdDialog.setCanceledOnTouchOutside(true)
        noAdDialog.setContentView(R.layout.dialog_successful_promotion)
        noAdDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        noAdDialog.show()
    }

    override fun onDestroy() {
        if(noAdDialog.isShowing){
            noAdDialog.dismiss()
        }
        super.onDestroy()
    }

    override fun adOnMOderate() {
        showModeratorDialog()
    }
}
