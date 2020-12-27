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

public class ViewAllCategories_Admin extends AppCompatActivity {

    public static ArrayList<CategoryClass> cats = new ArrayList<>();
    private CategoryClass cat;
    private String JSON_STRING;
    static HashMap<String, String> categories;
    public static ArrayList<HashMap<String, String>> list;
    ListView myList;

    SharedPreferences sharedPreferences;
    SwipeRefreshLayout refreshLayout;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_categories_admin);
        myList = (ListView) findViewById(R.id.listView);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.viewAllCategoriesAdminRefresh);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar.getProgressDrawable().setColorFilter(Color.argb(255, 226, 99, 226), android.graphics.PorterDuff.Mode.SRC_IN);

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
                    Toast.makeText(ViewAllCategories_Admin.this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
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

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(ViewAllCategories_Admin.this, EditCategory.class);
                HashMap<String, String> map = (HashMap) parent.getItemAtPosition(position);
                String catId = map.get(Config.TAG_ID).toString();
                intent.putExtra(Config.CAT_ID, catId);
                startActivity(intent);
            }
        });
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

    private void showCategory() {
        JSONObject jsonObject = null;
        list = new ArrayList<>();
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
                cat = new CategoryClass(id, name, parent_id, arabicName, visibility);
                cats.add(cat);

                categories = new HashMap<>();
                categories.put(Config.TAG_ID, id);
                categories.put(Config.TAG_NAME, name);
                categories.put("arabicName", arabicName);
                list.add(categories);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        myAdapter adapter = new myAdapter(list);
        myList.setAdapter(adapter);


    }

    private void getJSON() {


        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

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
                params1.put("function", "getAllCategory_Admin");
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
            numberAds.setVisibility(View.GONE);
            return myView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.admin_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.addCategory:
                startActivity(new Intent(ViewAllCategories_Admin.this, AddCategoryActivity.class));
                return true;

            case R.id.addAds:
                startActivity(new Intent(ViewAllCategories_Admin.this, Add_Ad.class));
                return true;

            case R.id.viewAllCategory:
                refreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        getJSON();
                    }
                }, 3000);
                return true;

            case R.id.viewAllAds:
                startActivity(new Intent(ViewAllCategories_Admin.this, ViewAllAds_Admin.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
