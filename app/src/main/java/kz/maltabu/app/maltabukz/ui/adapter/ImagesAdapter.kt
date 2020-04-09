package kz.maltabu.app.maltabukz.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ImagesAdapter(fragmentManager: FragmentManager, mFragmentList: List<Fragment>) : FragmentPagerAdapter(fragmentManager) {
    private val mFragmentList: List<Fragment> = mFragmentList

    override fun getCount(): Int {
        return mFragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return "Page $position"
    }

}