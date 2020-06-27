package kz.maltabu.app.maltabukz.ui.fragment.main.category

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_category.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.models.response.CategoryChild
import kz.maltabu.app.maltabukz.network.models.response.MenuCategory
import kz.maltabu.app.maltabukz.ui.activity.MainActivity
import kz.maltabu.app.maltabukz.ui.adapter.CatalogTabAdapter
import kz.maltabu.app.maltabukz.vm.CategoryViewModel


class CategoryFragment : Fragment(), CatalogTabAdapter.ChooseCategory {

    private lateinit var adapter: CatalogTabAdapter
    private lateinit var sortDialog: Dialog
    private lateinit var viewModel: CategoryViewModel
    private lateinit var category: MenuCategory

    companion object {
        fun newInstance(catagory: MenuCategory): CategoryFragment{
            val fragment = CategoryFragment()
            val bundle = Bundle()
            bundle.putSerializable("category", catagory)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        category = arguments!!.getSerializable("category") as MenuCategory
        setCategoryTitle()
        setAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CategoryViewModel::class.java)
        sortDialog=Dialog(activity!!)
        adapter= CatalogTabAdapter(activity!!, this)
        try {
            initAds()
        } catch (e:Exception){
            Log.d("TAGg", e.message)
        }
    }

    private fun setCategoryTitle() {
        (activity as MainActivity).hottitle.text= category.name
    }

    private fun setAdapter(){
        val list = category.categoryChildList
        list.sortBy { it.order }
        adapter.setData(list)
        tablayout.adapter = adapter
        selectCatalog((activity as MainActivity).catalogIndex)
    }

    override fun chooseCatalog(position:Int, catalog: CategoryChild) {
        val catalog = CatalogFragment.newInstance(catalog.id, position)
        (activity as MainActivity).setFragmentForCatalog(catalog)
        when(position){
            adapter.itemCount-1 -> {
                scroll(position)
            }
            1 ->{
                scroll(0)
            }
            else -> {
                scroll(position+1)
            }
        }
    }

    private fun initAds(){
        val config = YandexMetricaConfig.newConfigBuilder(resources.getString(R.string.yandex_app_id)).build()
        YandexMetrica.activate(activity!!.applicationContext, config)
        YandexMetrica.enableActivityAutoTracking(activity!!.application)
    }

    private fun scroll(position: Int) {
        Handler().postDelayed({
            tablayout.scrollToPosition(position)
        }, 0)
    }

    private fun selectCatalog(pos: Int){
        val cat = adapter.getData()[pos]
        val catalog = CatalogFragment.newInstance(cat.id, pos)
        (activity as MainActivity).setFragmentForCatalog(catalog)
        when(pos){
            adapter.itemCount-1 -> {
                scroll(pos)
            }
            1 ->{
                scroll(0)
            }
            else -> {
                scroll(pos+1)
            }
        }
    }
}
