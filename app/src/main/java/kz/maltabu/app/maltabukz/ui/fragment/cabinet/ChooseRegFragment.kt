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
import kotlinx.android.synthetic.main.fragment_reg_type.*
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

class ChooseRegFragment : Fragment() {
    companion object {
        fun newInstance() = ChooseRegFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reg_type, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        (activity as AuthActivity).toolbar_title.text=resources.getString(R.string.forgetPassTitle)
        val bundle = Bundle()
        with_email_button.setOnClickListener {
            bundle.putString("type", "email")
            openFragment(bundle)
        }
        with_phone_button.setOnClickListener {
            bundle.putString("type", "phone")
            openFragment(bundle)
        }
        (activity as AuthActivity).back_arrow.setOnClickListener {
            activity!!.onBackPressed()
        }
    }

    private fun openFragment(bundle: Bundle) {
        val fragment  = ForgetPasswordFragment.newInstance()
        fragment.arguments = bundle
        (activity as AuthActivity).setFragmentToBack(fragment)
    }
}
