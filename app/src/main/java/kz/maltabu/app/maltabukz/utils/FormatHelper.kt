package kz.maltabu.app.maltabukz.utils

import android.util.Patterns
import kz.maltabu.app.maltabukz.network.models.response.City
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols


class FormatHelper {
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

    fun removeInvelidSymbols(first: String): String{
        return first.replace("+","").replace("(", "").replace(")","")
    }

    fun getCityByName(cityList: List<City>, name: String): City {
        var city = City()
        for (i in cityList.indices){
            if(cityList[i].name == name){
                city=cityList[i]
            }
        }
        return city
    }

    fun validEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }
}