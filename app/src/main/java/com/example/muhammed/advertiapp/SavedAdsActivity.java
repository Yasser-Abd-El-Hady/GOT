package com.example.muhammed.advertiapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
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

public class SavedAdsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ProgressBar progressBar;
    SwipeRefreshLayout refreshLayout;
    SharedPreferences sharedPreferences;
    String JSON_STRING, userType, idUser, image_pathUser, nameUser, emailUser;

    HashMap<String, String> params1;
    public ArrayList<AdsClass> adsArrayList;
    HashMap<String, String> hashMap;
    Intent intent, intentNavigation;
    ArrayList<HashMap<String, String>> list;
    public AdsClass adsClass;
    NavigationView navigationView;
    View hView;
    TextView nav_user, userEmail;
    ImageView imageView;
    SearchView searchView;


    RecyclerViewAdapter adapterRecycler;
    RecyclerView gridView;
    GridLayoutManager layoutManagerRecycler;
    int numberOfColumns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_ads);

        gridView = (RecyclerView) findViewById(R.id.gridViewSavedAds);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        numberOfColumns = (int) (dpWidth / 180);

        layoutManagerRecycler = new GridLayoutManager(this, numberOfColumns);
        gridView.setLayoutManager(layoutManagerRecycler);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.savedAdsRefresh);

        progressBar = (ProgressBar) findViewById(R.id.progressBarSavedAds);

        progressBar.getProgressDrawable().setColorFilter(Color.argb(255, 226, 99, 226), android.graphics.PorterDuff.Mode.SRC_IN);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(SavedAdsActivity.this);
        navigationView.getMenu().getItem(0).setChecked(true);

        hView = navigationView.getHeaderView(0);
        nav_user = (TextView) hView.findViewById(R.id.userName);
        userEmail = (TextView) hView.findViewById(R.id.userEmail);
        imageView = (ImageView) hView.findViewById(R.id.userImage);


        sharedPreferences = getSharedPreferences("UserLogin", this.MODE_PRIVATE);
        userType = sharedPreferences.getString("type", "").toString();
        image_pathUser = sharedPreferences.getString("image_path", "");
        nameUser = sharedPreferences.getString("name", "");
        emailUser = sharedPreferences.getString("email", "");
        idUser = sharedPreferences.getString("id", "");

        nav_user.setText(nameUser);
        userEmail.setText(emailUser);

        PicassoClient.userImage(SavedAdsActivity.this, image_pathUser, imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentNavigation = new Intent(SavedAdsActivity.this, ProfileActivity.class);
                intentNavigation.putExtra("nameActivity", "SavedAdsActivity");
                intentNavigation.putExtra("idUserProfile", idUser);
                startActivity(intentNavigation);
            }
        });
        params1 = new HashMap<>();

        hashMap = new HashMap<>();

        intent = new Intent(SavedAdsActivity.this, CommentActivity.class);

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
                            getSavedAds();
                        }
                    }, 3000);
                } else {
                    refreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SavedAdsActivity.this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
                }

            }
        });

        if (isNetworkOnline()) {
            getSavedAds();
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
        navigationView.getMenu().getItem(3).setChecked(true);
        if (isNetworkOnline()) {
            getSavedAds();
        } else {
            refreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
                startActivity(new Intent(SavedAdsActivity.this, AddCategoryActivity.class));
                return true;

            case R.id.addAds:
                startActivity(new Intent(SavedAdsActivity.this, Add_Ad.class));
                return true;

            case R.id.viewAllCategory:
                startActivity(new Intent(SavedAdsActivity.this, ViewAllCategories_Admin.class));
                return true;

            case R.id.viewAllAds:
                startActivity(new Intent(SavedAdsActivity.this, ViewAllAds_Admin.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Advertisements) {
            intentNavigation = new Intent(SavedAdsActivity.this, HomeActivity.class);
            startActivity(intentNavigation);

        } else if (id == R.id.nav_categories) {
            intentNavigation = new Intent(SavedAdsActivity.this, ViewParentCategories.class);
            startActivity(intentNavigation);

        } else if (id == R.id.nav_profile) {
            intentNavigation = new Intent(SavedAdsActivity.this, ProfileActivity.class);
            intentNavigation.putExtra("nameActivity", "HomeActivity");
            intentNavigation.putExtra("idUserProfile", idUser);
            startActivity(intentNavigation);

        } else if (id == R.id.nav_about) {

            Intent intent = new Intent(SavedAdsActivity.this, AboutActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {
            if (isNetworkOnline()) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear().commit();
                if (ViewParentCategories.booleanCreate) {
                    ViewParentCategories.clearData();
                }
                Intent intent = new Intent(SavedAdsActivity.this, LogIn_SignUp_Main.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                Toast.makeText(this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
                navigationView.getMenu().getItem(3).setChecked(true);
            }

        } else if (id == R.id.nav_saved_ads) {
            if (isNetworkOnline()) {
                refreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        getSavedAds();
                    }
                }, 3000);
            } else {
                refreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
            }

        } else if (id == R.id.nav_contact) {

            Intent intent = new Intent(SavedAdsActivity.this, ContactUsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void showAds() {
        JSONObject jsonObject = null;
        list = new ArrayList<HashMap<String, String>>();

        adsArrayList = new ArrayList<>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("resultSavedAd");

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


        try {

            adapterRecycler = new RecyclerViewAdapter(this.getApplicationContext(), adsArrayList);
            gridView.setAdapter(adapterRecycler);


        } catch (Exception e) {
        }


    }

    private void getSavedAds() {


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
                params1.put("userId", idUser);
                params1.put("function", "getSavedAds");
                return params1;
            }
        };

        Singleton.getSingleton(this).setRequestQue(stringRequest);
    }


}
