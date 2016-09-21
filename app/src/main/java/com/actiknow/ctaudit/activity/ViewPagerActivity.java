package com.actiknow.ctaudit.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.actiknow.ctaudit.R;
import com.actiknow.ctaudit.adapter.MyPagerAdapter;
import com.actiknow.ctaudit.adapter.SmartFragmentStatePagerAdapter;
import com.actiknow.ctaudit.utils.Constants;
import com.actiknow.ctaudit.utils.CustomViewPager;


public class ViewPagerActivity extends AppCompatActivity {
    public static boolean ct_flag = false;
    public static boolean flag = false;
    static CustomViewPager vpPager;
    int count = 0;
    private SmartFragmentStatePagerAdapter adapterViewPager;

    public static void nextPage () {
        vpPager.setCurrentItem (vpPager.getCurrentItem () + 1);
    }

    public static void prevPage () {
        vpPager.setCurrentItem (vpPager.getCurrentItem () - 1);
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_pager);
        initView ();
        initListener ();
        initData ();
    }

    private void initData () {
        Constants.responseList.clear ();
        adapterViewPager = new MyPagerAdapter (getSupportFragmentManager ());
        vpPager.setAdapter (adapterViewPager);
        vpPager.setClipToPadding (false);
        vpPager.setPageMargin (10);
        vpPager.setOffscreenPageLimit (1);
    }

    private void initView () {
        vpPager = (CustomViewPager) findViewById (R.id.vpPager);
    }

    private void initListener () {
        vpPager.addOnPageChangeListener (new ViewPager.OnPageChangeListener () {
            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected (int position) {
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled (int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged (int state) {
                // Code goes here
            }
        });
        final View touchView = findViewById (R.id.vpPager);
        touchView.setOnTouchListener (new View.OnTouchListener () {
            @Override
            public boolean onTouch (View v, MotionEvent event) {
                return false;
            }
        });
    }

    @Override
    public void onBackPressed () {
        if (vpPager.getCurrentItem () > 0) {
            vpPager.setCurrentItem (vpPager.getCurrentItem () - 1);
        } else {
            finish ();
            overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
}
