package com.example.muhammed.advertiapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by Mu7ammed_A4raf on 06-Dec-17.
 */

class RecyclerViewMoreAdapter extends RecyclerView.Adapter<ViewMoreHolder> {

    ArrayList<HashMap<String, String>> arrayList;
    ArrayList<AdsClass> adsArrayList;
    Context c;
    SharedPreferences sharedPreferences;
    String idUser, idAds, savedAdId;
    Intent intent;
    HashMap<String, String> hashMap;

    DisplayMetrics displayMetrics;
    float dpWidth;
    int numberOfCharacters;

    public RecyclerViewMoreAdapter(Context c, ArrayList<AdsClass> adsArrayList) {
        this.c = c;
        this.adsArrayList = adsArrayList;

        sharedPreferences = c.getSharedPreferences("UserLogin", c.MODE_PRIVATE);
        idUser = sharedPreferences.getString("id", "");

        displayMetrics = c.getResources().getDisplayMetrics();
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        numberOfCharacters = (int) (dpWidth / 18);
        intent = new Intent(c, CommentActivity.class);
    }

    public RecyclerViewMoreAdapter(Context c, ArrayList<AdsClass> adsArrayList, String savedAdId) {
        this.c = c;
        this.adsArrayList = adsArrayList;
        this.savedAdId = savedAdId;

        sharedPreferences = c.getSharedPreferences("UserLogin", c.MODE_PRIVATE);
        idUser = sharedPreferences.getString("id", "");

        displayMetrics = c.getResources().getDisplayMetrics();
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        numberOfCharacters = (int) (dpWidth / 18);
        intent = new Intent(c, CommentActivity.class);

    }

    @Override
    public ViewMoreHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ads_list_item, parent, false);

        ViewMoreHolder vh = new ViewMoreHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(ViewMoreHolder holder, final int position) {
        final String detail = adsArrayList.get(position).getDetails().toString();
        final String imagePath = adsArrayList.get(position).getImage_path().toString();
        final String videoPath = adsArrayList.get(position).getVideo_path().toString();


        final String emailUser = adsArrayList.get(position).getEmailUser().toString();
        final String phoneUser = adsArrayList.get(position).getPhoneUser().toString();

        final String nameUser = adsArrayList.get(position).getNameUser().toString();
        final String idUserCompany = adsArrayList.get(position).getUser_id().toString();


        final String rate = adsArrayList.get(position).getRate().toString();
        final String numberView = adsArrayList.get(position).getViews_num().toString();
        final String countRate = adsArrayList.get(position).getCount_rate().toString();

        if (rate.equals("null")) {
            holder.rate.setText("0.00");
        } else {
            holder.rate.setText(rate);
        }

        holder.nameUser.setText(nameUser);
        if (detail.length() > numberOfCharacters) {
            holder.detail.setText(detail.substring(0, numberOfCharacters) + " ...");
        } else {
            holder.detail.setText(detail);
        }
        PicassoClient.downloadimage(c, imagePath, holder.imageView);


        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                hashMap = new HashMap<>();
                String adsId = adsArrayList.get(position).getId();

                hashMap.put("adsId", adsId);
                hashMap.put("userId", idUser);
                hashMap.put("function", "addVisit");

                intent.putExtra("idUserCompany", idUserCompany);
                intent.putExtra("nameUser", nameUser);
                intent.putExtra("rate", rate);
                intent.putExtra("numberView", numberView);
                intent.putExtra("countRate", countRate);

                intent.putExtra(Config.TAG_DESCRIPTION, detail);
                intent.putExtra(Config.TAG_IMAGE_PATH, imagePath);
                intent.putExtra("video_path", videoPath);
                intent.putExtra("adsId", hashMap.get("adsId").toString());
                intent.putExtra("phoneUser", phoneUser);
                intent.putExtra("emailUser", emailUser);

                intent.putExtra("adsId", hashMap.get("adsId").toString());
                c.startActivity(intent);

            }
        });

        holder.saveAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idAds = adsArrayList.get(position).getId().toString();
                if (savedAdId.contains("," + idAds + ",")) {
                    deleteSaveAd();
                } else {
                    saveAd();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return adsArrayList.size();
    }


    private void showSavedAd(String response) {
        JSONObject jsonObject = null;
        savedAdId = "";
        try {
            jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("resultSavedAd");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                savedAdId += "," + jo.getString(Config.TAG_ID) + ",";

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveAd() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_ADVERTI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(c, c.getString(R.string.saved), Toast.LENGTH_LONG).show();
                showSavedAd(response);
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
                params1.put("adId", idAds);
                params1.put("function", "saveAd");
                return params1;
            }
        };

        Singleton.getSingleton(c).setRequestQue(stringRequest);
    }

    private void deleteSaveAd() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_ADVERTI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(c, c.getString(R.string.Unsaved), Toast.LENGTH_LONG).show();
                showSavedAd(response);
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
                params1.put("adId", idAds);
                params1.put("function", "deleteSaveAd");
                return params1;
            }
        };

        Singleton.getSingleton(c).setRequestQue(stringRequest);
    }


    public void setFilter(List<AdsClass> adsList) {
        adsArrayList = new ArrayList<>();
        adsArrayList.addAll(adsList);
        notifyDataSetChanged();
    }
}
