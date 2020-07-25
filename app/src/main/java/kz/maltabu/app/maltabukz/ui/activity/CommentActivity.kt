package kz.maltabu.app.maltabukz.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_comments.*
import kz.maltabu.app.maltabukz.R
import kz.maltabu.app.maltabukz.network.ApiResponse
import kz.maltabu.app.maltabukz.network.models.comment.Comment
import kz.maltabu.app.maltabukz.network.models.response.User
import kz.maltabu.app.maltabukz.ui.adapter.CommentAdapter
import kz.maltabu.app.maltabukz.utils.customEnum.Status
import kz.maltabu.app.maltabukz.vm.CommentViewModel

class CommentActivity :  BaseActivity(){
    private lateinit var viewModel: CommentViewModel
    private lateinit var adapter: CommentAdapter
    private var user: User?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        viewModel = ViewModelProviders.of(this, CommentViewModel.ViewModelFactory(Paper.book().read(enum.LANG, enum.KAZAKH)))
            .get(CommentViewModel::class.java)
        viewModel.mainResponse().observe(this, Observer { consumeResponse(it) })
        setComments()
        setListeners()
    }

    private fun consumeResponse(response: ApiResponse) {
        when (response.status) {
            Status.LOADING -> {
                showLoader()
            }
            Status.SUCCESS -> {
                hideLoader()
                Log.d("TAGg", response.data.toString())
                onBackPressed()
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

    private fun setComments() {
        val comments = intent.getSerializableExtra("commentsList") as List<Comment>
        adapter = CommentAdapter(this)
        adapter.setData(comments)
        comments_recycler_view.adapter=adapter
    }

    private fun setListeners() {
        checkUser()
        send_comment.setOnClickListener {
            val txt = comment_text_field.text.toString()
            val id = intent.getIntExtra("adId",0)
            if(user!=null && txt.isNotEmpty()){
                viewModel.sendComment(txt, id, user!!.id)
            }
        }
    }

    private fun checkUser() {
        user = Paper.book().read<User>(enum.USER, null)
        if(user==null){
            comment_text_field.isEnabled=false
            comment_text_field.visibility=View.GONE
            send_comment.text=resources.getString(R.string.comments3)
        } else {
            comment_text_field.isEnabled=true
            comment_text_field.visibility=View.VISIBLE
            send_comment.text=resources.getString(R.string.comments2)
        }
    }

    fun showLoader(){
        comments_progress_lay.visibility= View.VISIBLE
    }

    fun hideLoader(){
        comments_progress_lay.visibility= View.GONE
    }
}
