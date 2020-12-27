package com.example.muhammed.advertiapp;

import android.annotation.SuppressLint;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Intent intentNavigation;

    NavigationView navigationView;
    RecyclerView recyclerView, recyclerViewTwo, recyclerViewThree, recyclerViewFour;
    RecyclerView.LayoutManager layoutManagerRecycler, layoutManagerRecyclerTwo, layoutManagerRecyclerThree, layoutManagerRecyclerFour;
    RecyclerView.Adapter adapterRecycler;
    Button topRates, topViews, suggested, newUpdate, more, moreTwo, moreThree, moreFour;


    private String JSON_STRING, userType, idUser, image_pathUser, nameUser, emailUser;
    Intent intent;
    HashMap<String, String> hashMap;
    String categoryId, typeShow, savedAdId;
    SharedPreferences sharedPreferences, sharedCategory;
    ArrayList<HashMap<String, String>> list;
    HashMap<String, String> params1;
    SwipeRefreshLayout refreshLayout;
    View hView;
    TextView nav_user, userEmail;
    CircleImageView imageView;
    ProgressBar progressBar;


    public ArrayList<AdsClass> adsArrayList;
    public AdsClass adsClass;

    TextView textCheckConnection;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        textCheckConnection = (TextView) findViewById(R.id.textCheckConnection);
        LinearLayout headerImageView = (LinearLayout) findViewById(R.id.headerView);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        hView = navigationView.getHeaderView(0);
        nav_user = (TextView) hView.findViewById(R.id.userName);
        userEmail = (TextView) hView.findViewById(R.id.userEmail);
        imageView = (CircleImageView) hView.findViewById(R.id.userImage);


        adsArrayList = new ArrayList<>();
        sharedCategory = getSharedPreferences("categoryData", this.MODE_PRIVATE);
        categoryId = sharedCategory.getString("categoryId", "").toString();

        sharedPreferences = getSharedPreferences("UserLogin", this.MODE_PRIVATE);
        userType = sharedPreferences.getString("type", "").toString();
        image_pathUser = sharedPreferences.getString("image_path", "");
        nameUser = sharedPreferences.getString("name", "");
        emailUser = sharedPreferences.getString("email", "");
        idUser = sharedPreferences.getString("id", "");

        nav_user.setText(nameUser);
        userEmail.setText(emailUser);
        PicassoClient.userImage(HomeActivity.this, image_pathUser, imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentNavigation = new Intent(HomeActivity.this, ProfileActivity.class);
                intentNavigation.putExtra("nameActivity", "HomeActivity");
                intentNavigation.putExtra("idUserProfile", idUser);
                startActivity(intentNavigation);
            }
        });

        intent = new Intent(HomeActivity.this, CommentActivity.class);


        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.homeRefresh);
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
                            getAllJSON();
                        }
                    }, 3000);
                } else {
                    refreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(HomeActivity.this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
                }
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerViewTwo = (RecyclerView) findViewById(R.id.recyclerViewTwo);
        recyclerViewThree = (RecyclerView) findViewById(R.id.recyclerViewThree);
        recyclerViewFour = (RecyclerView) findViewById(R.id.recyclerViewFour);
        recyclerView.setHasFixedSize(true);
        recyclerViewTwo.setHasFixedSize(true);
        recyclerViewThree.setHasFixedSize(true);
        recyclerViewFour.setHasFixedSize(true);
        layoutManagerRecycler = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManagerRecyclerTwo = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManagerRecyclerThree = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManagerRecyclerFour = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManagerRecycler);
        recyclerViewTwo.setLayoutManager(layoutManagerRecyclerTwo);
        recyclerViewThree.setLayoutManager(layoutManagerRecyclerThree);
        recyclerViewFour.setLayoutManager(layoutManagerRecyclerFour);

        topViews = (Button) findViewById(R.id.topViews);
        topRates = (Button) findViewById(R.id.topRates);
        suggested = (Button) findViewById(R.id.suggested);
        newUpdate = (Button) findViewById(R.id.newUpdate);

        more = (Button) findViewById(R.id.more);
        moreTwo = (Button) findViewById(R.id.moreTwo);
        moreThree = (Button) findViewById(R.id.moreThree);
        moreFour = (Button) findViewById(R.id.moreFour);

        progressBar.getProgressDrawable().setColorFilter(Color.argb(255, 226, 99, 226), android.graphics.PorterDuff.Mode.SRC_IN);


        topViews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, ViewSomeAds.class);
                intent.putExtra("typeShow", getString(R.string.topViews));
                startActivity(intent);
            }
        });

        topRates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, ViewSomeAds.class);
                intent.putExtra("typeShow", getString(R.string.topRates));
                startActivity(intent);
            }
        });

        suggested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, ViewSomeAds.class);
                intent.putExtra("typeShow", getString(R.string.suggested));
                startActivity(intent);
            }
        });

        newUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, ViewSomeAds.class);
                intent.putExtra("typeShow", getString(R.string.newAndUpdate));
                startActivity(intent);
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, GridViewSomeAds.class);
                intent.putExtra("typeShow", getString(R.string.newAndUpdate));
                startActivity(intent);
            }
        });

        moreTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, GridViewSomeAds.class);
                intent.putExtra("typeShow", getString(R.string.suggested));
                startActivity(intent);
            }
        });

        moreThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, GridViewSomeAds.class);
                intent.putExtra("typeShow", getString(R.string.topViews));
                startActivity(intent);
            }
        });

        moreFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, GridViewSomeAds.class);
                intent.putExtra("typeShow", getString(R.string.topRates));
                startActivity(intent);
            }
        });

        if (isNetworkOnline()) {
            getAllJSON();
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(HomeActivity.this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
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
        navigationView.getMenu().getItem(0).setChecked(true);
        if (isNetworkOnline()) {
            getAllJSON();
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(HomeActivity.this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        if (userType.equals("Admin")) {
            menuInflater.inflate(R.menu.admin_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.addCategory:
                startActivity(new Intent(HomeActivity.this, AddCategoryActivity.class));
                return true;

            case R.id.addAds:
                startActivity(new Intent(HomeActivity.this, Add_Ad.class));
                return true;

            case R.id.viewAllCategory:
                startActivity(new Intent(HomeActivity.this, ViewAllCategories_Admin.class));
                return true;

            case R.id.viewAllAds:
                startActivity(new Intent(HomeActivity.this, ViewAllAds_Admin.class));
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
            if (isNetworkOnline()) {
                refreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        getAllJSON();
                    }
                }, 3000);
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(HomeActivity.this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
            }

        } else if (id == R.id.nav_categories) {


            intentNavigation = new Intent(HomeActivity.this, ViewParentCategories.class);
            startActivity(intentNavigation);

        } else if (id == R.id.nav_profile) {
            intentNavigation = new Intent(HomeActivity.this, ProfileActivity.class);
            intentNavigation.putExtra("nameActivity", "HomeActivity");
            intentNavigation.putExtra("idUserProfile", idUser);
            startActivity(intentNavigation);

        } else if (id == R.id.nav_about) {

            Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {
            if (isNetworkOnline()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear().commit();
                if (ViewParentCategories.booleanCreate) {
                    ViewParentCategories.clearData();
                }
                Intent intent = new Intent(HomeActivity.this, LogIn_SignUp_Main.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                Toast.makeText(this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
                navigationView.getMenu().getItem(0).setChecked(true);
            }

        } else if (id == R.id.nav_saved_ads) {

            Intent intent = new Intent(HomeActivity.this, SavedAdsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_contact) {

            Intent intent = new Intent(HomeActivity.this, ContactUsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void getAllJSON() {


        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        progressBar.setSoundEffectsEnabled(true);

        RequestJsonObject requestJsonObject = new RequestJsonObject(Request.Method.POST, Config.URL_ADVERTI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressBar.setVisibility(View.GONE);
                JSON_STRING = response;
                showAllAds();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                params1 = new HashMap<>();
                typeShow = "getNewUpdate_Suggested_TopViews_TopRates";
                params1.put("user_id", idUser);
                params1.put("function", typeShow);
                return params1;
            }
        };

        Singleton.getSingleton(this).setRequestQue(requestJsonObject);

    }

    private void showAllAds() {
        for (int k = 1; k < 5; k++) {

            JSONObject jsonObject = null;
            list = new ArrayList<HashMap<String, String>>();

            try {
                jsonObject = new JSONObject(JSON_STRING);
                JSONArray result = null;
                if (k == 1) {
                    result = jsonObject.getJSONArray("resultNewUpdate");
                } else if (k == 2) {
                    result = jsonObject.getJSONArray("resultSuggested");
                } else if (k == 3) {
                    result = jsonObject.getJSONArray("resultTopViews");
                } else if (k == 4) {
                    result = jsonObject.getJSONArray("resultTopRates");
                }

                adsArrayList = new ArrayList<>();
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

            adapterRecycler = new RecyclerViewAdapter(HomeActivity.this, adsArrayList, savedAdId);

            if (k == 1) {
                recyclerView.setAdapter(adapterRecycler);
            } else if (k == 2) {
                recyclerViewTwo.setAdapter(adapterRecycler);
            } else if (k == 3) {
                recyclerViewThree.setAdapter(adapterRecycler);
            } else if (k == 4) {
                recyclerViewFour.setAdapter(adapterRecycler);
            }
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

}
