package com.example.muhammed.advertiapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GridViewSomeAds extends AppCompatActivity {


    private String JSON_STRING, userType, idUser;
    Intent intent;
    HashMap<String, String> hashMap;
    String categoryId, savedAdId;
    SharedPreferences sharedPreferences, sharedCategory;
    ArrayList<HashMap<String, String>> list;
    HashMap<String, String> params1;
    SwipeRefreshLayout refreshLayout;

    public ArrayList<AdsClass> adsArrayList;
    public AdsClass adsClass;
    ProgressBar progressBar;
    SearchView searchView;


    RecyclerViewAdapter adapterRecycler;
    RecyclerView gridView;
    GridLayoutManager layoutManagerRecycler;
    int numberOfColumns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view_some_ads);
        gridView = (RecyclerView) findViewById(R.id.gridViewSomeAds);


        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        numberOfColumns = (int) (dpWidth / 180);

        layoutManagerRecycler = new GridLayoutManager(this, numberOfColumns);
        gridView.setLayoutManager(layoutManagerRecycler);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.gridViewSomeAdsRefresh);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar.getProgressDrawable().setColorFilter(Color.argb(255, 226, 99, 226), android.graphics.PorterDuff.Mode.SRC_IN);

        Intent intentGet = getIntent();
        String typeShow = intentGet.getExtras().getString("typeShow").toString();
        getSupportActionBar().setTitle(typeShow);

        sharedCategory = getSharedPreferences("categoryData", this.MODE_PRIVATE);
        categoryId = sharedCategory.getString("categoryId", "").toString();

        sharedPreferences = getSharedPreferences("UserLogin", this.MODE_PRIVATE);
        userType = sharedPreferences.getString("type", "").toString();
        idUser = sharedPreferences.getString("id", "");
        params1 = new HashMap<>();
        params1.put("user_id", idUser);

        if (typeShow.equals(getString(R.string.topViews))) {

            params1.put("function", "getTopViewsAds");
        } else if (typeShow.equals(getString(R.string.topRates))) {

            params1.put("function", "getTopRatesAds");
        } else if (typeShow.equals(getString(R.string.suggested))) {
            params1.put("function", "getSuggestedAds");
        } else if (typeShow.equals(getString(R.string.newAndUpdate))) {
            params1.put("function", "getNewUpdateAds");
        } else {

        }


        hashMap = new HashMap<>();

        intent = new Intent(GridViewSomeAds.this, CommentActivity.class);

        refreshLayout.setColorSchemeResources(R.color.purple, R.color.colorPrimary, R.color.colorPrimaryDark);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetworkOnline()) {
                    refreshLayout.setRefreshing(true);
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.setRefreshing(false);
                            getJSON();
                        }
                    }, 3000);
                } else {
                    refreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(GridViewSomeAds.this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
                }

            }
        });
        if (isNetworkOnline()) {
            getJSON();
        } else {
            refreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
        }

    }

    public boolean isNetworkOnline() {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNetworkOnline()) {
            getJSON();
        } else {
            refreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isNetworkOnline()) {
            list.clear();
        }
        super.onBackPressed();
    }


    private void showAds() {
        JSONObject jsonObject = null;
        list = new ArrayList<HashMap<String, String>>();

        adsArrayList = new ArrayList<>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String id = jo.getString(Config.TAG_ID);
                String details = jo.getString(Config.TAG_DESCRIPTION);
                String path = jo.getString(Config.TAG_IMAGE_PATH);
                String pathVideo = jo.getString("video_path");
                String start_date = jo.getString(Config.TAG_START_DATE);
                String expire_date = jo.getString(Config.TAG_EXPIRE_DATE);
                String rate = jo.getString("rate");
                String views_num = jo.getString("views_num");
                String user_id = jo.getString("user_id");
                String nameUser = jo.getString("name");
                String emailUser = jo.getString("email");
                String phoneUser = jo.getString("phone_number");
                String count_rate = jo.getString("count_rate");

                adsClass = new AdsClass();
                adsClass.setId(id);
                adsClass.setDetails(details);
                adsClass.setImage_path(path);
                adsClass.setVideo_path(pathVideo);
                adsClass.setStart_date(start_date);
                adsClass.setExpire_date(expire_date);
                adsClass.setRate(rate);
                adsClass.setViews_num(views_num);
                adsClass.setUser_id(user_id);
                adsClass.setNameUser(nameUser);
                adsClass.setEmailUser(emailUser);
                adsClass.setPhoneUser(phoneUser);
                adsClass.setCount_rate(count_rate);
                adsArrayList.add(adsClass);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        showSavedAd();

        try {
            adapterRecycler = new RecyclerViewAdapter(GridViewSomeAds.this, adsArrayList, savedAdId);
            gridView.setAdapter(adapterRecycler);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void showSavedAd() {
        JSONObject jsonObject = null;
        savedAdId = "";
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("resultSavedAd");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                savedAdId += jo.getString(Config.TAG_ID) + ",";

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getJSON() {

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_ADVERTI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressBar.setVisibility(View.GONE);
                JSON_STRING = response;
                showAds();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params1;
            }
        };

        Singleton.getSingleton(this).setRequestQue(stringRequest);

    }


    private List<AdsClass> filter(List<AdsClass> pl, String query) {
        while (query.startsWith(" ")) {
            adapterRecycler.setFilter(adsArrayList);
            if (query.length() != 1) {
                query = query.substring(1);
            } else {
                return adsArrayList;
            }
        }

        query = query.toLowerCase();
        final List<AdsClass> filterModeList = new ArrayList<>();
        for (AdsClass ads : pl) {
            final String details = ads.getDetails().toLowerCase();
            final String companyName = ads.getNameUser().toLowerCase();
            if (details.contains(query) || companyName.contains(query)) {
                filterModeList.add(ads);
            }
        }
        return filterModeList;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (userType.equals("Admin")) {
            getMenuInflater().inflate(R.menu.admin_search_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.search_menu, menu);
        }
        final MenuItem item = menu.findItem(R.id.search);

        searchView = (SearchView) item.getActionView();

        searchView.setQueryHint("Search ad");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (isNetworkOnline()) {
                    searchView.setQuery(query, false);
                    final List<AdsClass> filterModeList = filter(adsArrayList, query);
                    adapterRecycler.setFilter(filterModeList);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (isNetworkOnline()) {
                    final List<AdsClass> filterModeList = filter(adsArrayList, newText);
                    adapterRecycler.setFilter(filterModeList);
                }
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.addCategory:
                startActivity(new Intent(GridViewSomeAds.this, AddCategoryActivity.class));
                return true;

            case R.id.addAds:
                startActivity(new Intent(GridViewSomeAds.this, Add_Ad.class));
                return true;

            case R.id.viewAllCategory:
                startActivity(new Intent(GridViewSomeAds.this, ViewAllCategories_Admin.class));
                return true;

            case R.id.viewAllAds:
                startActivity(new Intent(GridViewSomeAds.this, ViewAllAds_Admin.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
