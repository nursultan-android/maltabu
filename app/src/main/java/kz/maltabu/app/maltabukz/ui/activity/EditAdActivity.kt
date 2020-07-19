package kz.maltabu.app.maltabukz.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_new_ad.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.models.response.*
import kz.maltabu.app.maltabukz.ui.adapter.MenuAdapter
import kz.maltabu.app.maltabukz.ui.fragment.newAd.NewAdFragment
import kz.maltabu.app.maltabukz.utils.customEnum.Status
import kz.maltabu.app.maltabukz.vm.EditAdViewModel

public class EditAdActivity :  BaseActivity(), MenuAdapter.ChooseCategory {
    private lateinit var viewModel: EditAdViewModel
    var cityID = 0
    var regionID = 0
    var categoryID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, EditAdViewModel.ViewModelFactory(Paper.book().read(enum.LANG, enum.KAZAKH))).get(EditAdViewModel::class.java)
        setContentView(R.layout.activity_new_ad)
        setListeners()
        viewModel.catResponse().observe(this, Observer {
            consumeCategoryResponse(it)
        })
        viewModel.mainResponse().observe(this, Observer {
            consumeMainResponse(it)
        })
        viewModel.getCategories()
    }

    private fun consumeMainResponse(response: ApiResponse) {
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

    private fun renderRegionResponse(responseRegion: ResponseRegion) {
        val ad = (intent.getSerializableExtra("ad") as Ad)
        if(ad.region==null){
            val reg = getRegionByCityName(responseRegion.regions,ad.city)
            cityID = reg!!.id
            regionID = reg!!.id
        } else {
            if(ad.city!=null){
                val city = getRegionByName(responseRegion.regions,ad.region, ad.city)
                cityID = city!!.id
                regionID = city!!.region_id
            }
        }
        setFragment(NewAdFragment.newInstance(cityID, regionID, categoryID))
    }

    private fun getRegionByName(regions: List<Region>, regionName: String, cityName: String): City? {
        for(i in regions.indices){
            if(regions[i].name==regionName){
                if(regions[i].cities!=null){
                    for(j in regions[i].cities.indices) {
                        if(regions[i].cities[j].name==cityName){
                            return regions[i].cities[j]
                        }
                    }
                }
            }
        }
        return null
    }

    private fun getRegionByCityName(regions: List<Region>, cityName: String): Region? {
        for(i in regions.indices){
            if(regions[i].name==cityName){
                return regions[i]
            }
        }
        return null
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

    private fun renderCategoryResponse(responseCategories: ResponseCategories) {
        categoryID = getCategoryIdByName(responseCategories.categoriesList, (intent.getSerializableExtra("ad") as Ad).category)!!.id
        viewModel.getRegions()
    }

    private fun getCategoryIdByName(categoriesList: List<MenuCategory>, category: String): CategoryChild? {
        for(i in categoriesList.indices){
            if(categoriesList[i].categoryChildList!=null){
                for(j in 0 until categoriesList[i].categoryChildList.size){
                    if(categoriesList[i].categoryChildList[j].name==category){
                        return categoriesList[i].categoryChildList[j]
                    }
                }
            }
        }
        return null
    }

    private fun setListeners() {
        toolbar_title.text = resources.getString(R.string.cab01)
        back_arrow.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setFragment(fragment: Fragment) {
        ft = manager.beginTransaction()
        ft.replace(R.id.content, fragment).commit()
    }

    fun showLoader(){
        progress_view.visibility= View.VISIBLE
    }

    fun hideLoader(){
        progress_view.visibility= View.GONE
    }

    fun clearBackStack(){
        for (i in 0 until manager.backStackEntryCount) {
            manager.popBackStack()
        }
    }

    override fun onDestroy() {
        hideLoader()
        super.onDestroy()
    }


}
