package com.example.muhammed.advertiapp;

/**
 * Created by mido elgebaly1 on 11/6/2017.
 */


public class AdsClass {

    private String id;
    private String start_date;
    private String expire_date;
    private String rate;
    private String views_num;
    private String details;
    private String user_id;
    private String image_path;
    private String video_path;
    private String visible;
    private String nameUser;
    private String emailUser;
    private String phoneUser;
    private String count_rate;


    public AdsClass() {
    }

    public AdsClass(String id, String start_date, String expire_date, String rate, String views_num, String details, String user_id, String image_path, String nameUser, String emailUser, String phoneUser, String count_rate) {
        this.id = id;
        this.start_date = start_date;
        this.expire_date = expire_date;
        this.rate = rate;
        this.views_num = views_num;
        this.details = details;
        this.user_id = user_id;
        this.image_path = image_path;
        this.nameUser = nameUser;
        this.emailUser = emailUser;
        this.phoneUser = phoneUser;
        this.count_rate = count_rate;
    }

    public AdsClass(String id, String start_date, String expire_date, String rate, String views_num, String details, String user_id, String image_path, String visible) {
        this.id = id;
        this.start_date = start_date;
        this.expire_date = expire_date;
        this.rate = rate;
        this.views_num = views_num;
        this.details = details;
        this.user_id = user_id;
        this.image_path = image_path;
        this.visible = visible;
    }

    public AdsClass(String id, String start_date, String expire_date, String rate, String views_num, String details, String user_id, String image_path) {
        this.id = id;
        this.start_date = start_date;
        this.expire_date = expire_date;
        this.rate = rate;
        this.views_num = views_num;
        this.details = details;
        this.user_id = user_id;
        this.image_path = image_path;
        this.nameUser = nameUser;
        this.emailUser = emailUser;
    }

    public AdsClass(String id, String start_date, String expire_date, String rate, String views_num, String details, String user_id, String image_path, String nameUser, String emailUser) {
        this.id = id;
        this.start_date = start_date;
        this.expire_date = expire_date;
        this.rate = rate;
        this.views_num = views_num;
        this.details = details;
        this.user_id = user_id;
        this.image_path = image_path;
        this.nameUser = nameUser;
        this.emailUser = emailUser;
    }

    public AdsClass(String id, String start_date, String expire_date, String rate, String views_num, String details, String user_id, String image_path, String visible, String nameUser, String nothing) {
        this.id = id;
        this.start_date = start_date;
        this.expire_date = expire_date;
        this.rate = rate;
        this.views_num = views_num;
        this.details = details;
        this.user_id = user_id;
        this.image_path = image_path;
        this.visible = visible;
        this.nameUser = nameUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getExpire_date() {
        return expire_date;
    }

    public void setExpire_date(String expire_date) {
        this.expire_date = expire_date;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getViews_num() {
        return views_num;
    }

    public void setViews_num(String views_num) {
        this.views_num = views_num;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getPhoneUser() {
        return phoneUser;
    }

    public void setPhoneUser(String phoneUser) {
        this.phoneUser = phoneUser;
    }

    public String getCount_rate() {
        return count_rate;
    }

    public void setCount_rate(String count_rate) {
        this.count_rate = count_rate;
    }

    public String getVideo_path() {
        return video_path;
    }

    public void setVideo_path(String video_path) {
        this.video_path = video_path;
    }
}
