package kz.maltabu.app.maltabukz.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_new_ad.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.ui.activity.NewAdActivity

class SuccessFragment : Fragment() {

    companion object {
        fun newInstance() = SuccessFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ad_success, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as NewAdActivity).hideLoader()
        setArrowButtonColor()
    }

    private fun setArrowButtonColor(){
        activity!!.back_arrow.setOnClickListener {
            activity!!.finish()
        }
    }
}
