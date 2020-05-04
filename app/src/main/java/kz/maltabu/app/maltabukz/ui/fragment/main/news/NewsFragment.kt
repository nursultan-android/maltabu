package kz.maltabu.app.maltabukz.ui.fragment.main.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.paperdb.Paper
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_news.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.models.response.News
import kz.maltabu.app.maltabukz.network.models.response.ResponseNews
import kz.maltabu.app.maltabukz.ui.activity.BaseActivity
import kz.maltabu.app.maltabukz.ui.activity.MainActivity
import kz.maltabu.app.maltabukz.ui.adapter.NewsAdapter
import kz.maltabu.app.maltabukz.utils.customEnum.Status
import kz.maltabu.app.maltabukz.utils.views.EndlessListener
import kz.maltabu.app.maltabukz.vm.NewsViewModel


class NewsFragment : Fragment(), NewsAdapter.ChooseNews {

    private lateinit var viewModel:NewsViewModel
    private lateinit var adapter:NewsAdapter
    private val newsList = ArrayList<News>()
    private var pageNumber=1

    companion object {
        fun newInstance() =
            NewsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel= ViewModelProviders.of(this, NewsViewModel.ViewModelFactory(Paper.book().read((activity!! as BaseActivity).enum.LANG, (activity!! as BaseActivity).enum.KAZAKH)))
                .get(NewsViewModel::class.java)
        adapter= NewsAdapter(activity!!, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.mainResponse().observe(viewLifecycleOwner, Observer { consumeResponse(it) })
        if((activity as MainActivity).checker.isNetworkAvailable) {
            viewModel.getNews(pageNumber)
        } else {
            (activity as MainActivity).noInternetActivity()
        }
        adapter.setData(newsList)
        news_recycler.adapter=adapter
        setListeners()
        setNewsTitle()
        (activity as MainActivity).hideFilter()
    }

    private fun setListeners() {
        news_recycler.addOnScrollListener(
            object : EndlessListener(news_recycler.layoutManager as LinearLayoutManager){
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView){
                    if((activity as MainActivity).checker.isNetworkAvailable) {
                        pageNumber++
                        viewModel.getNews(pageNumber)
                    } else {
                        (activity as MainActivity).noInternetActivity()
                    }
                }
            })
    }

    private fun consumeResponse(response: ApiResponse) {
        when (response.status) {
            Status.LOADING -> {
                showLoader()
            }
            Status.SUCCESS -> {
                hideLoader()
                renderSocResponse(response.data!!.body() as ResponseNews)
            }
            Status.ERROR -> {
                hideLoader()
                Log.d("TAGg", response.error!!.message())
                Toast.makeText(activity!!, response.error.message(), Toast.LENGTH_SHORT).show()
            }
            Status.THROWABLE -> {
                hideLoader()
                Log.d("TAGg", response.throwabl.toString())
            }
        }
    }

    private fun setNewsTitle() {
        (activity as MainActivity).hottitle.text=resources.getString(R.string.news)
    }

    private fun renderSocResponse(response: ResponseNews) {
        newsList.addAll(response.newsList)
        adapter.notifyDataSetChanged()
    }

    private fun showProgress(){
        progress_hor.visibility= View.VISIBLE
    }

    private fun hideProgress(){
        progress_hor.visibility= View.INVISIBLE
    }

    private fun showLoader(){
        if(pageNumber==1)
            (activity as MainActivity).showLoader()
        else
            showProgress()
    }

    private fun hideLoader(){
        if(pageNumber==1)
            (activity as MainActivity).hideLoader()
        else
            hideProgress()
    }

    override fun chooseNews(news: News) {
        if((activity as MainActivity).checker.isNetworkAvailable) {
            (activity as MainActivity).setFullScreenFragment(NewsWebView.newInstance(news.slug))
        } else {
            (activity as MainActivity).noInternetActivity()
        }
    }
}
