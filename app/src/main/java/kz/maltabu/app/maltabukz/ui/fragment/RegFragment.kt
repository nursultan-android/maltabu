package kz.maltabu.app.maltabukz.ui.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.redmadrobot.inputmask.MaskedTextChangedListener
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_new_ad.*
import kotlinx.android.synthetic.main.fragment_reg.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.model.RegisterBody
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.models.response.ResponseRegister
import kz.maltabu.app.maltabukz.ui.activity.AuthActivity
import kz.maltabu.app.maltabukz.utils.customEnum.ApiLangEnum
import kz.maltabu.app.maltabukz.utils.customEnum.Keys
import kz.maltabu.app.maltabukz.utils.customEnum.Status
import kz.maltabu.app.maltabukz.vm.RegViewModel

class RegFragment : Fragment() {
    private lateinit var body: RegisterBody
    private lateinit var viewModel: RegViewModel

    companion object {
        fun newInstance() = RegFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reg, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        body= RegisterBody()
        viewModel=ViewModelProviders.of(this, RegViewModel.ViewModelFactory(Paper.book().read(Keys.LANG.constantKey, ApiLangEnum.KAZAKH.constantKey))).get(RegViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setMask()
        (activity as AuthActivity).toolbar_title.text=resources.getString(R.string.regTitle)
        viewModel.mainResponse().observe(viewLifecycleOwner, Observer { consumeResponse(it) })
    }

    private fun consumeResponse(response: ApiResponse) {
        when (response.status) {
            Status.LOADING -> {
                showDialog()
            }
            Status.SUCCESS -> {
                hideDialog()
                renderResponse(response.data!!.body() as ResponseRegister)
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

    private fun renderResponse(response: ResponseRegister) {
        if(response.status=="success"){
            Paper.book().write(Keys.TOKEN.constantKey, response.token.token)
            Paper.book().write(Keys.USER.constantKey, response.user)
            Toast.makeText(activity!!, resources.getString(R.string.noActivation2), Toast.LENGTH_LONG).show()
            (activity as AuthActivity).clearBackStack()
            (activity as AuthActivity).setFragment(ProfileFragment.newInstance())
        } else {
            Toast.makeText(activity!!, "", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setListeners() {
        (activity as AuthActivity).back_arrow.setOnClickListener {
            activity!!.onBackPressed()
        }
        register.setOnClickListener {
            if(validateForm()){
                addFieldsBody()
                viewModel.register(body)
            }
        }
    }

    private fun addFieldsBody() {
        body.name=name_edit_txt.text.toString()
        body.email=email_edit_txt.text.toString()
        body.password=pass_edit_txt.text.toString()
        body.confirm=conf_edit_txt.text.toString()
    }

    private fun setMask(){
        val listener =
            MaskedTextChangedListener.installOn(
                phone_edit_txt,
                "+7-[000]-[000]-[00]-[00]",
                object : MaskedTextChangedListener.ValueListener {
                    override fun onTextChanged(maskFilled: Boolean, extractedValue: String, formattedValue: String) {
                        if (maskFilled) {
                            body.phone = formattedValue.replace("+", "")
                        } else {
                            body.phone=null
                        }
                    }
                }
            )
        phone_edit_txt.hint = listener.placeholder()
    }

    private fun validateForm() : Boolean{
        if(body.phone==null){
            Toast.makeText(activity, resources.getString(R.string.phoneValid), Toast.LENGTH_SHORT).show()
            return false
        } else {
            if(name_edit_txt.text==null || name_edit_txt.text.toString().isEmpty()){
                Toast.makeText(activity, resources.getString(R.string.field_required), Toast.LENGTH_SHORT).show()
                return false
            } else {
                if(email_edit_txt.text==null || email_edit_txt.text.toString().isEmpty() || !validEmail(email_edit_txt.text.toString())){
                    Toast.makeText(activity, resources.getString(R.string.emailValid), Toast.LENGTH_SHORT).show()
                    return false
                } else {
                    if(pass_edit_txt.text==null || pass_edit_txt.text.isEmpty()){
                        Toast.makeText(activity, resources.getString(R.string.password_required), Toast.LENGTH_SHORT).show()
                        return false
                    } else {
                        if(pass_edit_txt.text.toString().length<8){
                            Toast.makeText(activity, resources.getString(R.string.reg32), Toast.LENGTH_SHORT).show()
                            return false
                        } else {
                            if(pass_edit_txt.text.toString()!=conf_edit_txt.text.toString()){
                                Toast.makeText(activity, resources.getString(R.string.passwords_not_quals), Toast.LENGTH_SHORT).show()
                                return false
                            } else {
                                return true
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showDialog(){
        (activity as AuthActivity).showLoader()
    }

    private fun hideDialog(){
        (activity as AuthActivity).hideLoader()
    }

    private fun validEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

}
