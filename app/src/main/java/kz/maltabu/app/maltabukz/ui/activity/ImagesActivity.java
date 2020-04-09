package kz.maltabu.app.maltabukz.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import kz.maltabu.app.maltabukz.R;
import kz.maltabu.app.maltabukz.network.models.response.Ad;
import kz.maltabu.app.maltabukz.ui.fragment.FullScreenImageFragment;
import kz.maltabu.app.maltabukz.utils.CustomAnimator;

public class ImagesActivity extends AppCompatActivity {
    private ImageView img;
    private Ad ad;
    private int PAGE_COUNT, selectedImg;
    private ViewPager pager;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ad =(Ad) getIntent().getSerializableExtra("ad");
        selectedImg = getIntent().getIntExtra("select",0);
        PAGE_COUNT = ad.getImages().size();
        setContentView(R.layout.activity_images);
        pager = (ViewPager) findViewById(R.id.pages);
        img = (ImageView) findViewById(R.id.arr);
        img.setOnClickListener(v ->{
                CustomAnimator.Companion.animateHotViewLinear(v);
                finish();}
            );
        final TextView txt = (TextView) findViewById(R.id.photos);
        if(ad.getImages().size()>0)
            txt.setText(String.valueOf(selectedImg+1+"/"+ ad.getImages().size()));
        pagerAdapter = new MyFragmentPagerAdapter2(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(selectedImg);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if(ad.getImages().size()>0)
                    txt.setText(String.valueOf(position+1+"/"+ ad.getImages().size()));
            }
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    private class MyFragmentPagerAdapter2 extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter2(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FullScreenImageFragment.newInstance(position, ad.getImages().get(position));
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
    }

}
