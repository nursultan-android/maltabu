package kz.maltabu.app.maltabukz.ui.activity

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.ui.adapter.MenuAdapter
import kz.maltabu.app.maltabukz.ui.fragment.newAd.ChooseFragment

class NewAdActivity :  BaseActivity(), MenuAdapter.ChooseCategory {
    private lateinit var dialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_ad)
        setFragment(ChooseFragment.newInstance())
        dialog= ProgressDialog(this)
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
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()
    }

    fun hideLoader(){
        if(dialog.isShowing)
            dialog.dismiss()
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
