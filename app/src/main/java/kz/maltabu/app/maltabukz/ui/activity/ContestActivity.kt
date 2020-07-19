package kz.maltabu.app.maltabukz.ui.activity

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.redmadrobot.inputmask.MaskedTextChangedListener
import io.paperdb.Paper
import kotlinx.android.synthetic.main.dialog_choose_catalog.*
import kotlinx.android.synthetic.main.dialog_choose_city.*
import kotlinx.android.synthetic.main.dialog_contest_success.*
import kotlinx.android.synthetic.main.fragment_contest.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.models.response.City
import kz.maltabu.app.maltabukz.network.models.response.Region
import kz.maltabu.app.maltabukz.network.models.response.ResponseContest
import kz.maltabu.app.maltabukz.network.models.response.ResponseRegion
import kz.maltabu.app.maltabukz.ui.adapter.RegionAdapter
import kz.maltabu.app.maltabukz.utils.FormatHelper
import kz.maltabu.app.maltabukz.utils.customEnum.Status
import kz.maltabu.app.maltabukz.vm.ContestViewModel
import org.koin.android.ext.android.inject

class ContestActivity :  BaseActivity(), RegionAdapter.ChooseRegion{
    private lateinit var dialog: Dialog
    private var regionId=0
    private var phoneNumber=""
    private var surname=""
    private var cityId=0
    private lateinit var viewModel: ContestViewModel
    private val regions: ArrayList<Region> = ArrayList()

    private val formatHelper: FormatHelper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_contest)
        dialog= Dialog(this)
        viewModel = ViewModelProviders.of(this, ContestViewModel.ViewModelFactory(Paper.book().read(enum.LANG, enum.KAZAKH)))
            .get(ContestViewModel::class.java)
        setListeners()
        viewModel.mainRegionsResponse().observe(this, Observer { consumeRegionResponse(it) })
        viewModel.mainResponse().observe(this, Observer { consumeResponse(it) })
        viewModel.getRegions()
    }

    private fun consumeResponse(response: ApiResponse) {
        when (response.status) {
            Status.LOADING -> {
                showLoader()
            }
            Status.SUCCESS -> {
                hideLoader()
                Log.d("TAGg", "ok")
                showSuccessDialog((response.data!!.body() as ResponseContest).code.toString())
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

    private fun setListeners() {
        contest_region.setOnClickListener {
            showRegionsDialog(regions)
        }
        go_contest.setOnClickListener {
            goContest()
        }
        val listener =
            MaskedTextChangedListener.installOn(
                contest_phone,
                "+7-([000])-[000]-[00]-[00]",
                object : MaskedTextChangedListener.ValueListener {
                    override fun onTextChanged(
                        maskFilled: Boolean,
                        extractedValue: String,
                        formattedValue: String
                    ) {
                        phoneNumber = if (maskFilled) {
                            formatHelper.removeInvelidSymbols(formattedValue)
                        } else {
                            ""
                        }
                    }
                }
            )
        contest_phone.hint = listener.placeholder()
    }

    private fun goContest(){
        if(validateFields()){
//            Toast.makeText(this, "${cityId.toString()}  ${regionId.toString()}", Toast.LENGTH_LONG).show()
            viewModel.doContest(phoneNumber, contest_first_name.text.toString(), contest_last_name.text.toString(),
                surname, regionId, cityId)
        }
    }

    private fun validateFields(): Boolean {
        if(contest_sur_name.text.toString()!=null && contest_sur_name.text.toString().isNotEmpty()){
            surname = contest_sur_name.text.toString()
        }
        if(phoneNumber.isEmpty()){
            Toast.makeText(this, resources.getString(R.string.empty_fields), Toast.LENGTH_LONG).show()
            return false
        } else {
            if(contest_first_name.text.toString()==null && contest_first_name.text.toString().isEmpty()){
                Toast.makeText(this, resources.getString(R.string.empty_fields), Toast.LENGTH_LONG).show()
                return false
            } else {
                return if(contest_last_name.text.toString()==null && contest_last_name.text.toString().isEmpty()){
                    Toast.makeText(this, resources.getString(R.string.empty_fields), Toast.LENGTH_LONG).show()
                    false
                } else {
                    if(cityId==0){
                        Toast.makeText(this, resources.getString(R.string.empty_fields), Toast.LENGTH_LONG).show()
                        false
                    } else {
                        true
                    }
                }
            }
        }
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
        regions.addAll(response.regions)
    }

    private fun showRegionsDialog(regions: List<Region>) {
        dialog.setContentView(R.layout.dialog_choose_region)
        val adapter = RegionAdapter(this)
        adapter.setData(regions)
        dialog.catalogs_recycler.adapter = adapter
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    fun showLoader(){
        progress_contest_view.visibility= View.VISIBLE
    }

    fun hideLoader(){
        progress_contest_view.visibility= View.GONE
    }

    override fun onDestroy() {
        if(dialog.isShowing){
            dialog.dismiss()
        }
        super.onDestroy()
    }

    override fun chooseRegion(region: Region) {
        if (region.cities!=null){
            regionId = region.id
            dialog.dismiss()
            showCityDialog(region.cities)
        } else {
            regionId = region.id
            cityId = region.id
            contest_region.text = region.name
            dialog.dismiss()
        }
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
        val adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, citiesNameList)
        dialog.auto_complete.setAdapter(adapter)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun chooseCity(city: City) {
        cityId=city.id
        contest_region.text = city.name
    }

    private fun showSuccessDialog(code: String){
        dialog.setContentView(R.layout.dialog_contest_success)
        dialog.text_code.text = code
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(true)
        dialog.setOnCancelListener {
            dialog.dismiss()
            finish()
        }
        dialog.show()
    }
}
