package com.example.muhammed.advertiapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Locale;
import java.util.Map;

/**
 * Created by mido elgebaly1 on 11/5/2017.
 */

public class EditCategory extends AppCompatActivity implements View.OnClickListener {

    ArrayList spinnerDataParent, spinnerDataVisibility;
    private EditText editTextNAme;
    private EditText editTextArabicName;


    private int pos = 0, posParent = 0, posVisibility = 0;
    private Spinner spinnerp_id, spinnerVisibility;
    private Button buttonUpdate;
    private Button buttonDelete;

    private String idParent;
    private String JSON_STRING, parent_id_cat;
    public static ArrayList<CategoryClass> cats = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> list;
    private CategoryClass category;
    ArrayAdapter adapterParent, adapterVisibility;

    ProgressBar progressBar;
    SwipeRefreshLayout refreshLayout;
    String name, parent_id, arabicName, visibility;
    HashMap<String, String> hashMap;

    TextInputLayout inputLayoutName, inputLayoutEditArabicName, inputLayoutVisibility;
    Vibrator vib;
    Animation animShake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        Intent intent = getIntent();

        idParent = intent.getStringExtra(Config.CAT_ID);

        editTextNAme = (EditText) findViewById(R.id.editTextName);
        editTextArabicName = (EditText) findViewById(R.id.editTextArabicName);

        buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
        buttonDelete = (Button) findViewById(R.id.buttonDelete);
        spinnerp_id = (Spinner) findViewById(R.id.p_id);
        spinnerVisibility = (Spinner) findViewById(R.id.spinnerVisibility);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.editCategoryRefresh);
        inputLayoutName = (TextInputLayout) findViewById(R.id.inputLayoutName);
        inputLayoutEditArabicName = (TextInputLayout) findViewById(R.id.inputLayoutEditArabicName);
        inputLayoutVisibility = (TextInputLayout) findViewById(R.id.inputLayoutVisibility);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        progressBar.getProgressDrawable().setColorFilter(Color.argb(255, 226, 99, 226), android.graphics.PorterDuff.Mode.SRC_IN);

        buttonUpdate.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);

        spinnerDataParent = new ArrayList();
        spinnerDataVisibility = new ArrayList();

        spinnerDataParent.add(getString(R.string.parent));

        spinnerDataVisibility.add(getString(R.string.Visibility));
        spinnerDataVisibility.add(getString(R.string.InVisible));
        spinnerDataVisibility.add(getString(R.string.Visible));

        adapterVisibility = new ArrayAdapter(EditCategory.this, R.layout.layout_spinner_item, R.id.textViewSpinnerItem, spinnerDataVisibility);
        spinnerVisibility.setAdapter(adapterVisibility);


        hashMap = new HashMap<>();
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

        spinnerVisibility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    posVisibility = 0;

                } else {
                    posVisibility = position - 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                posVisibility = spinnerVisibility.getSelectedItemPosition();
            }
        });


        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.black);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);

                        getAllCategories_ParentCategories();
                    }
                }, 3000);

            }
        });

        getAllCategories_ParentCategories();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllCategories_ParentCategories();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    private void submitForm() {
        inputLayoutEditArabicName.setErrorEnabled(false);
        inputLayoutName.setErrorEnabled(false);
        inputLayoutVisibility.setErrorEnabled(false);


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
        if (!checkVisibility()) {
            spinnerVisibility.setAnimation(animShake);
            spinnerVisibility.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }

        hashMap.put(Config.KEY_CAT_ID, idParent);
        hashMap.put(Config.KEY_CAT_NAME, name);
        hashMap.put(Config.KEY_CAT_PARENT_ID, parent_id);
        hashMap.put("arabicName", arabicName);
        hashMap.put(Config.KEY_CAT_VISI, visibility);
        hashMap.put("function", "updateCategory");

        updateCategory();
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
            inputLayoutEditArabicName.setErrorEnabled(true);
            inputLayoutEditArabicName.setError(getString(R.string.err_arabic_name));
            editTextArabicName.setError(getString(R.string.err_msg_required));
            requestFocus(editTextArabicName);
            return false;
        }
        inputLayoutEditArabicName.setErrorEnabled(false);
        return true;
    }

    private boolean checkVisibility() {
        if (spinnerVisibility.getSelectedItemPosition() == 0) {
            inputLayoutVisibility.setErrorEnabled(true);
            inputLayoutVisibility.setError(getString(R.string.err_visibility_required));
            requestFocus(spinnerVisibility);
            return false;
        }
        inputLayoutVisibility.setErrorEnabled(false);
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void getAllCategories_ParentCategories() {

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_ADVERTI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressBar.setVisibility(View.GONE);
                JSON_STRING = response;
                showAllCategories_ParentCategories();
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
                params1.put("id", idParent);
                params1.put("function", "getAllCategories_ParentCategories");
                return params1;
            }
        };

        Singleton.getSingleton(this).setRequestQue(stringRequest);
    }

    private void showAllCategories_ParentCategories() {

        showAllCategories();
        showParentCategories();
    }

    private void showAllCategories() {
        try {
            JSONObject jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("resultParent");
            JSONObject c = result.getJSONObject(0);
            String name = c.getString(Config.TAG_NAME);
            parent_id_cat = c.getString(Config.TAG_PARENT_ID);
            String arabicName = c.getString("arabicName");
            String visibility = c.getString(Config.TAG_VISI);
            posVisibility = Integer.valueOf(visibility);

            editTextNAme.setText(name);
            editTextArabicName.setText(arabicName);

            spinnerVisibility.setSelection(posVisibility + 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showParentCategories() {

        JSONObject jsonObject = null;
        list = new ArrayList<>();
        spinnerDataParent = new ArrayList();
        spinnerDataParent.add(getString(R.string.parent));
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("resultCategory");
            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String id = jo.getString(Config.TAG_ID);
                String name = jo.getString(Config.TAG_NAME);
                String parent_id = jo.getString(Config.TAG_PARENT_ID);
                String arabicName = jo.getString("arabicName");
                String visibility = jo.getString(Config.TAG_VISI);
                category = new CategoryClass(id, name, parent_id, arabicName, visibility);
                cats.add(category);

                if (Locale.getDefault().getDisplayLanguage().equals(getString(R.string.arabic))) {
                    spinnerDataParent.add(arabicName);
                }
                else {
                    spinnerDataParent.add(name);
                }


                if (parent_id_cat.equals(id)) {
                    posParent = i + 1;
                }

                adapterParent = new ArrayAdapter(EditCategory.this, R.layout.layout_spinner_item, R.id.textViewSpinnerItem, spinnerDataParent);

                spinnerp_id.setAdapter(adapterParent);

                spinnerp_id.setSelection(posParent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void showCategory(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);
            JSONObject c = result.getJSONObject(0);
            String name = c.getString(Config.TAG_NAME);
            parent_id_cat = c.getString(Config.TAG_PARENT_ID);
            String arabicName = c.getString("arabicName");
            String visibility = c.getString(Config.TAG_VISI);
            posVisibility = Integer.valueOf(visibility);

            editTextNAme.setText(name);
            editTextArabicName.setText(arabicName);

            spinnerVisibility.setSelection(posVisibility + 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateCategory() {

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_ADVERTI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("This category is used previously")) {
                    Toast.makeText(EditCategory.this, response, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(EditCategory.this, response, Toast.LENGTH_LONG).show();
                    finish();
                }
                progressBar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return hashMap;
            }
        };

        Singleton.getSingleton(this).setRequestQue(stringRequest);

    }

    private void deleteCategory() {

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_ADVERTI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id", idParent);
                hashMap.put("function", "deleteCategory");
                return hashMap;
            }
        };

        Singleton.getSingleton(this).setRequestQue(stringRequest);

    }

    private void confirmDeleteCategory() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.confirmDeleteCategory));

        alertDialogBuilder.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        deleteCategory();
                        finish();
                    }
                });

        alertDialogBuilder.setNegativeButton(getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        if (v == buttonUpdate) {
            name = editTextNAme.getText().toString().trim();
            if (pos == -1) {
                parent_id = "0";
            } else {
                parent_id = cats.get(pos).getId();
            }
            arabicName = editTextArabicName.getText().toString().trim();
            visibility = String.valueOf(posVisibility);

            submitForm();
        }

        if (v == buttonDelete) {
            confirmDeleteCategory();
        }
    }
}

