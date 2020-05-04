package kz.maltabu.app.maltabukz.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_no_internet.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.utils.CustomAnimator
import kz.maltabu.app.maltabukz.utils.web.NetworkChecker
import org.koin.android.ext.android.inject
import java.util.*


class NoInternetActivity : BaseActivity(){
    var intNumb=0
    private val checker: NetworkChecker by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet)
        setListeners()
    }

    private fun setListeners() {
        check_connection_button.setOnClickListener {
            CustomAnimator.animateViewBound(it)
            tryCheck()
        }
    }

    private fun tryCheck(){
        if(progress_connnection.visibility != View.VISIBLE){
            progress_connnection.visibility = View.VISIBLE
            check_connection_button.animate().y(check_connection_button.y+100f).start()
            no_internet_text.animate().y(no_internet_text.y-100f).start()
            setValues()
        }
    }

    override fun onBackPressed() {
        tryCheck()
    }

    private fun disableViewsAndFinishActivity(){
        progress_connnection.visibility = View.GONE
        check_connection_button.isEnabled=false
        finish()
    }

    private fun setValues() {
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val value =intNumb++
                runOnUiThread {
                    if(checker.isNetworkAvailable){
                        disableViewsAndFinishActivity()
                        timer.cancel()
                    }
                    Log.d("TAGg", "Value: $value")
                }
            }
        }, 0, 200)
    }
}

