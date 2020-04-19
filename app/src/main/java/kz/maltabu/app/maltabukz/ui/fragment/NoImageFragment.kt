package kz.maltabu.app.maltabukz.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.item_image.*
import kz.maltabu.app.maltabukz.R


class NoImageFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.item_image, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        load.visibility=View.GONE
    }

    companion object {
        fun newInstance(): NoImageFragment {
            return NoImageFragment()
        }
    }
}
