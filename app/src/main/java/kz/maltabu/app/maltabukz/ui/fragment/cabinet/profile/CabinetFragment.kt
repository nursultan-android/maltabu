package kz.maltabu.app.maltabukz.ui.fragment.cabinet.profile

import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.fragment_cabinet.*
import kz.maltabu.app.maltabukz.R
import java.lang.ref.WeakReference
import java.util.*

class CabinetFragment : Fragment() {
    lateinit var adapter: ViewPagerAdapter

    companion object {
        fun newInstance() = CabinetFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cabinet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ViewPagerAdapter(activity!!.supportFragmentManager)
        adapter.addFragment(ProfileFragment.newInstance(), resources.getString(R.string.Cabinet))
        adapter.addFragment(MyAdsFragment(), resources.getString(R.string.cab1))
        profile_view_pager.adapter=adapter
        profile_tab_layout.setupWithViewPager(profile_view_pager)
    }

    class ViewPagerAdapter internal constructor(manager: FragmentManager?) : FragmentPagerAdapter(manager!!) {
        private val instantiatedFragments = SparseArray<WeakReference<Fragment>>()
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val fragment =
                super.instantiateItem(container, position) as Fragment
            instantiatedFragments.put(position, WeakReference(fragment))
            return fragment
        }

        override fun getItemPosition(`object`: Any): Int {
            return super.getItemPosition(`object`)
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            instantiatedFragments.remove(position)
            super.destroyItem(container, position, `object`)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }
}
