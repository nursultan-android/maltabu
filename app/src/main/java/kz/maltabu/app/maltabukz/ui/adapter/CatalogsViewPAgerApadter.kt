package kz.maltabu.app.maltabukz.ui.adapter

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import java.util.*

class CatalogsViewPAgerApadter : PagerAdapter() {
    private val mFragmentTitleList: MutableList<String> = ArrayList()

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return true
    }

    override fun getCount(): Int {
        return mFragmentTitleList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }

    fun addFragment(title: String) {
        mFragmentTitleList.add(title)
    }
}