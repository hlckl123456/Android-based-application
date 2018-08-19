package com.ks.placesearch;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.URLEncoder;

public class detailActivtiy extends AppCompatActivity implements FragmentInfo.OnFragmentInteractionListener
                , FragmentPhotos.OnFragmentInteractionListener, FragmentMap.OnFragmentInteractionListener
                , FragmentReviews.OnFragmentInteractionListener{

    private TabLayout tabLayout;
    public ViewPager viewPager;
    private ImageView twitterShare, detailFav;
    private String placeId, address, position, name, icon;
    private SharedPreferenceManager sharedPreferenceManager;
    private result result;
    private  Toolbar detailToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        detailFav = (ImageView) findViewById(R.id.detailFav);
        viewPager = (ViewPager)findViewById(R.id.detailContainer);
        tabLayout = (TabLayout) findViewById(R.id.detailTab);
        setUpMainTabs();
        sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext());
        detailToolbar= (Toolbar) findViewById(R.id.detailoolbar);
        setSupportActionBar(detailToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("MyTitle");

        Bundle data = getIntent().getExtras();
        placeId = data.getString("placeId");
        address = data.getString("address");
        position = data.getString("position");
        name = data.getString("name");
        icon = data.getString("icon");
        if (sharedPreferenceManager.isFavourite(placeId)) {
            detailFav.setImageResource(R.drawable.heart_fill_white);
        }

        result = new result(name,address, icon, placeId,position);
        detailFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = (ImageView) v;
                if (sharedPreferenceManager.isFavourite(placeId)) {
                    sharedPreferenceManager.removeFavourite(placeId);
                    imageView.setImageResource(R.drawable.heart_outline_white);
                    toast(name + " was removed to favorites");
                } else {
                    Gson gson = new Gson();
                    String json = gson.toJson(result);
                    sharedPreferenceManager.setFavourite(placeId, json);
                    imageView.setImageResource(R.drawable.heart_fill_white);
                    toast(name + " was added to favorites");
                }
            }
        });

        detailToolbar.setTitle(name);

        // set adapter for viewPager
        final DetailPagerAdapter adapter = new DetailPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

    }

    private void  setUpMainTabs() {
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        View headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.custom_tab, null, false);
        LinearLayout linearLayout1 = (LinearLayout) headerView.findViewById(R.id.info);
        LinearLayout linearLayout2 = (LinearLayout) headerView.findViewById(R.id.photos);
        LinearLayout linearLayout3 = (LinearLayout) headerView.findViewById(R.id.map);
        LinearLayout linearLayout4 = (LinearLayout) headerView.findViewById(R.id.reviews);
        tabLayout.getTabAt(0).setCustomView(linearLayout1);
        tabLayout.getTabAt(1).setCustomView(linearLayout2);
        tabLayout.getTabAt(2).setCustomView(linearLayout3);
        tabLayout.getTabAt(3).setCustomView(linearLayout4);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void toast(String s){
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }
}
