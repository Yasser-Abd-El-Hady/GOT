package com.example.muhammed.advertiapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Mu7ammed_A4raf on 01-Mar-18.
 */

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView rate, detail;
    ImageView imageView;
    Button saveAd;

    ItemClickListener itemClickListener;

    public ViewHolder(View itemView) {
        super(itemView);

        rate = (TextView) itemView.findViewById(R.id.rate);
        detail = (TextView) itemView.findViewById(R.id.details);
        imageView = (ImageView) itemView.findViewById(R.id.imageView2);
        saveAd = (Button) itemView.findViewById(R.id.saveAd);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        this.itemClickListener.onItemClick(this.getLayoutPosition());
    }
}
