package kz.maltabu.app.maltabukz.utils

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols


class FormatHelper {
    companion object {
        fun setFormat(currency: String,number: Long):String{
            val symbols = DecimalFormatSymbols()
            symbols.groupingSeparator = ','
            symbols.decimalSeparator = '\''

            val decimalFormat = DecimalFormat("#,### $currency", symbols)
            return decimalFormat.format(number)
        }

        fun isAlpha(name: String): Boolean {
            return name.matches("[a-zA-Z]+".toRegex())
        }
    }
}