package kz.maltabu.app.maltabukz.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.paperdb.Paper
import kz.maltabu.app.maltabukz.utils.LocaleHelper
import kz.maltabu.app.maltabukz.utils.customEnum.ApiLangEnum
import kz.maltabu.app.maltabukz.utils.customEnum.Keys

open class BaseActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleHelper.setLanguage(this, Paper.book().read(Keys.LANG.constantKey, ApiLangEnum.KAZAKH.constantKey))
    }
}