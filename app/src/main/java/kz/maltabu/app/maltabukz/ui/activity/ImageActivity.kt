package kz.maltabu.app.maltabukz.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.models.response.Ad
import kz.maltabu.app.maltabukz.ui.fragment.FullScreenFragment
import kz.maltabu.app.maltabukz.utils.CustomAnimator.Companion.animateHotViewLinear

class ImageActivity : BaseActivity() {
    private var img: ImageView? = null
    private var ad: Ad? = null
    private var PAGE_COUNT = 0
    private var selectedImg = 0
    private var pager: ViewPager? = null
    private var pagerAdapter: PagerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ad = intent.getSerializableExtra("ad") as Ad
        selectedImg = intent.getIntExtra("select", 0)
        PAGE_COUNT = ad!!.images.size
        setContentView(R.layout.activity_images)
        pager = findViewById<View>(R.id.pages) as ViewPager
        img = findViewById<View>(R.id.arr) as ImageView
        img!!.setOnClickListener { v: View? ->
            animateHotViewLinear(v)
            finish()
        }
        val txt = findViewById<View>(R.id.photos) as TextView
        if (ad!!.images.size > 0) txt.text = ((selectedImg + 1).toString() + "/" + ad!!.images.size)
        pagerAdapter = MyFragmentPagerAdapter2(supportFragmentManager)
        pager!!.adapter = pagerAdapter
        pager!!.currentItem = selectedImg
        pager!!.offscreenPageLimit = PAGE_COUNT
        pager!!.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                if (ad!!.images.size > 0) txt.text = ((position + 1).toString() + "/" + ad!!.images.size)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private inner class MyFragmentPagerAdapter2(fm: FragmentManager?) :
        FragmentPagerAdapter(fm!!) {
        override fun getItem(position: Int): Fragment {
            return FullScreenFragment.newInstance(position, ad!!.images[position])
        }

        override fun getCount(): Int {
            return PAGE_COUNT
        }
    }
}
