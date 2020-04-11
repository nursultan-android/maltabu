package kz.maltabu.app.maltabukz.ui.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import io.paperdb.Paper
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.dialog_sort.*
import kotlinx.android.synthetic.main.fragment_category.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.models.response.MenuCategory
import kz.maltabu.app.maltabukz.ui.activity.MainActivity
import kz.maltabu.app.maltabukz.ui.adapter.CatalogsViewPAgerApadter
import kz.maltabu.app.maltabukz.utils.customEnum.Keys
import kz.maltabu.app.maltabukz.vm.CategoryViewModel

class CategoryFragment(val catagory: MenuCategory) : Fragment() {

    private lateinit var adapter: CatalogsViewPAgerApadter
    private lateinit var sortDialog: Dialog
    private lateinit var viewModel: CategoryViewModel

    companion object {
        fun newInstance(catagory: MenuCategory) = CategoryFragment(catagory)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CategoryViewModel::class.java)
        sortDialog=Dialog(activity!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCategoryTitle()
        setAdapter()
        setDefaultSort()
        sort.setOnClickListener {
//            showSortDialog()
        }
    }

    private fun setCategoryTitle() {
        (activity as MainActivity).hottitle.text=catagory.name
    }

    private fun setDefaultSort(){
        sort.text=resources.getString(R.string.sort1)
    }

    private fun showSortDialog() {
        sortDialog.setContentView(R.layout.dialog_sort)
        sortDialog.newAds.text = resources.getString(R.string.sort1)
        sortDialog.cheap.text = resources.getString(R.string.sort3)
        sortDialog.newAds.setOnClickListener {
            Paper.book().write(Keys.SORT.constantKey, "date")
            sort.text=resources.getString(R.string.sort1)
            sortDialog.dismiss()
        }
        sortDialog.cheap.setOnClickListener {
            Paper.book().write(Keys.SORT.constantKey, "price")
            sort.text=resources.getString(R.string.sort3)
            sortDialog.dismiss()
        }
        sortDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        sortDialog.show()
    }

    private fun setAdapter(){
        viewpager.offscreenPageLimit=1
        adapter = CatalogsViewPAgerApadter(activity!!.supportFragmentManager)
        val list = catagory.categoryChildList
        list.sortBy { it.order }
        for(i in 0 until list.size ) {
            val catalog = CatalogFragment(list[i].id)
            adapter.addFragment(catalog, list[i].title)
        }
        viewpager.adapter=adapter
        tablayout.setupWithViewPager(viewpager)
    }
}
