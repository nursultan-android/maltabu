package kz.maltabu.app.maltabukz.ui.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.dialog_update.*
import kotlinx.android.synthetic.main.fragment_hot.*
import kotlinx.android.synthetic.main.main_menu.view.*
import kz.maltabu.app.maltabukz.BuildConfig
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.model.FilterBody
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.models.response.MenuCategory
import kz.maltabu.app.maltabukz.network.models.response.ResponseCategories
import kz.maltabu.app.maltabukz.network.models.response.User
import kz.maltabu.app.maltabukz.ui.adapter.MenuAdapter
import kz.maltabu.app.maltabukz.ui.fragment.main.HotFragment
import kz.maltabu.app.maltabukz.ui.fragment.main.SearchFragment
import kz.maltabu.app.maltabukz.ui.fragment.main.category.CategoryFragment
import kz.maltabu.app.maltabukz.ui.fragment.main.category.FilterFragment
import kz.maltabu.app.maltabukz.ui.fragment.main.news.NewsFragment
import kz.maltabu.app.maltabukz.utils.CustomAnimator
import kz.maltabu.app.maltabukz.utils.LocaleHelper
import kz.maltabu.app.maltabukz.utils.customEnum.Status
import kz.maltabu.app.maltabukz.utils.web.NetworkChecker
import kz.maltabu.app.maltabukz.utils.web.VersionChecker
import kz.maltabu.app.maltabukz.vm.MainActivityViewModel
import org.jsoup.Jsoup
import org.koin.android.ext.android.inject


class MainActivity : BaseActivity(), MenuAdapter.ChooseCategory {

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var adapter: MenuAdapter
    private lateinit var customDialog: Dialog
    val checker: NetworkChecker by inject()
    var filer: FilterBody?=null
    var current = enum.HOT
    var catalogIndex=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setViewSettings()
        adapter= MenuAdapter(this, this)
        if (checker.isNetworkAvailable) {
            loadData()
        } else {
            noInternetActivity()
        }
    }

    fun noInternetActivity(){
        startActivityForResult(Intent(this, NoInternetActivity::class.java), 123)
        drawer.closeDrawer(Gravity.LEFT)
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

    fun setFragmentForCatalog(fragment: Fragment) {
        ft = manager.beginTransaction()
        ft.replace(R.id.catalog_place, fragment,"catalog").commit()
        drawer.closeDrawers()
    }

    fun setFragmentFilterFragment() {
        current = enum.FILTER
        ft = manager.beginTransaction()
        ft.replace(R.id.drawer, FilterFragment.newInstance()).addToBackStack("catalog").commit()
        drawer.closeDrawers()
    }

    fun setFullScreenFragment(fragment: Fragment){
        current = enum.NEWS_WEB_VIEW
        ft = manager.beginTransaction()
        ft.replace(R.id.drawer, fragment).addToBackStack("news").commit()
        drawer.closeDrawers()
    }

    fun getCatalogFragment(): Fragment {
        return manager.findFragmentByTag("catalog")!!
    }

    private fun recognizeFragment(fragment: Fragment) {
        if(fragment is CategoryFragment || fragment is NewsFragment){
            current = enum.CATEGORY
        } else {
            if(fragment is HotFragment) {
                current = enum.HOT
            }
        }
    }

    private fun consumeResponse(response: ApiResponse) {
        when (response.status) {
            Status.LOADING -> {}
            Status.SUCCESS -> {
                hideLoader()
                renderResponse(response.data!!.body() as ResponseCategories)
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

    private fun setViewSettings(){
        setSupportActionBar(toolbar)
        customDialog = Dialog(this)
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
        nav_view.getHeaderView(0).news_menu.setOnClickListener {
            setFragmentFromMenu(NewsFragment.newInstance())
        }
        nav_view.getHeaderView(0).logo_menu.setOnClickListener {
            clearBackStack()
            setFragmentFromMenu(HotFragment.newInstance())
        }
        nav_view.getHeaderView(0).search_icon.setOnClickListener {
            setFullScreenFragment(SearchFragment.newInstance())
        }
        hideFilter()
        setLangSettings()
    }

    private fun setLangSettings() {
        val lang = Paper.book().read(enum.LANG, enum.KAZAKH)
        if(lang == enum.KAZAKH){
            setLang(enum.KAZAKH, resources.getDrawable(R.drawable.kz))
        } else {
            setLang(enum.RUSSIAN, resources.getDrawable(R.drawable.ru))
        }
    }

    private fun setLangListeners(){
        nav_view.getHeaderView(0).lang.setOnClickListener {
            val lang = Paper.book().read(enum.LANG, enum.KAZAKH)
            if(lang == enum.KAZAKH) {
                setLang(enum.RUSSIAN, resources.getDrawable(R.drawable.ru))
                changeViewModel(enum.RUSSIAN)
            } else {
                setLang(enum.KAZAKH, resources.getDrawable(R.drawable.kz))
                changeViewModel(enum.KAZAKH)
            }
        }
        nav_view.getHeaderView(0).cabinet_lay.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
        }
        newAdButton.setOnClickListener {
            CustomAnimator.animateHotViewLinear(it)
            val intent = Intent(this, NewAdActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setLang(lang: String, img: Drawable) {
        Paper.book().write(enum.LANG, lang)
        nav_view.getHeaderView(0).counrty_img.setImageDrawable(img)
        val newRes = LocaleHelper.setLanguage(this, lang).resources
        nav_view.getHeaderView(0).langText.text = newRes.getString(R.string.other_lang)
        nav_view.getHeaderView(0).menu_title.text = newRes.getString(R.string.news)
        nav_view.getHeaderView(0).rules_granted.text = newRes.getString(R.string.rules2020)
        if(Paper.book().read(enum.TOKEN, "")==null|| Paper.book().read(enum.TOKEN, "").isEmpty()){
            nav_view.getHeaderView(0).user_name.text = newRes.getString(R.string.Cabinet)
        }
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

    fun hideFilter(){
        filter_image.visibility=View.GONE
        filter_text.visibility=View.GONE
        filter_badge.visibility=View.GONE
    }

    fun showFilter(){
        filter_image.visibility=View.VISIBLE
        filter_text.visibility=View.VISIBLE
    }

    private fun clearBackStack(){
        for (i in 0 until manager.backStackEntryCount) {
            manager.popBackStack()
        }
    }

    fun showLoader(){
        progress_bar_main.visibility=View.VISIBLE
    }

    fun hideLoader(){
        progress_bar_main.visibility=View.GONE
    }

    override fun onResume() {
        super.onResume()
        setUserInfo()
    }

    override fun onBackPressed() {
        if(current==enum.CATEGORY){
            val fragment = HotFragment.newInstance()
            setFragmentFromMenu(fragment)
        } else {
            if(current==enum.FILTER){
                super.onBackPressed()
                current=enum.CATEGORY
            } else {
                super.onBackPressed()
            }
        }
    }

    private fun setUserInfo() {
        val user: User? = Paper.book().read<User>(enum.USER, null)
        if(user!=null){
            if(user.name!=null && user.name.isNotEmpty()){
                nav_view.getHeaderView(0).user_name.text = user.name
            }
        } else {
            nav_view.getHeaderView(0).user_name.text = resources.getString(R.string.Cabinet)
        }
    }

    private fun checkVersion(){
        val checker = VersionChecker()
        checker.getData().observe(this, Observer {
            try {
                val doc = Jsoup.parse(it)
                val span = doc.select("span").first()
                compareVersions(span.text())
            } catch (e: Exception){
                Log.d("TAGg", e.message)
            }
        })
        checker.getVersion()
    }

    private fun compareVersions(versionFromPlayMarket: String) {
        val versionName = BuildConfig.VERSION_NAME
        if(versionFromPlayMarket!=versionName){
            showUpdateDialog()
        }
    }

    private fun loadData(){
        viewModel = ViewModelProviders.of(this, MainActivityViewModel.ViewModelFactory(Paper.book().read(enum.LANG, enum.KAZAKH)))
            .get(MainActivityViewModel::class.java)
        viewModel.mainResponse().observe(this, Observer { consumeResponse(it) })
        viewModel.getCategories()
        setLangListeners()
        setFragmentFromMenu(HotFragment.newInstance())
        try{
            checkVersion()
        } catch (e:Exception){
            Log.d("TAGg", e.message)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==123){
            if(current==enum.HOT)
            loadData()
        }
    }

    override fun onDestroy() {
        if(customDialog.isShowing){
            customDialog.dismiss()
        }
        super.onDestroy()
    }

    private fun showUpdateDialog() {
        customDialog.setContentView(R.layout.dialog_update)
        customDialog.update_button.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.play_market_link))))
            customDialog.dismiss()
        }
        customDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog.show()
    }
}
