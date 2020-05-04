package kz.maltabu.app.maltabukz.ui.fragment.cabinet

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
import kz.maltabu.app.maltabukz.ui.activity.BaseActivity
import kz.maltabu.app.maltabukz.utils.FormatHelper
import kz.maltabu.app.maltabukz.utils.customEnum.*
import kz.maltabu.app.maltabukz.vm.AuthViewModel
import org.json.JSONException
import org.koin.android.ext.android.inject


class AuthFragment : BaseSocialFragment() {
    private lateinit var viewModel:AuthViewModel
    private val formatHelper: FormatHelper by inject()

    companion object {
        fun newInstance() =
            AuthFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel=ViewModelProviders.of(this, AuthViewModel.ViewModelFactory(Paper.book().read((activity!! as BaseActivity).enum.LANG, (activity!! as BaseActivity).enum.KAZAKH)))
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
            Paper.book().write((activity!! as BaseActivity).enum.TOKEN, response.token.token)
            Paper.book().write((activity!! as BaseActivity).enum.USER, response.user)
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
            Paper.book().write((activity!! as BaseActivity).enum.TOKEN, response.token)
            Paper.book().write((activity!! as BaseActivity).enum.USER, response.user)
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
                if(formatHelper.isAlpha(login) && login.length == 10) {
                    login="7$login"
                }
                Log.d("TAGg", login)
//                viewModel.login(login, password_edit_txt.text.toString())
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
                        viewModel.socail(email, userId, (activity!! as BaseActivity).enum.FACEBOOK)
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
                viewModel.socail(account!!.email!!, account!!.id!!, (activity!! as BaseActivity).enum.GOOGLE)
            } catch (e: ApiException) {
                Toast.makeText(activity, e.message,Toast.LENGTH_LONG).show()
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
