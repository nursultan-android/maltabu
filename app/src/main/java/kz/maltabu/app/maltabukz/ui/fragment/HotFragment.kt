package kz.maltabu.app.maltabukz.ui.fragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.vm.HotViewModel

class HotFragment : Fragment() {

    companion object {
        fun newInstance() = HotFragment()
    }

    private lateinit var viewModel: HotViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hot, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HotViewModel::class.java)
    }

}
