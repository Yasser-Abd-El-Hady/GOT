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
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewParentCategories extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static ArrayList<CategoryClass> cats;
    static ArrayList<CategoryClass> subcatarr = new ArrayList<>();
    private CategoryClass cat;
    private String JSON_STRING, catId, catName, catArabicName;
    static HashMap<String, String> categories;
    public static ArrayList<HashMap<String, String>> list;
    public static ArrayList<ArrayList<HashMap<String, String>>> combo;
    ListView myList;
    SharedPreferences sharedCategory;
    SharedPreferences.Editor editor;

    SharedPreferences sharedPreferences;
    String userType, userId, image_pathUser, nameUser, emailUser;

    NavigationView navigationView;
    SwipeRefreshLayout refreshLayout;

    View hView;
    TextView nav_user, userEmail;
    CircleImageView imageView;
    static boolean booleanCreate = false;


    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_parent);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.viewParentCategoriesRefresh);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(1).setChecked(true);
        hView = navigationView.getHeaderView(0);
        nav_user = (TextView) hView.findViewById(R.id.userName);
        userEmail = (TextView) hView.findViewById(R.id.userEmail);
        imageView = (CircleImageView) hView.findViewById(R.id.userImage);
        myList = (ListView) findViewById(R.id.listView11);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        progressBar.getProgressDrawable().setColorFilter(Color.argb(255, 226, 99, 226), android.graphics.PorterDuff.Mode.SRC_IN);

        sharedPreferences = getSharedPreferences("UserLogin", this.MODE_PRIVATE);
        userType = sharedPreferences.getString("type", "").toString();
        image_pathUser = sharedPreferences.getString("image_path", "");
        nameUser = sharedPreferences.getString("name", "");
        emailUser = sharedPreferences.getString("email", "");
        userId = sharedPreferences.getString("id", "");

        nav_user.setText(nameUser);
        userEmail.setText(emailUser);
        PicassoClient.userImage(ViewParentCategories.this, image_pathUser, imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearData();
                Intent intent = new Intent(ViewParentCategories.this, ProfileActivity.class);
                intent.putExtra("nameActivity", "ViewParentCategories");
                intent.putExtra("idUserProfile", userId);
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
                    Toast.makeText(ViewParentCategories.this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
                }
            }
        });

        booleanCreate = true;
        getJSON();

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean check = false;
                list = new ArrayList<HashMap<String, String>>();
                HashMap<String, String> map = (HashMap) myList.getAdapter().getItem(position);
                catId = map.get(Config.TAG_ID).toString();
                catName = map.get("name").toString();
                catArabicName = map.get("arabicName").toString();

                for (int i = 0; i < cats.size(); i++) {
                    if (cats.get(i).getParent_id().equals(catId)) {
                        subcatarr.add(cats.get(i));
                        check = true;
                    }

                }

                if (!check) {
                    Intent intent = new Intent(ViewParentCategories.this, View_Ads_By_Category.class);
                    sharedCategory = getSharedPreferences("categoryData", MODE_PRIVATE);
                    editor = sharedCategory.edit();
                    editor.clear().commit();
                    editor.putString("categoryId", catId);
                    editor.putString("categoryName", catName);
                    editor.putString("categoryArabicName", catArabicName);
                    editor.commit();
                    startActivity(intent);
                } else {

                    for (int i = 0; i < subcatarr.size(); i++) {
                        categories = new HashMap<>();
                        categories.put(Config.TAG_ID, subcatarr.get(i).getId());
                        categories.put(Config.TAG_NAME, subcatarr.get(i).getName());
                        categories.put("arabicName", subcatarr.get(i).getArabicName());
                        categories.put("numberAds", subcatarr.get(i).getNumberAds());
                        list.add(categories);

                    }
                    combo.add(list);
                    subcatarr.clear();


                    ViewParentCategories.myAdapter adapter = new ViewParentCategories.myAdapter(list);
                    myList.setAdapter(adapter);
                }
            }
        });

        if (isNetworkOnline()) {
            getJSON();
        } else {
            refreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(ViewParentCategories.this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
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
        if (isNetworkOnline()) {
            navigationView.getMenu().getItem(1).setChecked(true);
            getJSON();
        } else {
            refreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(ViewParentCategories.this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
        }
        super.onResume();
    }

    private void showCategory() {
        JSONObject jsonObject = null;
        list = new ArrayList<>();
        combo = new ArrayList<>();
        cats = new ArrayList<>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String id = jo.getString(Config.TAG_ID);
                String name = jo.getString(Config.TAG_NAME);
                String parent_id = jo.getString(Config.TAG_PARENT_ID);
                String arabicName = jo.getString("arabicName");
                String visibility = jo.getString(Config.TAG_VISI);
                String numberAds = jo.getString("numberAds");
                cat = new CategoryClass(id, name, parent_id, arabicName, visibility, numberAds);
                cats.add(cat);
                if (cat.getParent_id().equals("0")) {
                    categories = new HashMap<>();
                    categories.put(Config.TAG_ID, id);
                    categories.put(Config.TAG_NAME, name);
                    categories.put("arabicName", arabicName);
                    categories.put("numberAds", numberAds);
                    list.add(categories);
                }

            }
            combo.add(list);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ViewParentCategories.myAdapter adapter = new ViewParentCategories.myAdapter(list);
        myList.setAdapter(adapter);


    }

    private void getJSON() {

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        progressBar.setSoundEffectsEnabled(true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_ADVERTI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressBar.setVisibility(View.GONE);
                JSON_STRING = response;
                showCategory();
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
                params1.put("function", "getAllCategory");
                return params1;
            }
        };

        Singleton.getSingleton(this).setRequestQue(stringRequest);

    }


    class myAdapter extends BaseAdapter {

        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();


        myAdapter(ArrayList<HashMap<String, String>> list) {
            data = list;

        }


        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {

            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            LayoutInflater inflater = getLayoutInflater();

            View myView = inflater.inflate(R.layout.list_item_category, viewGroup, false);

            TextView name, numberAds;
            name = (TextView) myView.findViewById(R.id.name);
            numberAds = (TextView) myView.findViewById(R.id.numberAds);
            if (Locale.getDefault().getDisplayLanguage().equals(getString(R.string.arabic))) {
                name.setText(data.get(i).get("arabicName"));
            }
            else {
                name.setText(data.get(i).get(Config.TAG_NAME));
            }

            if (data.get(i).get("numberAds").equals("1")) {
                numberAds.setText(data.get(i).get("numberAds") + getString(R.string.ad));
            } else {
                numberAds.setText(data.get(i).get("numberAds") + getString(R.string.ads));
            }
            return myView;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {

        if (isNetworkOnline()) {
            if (combo.size() == 1) {
                combo.clear();
                super.onBackPressed();
            } else {
                list.clear();
                list = combo.get(combo.size() - 2);
                combo.remove(combo.size() - 1);
                ViewParentCategories.myAdapter adapter = new ViewParentCategories.myAdapter(list);
                myList.setAdapter(adapter);
            }
        } else {
            super.onBackPressed();
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
                startActivity(new Intent(ViewParentCategories.this, AddCategoryActivity.class));
                return true;

            case R.id.addAds:
                startActivity(new Intent(ViewParentCategories.this, Add_Ad.class));
                return true;

            case R.id.viewAllCategory:
                startActivity(new Intent(ViewParentCategories.this, ViewAllCategories_Admin.class));
                return true;

            case R.id.viewAllAds:
                startActivity(new Intent(ViewParentCategories.this, ViewAllAds_Admin.class));
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
                clearData();
            }
            Intent intent;
            intent = new Intent(ViewParentCategories.this, HomeActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_categories) {
            if (isNetworkOnline()) {
                clearData();

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
                Toast.makeText(ViewParentCategories.this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
            }

        } else if (id == R.id.nav_profile) {

            if (isNetworkOnline()) {
                clearData();
            }
            Intent intent = new Intent(ViewParentCategories.this, ProfileActivity.class);
            intent.putExtra("nameActivity", "ViewParentCategories");
            intent.putExtra("idUserProfile", userId);
            startActivity(intent);

        } else if (id == R.id.nav_about) {
            if (isNetworkOnline()) {
                clearData();
            }

            Intent intent = new Intent(ViewParentCategories.this, AboutActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {
            if (isNetworkOnline()) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear().commit();
                clearData();
                Intent intent = new Intent(ViewParentCategories.this, LogIn_SignUp_Main.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                Toast.makeText(this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
                navigationView.getMenu().getItem(1).setChecked(true);
            }

        } else if (id == R.id.nav_saved_ads) {

            Intent intent = new Intent(ViewParentCategories.this, SavedAdsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_contact) {

            Intent intent = new Intent(ViewParentCategories.this, ContactUsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void clearData() {
        subcatarr.clear();
        combo.clear();
        list.clear();
        cats.clear();
        categories.clear();
    }
}
