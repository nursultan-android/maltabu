package kz.maltabu.app.maltabukz.ui.fragment

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
import kotlinx.android.synthetic.main.fragment_catalog.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.model.QueryPaginationModel
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.models.response.Ad
import kz.maltabu.app.maltabukz.network.models.response.ResponseAds
import kz.maltabu.app.maltabukz.ui.adapter.AdAdapter
import kz.maltabu.app.maltabukz.utils.customEnum.ApiLangEnum
import kz.maltabu.app.maltabukz.utils.customEnum.Keys
import kz.maltabu.app.maltabukz.utils.customEnum.Status
import kz.maltabu.app.maltabukz.utils.views.EndlessListener
import kz.maltabu.app.maltabukz.vm.CatalogViewModel

class CatalogFragment(private val id: Int?) : Fragment() {
    var pageNumber=1
    lateinit var viewModel: CatalogViewModel
    lateinit var adapter: AdAdapter
    private val adList = ArrayList<Ad>()

    companion object {
        fun newInstance(id: Int) = CatalogFragment(id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, CatalogViewModel.ViewModelFactory(Paper.book().read(Keys.LANG.constantKey, ApiLangEnum.KAZAKH.constantKey)))
            .get(CatalogViewModel::class.java)
        adapter= AdAdapter(activity!!)
        adapter.setData(adList)
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
        ad_recycler.adapter = adapter
        addListener()
    }

    private fun addListener(){
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
            Status.LOADING -> {}
            Status.SUCCESS -> {
                renderResponse(response.data!!.body() as ResponseAds)
            }
            Status.ERROR -> {
                Log.d("TAGg", response.error.toString())
            }
            Status.THROWABLE -> {
                Log.d("TAGg", response.throwabl.toString())
            }
        }
    }

    private fun renderResponse(response: ResponseAds) {
        adList.addAll(response.adList)
        adapter.notifyDataSetChanged()
    }

    fun getQuery():QueryPaginationModel{
        Paper.book().delete(Keys.SORT.constantKey)
        val sort = Paper.book().read(Keys.SORT.constantKey, "date")
        return QueryPaginationModel(page = pageNumber, category = id, order = sort)
    }


}
