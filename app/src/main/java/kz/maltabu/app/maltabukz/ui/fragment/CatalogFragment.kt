package kz.maltabu.app.maltabukz.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yandex.mobile.ads.AdRequestError
import com.yandex.mobile.ads.nativeads.NativeAdLoader
import com.yandex.mobile.ads.nativeads.NativeAppInstallAd
import com.yandex.mobile.ads.nativeads.NativeContentAd
import com.yandex.mobile.ads.nativeads.NativeImageAd
import io.paperdb.Paper
import kotlinx.android.synthetic.main.fragment_catalog.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.model.QueryPaginationModel
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.models.response.Ad
import kz.maltabu.app.maltabukz.network.models.response.ResponseAds
import kz.maltabu.app.maltabukz.ui.activity.ShowAdActivity
import kz.maltabu.app.maltabukz.ui.adapter.AdAdapterWithAdvers
import kz.maltabu.app.maltabukz.utils.customEnum.ApiLangEnum
import kz.maltabu.app.maltabukz.utils.customEnum.Keys
import kz.maltabu.app.maltabukz.utils.customEnum.Status
import kz.maltabu.app.maltabukz.utils.views.EndlessListener
import kz.maltabu.app.maltabukz.utils.yandexAds.AdapterHolder
import kz.maltabu.app.maltabukz.utils.yandexAds.YandexHelper
import kz.maltabu.app.maltabukz.vm.CatalogViewModel

class CatalogFragment(private val id: Int?) : Fragment(), AdAdapterWithAdvers.ChooseAd {
    var pageNumber=1
    lateinit var viewModel: CatalogViewModel
    lateinit var adapterWithAd: AdAdapterWithAdvers
    lateinit var yandexHelper: YandexHelper
    private var adIndex=5
    private var mData = ArrayList<Pair<Int, Any>>()

//    companion object {
//        fun newInstance(id: Int) = CatalogFragment(id)
//    }
    fun newInstance(id: Int) = CatalogFragment(id)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, CatalogViewModel.ViewModelFactory(Paper.book().read(Keys.LANG.constantKey, ApiLangEnum.KAZAKH.constantKey)))
            .get(CatalogViewModel::class.java)
        adapterWithAd= AdAdapterWithAdvers(activity!!,this)
        adapterWithAd.setData(mData)
        yandexHelper= YandexHelper(activity!!)
        yandexHelper.createNativeAdLoader(nativeAdLoadListener)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_catalog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.mainResponse().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            consumeResponse(it)
        })
        viewModel.getAds(getQuery())
        ad_recycler.adapter = adapterWithAd
        addListener()
    }

    private fun addListener(){
        swipe_lay.setOnRefreshListener {
            resetData()
            viewModel.getAds(getQuery())
        }

        ad_recycler.addOnScrollListener(
            object : EndlessListener(ad_recycler.layoutManager as LinearLayoutManager){
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView){
                    pageNumber++
                    viewModel.getAds(getQuery())
                }
            }
        )
    }

    private fun consumeResponse(response: ApiResponse) {
        when (response.status) {
            Status.LOADING -> {
                showProgress()
            }
            Status.SUCCESS -> {
                hideProgress()
                swipe_lay.isRefreshing=false
                renderResponse(response.data!!.body() as ResponseAds)
            }
            Status.ERROR -> {
                hideProgress()
                swipe_lay.isRefreshing=false
                Log.d("TAGg", response.error.toString())
            }
            Status.THROWABLE -> {
                hideProgress()
                swipe_lay.isRefreshing=false
                Log.d("TAGg", response.throwabl.toString())
            }
        }
    }

    private fun renderResponse(response: ResponseAds) {
        addToPair(response.adList)
        if(mData.isNotEmpty()){
            setHasAd()
            adapterWithAd.notifyDataSetChanged()
            adapterWithAd.setData(mData)
        } else {
            setNoAd()
        }
    }

    private fun setNoAd() {
        no_ads_image.visibility=View.VISIBLE
        no_ads_text.visibility=View.VISIBLE
    }

    private fun setHasAd() {
        no_ads_image.visibility=View.GONE
        no_ads_text.visibility=View.GONE
    }

    private fun showProgress(){
        progress_hor.visibility=View.VISIBLE
    }

    private fun hideProgress(){
        progress_hor.visibility=View.INVISIBLE
    }

    override fun chooseAd(ad: Ad) {
        val intent = Intent(activity, ShowAdActivity::class.java)
        intent.putExtra("ad", ad)
        startActivity(intent)
    }

    private fun resetData() {
        adIndex=5
        mData.clear()
        pageNumber=1
    }

    fun getQuery():QueryPaginationModel{
        Paper.book().delete(Keys.SORT.constantKey)
        val sort = Paper.book().read(Keys.SORT.constantKey, "date")
        return QueryPaginationModel(page = pageNumber, category = id, order = sort)
    }

    private fun addToPair(list: List<Ad>){
        if(list.isNotEmpty())
            for (i in list.indices) {
                mData.add(Pair(AdapterHolder.BlockContentProvider.DEFAULT, list[i]))
                if(i%5==4){
                    loadAd()
                }
            }
    }

    private fun loadAd(){
        yandexHelper.loadAd()
    }

    private val nativeAdLoadListener = object : NativeAdLoader.OnImageAdLoadListener {
        override fun onAppInstallAdLoaded(nativeAppInstallAd: NativeAppInstallAd) {
            fillData(Pair(AdapterHolder.BlockContentProvider.NATIVE_BANNER, nativeAppInstallAd))
        }

        override fun onContentAdLoaded(nativeContentAd: NativeContentAd) {
            fillData(Pair(AdapterHolder.BlockContentProvider.NATIVE_BANNER, nativeContentAd))
        }

        override fun onImageAdLoaded(nativeImageAd: NativeImageAd) {
            fillData(Pair(AdapterHolder.BlockContentProvider.NATIVE_BANNER, nativeImageAd))
        }

        override fun onAdFailedToLoad(error: AdRequestError) {
            Log.d("SAMPLE_TAG", error.description)
        }

        private fun fillData(nativeAd: Pair<Int, Any>) {
            if (adIndex<mData.size) {
                mData.add(adIndex, nativeAd)
                adIndex += 6
                adapterWithAd.notifyDataSetChanged()
                adapterWithAd.setData(mData)
            }
        }
    }

}
