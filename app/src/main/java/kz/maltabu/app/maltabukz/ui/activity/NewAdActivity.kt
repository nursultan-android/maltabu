package kz.maltabu.app.maltabukz.ui.activity

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_new_ad.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.ui.adapter.MenuAdapter
import kz.maltabu.app.maltabukz.ui.fragment.newAd.ChooseFragment

class NewAdActivity :  BaseActivity(), MenuAdapter.ChooseCategory {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_ad)
        setFragment(ChooseFragment.newInstance())
    }

    fun setFragment(fragment: Fragment) {
        ft = manager.beginTransaction()
        ft.replace(R.id.content, fragment).commit()
    }

    fun setFragmentToBack(fragment: Fragment) {
        ft = manager.beginTransaction()
        ft.replace(R.id.content, fragment).addToBackStack("ChooseCatalog").commit()
    }

    fun showLoader(){
        progress_view.visibility= View.VISIBLE
    }

    fun hideLoader(){
        progress_view.visibility= View.GONE
    }

    fun clearBackStack(){
        for (i in 0 until manager.backStackEntryCount) {
            manager.popBackStack()
        }
    }

    override fun onDestroy() {
        hideLoader()
        super.onDestroy()
    }
}
