package kz.maltabu.app.maltabukz.ui.fragment.cabinet

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.facebook.CallbackManager
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

abstract class BaseSocialFragment : Fragment() {
    lateinit var google: ImageView
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var facebook: ImageView
    lateinit var fb: LoginButton
    lateinit var callbackManager: CallbackManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(activity!!, gso)
        fb = LoginButton(context)
        fb.setPermissions("email", "public_profile")
        fb.fragment = this
        callbackManager = CallbackManager.Factory.create()
    }

    fun setSocialButtonsListeners(){
        google.setOnClickListener {
            val account = GoogleSignIn.getLastSignedInAccount(activity)
            if (account != null)
                mGoogleSignInClient.signOut().addOnCompleteListener(activity!!) {}
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, 321)
        }
        facebook.setOnClickListener {
            fb.performClick()
        }
    }
}
