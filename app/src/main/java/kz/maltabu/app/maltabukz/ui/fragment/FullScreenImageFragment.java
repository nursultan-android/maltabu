package kz.maltabu.app.maltabukz.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import kz.maltabu.app.maltabukz.R;
import uk.co.senab.photoview.PhotoViewAttacher;


public class FullScreenImageFragment extends Fragment {
    public FullScreenImageFragment(){}

    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    public static FullScreenImageFragment newInstance(int page, String url) {
        FullScreenImageFragment imgFragment = new FullScreenImageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        arguments.putString(ARGUMENT_PAGE_NUMBER, url);
        imgFragment.setArguments(arguments);
        return imgFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_full_screen,null);
        Bundle bundle = this.getArguments();
        ImageView card = (ImageView) view.findViewById(R.id.imgPage);
        final PhotoViewAttacher pAttacher;
        pAttacher = new PhotoViewAttacher(card);
        if (this.getArguments() != null && getArguments().containsKey(ARGUMENT_PAGE_NUMBER)) {
            String url = bundle.getString(ARGUMENT_PAGE_NUMBER);
            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.load);
            Picasso.get().load(url).into(card, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                    pAttacher.update();
                }
                @Override
                public void onError(Exception e) {

                }
            });
        }
        return view;
    }

}