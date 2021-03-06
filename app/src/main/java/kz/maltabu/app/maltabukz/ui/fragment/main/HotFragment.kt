package kz.maltabu.app.maltabukz.ui.fragment.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.RequestManager
import io.paperdb.Paper
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_hot.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.models.response.Ad
import kz.maltabu.app.maltabukz.network.models.response.ResponseAds
import kz.maltabu.app.maltabukz.network.models.response.ResponseBanner
import kz.maltabu.app.maltabukz.ui.activity.BaseActivity
import kz.maltabu.app.maltabukz.ui.activity.ContestActivity
import kz.maltabu.app.maltabukz.ui.activity.MainActivity
import kz.maltabu.app.maltabukz.ui.activity.ShowAdActivity
import kz.maltabu.app.maltabukz.ui.adapter.HotAdAdapter
import kz.maltabu.app.maltabukz.utils.customEnum.Status
import kz.maltabu.app.maltabukz.vm.HotViewModel
import org.koin.android.ext.android.inject
import org.koin.core.inject


class HotFragment : Fragment(), HotAdAdapter.ChooseAd {

    private val glideManager: RequestManager by inject()

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
        viewModel= ViewModelProviders.of(this, HotViewModel.ViewModelFactory(Paper.book().read((activity!! as BaseActivity).enum.LANG, (activity!! as BaseActivity).enum.KAZAKH)))
            .get(HotViewModel::class.java)
        adapter=HotAdAdapter(activity!!, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.mainResponse().observe(viewLifecycleOwner, Observer {
            consumeResponse(it)
        })
        viewModel.getBannerResponse().observe(viewLifecycleOwner, Observer {
            consumeBannerResponse(it)
        })
        setAdapterSettings()
        viewModel.getAds()
        viewModel.getBanners()
        (activity as MainActivity).hottitle.setText(R.string.hotTitle)
        (activity as MainActivity).hideFilter()
        button_contest.setOnClickListener {
            activity!!.startActivity(Intent(activity, ContestActivity::class.java))
        }
    }

    private fun consumeBannerResponse(response: ApiResponse) {
        when (response.status) {
            Status.LOADING -> {
                showProgress()
            }
            Status.SUCCESS -> {
                hideProgress()
                renderBannerResponse(response.data!!.body() as ResponseBanner)
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

    private fun renderBannerResponse(responseBanner: ResponseBanner) {
        val picture = responseBanner.mainBanner.picture
        if(picture.endsWith("gif")){
            glideManager.asGif().load(picture).into(banner)
        } else {
            glideManager.load(picture).into(banner)
        }
        banner.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(responseBanner.mainBanner.link))
            startActivity(intent)
        }
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
        if((activity as MainActivity).checker.isNetworkAvailable) {
            val intent = Intent(activity, ShowAdActivity::class.java)
            intent.putExtra("ad", ad)
            startActivity(intent)
        } else {
            (activity as MainActivity).noInternetActivity()
        }
    }

}
