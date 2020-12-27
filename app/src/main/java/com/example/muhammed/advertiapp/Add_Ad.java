package com.example.muhammed.advertiapp;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Add_Ad extends AppCompatActivity {

    ImageView adPicture;
    Button adVideo;
    EditText detailsEditText, startDateEditText, startTimeEditText, expireDateEditText, expireTimeEditText;
    Button categoriesSelectedBtn, add_AdBtn;
    Spinner companyNameSpinner;
    ProgressBar progressBar;
    SwipeRefreshLayout refreshLayout;

    private static final int SELECTED_PICTURE = 1, CROP_PICTURE = 2, SELECT_VIDEO = 3;
    Uri imageUri;

    String JSON_STRING, strAdPictureName, strAdPicturePath, strDetails, strStartDate, strStartTime, strExpireDate, strExpireTime, companyId,
            categoryIds, selectedPathVideo;

    HashMap<String, String> hashMapAdd_ad = new HashMap<String, String>();

    public static String[] allCategories;
    boolean[] checkedCategories;
    private CategoryClass category;
    public static ArrayList<CategoryClass> arrayListCategories = new ArrayList<>();
    public static List<String> selectedCategory;
    ArrayList spinnerData;
    ArrayAdapter adapter;
    private UserClass userClass;
    public static ArrayList<UserClass> userClassArrayList;
    int positionSpinner;


    TextInputLayout inputLayoutDetails, inputLayoutStartDate, inputLayoutStartTime, inputLayoutExpireDate, inputLayoutExpireTime,
            inputLayoutCompanyName;
    Vibrator vib;
    Animation animShake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__ad);

        adPicture = (ImageView) findViewById(R.id.adPicture);
        adVideo = (Button) findViewById(R.id.adVideo);
        detailsEditText = (EditText) findViewById(R.id.details);
        startDateEditText = (EditText) findViewById(R.id.startDate);
        startTimeEditText = (EditText) findViewById(R.id.startTime);
        expireDateEditText = (EditText) findViewById(R.id.expireDate);
        expireTimeEditText = (EditText) findViewById(R.id.expireTime);
        categoriesSelectedBtn = (Button) findViewById(R.id.categoriesSelected);
        add_AdBtn = (Button) findViewById(R.id.add_Ad);
        companyNameSpinner = (Spinner) findViewById(R.id.companyName);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.Add_AdRefresh);
        inputLayoutDetails = (TextInputLayout) findViewById(R.id.inputLayoutDetails);
        inputLayoutStartDate = (TextInputLayout) findViewById(R.id.inputLayoutStartDate);
        inputLayoutStartTime = (TextInputLayout) findViewById(R.id.inputLayoutStartTime);
        inputLayoutExpireDate = (TextInputLayout) findViewById(R.id.inputLayoutExpireDate);
        inputLayoutExpireTime = (TextInputLayout) findViewById(R.id.inputLayoutExpireTime);
        inputLayoutCompanyName = (TextInputLayout) findViewById(R.id.inputLayoutCompanyName);

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
                            getJSONCategories_Companies();
                        }
                    }, 3000);
                } else {
                    refreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Add_Ad.this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
                }

            }
        });

        startDateEditText.setFocusable(false);
        startTimeEditText.setFocusable(false);
        expireDateEditText.setFocusable(false);
        expireTimeEditText.setFocusable(false);


        animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        strAdPictureName = "";
        categoryIds = "";
        selectedPathVideo = "";
        selectedCategory = new ArrayList<>();
        spinnerData = new ArrayList();
        spinnerData.add("Company Name");

        // Selecting an image from gallery
        adPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(photoPickerIntent, getString(R.string.selectImage)), SELECTED_PICTURE);
            }
        });

        // Selecting a video from gallery
        adVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, SELECT_VIDEO);
                } else {
                    Toast.makeText(Add_Ad.this, getString(R.string.sdkPhone), Toast.LENGTH_LONG).show();
                }
            }
        });

        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialog dialog = new DateDialog(Add_Ad.this, v, true);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "DatePicker");
                startDateEditText.setFocusable(false);
                startDateEditText.setClickable(true);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Add_Ad.this);
                builder.setMultiChoiceItems(allCategories, checkedCategories, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        checkedCategories[which] = isChecked;
                        if (isChecked == true) {
                            selectedCategory.add("" + arrayListCategories.get(which).getId());
                        }
                        if (isChecked == false) {
                            selectedCategory.remove(Integer.valueOf(arrayListCategories.get(which).getId()));
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
                if (position != 0) {
                    positionSpinner = position - 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                positionSpinner = companyNameSpinner.getSelectedItemPosition();
            }
        });

        add_AdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkOnline()) {
                    strDetails = detailsEditText.getText().toString().trim();
                    strStartDate = startDateEditText.getText().toString().trim();
                    strStartTime = startTimeEditText.getText().toString().trim();
                    strExpireDate = expireDateEditText.getText().toString().trim();
                    strExpireTime = expireTimeEditText.getText().toString().trim();
                    companyId = userClassArrayList.get(positionSpinner).getId();
                    submitForm();
                } else {
                    Toast.makeText(Add_Ad.this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
                }
            }
        });

        if (isNetworkOnline()) {
            getJSONCategories_Companies();
        } else {
            refreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (isNetworkOnline()) {
            getJSONCategories_Companies();
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

    private void submitForm() {
        inputLayoutDetails.setErrorEnabled(false);
        inputLayoutStartDate.setErrorEnabled(false);
        inputLayoutStartTime.setErrorEnabled(false);
        inputLayoutExpireDate.setErrorEnabled(false);
        inputLayoutExpireTime.setErrorEnabled(false);
        inputLayoutCompanyName.setErrorEnabled(false);


        if (!checkImage()) {
            return;
        }
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
        hashMapAdd_ad.put("function", "add_ad");
        hashMapAdd_ad.put("adPictureName", strAdPictureName);
        hashMapAdd_ad.put("adPicturePath", strAdPicturePath);
        hashMapAdd_ad.put("details", strDetails);
        hashMapAdd_ad.put("startDate", strStartDate + " " + strStartTime);
        hashMapAdd_ad.put("expireDate", strExpireDate + " " + strExpireTime);
        hashMapAdd_ad.put("categoryIds", categoryIds);
        hashMapAdd_ad.put("companyId", companyId);

        getJSONAdd_Ad();


    }


    private boolean checkImage() {
        if (strAdPictureName.isEmpty()) {
            Toast.makeText(Add_Ad.this, getString(R.string.enterImage), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
            Toast.makeText(Add_Ad.this, getString(R.string.err_select_category), Toast.LENGTH_SHORT).show();
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
                HashMap<String, String> hashMapCategory = new HashMap<>();
                hashMapCategory.put("function", "getAllCategory_Company");
                return hashMapCategory;
            }
        };

        Singleton.getSingleton(this).setRequestQue(stringRequest);
    }

    private void showCategories_Companies() {

        getAllCategories();

        getAllCompanies();


    }

    private void getAllCategories() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("resultCategory");
            allCategories = new String[result.length()];
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

    private void getAllCompanies() {
        userClassArrayList = new ArrayList<>();
        spinnerData = new ArrayList();
        spinnerData.add("Company Name");
        try {
            JSONObject jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("resultCompany");
            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String id = jo.getString(Config.TAG_ID);
                String name = jo.getString(Config.TAG_NAME);

                userClass = new UserClass(id, name);
                userClassArrayList.add(userClass);
                spinnerData.add(name);

            }

            adapter = new ArrayAdapter(Add_Ad.this, R.layout.layout_spinner_item, R.id.textViewSpinnerItem, spinnerData);
            companyNameSpinner.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void getJSONAdd_Ad() {

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_ADVERTI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressBar.setVisibility(View.GONE);
                if (response.contains("Done")) {
                    Toast.makeText(Add_Ad.this, "Ad Added", Toast.LENGTH_SHORT).show();
                    if (!selectedPathVideo.equals("")) {
                        uploadVideo();
                    }
                } else {
                    Toast.makeText(Add_Ad.this, "Failed", Toast.LENGTH_SHORT).show();
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
                return hashMapAdd_ad;
            }
        };

        Singleton.getSingleton(this).setRequestQue(stringRequest);
    }


    private void uploadVideo() {
        class Upload extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(Add_Ad.this, s, Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String doInBackground(Void... params) {
                UploadVideo u = new UploadVideo();
                String msg = u.uploadVideo(selectedPathVideo);
                return msg;

            }
        }
        Upload uv = new Upload();
        uv.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECTED_PICTURE && resultCode == RESULT_OK) {

            if (data != null) {
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
            } else {
                Toast.makeText(Add_Ad.this, getString(R.string.pickedImage), Toast.LENGTH_LONG).show();
            }
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
            } else {
                Toast.makeText(Add_Ad.this, getString(R.string.cropImage), Toast.LENGTH_LONG).show();
            }

        } else if (requestCode == SELECT_VIDEO) {

            if (data != null) {
                Uri selectedImageUri = data.getData();
                selectedPathVideo = getPath(selectedImageUri);
            } else {
                Toast.makeText(Add_Ad.this, getString(R.string.pickedVideo), Toast.LENGTH_LONG).show();
            }

        }
    }


    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);

        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();


        return path;
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
                startActivity(new Intent(Add_Ad.this, AddCategoryActivity.class));
                return true;

            case R.id.addAds:
                refreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        getJSONCategories_Companies();
                        //getJSONCategories();
                    }
                }, 3000);
                return true;

            case R.id.viewAllCategory:
                startActivity(new Intent(Add_Ad.this, ViewAllCategories_Admin.class));
                return true;

            case R.id.viewAllAds:
                startActivity(new Intent(Add_Ad.this, ViewAllAds_Admin.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
