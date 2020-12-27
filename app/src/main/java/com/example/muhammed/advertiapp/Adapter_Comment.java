package com.example.muhammed.advertiapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Bahaa on 21/10/2017.
 */
public class Adapter_Comment extends BaseAdapter {

    ArrayList<HashMap<String, String>> arrayList;
    Context context;
    LayoutInflater inflater;

    Adapter_Comment(Context context, ArrayList<HashMap<String, String>> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_comment, parent, false);
        }
        CircleImageView userImageComment = (CircleImageView) convertView.findViewById(R.id.userImageComment);
        TextView userName = (TextView) convertView.findViewById(R.id.userNameComment);
        TextView textComment = (TextView) convertView.findViewById(R.id.textComment);
        TextView timeComment = (TextView) convertView.findViewById(R.id.timeComment);

        String commentDate = arrayList.get(position).get("time").substring(0, 10);
        String commentDateYear = commentDate.substring(0, 4);
        String commentDateMonth = commentDate.substring(5, 7);
        String commentDateDay = commentDate.substring(8, 10);
        String commentTime = arrayList.get(position).get("time").substring(11);
        String commentTimeHour = commentTime.substring(0, 2);
        String commentTimeMinute = commentTime.substring(3, 5);
        String commentTimeSecond = commentTime.substring(6, 8);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00"));
        Date currentLocalTime = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateFormatYear = new SimpleDateFormat("yyyy");
        DateFormat dateFormatMonth = new SimpleDateFormat("MM");
        DateFormat dateFormatDay = new SimpleDateFormat("dd");
        //DateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        DateFormat dateFormatHour = new SimpleDateFormat("HH");
        DateFormat dateFormatMinute = new SimpleDateFormat("mm");
        DateFormat dateFormatSecond = new SimpleDateFormat("ss");

        int localDate = dateFormat.format(currentLocalTime).compareTo(commentDate);
        //int localTime = simpleDateFormat.format(currentLocalTime).compareTo(commentTime);
        dateFormatHour.setTimeZone(TimeZone.getTimeZone("GMT+2:00"));
        int localTimeYear = Integer.valueOf(dateFormatYear.format(currentLocalTime)) - (Integer.valueOf(commentDateYear));
        int localTimeMonth = Integer.valueOf(dateFormatMonth.format(currentLocalTime)) - (Integer.valueOf(commentDateMonth));
        int localTimeDay = Integer.valueOf(dateFormatDay.format(currentLocalTime)) - (Integer.valueOf(commentDateDay));
        int localTimeHour = Integer.valueOf(dateFormatHour.format(currentLocalTime)) - (Integer.valueOf(commentTimeHour));
        int localTimeMinute = Integer.valueOf(dateFormatMinute.format(currentLocalTime)) - (Integer.valueOf(commentTimeMinute));
        int localTimeSecond = Integer.valueOf(dateFormatSecond.format(currentLocalTime)) - (Integer.valueOf(commentTimeSecond));

        Calendar monthStart = new GregorianCalendar(Integer.valueOf(commentDateYear), Integer.valueOf(commentDateMonth), 0);
        int localTimeDay2 = localTimeDay + monthStart.getActualMaximum(Calendar.DAY_OF_MONTH);

        if (localTimeYear == 0 && localTimeMonth == 0 && localTimeDay == 0) {
            if (localTimeHour == 0) {

                if (localTimeMinute == 0) {

                    if (localTimeSecond == 0) {
                        timeComment.setText(context.getString(R.string.now));
                    } else if (localTimeSecond == 1) {
                        timeComment.setText(localTimeSecond + context.getString(R.string.second));
                    } else {
                        timeComment.setText(localTimeSecond + context.getString(R.string.seconds));
                    }
                } else if (localTimeMinute == 1) {
                    timeComment.setText(localTimeMinute + context.getString(R.string.minute));
                } else {
                    timeComment.setText(localTimeMinute + context.getString(R.string.minutes));
                }
            } else if (localTimeHour == 1 && localTimeMinute < 0) {
                localTimeMinute = 60 - (Integer.valueOf(commentTimeMinute)) + Integer.valueOf(dateFormatMinute.format(currentLocalTime));
                timeComment.setText(localTimeMinute + context.getString(R.string.minutes));
            } else if (localTimeHour == 1 && localTimeMinute > 0) {
                timeComment.setText(localTimeHour + context.getString(R.string.hour));
            } else {
                timeComment.setText(localTimeHour + context.getString(R.string.hours));
            }

        } else if ((localTimeYear == 0 && localTimeMonth == 0 && localTimeDay == 1) || (localTimeYear == 0 && localTimeMonth == 1 && localTimeDay2 == 1) || (localTimeYear == 1 && (Integer.valueOf(commentDateMonth)) == 12 && localTimeDay2 == 1)) {
            timeComment.setText(context.getString(R.string.yesterday) + commentTime);
        } else {
            timeComment.setText(commentDate + context.getString(R.string.at) + commentTime.substring(0, 5));
        }
        userName.setText(arrayList.get(position).get("name"));
        textComment.setText(arrayList.get(position).get("text"));
        if (arrayList.get(position).get("image_path").length() > 0) {
            Picasso.with(context).load(arrayList.get(position).get("image_path")).into(userImageComment);
        } else {
            Picasso.with(context).load(R.mipmap.ic_profile_picture_user).into(userImageComment);
        }

        return convertView;
    }
}
