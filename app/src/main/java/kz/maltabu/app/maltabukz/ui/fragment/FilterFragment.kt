package kz.maltabu.app.maltabukz.ui.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import io.paperdb.Paper
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.dialog_choose_region.*
import kotlinx.android.synthetic.main.fragment_fiter.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.model.FilterBody
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.models.response.City
import kz.maltabu.app.maltabukz.network.models.response.Region
import kz.maltabu.app.maltabukz.network.models.response.ResponseRegion
import kz.maltabu.app.maltabukz.ui.activity.MainActivity
import kz.maltabu.app.maltabukz.ui.adapter.CityAdapter
import kz.maltabu.app.maltabukz.ui.adapter.RegionAdapter
import kz.maltabu.app.maltabukz.utils.customEnum.ApiLangEnum
import kz.maltabu.app.maltabukz.utils.customEnum.Keys
import kz.maltabu.app.maltabukz.utils.customEnum.Status
import kz.maltabu.app.maltabukz.vm.FilterViewModel

class FilterFragment : Fragment(), RegionAdapter.ChooseRegion, CityAdapter.ChooseCity {

    private lateinit var viewModel: FilterViewModel
    private lateinit var filter: FilterBody
    private lateinit var sortDialog: Dialog
    private var cities: List<City>? = null

    companion object {
        fun newInstance() = FilterFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fiter, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, FilterViewModel.ViewModelFactory(Paper.book().read(Keys.LANG.constantKey, ApiLangEnum.KAZAKH.constantKey)))
            .get(FilterViewModel::class.java)
        filter = FilterBody()
        sortDialog = Dialog(activity!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.mainResponse().observe(viewLifecycleOwner, Observer { consumeResponse(it) })
        setListeners()
        setInitState()
    }

    private fun consumeResponse(response: ApiResponse) {
        when (response.status) {
            Status.LOADING -> {
                showLoader()
            }
            Status.SUCCESS -> {
                hideLoader()
                renderResponse(response.data!!.body() as ResponseRegion)
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

    private fun renderResponse(response: ResponseRegion){
        showRegionsDialog(response.regions)
    }

    private fun showRegionsDialog(regions: List<Region>) {
        sortDialog.setContentView(R.layout.dialog_choose_region)
        val adapter = RegionAdapter(this)
        adapter.setData(regions)
        val rec = sortDialog.findViewById<RecyclerView>(R.id.catalogs_recycler)
        rec.adapter = adapter
        sortDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        sortDialog.show()
    }

    private fun showCitiesDialog(cities: List<City>) {
        sortDialog.setContentView(R.layout.dialog_choose_region)
        val adapter = CityAdapter(this)
        adapter.setData(cities)
        sortDialog.text_view_title!!.text = resources.getString(R.string.chooseCity)
        sortDialog.catalogs_recycler.adapter = adapter
        sortDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        sortDialog.show()
    }

    private fun setListeners(){
        regSpin.setOnClickListener {
            viewModel.getRegions()
        }

        citySpin.setOnClickListener {
            if(cities!=null){
                showCitiesDialog(cities!!)
            }
        }

        clear_filter.setOnClickListener {
            clearFilter()
        }

        close_filter.setOnClickListener {
            activity!!.onBackPressed()
        }

        show_filter_result.setOnClickListener {
            if(addToBody()) {
                (activity as MainActivity).filer = filter
                (activity as MainActivity).filter_badge.visibility = View.VISIBLE
                val fragment = (activity as MainActivity).getCatalogFragment() as CatalogFragment
                fragment.resetDataAndGetAds()
            } else {
                clearFilter()
            }
            activity!!.onBackPressed()
        }
    }

    private fun setInitState(){
        regSpin.text = resources.getString(R.string.chooseRegion)
        val oldFilter = (activity as MainActivity).filer
        if(oldFilter!=null){
            if(oldFilter.image_required!=null){
                checkBox_only_photo.isChecked=true
            }
            if(oldFilter.exchange!=null && oldFilter.exchange==1){
                checkBox_exchange.isChecked=true
            }
            if(oldFilter.price_from!=null){
                from_price.setText(oldFilter.price_from.toString())
            }
            if(oldFilter.price_to!=null){
                to_price.setText(oldFilter.price_to.toString())
            }
            if(oldFilter.regionName!=null){
                regSpin.text = oldFilter.regionName
            }
            if(oldFilter.cityName!=null){
                citySpin.text = oldFilter.cityName
            }
        }
    }

    private fun showLoader(){
        (activity as MainActivity).showLoader()
    }

    private fun hideLoader(){
        (activity as MainActivity).hideLoader()
    }

    private fun clearFilter(){
        checkBox_only_photo.isChecked =false
        checkBox_exchange.isChecked =false
        from_price.setText("")
        to_price.setText("")
        regSpin.text=""
        citySpin.text=""
        (activity as MainActivity).filer = null
        (activity as MainActivity).filter_badge.visibility=View.GONE
    }

    private fun addToBody(): Boolean{
        var match = false
        if(checkBox_only_photo.isChecked){
            filter.image_required=true
            match=true
        }
        if(checkBox_exchange.isChecked){
            filter.exchange=1
            match=true
        } else {
            filter.exchange=0
        }
        if(from_price.text!=null && from_price.text.toString().isNotEmpty()){
            filter.price_from=from_price.text.toString().toInt()
            match=true
        }
        if(to_price.text!=null && to_price.text.toString().isNotEmpty()){
            filter.price_to=to_price.text.toString().toInt()
            match=true
        }
        if(filter.region_id!=null){
            match=true
        }
        return match
    }

    override fun chooseRegion(region: Region) {
        regSpin.text=region.name
        if(region.cities!=null) {
            filter.region_id=region.id
            filter.city_id=null
        } else {
            citySpin.text=region.name
            filter.region_id=region.id
            filter.city_id=region.id
            filter.cityName = region.name
            filter.regionName = region.name
        }
        sortDialog.dismiss()
    }

    override fun chooseCity(city: City) {
        citySpin.text=city.name
        filter.region_id=city.region_id
        filter.city_id=city.id
        filter.cityName = city.name
        sortDialog.dismiss()
    }
}
