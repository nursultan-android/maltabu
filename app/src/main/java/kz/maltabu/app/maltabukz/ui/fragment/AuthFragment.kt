package kz.maltabu.app.maltabukz.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_new_ad.*
import kotlinx.android.synthetic.main.fragment_auth.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.models.response.ResponseAuth
import kz.maltabu.app.maltabukz.network.models.response.ResponseRegister
import kz.maltabu.app.maltabukz.ui.activity.AuthActivity
import kz.maltabu.app.maltabukz.utils.FormatHelper
import kz.maltabu.app.maltabukz.utils.customEnum.ApiLangEnum
import kz.maltabu.app.maltabukz.utils.customEnum.Keys
import kz.maltabu.app.maltabukz.utils.customEnum.SocialEnum
import kz.maltabu.app.maltabukz.utils.customEnum.Status
import kz.maltabu.app.maltabukz.vm.AuthViewModel
import org.json.JSONException


class AuthFragment : BaseSocialFragment() {
    private lateinit var viewModel:AuthViewModel

    companion object {
        fun newInstance() = AuthFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel=ViewModelProviders.of(this, AuthViewModel.ViewModelFactory(Paper.book().read(Keys.LANG.constantKey, ApiLangEnum.KAZAKH.constantKey)))
            .get(AuthViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.mainResponse().observe(viewLifecycleOwner, Observer {
            consumeResponse(it)
        })
        viewModel.socResponse().observe(viewLifecycleOwner, Observer {
            consumeSocResponse(it)
        })
        setListeners()
    }

    private fun consumeSocResponse(response: ApiResponse) {
        when (response.status) {
            Status.LOADING -> {
                showDialog()
            }
            Status.SUCCESS -> {
                hideDialog()
                renderSocResponse(response.data!!.body() as ResponseRegister)
            }
            Status.ERROR -> {
                hideDialog()
                Log.d("TAGg", response.error!!.message())
                Toast.makeText(activity!!, response.error.message(), Toast.LENGTH_SHORT).show()
            }
            Status.THROWABLE -> {
                hideDialog()
                Log.d("TAGg", response.throwabl.toString())
            }
        }
    }

    private fun renderSocResponse(response: ResponseRegister) {
        if(response.status=="success"){
            Paper.book().write(Keys.TOKEN.constantKey, response.token.token)
            Paper.book().write(Keys.USER.constantKey, response.user)
            (activity as AuthActivity).clearBackStack()
            (activity as AuthActivity).setFragment(ProfileFragment.newInstance())
        } else {
            Toast.makeText(activity!!, "", Toast.LENGTH_SHORT).show()
        }
    }

    private fun consumeResponse(response: ApiResponse) {
        when (response.status) {
            Status.LOADING -> {
                showDialog()
            }
            Status.SUCCESS -> {
                hideDialog()
                renderResponse(response.data!!.body() as ResponseAuth)
            }
            Status.ERROR -> {
                hideDialog()
                Log.d("TAGg", response.error!!.message())
                Toast.makeText(activity!!, response.error.message(), Toast.LENGTH_SHORT).show()
            }
            Status.THROWABLE -> {
                hideDialog()
                Log.d("TAGg", response.throwabl.toString())
            }
        }
    }

    private fun renderResponse(response: ResponseAuth) {
        if(response.status=="success"){
            Paper.book().write(Keys.TOKEN.constantKey, response.token)
            Paper.book().write(Keys.USER.constantKey, response.user)
            (activity as AuthActivity).clearBackStack()
            (activity as AuthActivity).setFragment(ProfileFragment.newInstance())
        } else {
            Toast.makeText(activity!!, "", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setListeners() {
        (activity as AuthActivity).toolbar_title.text=resources.getString(R.string.auth)
        button_reg.setOnClickListener {
            (activity as AuthActivity).setFragmentToBack(RegFragment.newInstance())
        }
        forgot_button.setOnClickListener {
            (activity as AuthActivity).setFragmentToBack(ForgetPasswordFragment.newInstance())
        }
        button_auth.setOnClickListener {
            if(validateFields()){
                var login  = login_edit_txt.text.toString()
                if(FormatHelper.isAlpha(login)|| login.length == 10) {
                    login="7$login"
                }
                viewModel.login(login, password_edit_txt.text.toString())
            }
        }
        (activity as AuthActivity).back_arrow.setOnClickListener {
            activity!!.finish()
        }
        google=social_btn_google
        facebook=social_btn_facebook
        setSocialButtonsListeners()
        fb.registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                val request = GraphRequest.newMeRequest(result!!.accessToken) { resultObject, _ ->
                    try {
                        val userId = resultObject.getString("id")
                        var email = ""
                        var firstName = ""
                        var lastName = ""
                        if (resultObject.has("email"))
                            email = resultObject.getString("email")
                        if (resultObject.has("first_name"))
                            firstName = resultObject.getString("first_name")
                        if (resultObject.has("last_name"))
                            lastName = resultObject.getString("last_name")
                        viewModel.socail(email, userId, SocialEnum.FACEBOOK.constantKey)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                val parameters = Bundle()
                parameters.putString("fields", "id, first_name, last_name, email")
                request.parameters = parameters
                request.executeAsync()
            }
            override fun onCancel() {}
            override fun onError(error: FacebookException?) {
                Toast.makeText(context, error!!.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun validateFields(): Boolean {
        return if(login_edit_txt.text==null || login_edit_txt.text.toString().isEmpty()){
            Toast.makeText(activity, resources.getString(R.string.login_validate), Toast.LENGTH_SHORT).show()
            false
        } else {
            if(password_edit_txt.text==null || password_edit_txt.text.toString().isEmpty()){
                Toast.makeText(activity, resources.getString(R.string.password_required), Toast.LENGTH_SHORT).show()
                false
            } else {
                true
            }
        }
    }

    private fun showDialog(){
        (activity as AuthActivity).showLoader()
    }

    private fun hideDialog(){
        (activity as AuthActivity).hideLoader()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 321) {
            val completedTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = completedTask.getResult(ApiException::class.java)
                viewModel.socail(account!!.email!!, account!!.id!!, SocialEnum.GOOGLE.constantKey)
            } catch (e: ApiException) {
                Toast.makeText(activity, e.message,Toast.LENGTH_LONG).show()
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
