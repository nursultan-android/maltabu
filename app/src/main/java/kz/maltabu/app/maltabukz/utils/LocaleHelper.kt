package kz.maltabu.app.maltabukz.utils

import android.content.Context
import android.content.res.Configuration
import java.util.*

object LocaleHelper {
    fun setLanguage(context: Context, lang: String): Context {
        val locale= Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        return context
    }
}