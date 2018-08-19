package com.ks.placesearch;

import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.icu.util.ValueIterator;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements FragmentFavorites.OnFragmentInteractionListener, FragmentSearch.OnFragmentInteractionListener{


    private TabLayout searchLayout;
    public ViewPager viewPager;
    public static HashMap<String, String> favMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager)findViewById(R.id.pager);
        searchLayout = (TabLayout)findViewById(R.id.searchlayout);
        setUpMainTabs();

        // set adapter for viewPager
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), searchLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(searchLayout));



        searchLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void setUpMainTabs() {
        searchLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        View headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.custom_tab, null, false);
        LinearLayout linearLayout1 = (LinearLayout) headerView.findViewById(R.id.ll);
        LinearLayout linearLayout2 = (LinearLayout) headerView.findViewById(R.id.ll2);
        searchLayout.getTabAt(0).setCustomView(linearLayout1);
        searchLayout.getTabAt(1).setCustomView(linearLayout2);
    }
}
