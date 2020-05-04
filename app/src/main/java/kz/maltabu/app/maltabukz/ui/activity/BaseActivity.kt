package kz.maltabu.app.maltabukz.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import io.paperdb.Paper
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.utils.LocaleHelper
import kz.maltabu.app.maltabukz.utils.customEnum.EnumsClass
import org.koin.android.ext.android.inject

abstract class BaseActivity : AppCompatActivity(){
    var manager = supportFragmentManager
    lateinit var ft: FragmentTransaction
    val enum: EnumsClass by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleHelper.setLanguage(this, Paper.book().read(enum.LANG, enum.KAZAKH))
    }

    fun setNoInternetView(){
        startActivity(Intent(this, NoInternetActivity::class.java))
    }
}