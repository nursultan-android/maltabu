package kz.maltabu.app.maltabukz.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import io.paperdb.Paper
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_hot.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.models.response.Ad
import kz.maltabu.app.maltabukz.network.models.response.ResponseAds
import kz.maltabu.app.maltabukz.ui.activity.MainActivity
import kz.maltabu.app.maltabukz.ui.activity.ShowAdActivity
import kz.maltabu.app.maltabukz.ui.adapter.HotAdAdapter
import kz.maltabu.app.maltabukz.utils.customEnum.ApiLangEnum
import kz.maltabu.app.maltabukz.utils.customEnum.Keys
import kz.maltabu.app.maltabukz.utils.customEnum.Status
import kz.maltabu.app.maltabukz.vm.HotViewModel


class HotFragment : Fragment(), HotAdAdapter.ChooseAd {

    companion object {
        fun newInstance() = HotFragment()
    }

    private lateinit var viewModel: HotViewModel
    private lateinit var adapter: HotAdAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_hot, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel= ViewModelProviders.of(this, HotViewModel.ViewModelFactory(Paper.book().read(Keys.LANG.constantKey, ApiLangEnum.KAZAKH.constantKey)))
            .get(HotViewModel::class.java)
        adapter=HotAdAdapter(activity!!, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.mainResponse().observe(viewLifecycleOwner, Observer {
            consumeResponse(it)
        })
        setAdapterSettings()
        viewModel.getAds()
        (activity as MainActivity).hottitle.setText(R.string.hotTitle)
        (activity as MainActivity).hideFilter()
    }

    private fun setAdapterSettings() {
        val manager = StaggeredGridLayoutManager(2, 1)
        manager.gapStrategy = 2
        hots_recycler.layoutManager = manager
        hots_recycler.adapter=adapter
    }

    private fun consumeResponse(response: ApiResponse) {
        when (response.status) {
            Status.LOADING -> {
                showProgress()
            }
            Status.SUCCESS -> {
                hideProgress()
                renderResponse(response.data!!.body() as ResponseAds)
            }
            Status.ERROR -> {
                hideProgress()
                Log.d("TAGg", response.error.toString())
            }
            Status.THROWABLE -> {
                hideProgress()
                Log.d("TAGg", response.throwabl.toString())
            }
        }
    }

    private fun renderResponse(response: ResponseAds) {
        adapter.setData(response.adList)
    }

    private fun showProgress(){
        (activity as MainActivity).showLoader()
    }

    private fun hideProgress(){
        (activity as MainActivity).hideLoader()
    }

    override fun chooseAd(ad: Ad) {
        val intent = Intent(activity, ShowAdActivity::class.java)
        intent.putExtra("ad", ad)
        startActivity(intent)
    }

}
