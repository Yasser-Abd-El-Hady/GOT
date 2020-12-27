package com.example.muhammed.advertiapp;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Edit_Ad extends AppCompatActivity {

    ImageView adPicture;
    EditText detailsEditText, startDateEditText, expireDateEditText, startTimeEditText, expireTimeEditText;
    Button categoriesSelectedBtn, update_AdBtn, delete_AdBtn;
    Spinner companyNameSpinner, spinnerVisibility;
    ProgressBar progressBar;
    SwipeRefreshLayout refreshLayout;

    private static final int SELECTED_PICTURE = 1, CROP_PICTURE = 2;
    Uri imageUri;

    String JSON_STRING, strAdPictureName, strAdPicturePath, strDetails, strStartDate, strExpireDate, strStartTime, strExpireTime, companyId,
            categoryIds, strVisibility, idAd;

    HashMap<String, String> hashMapEdit_ad;

    public static String[] allCategories;
    boolean[] checkedCategories;
    private CategoryClass category;
    public static ArrayList<CategoryClass> arrayListCategories;
    public static List<String> selectedCategory;
    ArrayList spinnerDataCompany, spinnerDataVisibility;
    ArrayAdapter adapter, adapterVisibility;
    private UserClass userClass;
    public static ArrayList<UserClass> userClassArrayList;
    int positionSpinner, positionSpinnerVisibility;


    TextInputLayout inputLayoutDetails, inputLayoutStartDate, inputLayoutExpireDate, inputLayoutStartTime, inputLayoutExpireTime,
            inputLayoutCompanyName, inputLayoutVisibility;
    Vibrator vib;
    Animation animShake;
    String[] categoriesId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__ad);


        adPicture = (ImageView) findViewById(R.id.adPictureEdit);
        detailsEditText = (EditText) findViewById(R.id.editDetails);
        startDateEditText = (EditText) findViewById(R.id.editStartDate);
        expireDateEditText = (EditText) findViewById(R.id.editExpireDate);
        startTimeEditText = (EditText) findViewById(R.id.editStartTime);
        expireTimeEditText = (EditText) findViewById(R.id.editExpireTime);
        categoriesSelectedBtn = (Button) findViewById(R.id.categoriesSelectedEdit);
        update_AdBtn = (Button) findViewById(R.id.edit_Ad);
        delete_AdBtn = (Button) findViewById(R.id.delete_Ad);
        companyNameSpinner = (Spinner) findViewById(R.id.editCompanyName);
        spinnerVisibility = (Spinner) findViewById(R.id.spinnerVisibility);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.edit_AdRefresh);
        inputLayoutDetails = (TextInputLayout) findViewById(R.id.inputLayoutEditDetails);
        inputLayoutStartDate = (TextInputLayout) findViewById(R.id.inputLayoutStartDateEdit);
        inputLayoutExpireDate = (TextInputLayout) findViewById(R.id.inputLayoutExpireDateEdit);
        inputLayoutStartTime = (TextInputLayout) findViewById(R.id.inputLayoutStartTimeEdit);
        inputLayoutExpireTime = (TextInputLayout) findViewById(R.id.inputLayoutExpireTimeEdit);
        inputLayoutCompanyName = (TextInputLayout) findViewById(R.id.inputLayoutCompanyNameEdit);
        inputLayoutVisibility = (TextInputLayout) findViewById(R.id.inputLayoutVisibility);

        arrayListCategories = new ArrayList<>();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar.getProgressDrawable().setColorFilter(Color.argb(255, 226, 99, 226), android.graphics.PorterDuff.Mode.SRC_IN);

        refreshLayout.setColorSchemeResources(R.color.purple, R.color.colorPrimary, R.color.colorPrimaryDark);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        getJSONCategories_Companies();
                    }
                }, 3000);
            }
        });

        startDateEditText.setFocusable(false);
        expireDateEditText.setFocusable(false);
        startTimeEditText.setFocusable(false);
        expireTimeEditText.setFocusable(false);


        animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        categoryIds = "";
        selectedCategory = new ArrayList<>();

        spinnerDataCompany = new ArrayList();
        spinnerDataCompany.add("Company Name");

        hashMapEdit_ad = new HashMap<String, String>();
        hashMapEdit_ad.put("function", "edit_ad");

        spinnerDataVisibility = new ArrayList();
        spinnerDataVisibility.add(getString(R.string.Visibility));
        spinnerDataVisibility.add(getString(R.string.InVisible));
        spinnerDataVisibility.add(getString(R.string.Visible));

        adapterVisibility = new ArrayAdapter(Edit_Ad.this, R.layout.layout_spinner_item, R.id.textViewSpinnerItem, spinnerDataVisibility);
        spinnerVisibility.setAdapter(adapterVisibility);

        Intent intent = getIntent();
        idAd = intent.getStringExtra("idAd");
        strDetails = intent.getStringExtra("detailsAd");
        companyId = intent.getStringExtra("companyId");
        strVisibility = intent.getStringExtra("visibilityAd");
        strAdPicturePath = intent.getStringExtra("pathAd");
        strStartDate = intent.getStringExtra("startDateAd").substring(0, 10);
        strExpireDate = intent.getStringExtra("expireDateAd").substring(0, 10);
        strStartTime = intent.getStringExtra("startDateAd").substring(11);
        strExpireTime = intent.getStringExtra("expireDateAd").substring(11);

        if (strAdPicturePath.length() > 0 && strAdPicturePath != null) {

            Picasso.with(this).load(strAdPicturePath).into(adPicture);
        } else {
            Picasso.with(this).load(R.drawable.picture_ad).into(adPicture);
        }
        detailsEditText.setText(strDetails);
        startDateEditText.setText(strStartDate);
        expireDateEditText.setText(strExpireDate);
        startTimeEditText.setText(strStartTime);
        expireTimeEditText.setText(strExpireTime);
        spinnerVisibility.setSelection(Integer.valueOf(strVisibility) + 1);

        adPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(photoPickerIntent, getString(R.string.selectImage)), SELECTED_PICTURE);
            }
        });

        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialog dialog = new DateDialog(v, true);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "DatePicker");
                startDateEditText.setFocusable(false);
                startDateEditText.setClickable(true);
            }
        });

        expireDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialog dialog = new DateDialog(v, true);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "DatePicker");
                expireDateEditText.setFocusable(false);
                expireDateEditText.setClickable(true);
            }
        });


        startTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeDialog dialog = new TimeDialog(v);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "TimePicker");
                startTimeEditText.setFocusable(false);
                startTimeEditText.setClickable(true);
            }
        });

        expireTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeDialog dialog = new TimeDialog(v);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "TimePicker");
                expireTimeEditText.setFocusable(false);
                expireTimeEditText.setClickable(true);
            }
        });

        categoriesSelectedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Edit_Ad.this);
                builder.setMultiChoiceItems(allCategories, checkedCategories, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        checkedCategories[which] = isChecked;
                        if (isChecked == true) {
                            selectedCategory.add("" + arrayListCategories.get(which).getId());
                        } else {
                            selectedCategory.remove(Integer.valueOf(arrayListCategories.get(which).getId()).toString());
                        }
                    }

                });

                builder.setTitle("Select Category");
                builder.setCancelable(false);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        categoryIds = "";
                        for (int i = 0; i < selectedCategory.size(); i++) {
                            if (i == 0) {
                                categoryIds += selectedCategory.get(i);
                            } else {
                                categoryIds += " " + selectedCategory.get(i);
                            }
                        }

                    }
                });


                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        companyNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Toast.makeText(Edit_Ad.this, getString(R.string.err_company_name_required), Toast.LENGTH_SHORT).show();

                } else {
                    positionSpinner = position - 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                positionSpinner = companyNameSpinner.getSelectedItemPosition();
            }
        });

        spinnerVisibility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    positionSpinnerVisibility = position - 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                positionSpinnerVisibility = spinnerVisibility.getSelectedItemPosition();
            }
        });

        update_AdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strDetails = detailsEditText.getText().toString().trim();
                strStartDate = startDateEditText.getText().toString().trim();
                strExpireDate = expireDateEditText.getText().toString().trim();
                strStartTime = startTimeEditText.getText().toString().trim();
                strExpireTime = expireTimeEditText.getText().toString().trim();
                companyId = userClassArrayList.get(positionSpinner).getId();
                strVisibility = String.valueOf(positionSpinnerVisibility);
                submitForm();
            }
        });

        delete_AdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDeleteAd();
            }
        });

        getJSONCategories_Companies();
    }

    private void submitForm() {
        inputLayoutDetails.setErrorEnabled(false);
        inputLayoutStartDate.setErrorEnabled(false);
        inputLayoutExpireDate.setErrorEnabled(false);
        inputLayoutStartTime.setErrorEnabled(false);
        inputLayoutExpireTime.setErrorEnabled(false);
        inputLayoutCompanyName.setErrorEnabled(false);
        inputLayoutVisibility.setErrorEnabled(false);

        if (!checkDetails()) {
            detailsEditText.setAnimation(animShake);
            detailsEditText.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }
        if (!checkStartDate()) {
            startDateEditText.setAnimation(animShake);
            startDateEditText.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }

        if (!checkStartTime()) {
            startTimeEditText.setAnimation(animShake);
            startTimeEditText.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }
        if (!checkExpiryDate()) {
            expireDateEditText.setAnimation(animShake);
            expireDateEditText.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }
        if (!checkExpiryTime()) {
            expireTimeEditText.setAnimation(animShake);
            expireTimeEditText.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }
        if (!checkCategory()) {
            return;
        }
        if (!checkCompanyName()) {
            companyNameSpinner.setAnimation(animShake);
            companyNameSpinner.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }
        if (!checkVisibility()) {
            spinnerVisibility.setAnimation(animShake);
            spinnerVisibility.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }


        hashMapEdit_ad.put("adId", idAd);
        hashMapEdit_ad.put("details", strDetails);
        hashMapEdit_ad.put("startDate", strStartDate + " " + strStartTime);
        hashMapEdit_ad.put("expireDate", strExpireDate + " " + strExpireTime);
        hashMapEdit_ad.put("categoryIds", categoryIds);
        hashMapEdit_ad.put("companyId", companyId);
        hashMapEdit_ad.put("Visibility", strVisibility);


        getJSONEdit_Ad();

    }

    private boolean checkDetails() {
        if (strDetails.isEmpty()) {
            inputLayoutDetails.setErrorEnabled(true);
            inputLayoutDetails.setError(getString(R.string.err_details_name));
            detailsEditText.setError(getString(R.string.err_msg_required));
            requestFocus(detailsEditText);
            return false;
        }
        inputLayoutDetails.setErrorEnabled(false);
        return true;
    }

    private boolean checkStartDate() {
        if (strStartDate.isEmpty()) {
            inputLayoutStartDate.setErrorEnabled(true);
            inputLayoutStartDate.setError(getString(R.string.err_start_date));
            startDateEditText.setError(getString(R.string.err_msg_required));
            requestFocus(startDateEditText);
            return false;
        }
        inputLayoutStartDate.setErrorEnabled(false);
        return true;
    }

    private boolean checkStartTime() {
        if (strStartTime.isEmpty()) {
            inputLayoutStartTime.setErrorEnabled(true);
            inputLayoutStartTime.setError(getString(R.string.err_start_time));
            startTimeEditText.setError(getString(R.string.err_msg_required));
            requestFocus(startTimeEditText);
            return false;
        }
        inputLayoutStartTime.setErrorEnabled(false);
        return true;
    }

    private boolean checkExpiryDate() {
        if (strExpireDate.isEmpty()) {
            inputLayoutExpireDate.setErrorEnabled(true);
            inputLayoutExpireDate.setError(getString(R.string.err_expiry_date));
            expireDateEditText.setError(getString(R.string.err_msg_required));
            requestFocus(expireDateEditText);
            return false;
        }
        inputLayoutExpireDate.setErrorEnabled(false);
        return true;
    }

    private boolean checkExpiryTime() {
        if (strExpireTime.isEmpty()) {
            inputLayoutExpireTime.setErrorEnabled(true);
            inputLayoutExpireTime.setError(getString(R.string.err_expiry_time));
            expireTimeEditText.setError(getString(R.string.err_msg_required));
            requestFocus(expireTimeEditText);
            return false;
        }
        inputLayoutExpireTime.setErrorEnabled(false);
        return true;
    }

    private boolean checkCategory() {
        if (categoryIds.isEmpty()) {
            Toast.makeText(Edit_Ad.this, getString(R.string.err_select_category), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkCompanyName() {
        if (companyNameSpinner.getSelectedItemPosition() == 0) {
            inputLayoutCompanyName.setErrorEnabled(true);
            inputLayoutCompanyName.setError(getString(R.string.err_company_name_required));
            requestFocus(companyNameSpinner);
            return false;
        }
        inputLayoutCompanyName.setErrorEnabled(false);
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    private void getJSONCategories_Companies() {

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_ADVERTI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressBar.setVisibility(View.GONE);
                JSON_STRING = response;
                showCategories_Companies();
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
                hashMap.put("function", "getAllCategory_Selected_Company");
                hashMap.put("id", idAd);
                return hashMap;
            }
        };

        Singleton.getSingleton(this).setRequestQue(stringRequest);
    }

    private void showCategories_Companies() {

        getAllCategories();

        getCategorySelected();

        getAllCompanies();
    }


    private void getAllCategories() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("resultCategory");
            allCategories = new String[result.length()];
            arrayListCategories = new ArrayList<>();
            checkedCategories = new boolean[result.length()];
            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String id = jo.getString(Config.TAG_ID);
                String name = jo.getString(Config.TAG_NAME);
                String parent_id = jo.getString(Config.TAG_PARENT_ID);
                String arabicName = jo.getString("arabicName");
                String visibility = jo.getString(Config.TAG_VISI);
                category = new CategoryClass(id, name, parent_id, arabicName, visibility);
                arrayListCategories.add(category);
                if (Locale.getDefault().getDisplayLanguage().equals(getString(R.string.arabic))) {
                    allCategories[i] = arabicName;
                }
                else {
                    allCategories[i] = name;
                }
                checkedCategories[i] = false;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void getCategorySelected() {
        try {
            JSONObject jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("resultCategorySelected");
            categoryIds = "";
            categoriesId = new String[result.length()];
            for (int i = 0; i < result.length(); i++) {
                JSONObject c = result.getJSONObject(i);
                categoriesId[i] = c.getString("category_id");
            }

            for (int i = 0; i < arrayListCategories.size(); i++) {
                for (int j = 0; j < categoriesId.length; j++) {
                    if (arrayListCategories.get(i).getId().equals(categoriesId[j])) {
                        checkedCategories[i] = true;
                        selectedCategory.add("" + arrayListCategories.get(i).getId());
                        if (j == 0) {
                            categoryIds += selectedCategory.get(j);
                        } else {
                            categoryIds += " " + selectedCategory.get(j);
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getAllCompanies() {

        userClassArrayList = new ArrayList<>();
        spinnerDataCompany = new ArrayList();
        spinnerDataCompany.add("Company Name");
        try {
            JSONObject jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("resultCompany");
            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String id = jo.getString(Config.TAG_ID);
                String name = jo.getString(Config.TAG_NAME);

                userClass = new UserClass(id, name);
                userClassArrayList.add(userClass);
                spinnerDataCompany.add(name);
                adapter = new ArrayAdapter(Edit_Ad.this, R.layout.layout_spinner_item, R.id.textViewSpinnerItem, spinnerDataCompany);
                companyNameSpinner.setAdapter(adapter);
                if (companyId.equals(id)) {
                    positionSpinnerVisibility = i + 1;
                }

            }
            companyNameSpinner.setSelection(positionSpinnerVisibility);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getJSONEdit_Ad() {

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_ADVERTI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressBar.setVisibility(View.GONE);
                if (response.contains("Done")) {
                    Toast.makeText(Edit_Ad.this, getString(R.string.editedSuccessfully), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(Edit_Ad.this, getString(R.string.editFailed), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return hashMapEdit_ad;
            }
        };

        Singleton.getSingleton(this).setRequestQue(stringRequest);
    }

    private void confirmDeleteAd() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.confirmDeleteAd));

        alertDialogBuilder.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        getJSONDeleteAd();
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


    private void getJSONDeleteAd() {

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_ADVERTI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressBar.setVisibility(View.GONE);
                if (response.equals("Done")) {
                    Toast.makeText(Edit_Ad.this, getString(R.string.deleteAd), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Edit_Ad.this, getString(R.string.failedDeleteAd), Toast.LENGTH_LONG).show();
                }
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
                params1.put("function", "deleteAd");
                params1.put("adId", idAd);
                return params1;
            }
        };

        Singleton.getSingleton(this).setRequestQue(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECTED_PICTURE && resultCode == RESULT_OK) {

            imageUri = data.getData();

            Intent photoPickerCrop = new Intent("com.android.camera.action.CROP");
            photoPickerCrop.setDataAndType(imageUri, "image/*");
            photoPickerCrop.putExtra("crop", "true");
            // indicate aspect of desired crop
            photoPickerCrop.putExtra("aspectX", 1);
            photoPickerCrop.putExtra("aspectY", 1);
            // indicate output X and Y
            photoPickerCrop.putExtra("outputX", 360);
            photoPickerCrop.putExtra("outputY", 360);
            // retrieve data on return
            photoPickerCrop.putExtra("scaleUpIfNeeded", true);
            photoPickerCrop.putExtra("return-data", true);

            startActivityForResult(photoPickerCrop, CROP_PICTURE);
        } else if (requestCode == CROP_PICTURE) {

            if (data != null) {
                Bundle bundle = data.getExtras();
                Bitmap selectedImage = bundle.getParcelable("data");
                adPicture.setImageBitmap(selectedImage);

                ByteArrayOutputStream byteArrayOutputStreamObject;
                byteArrayOutputStreamObject = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);
                byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
                strAdPictureName = "GOT_IMG_" + String.valueOf(System.currentTimeMillis());
                strAdPicturePath = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

                hashMapEdit_ad.put("function", "edit_adWithPicture");
                hashMapEdit_ad.put("adPictureName", strAdPictureName);
                hashMapEdit_ad.put("adPicturePath", strAdPicturePath);
            } else {
                Toast.makeText(Edit_Ad.this, getString(R.string.cropImage), Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(Edit_Ad.this, getString(R.string.pickedImage), Toast.LENGTH_LONG).show();
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
                startActivity(new Intent(Edit_Ad.this, AddCategoryActivity.class));
                return true;

            case R.id.addAds:
                refreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        getJSONCategories_Companies();
                    }
                }, 3000);
                return true;

            case R.id.viewAllCategory:
                startActivity(new Intent(Edit_Ad.this, ViewAllCategories_Admin.class));
                return true;

            case R.id.viewAllAds:
                startActivity(new Intent(Edit_Ad.this, ViewAllAds_Admin.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
