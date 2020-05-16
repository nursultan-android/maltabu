package kz.maltabu.app.maltabukz.ui.fragment.cabinet.profile

import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.fragment_cabinet.*
import kz.maltabu.app.maltabukz.R
import java.lang.ref.WeakReference
import java.util.*

class EditAdFragment : Fragment() {

    companion object {
        fun newInstance() = EditAdFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_ad, container, false)
    }
}
