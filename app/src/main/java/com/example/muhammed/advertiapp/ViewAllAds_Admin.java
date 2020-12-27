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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

public class ViewAllAds_Admin extends AppCompatActivity {


    private ListView listView;

    private String JSON_STRING;
    Intent intent;
    HashMap<String, String> hashMap;
    String categoryId;
    SharedPreferences sharedPreferences;
    ArrayList<HashMap<String, String>> list;
    SwipeRefreshLayout refreshLayout;

    public ArrayList<AdsClass> adsArrayList;
    public AdsClass adsClass;

    ProgressBar progressBar;
    ListViewAdapter customAdapter;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_ads__admin);
        listView = (ListView) findViewById(R.id.listViewAds);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.viewAllAdsAdminRefresh);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar.getProgressDrawable().setColorFilter(Color.argb(255, 226, 99, 226), android.graphics.PorterDuff.Mode.SRC_IN);

        hashMap = new HashMap<>();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String adsId = adsArrayList.get(position).getId().toString();
                String Adddesc = adsArrayList.get(position).getDetails().toString();
                String user_id = adsArrayList.get(position).getUser_id().toString();
                String visibility = adsArrayList.get(position).getVisible().toString();
                String path = adsArrayList.get(position).getImage_path().toString();
                String startDate = adsArrayList.get(position).getStart_date().toString();
                String expireDate = adsArrayList.get(position).getExpire_date().toString();

                Intent intent = new Intent(ViewAllAds_Admin.this, Edit_Ad.class);


                intent.putExtra("idAd", adsId);
                intent.putExtra("detailsAd", Adddesc);
                intent.putExtra("companyId", user_id);
                intent.putExtra("visibilityAd", visibility);
                intent.putExtra("pathAd", path);
                intent.putExtra("startDateAd", startDate);
                intent.putExtra("expireDateAd", expireDate);
                startActivity(intent);

            }
        });


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
                    Toast.makeText(ViewAllAds_Admin.this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
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
                String user_id = jo.getString("user_id");
                String visibility = jo.getString("visible");
                String start_date = jo.getString(Config.TAG_START_DATE);
                String expire_date = jo.getString(Config.TAG_EXPIRE_DATE);
                String rate = jo.getString("rate");
                String views_num = jo.getString("views_num");
                String nameUser = jo.getString("name");

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
                adsClass.setVisible(visibility);
                adsArrayList.add(adsClass);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            customAdapter = new ListViewAdapter(this.getApplicationContext(), adsArrayList);
            listView.setAdapter(customAdapter);
        } catch (Exception e) {
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
                HashMap<String, String> params1 = new HashMap<>();
                params1.put("function", "getAllAds_Admin");
                return params1;
            }
        };

        Singleton.getSingleton(this).setRequestQue(stringRequest);

    }


    private List<AdsClass> filter(List<AdsClass> pl, String query) {
        while (query.startsWith(" ")) {
            customAdapter.setFilter(adsArrayList);
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
        getMenuInflater().inflate(R.menu.admin_search_menu, menu);

        final MenuItem item = menu.findItem(R.id.search);

        searchView = (SearchView) item.getActionView();

        searchView.setQueryHint("Search ad");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (isNetworkOnline()) {
                    searchView.setQuery(query, false);
                    final List<AdsClass> filterModeList = filter(adsArrayList, query);
                    customAdapter.setFilter(filterModeList);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (isNetworkOnline()) {
                    final List<AdsClass> filterModeList = filter(adsArrayList, newText);
                    customAdapter.setFilter(filterModeList);
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
                startActivity(new Intent(ViewAllAds_Admin.this, AddCategoryActivity.class));
                return true;

            case R.id.addAds:
                startActivity(new Intent(ViewAllAds_Admin.this, Add_Ad.class));
                return true;

            case R.id.viewAllCategory:
                startActivity(new Intent(ViewAllAds_Admin.this, ViewAllCategories_Admin.class));
                return true;

            case R.id.viewAllAds:
                refreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        getJSON();
                    }
                }, 3000);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
