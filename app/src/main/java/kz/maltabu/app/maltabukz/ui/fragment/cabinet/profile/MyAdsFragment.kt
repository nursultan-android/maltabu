package kz.maltabu.app.maltabukz.ui.fragment.cabinet.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.paperdb.Paper
import kotlinx.android.synthetic.main.fragment_my_ads.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.models.response.Ad
import kz.maltabu.app.maltabukz.network.models.response.ResponseAds
import kz.maltabu.app.maltabukz.ui.activity.AuthActivity
import kz.maltabu.app.maltabukz.ui.activity.BaseActivity
import kz.maltabu.app.maltabukz.ui.activity.EditAdActivity
import kz.maltabu.app.maltabukz.ui.activity.ShowAdActivity
import kz.maltabu.app.maltabukz.ui.adapter.MyAdAdapter
import kz.maltabu.app.maltabukz.utils.customEnum.Status
import kz.maltabu.app.maltabukz.utils.views.EndlessListener
import kz.maltabu.app.maltabukz.vm.MyAdsViewModel

class MyAdsFragment : Fragment(), MyAdAdapter.ChooseAd{

    private lateinit var viewModel: MyAdsViewModel
    private lateinit var adapter : MyAdAdapter
    private val adList = ArrayList<Ad>()
    var pageNumber= 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, MyAdsViewModel.ViewModelFactory(Paper.book().read((activity!! as BaseActivity).enum.LANG, (activity!! as BaseActivity).enum.KAZAKH)))
            .get(MyAdsViewModel::class.java)
        adapter = MyAdAdapter(activity!!, this)
        adapter.setData(adList)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_ads, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        my_ads_recycler.adapter=adapter
        viewModel.mainResponse().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            consumeReesponse(it)
        })
        resetData()
        setListeners()
    }

    private fun resetData(){
        pageNumber=1
        adList.clear()
        viewModel.getAds(Paper.book().read((activity!! as BaseActivity).enum.TOKEN), pageNumber)
    }

    private fun consumeReesponse(response: ApiResponse) {
        when (response.status) {
            Status.LOADING -> {
                showDialog()
            }
            Status.SUCCESS -> {
                hideDialog()
                renderResponse(response.data!!.body() as ResponseAds)
            }
            Status.ERROR -> {
                hideDialog()
                Log.d("TAGg", response.error.toString())
            }
            Status.THROWABLE -> {
                hideDialog()
                Log.d("TAGg", response.throwabl.toString())
            }
        }
    }

    private fun renderResponse(responseAds: ResponseAds) {
        adList.addAll(responseAds.adList)
        adapter.notifyDataSetChanged()
    }

    private fun showDialog(){
        (activity as AuthActivity).showLoader()
    }

    private fun hideDialog(){
        swipe_refresh_lay.isRefreshing=false
        (activity as AuthActivity).hideLoader()
    }

    private fun setListeners(){
        swipe_refresh_lay.setOnRefreshListener {
            resetData()
        }
        my_ads_recycler.addOnScrollListener(
            object : EndlessListener(my_ads_recycler.layoutManager as LinearLayoutManager){
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView){
                        pageNumber++
                        viewModel.getAds(Paper.book().read((activity!! as BaseActivity).enum.TOKEN), pageNumber)
                }
            }
        )
    }

    override fun chooseAd(ad: Ad) {
        val intent = Intent(activity, ShowAdActivity::class.java)
        intent.putExtra("ad", ad)
        startActivity(intent)
    }

    override fun editAd(ad: Ad) {
        val intent = Intent(activity, EditAdActivity::class.java)
        intent.putExtra("ad", ad)
        startActivity(intent)
    }
}
