package com.example.muhammed.advertiapp;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bahaa on 21/10/2017.
 */
public class ListViewAdapter extends BaseAdapter {
    //Array list inside it HashMap to get data in the format of blocks of HashMap

    ArrayList<AdsClass> adsArrayList;
    Context c;
    LayoutInflater inflater;

    DisplayMetrics displayMetrics;
    float dpWidth;
    int numberOfCharacters;

    ListViewAdapter(Context c, ArrayList<AdsClass> adsArrayList) {
        this.c = c;
        this.adsArrayList = adsArrayList;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        displayMetrics = c.getResources().getDisplayMetrics();
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        numberOfCharacters = (int) (dpWidth / 18);

    }

    @Override
    public int getCount() {
        return adsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return adsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = inflater.inflate(R.layout.ads_list_item_admin, parent, false);
        }

        //get the ImageView Which id is ->imageView2 in list_item layout mensioned before
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView2);

        //get the TextView Which id is ->nametxt in list_item layout mensioned before
        TextView detail = (TextView) convertView.findViewById(R.id.details);

        //get the TextView Which id is ->idtxt in  layout mensioned before
        TextView nameUser = (TextView) convertView.findViewById(R.id.nameUser);


        TextView rate = (TextView) convertView.findViewById(R.id.rate);


        //for each arraylist position make the following

        PicassoClient.downloadimage(c, adsArrayList.get(position).getImage_path(), imageView);
        if (adsArrayList.get(position).getDetails().length() > numberOfCharacters) {
            detail.setText(adsArrayList.get(position).getDetails().substring(0, numberOfCharacters) + " ...");
        } else {
            detail.setText(adsArrayList.get(position).getDetails());
        }
        nameUser.setText(adsArrayList.get(position).getNameUser());
        rate.setText(adsArrayList.get(position).getRate());


        return convertView;
    }


    public void setFilter(List<AdsClass> adsList) {
        adsArrayList = new ArrayList<>();
        adsArrayList.addAll(adsList);
        notifyDataSetChanged();
    }
}
