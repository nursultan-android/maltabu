package kz.maltabu.app.maltabukz.ui.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.vk.api.sdk.VK.onActivityResult
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import io.paperdb.Paper
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.models.response.ResponseRegister
import kz.maltabu.app.maltabukz.ui.adapter.MenuAdapter
import kz.maltabu.app.maltabukz.ui.fragment.cabinet.AuthFragment
import kz.maltabu.app.maltabukz.ui.fragment.cabinet.profile.CabinetFragment
import kz.maltabu.app.maltabukz.utils.customEnum.Status
import kz.maltabu.app.maltabukz.utils.web.NetworkChecker
import kz.maltabu.app.maltabukz.vm.AuthViewModel
import org.koin.android.ext.android.inject

class AuthActivity :  BaseActivity(), MenuAdapter.ChooseCategory {
    private lateinit var dialog: ProgressDialog
    private lateinit var authViewModel: AuthViewModel
    val checker: NetworkChecker by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_ad)
        dialog= ProgressDialog(this)
        authViewModel = ViewModelProviders.of(this, AuthViewModel.ViewModelFactory(Paper.book().read(enum.LANG, enum.KAZAKH))).get(AuthViewModel::class.java)
        if((Paper.book().read(enum.TOKEN, "") as String).isNotEmpty()){
            setFragment(CabinetFragment.newInstance())
        } else {
            setFragment(AuthFragment.newInstance())
        }
        authViewModel.socResponse().observe(this, Observer { consumeResponse(it) })
    }

    private fun consumeResponse(response: ApiResponse) {
        when (response.status) {
            Status.LOADING -> {
                showLoader()
            }
            Status.SUCCESS -> {
                hideLoader()
                renderSocResponse(response.data!!.body() as ResponseRegister)
            }
            Status.ERROR -> {
                hideLoader()
                Log.d("TAGg", response.error!!.message())
                Toast.makeText(this, response.error.message(), Toast.LENGTH_SHORT).show()
            }
            Status.THROWABLE -> {
                hideLoader()
                Log.d("TAGg", response.throwabl.toString())
            }
        }
    }

    private fun renderSocResponse(response: ResponseRegister) {
        if(response.status=="success"){
            Paper.book().write(enum.TOKEN, response.token.token)
            Paper.book().write(enum.USER, response.user)
            clearBackStack()
            setFragment(CabinetFragment.newInstance())
        } else {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
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

    fun setCabFragmentToBack(fragment: Fragment) {
        ft = manager.beginTransaction()
        ft.replace(R.id.content, fragment).addToBackStack("CabFragment").commit()
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

    override fun onDestroy() {
        hideLoader()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!onActivityResult(requestCode, resultCode, data, object : VKAuthCallback {
                override fun onLoginFailed(i: Int) {
                    Log.d("TAGg", "vk Auth Failed")
                }
                override fun onLogin(vkAccessToken: VKAccessToken) {
                    if (vkAccessToken.email != null) {
                        authViewModel.social(vkAccessToken.email.toString(), vkAccessToken.userId.toString(), "vkontakte")
                        Log.d("TAGg", "vk " + vkAccessToken.email + " id " + vkAccessToken.userId)
                    } else {
                        Toast.makeText(this@AuthActivity, resources.getString(R.string.social_no_email), Toast.LENGTH_LONG).show()
                    }
                }
            })) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
