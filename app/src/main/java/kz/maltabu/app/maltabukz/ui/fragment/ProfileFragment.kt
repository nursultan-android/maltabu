package kz.maltabu.app.maltabukz.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.redmadrobot.inputmask.MaskedTextChangedListener
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_new_ad.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.models.response.User
import kz.maltabu.app.maltabukz.ui.activity.AuthActivity
import kz.maltabu.app.maltabukz.utils.customEnum.Keys

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setUserInfo()
    }

    private fun setUserInfo() {
        setMask()
        val user:User? = Paper.book().read<User>(Keys.USER.constantKey, null)
        if(user!=null){
            if(user.phone!=null && user.phone.isNotEmpty()){
                edit_phone_txt.setText(user.phone)
            }
            if(user.name!=null && user.name.isNotEmpty()){
                edit_name_txt.setText(user.name)
            }
        }
    }

    private fun setListeners() {
        (activity as AuthActivity).toolbar_title.text=resources.getString(R.string.Cabinet)
        button_logout.setOnClickListener {
            showLogoutDialog()
        }
        (activity as AuthActivity).back_arrow.setOnClickListener {
            activity!!.finish()
        }
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(resources.getString(R.string.logoutMessage))
            .setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
                logout()
                dialog.cancel()
            }
            .setCancelable(false)
        val dialog = builder.create()
        dialog.show()
    }

    private fun logout() {
        Paper.book().delete(Keys.USER.constantKey)
        Paper.book().delete(Keys.TOKEN.constantKey)
        activity!!.onBackPressed()
    }

    private fun setMask(){
        val listener =
            MaskedTextChangedListener.installOn(
                edit_phone_txt,
                "+7-[000]-[000]-[00]-[00]",
                object : MaskedTextChangedListener.ValueListener {
                    override fun onTextChanged(maskFilled: Boolean, extractedValue: String, formattedValue: String) {
                        if (maskFilled) {}
                    }
                }
            )
        edit_phone_txt.hint = listener.placeholder()
    }

}
