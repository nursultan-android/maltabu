package kz.maltabu.app.maltabukz.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_new_ad.*
import kotlinx.android.synthetic.main.fragment_auth.*
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.ui.activity.AuthActivity

class ForgetPasswordFragment : Fragment() {

    companion object {
        fun newInstance() = ForgetPasswordFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        (activity as AuthActivity).toolbar_title.text=resources.getString(R.string.auth)
        button_forget.setOnClickListener {

        }
        (activity as AuthActivity).back_arrow.setOnClickListener {
            activity!!.onBackPressed()
        }
    }

}
