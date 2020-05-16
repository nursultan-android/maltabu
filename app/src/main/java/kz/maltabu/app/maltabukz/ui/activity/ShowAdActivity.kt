package kz.maltabu.app.maltabukz.ui.activity

import android.Manifest
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.gms.ads.MobileAds
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_show_ad.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.models.response.Ad
import kz.maltabu.app.maltabukz.network.models.response.ResponseAd
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
    private lateinit var dialog: ProgressDialog
    private lateinit var noAdDialog: Dialog
    lateinit var imagesIntent: Intent
    var current=0
    var size=0
    var PERMISSION_ALL = 1
    var PERMISSIONS =  Manifest.permission.CALL_PHONE

    private val formatHelper: FormatHelper by inject()
    private val checker: NetworkChecker by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = (intent.getSerializableExtra("ad") as Ad).id
        setContentView(R.layout.activity_show_ad)
//        val data = intent.data
//        if(data!=null){
//            var slug = data.toString()
//            slug = slug.substring(slug.lastIndexOf("/") + 1)
//            Log.d("TAGg", slug)
//
//        }
        dialog = ProgressDialog(this)
        noAdDialog = Dialog(this)
        viewModel = ViewModelProviders.of(this, ShowAdActivityViewModel.ViewModelFactory(Paper.book().read(enum.LANG, enum.KAZAKH)))
            .get(ShowAdActivityViewModel::class.java)
        viewModel.mainResponse().observe(this, Observer { consumeResponse(it) })
        if(checker.isNetworkAvailable) {
            viewModel.getAdById(id, this)
        } else {
            setNoInternetView()
        }
        imagesIntent = Intent(this, ImageActivity::class.java)
        finish.setOnClickListener {
            CustomAnimator.animateHotViewLinear(it)
            finish()
        }
        loadGoogleAd()
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
        } else {
            showModeratorDialog()
        }
    }

    private fun showDialog(){
        if(!dialog.isShowing)
            dialog.show()
    }

    private fun hideDialog(){
        if(dialog.isShowing)
            dialog.dismiss()
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

    override fun onDestroy() {
        hideDialog()
        super.onDestroy()
    }

    override fun adOnMOderate() {
        showModeratorDialog()
    }
}
