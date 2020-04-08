package kz.maltabu.app.maltabukz.ui.activity

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.main_menu.view.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.models.response.MenuCategory
import kz.maltabu.app.maltabukz.network.models.response.ResponseCategories
import kz.maltabu.app.maltabukz.ui.adapter.MenuAdapter
import kz.maltabu.app.maltabukz.ui.fragment.CategoryFragment
import kz.maltabu.app.maltabukz.ui.fragment.HotFragment
import kz.maltabu.app.maltabukz.utils.customEnum.ApiLangEnum
import kz.maltabu.app.maltabukz.utils.customEnum.FragmentTagEnum
import kz.maltabu.app.maltabukz.utils.customEnum.Keys
import kz.maltabu.app.maltabukz.utils.customEnum.Status
import kz.maltabu.app.maltabukz.vm.MainActivityViewModel

class MainActivity : BaseActivity(), MenuAdapter.ChooseCategory {

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var adapter: MenuAdapter
    var manager = supportFragmentManager
    private var current = FragmentTagEnum.HOT.constantKey
    private lateinit var ft: FragmentTransaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setViewSettings()
        adapter= MenuAdapter(this, this)
        viewModel = ViewModelProviders.of(this, MainActivityViewModel.ViewModelFactory(Paper.book().read(Keys.LANG.constantKey, ApiLangEnum.KAZAKH.constantKey)))
            .get(MainActivityViewModel::class.java)

        viewModel.mainResponse().observe(this, Observer { consumeResponse(it) })
        viewModel.getCategories()
        setLangListeners()
    }

    override fun chooseCategory(category: MenuCategory) {
        val fragment = CategoryFragment.newInstance(category)
        setFragmentFromMenu(fragment)
    }

    private fun setFragmentFromMenu(fragment: Fragment) {
        recognizeFragment(fragment)
        ft = manager.beginTransaction()
        ft.replace(R.id.main, fragment).commit()
        drawer.closeDrawers()
    }

    private fun recognizeFragment(fragment: Fragment) {
        if(fragment is CategoryFragment){
            current = FragmentTagEnum.CATEGORY.constantKey
        } else {
            if(fragment is HotFragment) {
                current = FragmentTagEnum.HOT.constantKey
            }
        }
    }

    private fun consumeResponse(response: ApiResponse) {
        when (response.status) {
            Status.LOADING -> {}
            Status.SUCCESS -> {
                renderResponse(response.data!!.body() as ResponseCategories)
            }
            Status.ERROR -> {
                Log.d("TAGg", response.error.toString())
            }
            Status.THROWABLE -> {
                Log.d("TAGg", response.throwabl.toString())
            }
        }
    }

    private fun setViewSettings(){
        setSupportActionBar(toolbar)
        appBarLayout.outlineProvider = null
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_action_toggle)
        supportActionBar!!.title = ""
        drawer.openDrawer(Gravity.LEFT)
        hideFilter()
        setLangSettings()
    }

    private fun setLangSettings() {
        val lang = Paper.book().read(Keys.LANG.constantKey, ApiLangEnum.KAZAKH.constantKey)
        if(lang == ApiLangEnum.KAZAKH.constantKey){
            setLang(ApiLangEnum.KAZAKH.constantKey, resources.getDrawable(R.drawable.kz))
        } else {
            setLang(ApiLangEnum.RUSSIAN.constantKey, resources.getDrawable(R.drawable.ru))
        }
    }

    private fun setLangListeners(){
        nav_view.getHeaderView(0).lang.setOnClickListener {
            val lang = Paper.book().read(Keys.LANG.constantKey, ApiLangEnum.KAZAKH.constantKey)
            if(lang == ApiLangEnum.KAZAKH.constantKey) {
                setLang(ApiLangEnum.RUSSIAN.constantKey, resources.getDrawable(R.drawable.ru))
                changeViewModel(ApiLangEnum.RUSSIAN.constantKey)
            } else {
                setLang(ApiLangEnum.KAZAKH.constantKey, resources.getDrawable(R.drawable.kz))
                changeViewModel(ApiLangEnum.KAZAKH.constantKey)
            }
        }

        newAdButton.setOnClickListener {
            val intent = Intent(this, NewAdActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setLang(lang: String, img: Drawable) {
        Paper.book().write(Keys.LANG.constantKey, lang)
        nav_view.getHeaderView(0).counrty_img.setImageDrawable(img)
    }

    private fun changeViewModel(key: String) {
        viewModel.setLang(key)
        viewModel.getCategories()
    }

    private fun renderResponse(response: ResponseCategories){
        adapter.clearData()
        adapter.setData(sortMenu(response.categoriesList))
        adapter.notifyDataSetChanged()
        nav_view.getHeaderView(0).menu_recycler.adapter=adapter
    }

    private fun sortMenu(categories: List<MenuCategory>): List<MenuCategory>{
       return categories.sortedBy { it.order }
    }

    private fun hideFilter(){
        filter.visibility=View.GONE
        filter_text.visibility=View.GONE
    }

    private fun showFilter(){
        filter.visibility=View.VISIBLE
        filter_text.visibility=View.VISIBLE
    }

    fun clearBackStack(){
        for (i in 0 until manager.backStackEntryCount) {
            manager.popBackStack()
        }
    }

    override fun onBackPressed() {
        if(current==FragmentTagEnum.CATEGORY.constantKey){
            val fragment = HotFragment.newInstance()
            setFragmentFromMenu(fragment)
        } else {
            super.onBackPressed()
        }
    }
}
