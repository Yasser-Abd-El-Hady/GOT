package com.example.muhammed.advertiapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import java.util.Map;

public class AddCategoryActivity extends AppCompatActivity implements View.OnClickListener {
    //Defining views
    ArrayList spinnerdata;
    private int pos = 0;
    private EditText editTextNAme;
    private Spinner spinnerp_id;
    private EditText editTextArabicName;


    private Button buttonAdd;
    String name, parent_id, arabicName, visibility;

    SwipeRefreshLayout refreshLayout;


    public static ArrayList<CategoryClass> cats = new ArrayList<>();
    private CategoryClass category;
    private String JSON_STRING;
    public static ArrayList<HashMap<String, String>> list;
    ArrayAdapter adapter;


    ProgressBar progressBar;
    ProgressDialog loading;


    TextInputLayout inputLayoutName, inputLayoutArabicName;
    Vibrator vib;
    Animation animShake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_add);
        //Initializing views
        editTextNAme = (EditText) findViewById(R.id.editTextName);
        spinnerp_id = (Spinner) findViewById(R.id.p_id);
        editTextArabicName = (EditText) findViewById(R.id.editTextArabicName);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.addCategoryRefresh);
        inputLayoutName = (TextInputLayout) findViewById(R.id.inputLayoutName);
        inputLayoutArabicName = (TextInputLayout) findViewById(R.id.inputLayoutArabicName);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

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
                    Toast.makeText(AddCategoryActivity.this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
                }

            }
        });
        spinnerdata = new ArrayList();
        spinnerdata.add(getString(R.string.parent));

        spinnerp_id.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                pos = position - 1;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                pos = spinnerp_id.getSelectedItemPosition();

            }
        });

        buttonAdd = (Button) findViewById(R.id.buttonAdd);


        //Setting listeners to button
        buttonAdd.setOnClickListener(this);


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


    private void submitForm() {
        inputLayoutArabicName.setErrorEnabled(false);
        inputLayoutName.setErrorEnabled(false);

        if (!checkName()) {
            editTextNAme.setAnimation(animShake);
            editTextNAme.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }
        if (!checkArabicName()) {
            editTextArabicName.setAnimation(animShake);
            editTextArabicName.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }

        addCategory();
    }


    private boolean checkName() {
        if (name.isEmpty()) {
            inputLayoutName.setErrorEnabled(true);
            inputLayoutName.setError(getString(R.string.err_msg_name));
            editTextNAme.setError(getString(R.string.err_msg_required));
            requestFocus(editTextNAme);
            return false;
        }
        inputLayoutName.setErrorEnabled(false);
        return true;
    }

    private boolean checkArabicName() {
        if (arabicName.isEmpty()) {
            inputLayoutArabicName.setErrorEnabled(true);
            inputLayoutArabicName.setError(getString(R.string.err_arabic_name));
            editTextArabicName.setError(getString(R.string.err_msg_required));
            requestFocus(editTextArabicName);
            return false;
        }
        inputLayoutArabicName.setErrorEnabled(false);
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void showCategory() {
        JSONObject jsonObject = null;
        list = new ArrayList<>();
        spinnerdata = new ArrayList();
        spinnerdata.add(getString(R.string.parent));
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
                category = new CategoryClass(id, name, parent_id, arabicName, visibility);
                cats.add(category);
                spinnerdata.add(name);

            }

            adapter = new ArrayAdapter(AddCategoryActivity.this, R.layout.layout_spinner_item, R.id.textViewSpinnerItem, spinnerdata);

            spinnerp_id.setAdapter(adapter);

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

    //Adding an employee
    private void addCategory() {

        loading = ProgressDialog.show(AddCategoryActivity.this, "Add Category", "Wait", false, false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_ADVERTI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("This category is used previously")) {
                    Toast.makeText(AddCategoryActivity.this, response, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AddCategoryActivity.this, response, Toast.LENGTH_LONG).show();
                }
                loading.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put(Config.KEY_CAT_NAME, name);
                params.put(Config.KEY_CAT_PARENT_ID, parent_id);
                params.put("arabicName", arabicName);
                params.put(Config.KEY_CAT_VISI, visibility);
                params.put("function", "addCategory");
                return params;
            }
        };

        Singleton.getSingleton(this).setRequestQue(stringRequest);

    }

    @Override
    public void onClick(View v) {
        if (v == buttonAdd) {
            if (isNetworkOnline()) {
                name = editTextNAme.getText().toString().trim();
                if (pos == -1) {
                    parent_id = "0";
                } else {
                    parent_id = cats.get(pos).getId();
                }
                arabicName = editTextArabicName.getText().toString().trim();
                visibility = "1";

                submitForm();
            } else {
                Toast.makeText(AddCategoryActivity.this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
            }
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
                refreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        getJSON();
                    }
                }, 3000);
                return true;

            case R.id.addAds:
                startActivity(new Intent(AddCategoryActivity.this, Add_Ad.class));
                return true;

            case R.id.viewAllCategory:
                startActivity(new Intent(AddCategoryActivity.this, ViewAllCategories_Admin.class));
                return true;

            case R.id.viewAllAds:
                startActivity(new Intent(AddCategoryActivity.this, ViewAllAds_Admin.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


