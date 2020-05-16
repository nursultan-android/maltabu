package kz.maltabu.app.maltabukz.ui.fragment.main

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.paperdb.Paper
import kotlinx.android.synthetic.main.dialog_choose_catalog.catalogs_recycler
import kotlinx.android.synthetic.main.dialog_choose_city.*
import kotlinx.android.synthetic.main.dialog_choose_region.*
import kotlinx.android.synthetic.main.fragment_search.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.model.QueryPaginationModel
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.models.response.*
import kz.maltabu.app.maltabukz.ui.activity.BaseActivity
import kz.maltabu.app.maltabukz.ui.activity.MainActivity
import kz.maltabu.app.maltabukz.ui.activity.ShowAdActivity
import kz.maltabu.app.maltabukz.ui.adapter.*
import kz.maltabu.app.maltabukz.utils.FormatHelper
import kz.maltabu.app.maltabukz.utils.customEnum.Status
import kz.maltabu.app.maltabukz.utils.views.EndlessListener
import kz.maltabu.app.maltabukz.vm.SearchViewModel
import org.koin.android.ext.android.inject


class SearchFragment : Fragment(), AdAdapterWithAdvers.ChooseAd, RegionAdapter.ChooseRegion, CategoryAdapter.ChooseCategory {

    companion object {
        fun newInstance() = SearchFragment()
    }

    private lateinit var viewModel: SearchViewModel
    private lateinit var dialog: Dialog
    private lateinit var adapter : AdAdapter
    private val adList = ArrayList<Ad>()
    private var pageNumber=1
    private var regionId=0
    private var cityId=0
    private var catagoryId=0

    private val formatHelper: FormatHelper by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, SearchViewModel.ViewModelFactory(Paper.book().read((activity!! as BaseActivity).enum.LANG, (activity!! as BaseActivity).enum.KAZAKH)))
            .get(SearchViewModel::class.java)
        adapter = AdAdapter(activity!!, this)
        dialog = Dialog(activity!!)
        adapter.setData(adList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ad_search_recyler.adapter = adapter
        setListeners()
        viewModel.mainResponse().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            consumeResponse(it)
        })
        viewModel.mainRegionsResponse().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            consumeRegionResponse(it)
        })
        viewModel.mainCategoryResponse().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            consumeCategoryResponse(it)
        })
    }

    private fun consumeCategoryResponse(response: ApiResponse) {
        when (response.status) {
            Status.LOADING -> {
                showLoader()
            }
            Status.SUCCESS -> {
                hideLoader()
                renderCategoryResponse(response.data!!.body() as ResponseCategories)
            }
            Status.ERROR -> {
                hideLoader()
                Log.d("TAGg", response.error.toString())
            }
            Status.THROWABLE -> {
                hideLoader()
                Log.d("TAGg", response.throwabl.toString())
            }
        }
    }

    private fun renderCategoryResponse(response: ResponseCategories) {
        showCategoryDialog(response.categoriesList)
    }

    private fun showCategoryDialog(categories: List<MenuCategory>) {
        dialog.setContentView(R.layout.dialog_choose_region)
        val adapter = CategoryAdapter(activity!!, this)
        (categories as ArrayList).add(0, MenuCategory(0, resources.getString(R.string.Option1)))
        adapter.setData(categories)
        dialog.text_view_title.text = resources.getString(R.string.chooseCategoty)
        dialog.catalogs_recycler.adapter = adapter
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun consumeRegionResponse(response: ApiResponse) {
        when (response.status) {
            Status.LOADING -> {
                showLoader()
            }
            Status.SUCCESS -> {
                hideLoader()
                renderRegionResponse(response.data!!.body() as ResponseRegion)
            }
            Status.ERROR -> {
                hideLoader()
                Log.d("TAGg", response.error.toString())
            }
            Status.THROWABLE -> {
                hideLoader()
                Log.d("TAGg", response.throwabl.toString())
            }
        }
    }

    private fun renderRegionResponse(response: ResponseRegion) {
        showRegionsDialog(response.regions)
    }

    private fun showRegionsDialog(regions: List<Region>) {
        dialog.setContentView(R.layout.dialog_choose_region)
        val adapter = RegionAdapter(this)
        (regions as ArrayList).add(0, Region(0, resources.getString(R.string.Option2)))
        adapter.setData(regions)
        dialog.catalogs_recycler.adapter = adapter
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun consumeResponse(response: ApiResponse) {
        when (response.status) {
            Status.LOADING -> {
                if(pageNumber==1)
                    showLoader()
                else
                    progress_hor_search.visibility=View.VISIBLE
            }
            Status.SUCCESS -> {
                if(pageNumber==1)
                    hideLoader()
                else
                    progress_hor_search.visibility=View.GONE
                renderResponse(response.data!!.body() as ResponseAds)
            }
            Status.ERROR -> {
                if(pageNumber==1)
                    hideLoader()
                else
                    progress_hor_search.visibility=View.GONE
                Log.d("TAGg", response.error.toString())
            }
            Status.THROWABLE -> {
                if(pageNumber==1)
                    hideLoader()
                else
                    progress_hor_search.visibility=View.GONE
                Log.d("TAGg", response.throwabl.toString())
            }
        }
    }

    private fun renderResponse(response: ResponseAds) {
        adList.addAll(response.adList)
        adapter.notifyDataSetChanged()
        if(adList.size>0){
            setHasAd()
        } else {
            setNoAd()
        }
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
        adList.clear()
        pageNumber=1
    }

    private fun getQuery(): QueryPaginationModel {
        val query = QueryPaginationModel(page = pageNumber, order = "date")
        if(catagoryId!=0){
            query.category=catagoryId
        }
        if(regionId!=0){
            query.region=regionId
        }
        if(cityId!=0){
            query.city=cityId
        }
        if(search_text!= null && search_text.text.toString().isNotEmpty()){
            query.word= search_text.text.toString()
        }
        return query
    }

    private fun showLoader(){
        (activity as MainActivity).showLoader()
    }

    private fun hideLoader(){
        (activity as MainActivity).hideLoader()
    }

    private fun setNoAd() {
        no_ads_image.visibility=View.VISIBLE
        no_ads_text.visibility=View.VISIBLE
    }

    private fun setHasAd() {
        no_ads_image.visibility=View.GONE
        no_ads_text.visibility=View.GONE
    }

    private fun setListeners(){
        search_button.setOnClickListener {
            if((activity as MainActivity).checker.isNetworkAvailable) {
                resetData()
                Log.d("TAGg", cityId.toString())
                viewModel.getAds(getQuery())
            } else {
                (activity as MainActivity).noInternetActivity()
            }
        }

        choose_region_button.setOnClickListener {
            if((activity as MainActivity).checker.isNetworkAvailable) {
                resetData()
                viewModel.getRegions()
            } else {
                (activity as MainActivity).noInternetActivity()
            }
        }

        choose_category_button.setOnClickListener {
            if((activity as MainActivity).checker.isNetworkAvailable) {
                viewModel.getCategories()
            } else {
                (activity as MainActivity).noInternetActivity()
            }
        }

        close_search.setOnClickListener {
            activity!!.onBackPressed()
        }

        close_search2.setOnClickListener {
            activity!!.onBackPressed()
        }

        toolbar2.setOnClickListener {
            ad_search_recyler.smoothScrollToPosition(0)
        }

        floating_button.setOnClickListener {
            ad_search_recyler.smoothScrollToPosition(0)
        }

        ad_search_recyler.addOnScrollListener(
            object : EndlessListener(ad_search_recyler.layoutManager as LinearLayoutManager){
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView){
                    if((activity as MainActivity).checker.isNetworkAvailable) {
                        pageNumber++
                        viewModel.getAds(getQuery())
                    } else {
                        (activity as MainActivity).noInternetActivity()
                    }
                }
            }
        )
    }

    override fun chooseRegion(region: Region) {
        if (region.cities!=null){
            regionId = region.id
            dialog.dismiss()
            showCityDialog(region.cities)
        } else {
            regionId = region.id
            cityId = 0
            choose_region_button.text = region.name
            dialog.dismiss()
        }
    }

    private fun showCatalogsDialog(childList: List<CategoryChild>) {
        dialog.setContentView(R.layout.dialog_choose_region)
        val adapter = CatalogAdapter(this)
        adapter.setData(childList)
        dialog.text_view_title.text = resources.getString(R.string.chooseCategoryCatalog)
        dialog.catalogs_recycler.adapter = adapter
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun showCityDialog(cityList: List<City>) {
        dialog.setContentView(R.layout.dialog_choose_city)
        val citiesNameList= ArrayList<String>()
        for(element in cityList!!){
            citiesNameList.add(element.name)
        }
        dialog.choose_city_button.setOnClickListener {
            chooseCity(formatHelper.getCityByName(cityList, dialog.auto_complete.text.toString()))
            dialog.dismiss()
        }
        val adapter: ArrayAdapter<String> = ArrayAdapter(activity!!, android.R.layout.simple_list_item_1, citiesNameList)
        dialog.auto_complete.setAdapter(adapter)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    override fun chooseCategory(category: MenuCategory) {
        dialog.dismiss()
        if(category.id==0){
            choose_category_button.text=category.name
            catagoryId=0
        } else {
            showCatalogsDialog(category.categoryChildList)
        }
    }

    override fun chooseCatalog(catalog: CategoryChild) {
        catagoryId=catalog.id
        choose_category_button.text=catalog.name
        dialog.dismiss()
    }

    private fun chooseCity(city: City) {
        cityId=city.id
        choose_region_button.text = city.name
    }

    override fun onDestroy() {
        if(dialog!=null && dialog.isShowing){
            dialog.dismiss()
        }
        super.onDestroy()
    }
}
