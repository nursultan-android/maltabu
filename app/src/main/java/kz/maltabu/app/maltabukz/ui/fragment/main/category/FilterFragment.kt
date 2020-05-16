package kz.maltabu.app.maltabukz.ui.fragment.main.category

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import io.paperdb.Paper
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_fiter.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.model.FilterBody
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.models.response.City
import kz.maltabu.app.maltabukz.network.models.response.Region
import kz.maltabu.app.maltabukz.network.models.response.ResponseRegion
import kz.maltabu.app.maltabukz.ui.activity.BaseActivity
import kz.maltabu.app.maltabukz.ui.activity.MainActivity
import kz.maltabu.app.maltabukz.ui.adapter.RegionAdapter
import kz.maltabu.app.maltabukz.utils.FormatHelper
import kz.maltabu.app.maltabukz.utils.customEnum.Status
import kz.maltabu.app.maltabukz.vm.FilterViewModel
import org.koin.android.ext.android.inject

class FilterFragment : Fragment(), RegionAdapter.ChooseRegion{

    private lateinit var viewModel: FilterViewModel
    private lateinit var filter: FilterBody
    private lateinit var sortDialog: Dialog
    private var cities: List<City>? = null
    private val formatHelper: FormatHelper by inject()

    companion object {
        fun newInstance() =
            FilterFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fiter, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, FilterViewModel.ViewModelFactory(Paper.book().read((activity!! as BaseActivity).enum.LANG, (activity!! as BaseActivity).enum.KAZAKH)))
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

    private fun setcityAdapter(){
        val citiesNameList= ArrayList<String>()
        for(element in cities!!){
            citiesNameList.add(element.name)
        }
        val adapter: ArrayAdapter<String> = ArrayAdapter(activity!!, android.R.layout.simple_list_item_1, citiesNameList)
        citySpin.setAdapter(adapter)
    }

    private fun setListeners(){
        regSpin.setOnClickListener {
            viewModel.getRegions()
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
            } else {
                clearFilter()
            }
            val fragment = (activity as MainActivity).getCatalogFragment() as CatalogFragment
            fragment.resetDataAndGetAds()
            Log.d("TAGg", filter.city_id.toString()+" "+filter.region_id.toString())
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
                citySpin.setText(oldFilter.cityName)
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
        citySpin.setText("")
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
        if(citySpin.text.toString()!=null && citySpin.text.toString().isNotEmpty() && cities!=null){
            chooseCity(formatHelper.getCityByName(cities!!, citySpin.text.toString()))
            match=true
        }
        if(from_price.text!=null && from_price.text.toString().isNotEmpty()){
            try {
                filter.price_from=from_price.text.toString().toInt()
            } catch (e:Exception){
                Log.d("TAGg", e.message)
            }
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
            Log.d("TAGg", filter.region_id.toString())
            cities=region.cities
            citySpin.isEnabled=true
            setcityAdapter()
        } else {
            cities=null
            citySpin.isEnabled=false
            citySpin.setText(region.name)
            filter.region_id=region.id
            filter.city_id=null
            filter.cityName = region.name
            filter.regionName = region.name
        }
        sortDialog.dismiss()
    }

    private fun chooseCity(city: City) {
        citySpin.setText(city.name)
        filter.region_id=city.region_id
        filter.city_id=city.id
        filter.cityName = city.name
        sortDialog.dismiss()
    }

    override fun onDestroy() {
        if(sortDialog!=null && sortDialog.isShowing){
            sortDialog.dismiss()
        }
        super.onDestroy()
    }
}
