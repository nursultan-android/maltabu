package kz.maltabu.app.maltabukz.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_category.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.models.response.CategoryChild
import kz.maltabu.app.maltabukz.network.models.response.MenuCategory
import kz.maltabu.app.maltabukz.ui.activity.MainActivity
import kz.maltabu.app.maltabukz.ui.adapter.CatalogTabAdapter
import kz.maltabu.app.maltabukz.vm.CategoryViewModel


class CategoryFragment(private val catagory: MenuCategory) : Fragment(), CatalogTabAdapter.ChooseCategory {

    private lateinit var adapter: CatalogTabAdapter
    private lateinit var sortDialog: Dialog
    private lateinit var viewModel: CategoryViewModel

    companion object {
        fun newInstance(catagory: MenuCategory) = CategoryFragment(catagory)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCategoryTitle()
        setAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CategoryViewModel::class.java)
        sortDialog=Dialog(activity!!)
        adapter= CatalogTabAdapter(activity!!, this)
    }

    private fun setCategoryTitle() {
        (activity as MainActivity).hottitle.text=catagory.name
    }

    private fun setAdapter(){
        val list = catagory.categoryChildList
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

    private fun scroll(position: Int) {
        Handler().postDelayed({
            tablayout.scrollToPosition(position)
        }, 0)
    }

    private fun selectCatalog(pos: Int){
        try {
            if(tablayout!=null) {
                Handler().postDelayed({
                    tablayout.findViewHolderForAdapterPosition(pos)!!.itemView.performClick()
                }, 20)
            }
        } catch (e:Exception){}
    }
}
