package com.example.muhammed.advertiapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * Created by Bahaa on 22/10/2017.
 */
public class PicassoClient {
    public static void downloadimage(Context c, String imageurl, ImageView img) {
        if (imageurl.length() > 0 && imageurl != null) {
            Picasso.with(c).load(imageurl).into(img);
        } else {
            Picasso.with(c).load(R.drawable.olx).into(img);
        }
    }

    public static void frameImage(Context c, String videoPath, ImageView videoImageView) {
        if (!videoPath.equals("null")) {
            try {
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
                int currentPosition = 1;
                Bitmap bmFrame = mediaMetadataRetriever
                        .getFrameAtTime(currentPosition * 1000);
                if (bmFrame == null) {

                } else {
                    videoImageView.setImageBitmap(bmFrame);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        } else {
            videoPath = Config.URL + "Upload/Ads/defaultAd.mp4";
            try {
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
                int currentPosition = 1;
                Bitmap bmFrame = mediaMetadataRetriever
                        .getFrameAtTime(currentPosition * 1000);
                if (bmFrame == null) {

                } else {
                    videoImageView.setImageBitmap(bmFrame);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public static void userImage(Context c, String imageurl, ImageView img) {
        if (imageurl.length() > 0 && imageurl != null) {
            Picasso.with(c).load(imageurl).into(img);
        } else {
            Picasso.with(c).load(R.mipmap.ic_profile_picture_user).into(img);
        }
    }
}
