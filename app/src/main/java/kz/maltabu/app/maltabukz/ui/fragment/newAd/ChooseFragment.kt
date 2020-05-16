package kz.maltabu.app.maltabukz.ui.fragment.newAd

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
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_new_ad.*
import kotlinx.android.synthetic.main.dialog_choose_catalog.*
import kotlinx.android.synthetic.main.fragment_choose_category.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.models.response.CategoryChild
import kz.maltabu.app.maltabukz.network.models.response.MenuCategory
import kz.maltabu.app.maltabukz.network.models.response.ResponseCategories
import kz.maltabu.app.maltabukz.ui.activity.BaseActivity
import kz.maltabu.app.maltabukz.ui.activity.NewAdActivity
import kz.maltabu.app.maltabukz.ui.adapter.CatalogAdapter
import kz.maltabu.app.maltabukz.ui.adapter.CategoryAdapter
import kz.maltabu.app.maltabukz.utils.customEnum.Status
import kz.maltabu.app.maltabukz.vm.ChooseViewModel

class ChooseFragment : Fragment(), CategoryAdapter.ChooseCategory {

    companion object {
        fun newInstance() = ChooseFragment()
    }

    private lateinit var adapter: CategoryAdapter

    private lateinit var viewModel: ChooseViewModel

    private lateinit var sortDialog: Dialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_choose_category, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, ChooseViewModel.ViewModelFactory(Paper.book().read((activity!! as BaseActivity).enum.LANG, (activity!! as BaseActivity).enum.KAZAKH)))
            .get(ChooseViewModel::class.java)
        sortDialog = Dialog(activity!!)
        adapter = CategoryAdapter(activity!!, this)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setArrowButtonColor()
        viewModel.mainResponse().observe(viewLifecycleOwner, Observer {
            consumeResponse(it)
        })
        viewModel.getCategories()
    }

    private fun consumeResponse(response: ApiResponse) {
        when (response.status) {
            Status.LOADING -> {
                (activity as NewAdActivity).showLoader()
            }
            Status.SUCCESS -> {
                (activity as NewAdActivity).hideLoader()
                renderResponse(response.data!!.body() as ResponseCategories)
            }
            Status.ERROR -> {
                (activity as NewAdActivity).hideLoader()
                Log.d("TAGg", response.error.toString())
            }
            Status.THROWABLE -> {
                (activity as NewAdActivity).hideLoader()
                Log.d("TAGg", response.throwabl.toString())
            }
        }
    }

    private fun renderResponse(response: ResponseCategories){
        adapter.setData(response.categoriesList.sortedBy { it.order })
        categories_recycler.adapter=adapter
    }

    override fun chooseCategory(category: MenuCategory) {
        showDialog(category)
    }

    override fun chooseCatalog(catalog: CategoryChild) {
        sortDialog.dismiss()
        (activity as NewAdActivity).setFragmentToBack(
            NewAdFragment.newInstance(
                catalog.id
            )
        )
    }

    private fun setArrowButtonColor(){
        activity!!.back_arrow.setOnClickListener {
            activity!!.finish()
        }
    }

    private fun showDialog(category: MenuCategory) {
        sortDialog.setContentView(R.layout.dialog_choose_catalog)
        val adapter = CatalogAdapter(this)
        adapter.setData(category.categoryChildList)
        sortDialog.catalogs_recycler.adapter = adapter
        sortDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        sortDialog.show()
    }

    override fun onDestroy() {
        if(sortDialog!=null && sortDialog.isShowing){
            sortDialog.dismiss()
        }
        super.onDestroy()
    }
}
