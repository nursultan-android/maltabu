package kz.maltabu.app.maltabukz.ui.fragment.main.category

import android.app.Dialog
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
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.dialog_sort.*
import kotlinx.android.synthetic.main.fragment_catalog.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.model.QueryPaginationModel
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.models.response.Ad
import kz.maltabu.app.maltabukz.network.models.response.MenuCategory
import kz.maltabu.app.maltabukz.network.models.response.ResponseAds
import kz.maltabu.app.maltabukz.ui.activity.BaseActivity
import kz.maltabu.app.maltabukz.ui.activity.MainActivity
import kz.maltabu.app.maltabukz.ui.activity.ShowAdActivity
import kz.maltabu.app.maltabukz.ui.adapter.AdAdapterWithAdvers
import kz.maltabu.app.maltabukz.utils.customEnum.Status
import kz.maltabu.app.maltabukz.utils.views.EndlessListener
import kz.maltabu.app.maltabukz.utils.yandexAds.AdapterHolder
import kz.maltabu.app.maltabukz.utils.yandexAds.YandexHelper
import kz.maltabu.app.maltabukz.vm.CatalogViewModel

class CatalogFragment : Fragment(), AdAdapterWithAdvers.ChooseAd {
    var pageNumber=1
    var viewModel: CatalogViewModel?=null
    var adapterWithAd: AdAdapterWithAdvers?=null
    var yandexHelper: YandexHelper?=null
    private var adIndex=5
    private var mData = ArrayList<Pair<Int, Any>>()
    private lateinit var dialog : Dialog
    private var id: Int?=null
    private var currentCatalog: Int=0

    companion object {
        fun newInstance(id: Int, position: Int): CatalogFragment{
            val fragment = CatalogFragment()
            val bundle = Bundle()
            bundle.putInt("categoryId", id)
            bundle.putInt("pos", position)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, CatalogViewModel.ViewModelFactory(Paper.book().read((activity!! as BaseActivity).enum.LANG, (activity!! as BaseActivity).enum.KAZAKH)))
            .get(CatalogViewModel::class.java)
        adapterWithAd= AdAdapterWithAdvers(activity!!,this)
        adapterWithAd!!.setData(mData)
        yandexHelper= YandexHelper(activity!!)
        yandexHelper!!.createNativeAdLoader(nativeAdLoadListener)
        dialog= Dialog(activity!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_catalog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel!!.mainResponse().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            consumeResponse(it)
        })
        id=arguments!!.getInt("categoryId")
        currentCatalog=arguments!!.getInt("pos")
        ad_recycler.adapter = adapterWithAd
        addListener()
        setSortInitState()
        if((activity as MainActivity).checker.isNetworkAvailable) {
            viewModel!!.getAds(getQuery())
        } else {
            (activity as MainActivity).noInternetActivity()
        }
    }

    private fun addListener(){
        swipe_lay.setOnRefreshListener {
            if((activity as MainActivity).checker.isNetworkAvailable) {
                resetData()
                viewModel!!.getAds(getQuery())
            } else {
                (activity as MainActivity).noInternetActivity()
            }
        }
        ad_recycler.addOnScrollListener(
            object : EndlessListener(ad_recycler.layoutManager as LinearLayoutManager){
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView){
                    if((activity as MainActivity).checker.isNetworkAvailable) {
                        pageNumber++
                        viewModel!!.getAds(getQuery())
                    } else {
                        (activity as MainActivity).noInternetActivity()
                    }
                }
            }
        )
        (activity as MainActivity).showFilter()
        activity!!.filter_image.setOnClickListener {
            if((activity as MainActivity).checker.isNetworkAvailable) {
                openFilter()
            } else {
                (activity as MainActivity).noInternetActivity()
            }
        }
        activity!!.filter_text.setOnClickListener {
            if((activity as MainActivity).checker.isNetworkAvailable) {
                openFilter()
            } else {
                (activity as MainActivity).noInternetActivity()
            }
        }
        sort.setOnClickListener {
            showSortDialog()
        }
    }

    private fun openFilter(){
        (activity as MainActivity).catalogIndex=currentCatalog
        (activity as MainActivity).setFragmentFilterFragment()
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
            adapterWithAd!!.notifyDataSetChanged()
            adapterWithAd!!.setData(mData)
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
        if((activity as MainActivity).checker.isNetworkAvailable) {
            val intent = Intent(activity, ShowAdActivity::class.java)
            intent.putExtra("ad", ad)
            startActivity(intent)
        } else {
            (activity as MainActivity).noInternetActivity()
        }
    }

    private fun resetData() {
        adIndex=5
        mData.clear()
        pageNumber=1
    }

    fun getQuery():QueryPaginationModel{
        val sort = Paper.book().read((activity!! as BaseActivity).enum.SORT, "date")
        val query = QueryPaginationModel(page = pageNumber, category = id, order = sort)
        if((activity as MainActivity).filer!=null){
            val filterFromAActivity = (activity as MainActivity).filer
            if(filterFromAActivity!!.image_required!=null){
                 query.image_required=filterFromAActivity.image_required
            }
            if(filterFromAActivity.exchange!=null){
                query.exchange=filterFromAActivity.exchange
            }
            if(filterFromAActivity.price_from!=null){
                query.price_from=filterFromAActivity.price_from
            }
            if(filterFromAActivity.price_to!=null){
                query.price_to=filterFromAActivity.price_to
            }
            if(filterFromAActivity.region_id!=null){
                query.region=filterFromAActivity.region_id
            }
            if(filterFromAActivity.city_id!=null && filterFromAActivity.city_id!=0){
                query.city=filterFromAActivity.city_id
            }
        }
        return query
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

    fun resetDataAndGetAds(){
        resetData()
        viewModel!!.getAds(getQuery())
    }

    private fun loadAd(){
        yandexHelper!!.loadAd()
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
                adapterWithAd!!.notifyDataSetChanged()
                adapterWithAd!!.setData(mData)
            }
        }
    }

    private fun setSortInitState(){
        when (Paper.book().read((activity!! as BaseActivity).enum.SORT, "date")){
            "date" -> {
                sort_text.text=resources.getString(R.string.sort1)
            }
            "price" -> {
                sort_text.text=resources.getString(R.string.sort3)
            }
        }
    }

    private fun showSortDialog(){
        dialog.setContentView(R.layout.dialog_sort)
        dialog.newAds.setOnClickListener {
            chooseSort("date")
        }
        dialog.cheap.setOnClickListener {
            chooseSort("price")
        }
        dialog.show()
    }

    private fun chooseSort(type: String){
        Paper.book().write((activity!! as BaseActivity).enum.SORT, type)
        resetDataAndGetAds()
        setSortInitState()
        dialog.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapterWithAd=null
        yandexHelper=null
        mData.clear()
        viewModel!!.clear()
        viewModel=null
    }

    override fun onDestroy() {
        if(dialog!=null && dialog.isShowing){
            dialog.dismiss()
        }
        super.onDestroy()
    }
}
