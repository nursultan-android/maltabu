package kz.maltabu.app.maltabukz.ui.fragment.showAd

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kz.maltabu.app.maltabukz.R
import uk.co.senab.photoview.PhotoViewAttacher

class FullScreenFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_image_full_screen, null)
        val bundle = this.arguments
        val card = view.findViewById<View>(R.id.imgPage) as ImageView
        val attacker = PhotoViewAttacher(card)
        if (this.arguments != null && arguments!!.containsKey(ARGUMENT_PAGE_NUMBER)) {
            val url = bundle!!.getString(ARGUMENT_PAGE_NUMBER)
            val progressBar = view.findViewById<View>(R.id.load) as ProgressBar
            Picasso.get().load(url).into(card, object : Callback {
                override fun onSuccess() {
                    progressBar.visibility = View.GONE
//                    setImageListener(card)
                    attacker.update()
                }
                override fun onError(e: Exception) {}
            })
        }
        return view
    }

    companion object {
        const val ARGUMENT_PAGE_NUMBER = "arg_page_number"
        fun newInstance(page: Int, url: String?): FullScreenFragment {
            val imgFragment =
                FullScreenFragment()
            val arguments = Bundle()
            arguments.putInt(ARGUMENT_PAGE_NUMBER, page)
            arguments.putString(ARGUMENT_PAGE_NUMBER, url)
            imgFragment.arguments = arguments
            return imgFragment
        }
    }

    private fun setImageListener(card: ImageView){
        var dY=0f
        var mY=0f
        card.setOnTouchListener(View.OnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dY = view.y - event.rawY
                    mY = view.y
                }
                MotionEvent.ACTION_MOVE -> {
                    view.animate()
                        .y(event.rawY + dY)
                        .setDuration(0)
                        .start()
                }

                MotionEvent.ACTION_UP -> {
                    view.animate()
                        .y(mY)
                        .setDuration(0)
                        .start()
                }
                else -> return@OnTouchListener false
            }
            true
        })
    }
}