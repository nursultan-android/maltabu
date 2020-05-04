package kz.maltabu.app.maltabukz.ui.fragment.cabinet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_new_ad.*
import kotlinx.android.synthetic.main.fragment_auth.*
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.android.synthetic.main.fragment_forgot_password.login_edit_txt
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.models.response.ResponseAuth
import kz.maltabu.app.maltabukz.ui.activity.AuthActivity
import kz.maltabu.app.maltabukz.ui.activity.BaseActivity
import kz.maltabu.app.maltabukz.utils.FormatHelper
import kz.maltabu.app.maltabukz.utils.customEnum.EnumsClass
import kz.maltabu.app.maltabukz.utils.customEnum.Status
import kz.maltabu.app.maltabukz.vm.ForgotViewModel
import org.koin.android.ext.android.inject

class ForgetPasswordFragment : Fragment() {
    private lateinit var viewModel:ForgotViewModel
    private var isPhone = false
    private val formatHelper:FormatHelper by inject()

    companion object {
        fun newInstance() =
            ForgetPasswordFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, ForgotViewModel.ViewModelFactory(Paper.book().read((activity!! as BaseActivity).enum.LANG, (activity!! as BaseActivity).enum.KAZAKH)))
            .get(ForgotViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        viewModel.mainResponse().observe(viewLifecycleOwner, Observer {
            consumeResponse(it)
        })
    }

    private fun consumeResponse(response: ApiResponse) {
        when (response.status) {
            Status.LOADING -> {
                showDialog()
            }
            Status.SUCCESS -> {
                hideDialog()
                showToast()
                activity!!.onBackPressed()
                Log.d("TAGg", "ok")
            }
            Status.ERROR -> {
                hideDialog()
                showToast()
                activity!!.onBackPressed()
                Log.d("TAGg", "error")
            }
            Status.THROWABLE -> {
                hideDialog()
                showToast()
                activity!!.onBackPressed()
                Log.d("TAGg", "throw")
            }
        }
    }

    private fun showDialog(){
        (activity as AuthActivity).showLoader()
    }

    private fun hideDialog(){
        (activity as AuthActivity).hideLoader()
    }

    private fun setListeners() {
        (activity as AuthActivity).toolbar_title.text=resources.getString(R.string.auth)
        button_forget.setOnClickListener {
            if(login_edit_txt.text.toString().isNotEmpty()){
                var login  = login_edit_txt.text.toString()
                if(!formatHelper.isAlpha(login) && login.length>9) {
                    isPhone=true
                    if(login.length == 10)
                        login="7$login"
                } else {
                    isPhone=false
                }
                viewModel.resetPassword(login)
            }
        }
        (activity as AuthActivity).back_arrow.setOnClickListener {
            activity!!.onBackPressed()
        }
    }

    private fun showToast(){
        Toast.makeText(activity!!, resources.getText(R.string.reset_password_success),Toast.LENGTH_LONG).show()
    }

}
