package kz.maltabu.app.maltabukz.ui.activity

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import io.paperdb.Paper
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.ui.adapter.MenuAdapter
import kz.maltabu.app.maltabukz.ui.fragment.AuthFragment
import kz.maltabu.app.maltabukz.ui.fragment.ChooseFragment
import kz.maltabu.app.maltabukz.ui.fragment.ProfileFragment
import kz.maltabu.app.maltabukz.ui.fragment.RegFragment
import kz.maltabu.app.maltabukz.utils.customEnum.Keys

class AuthActivity :  BaseActivity(), MenuAdapter.ChooseCategory {

    var manager = supportFragmentManager
    private lateinit var ft: FragmentTransaction
    private lateinit var dialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_ad)
        dialog= ProgressDialog(this)
        if((Paper.book().read(Keys.TOKEN.constantKey, "") as String).isNotEmpty()){
            setFragment(ProfileFragment.newInstance())
        } else {
            setFragment(AuthFragment.newInstance())
        }
    }

    fun setFragment(fragment: Fragment) {
        ft = manager.beginTransaction()
        ft.replace(R.id.content, fragment).commit()
    }

    fun setFragmentToBack(fragment: Fragment) {
        ft = manager.beginTransaction()
        ft.replace(R.id.content, fragment).addToBackStack("AuthFragment").commit()
    }

    fun showLoader(){
        if (!dialog.isShowing) {
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            dialog.show()
        }
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
}
