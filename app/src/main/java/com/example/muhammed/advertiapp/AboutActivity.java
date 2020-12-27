package com.example.muhammed.advertiapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;
    View hView;
    TextView nav_user, userEmail;
    ImageView imageView;
    SharedPreferences sharedPreferences;
    String JSON_STRING, userType, idUser, image_pathUser, nameUser, emailUser;
    Intent intent, intentNavigation;

    TextView textView, textViewSummary;
    String nameApp, textToBeColored, htmlText, summary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        textView = (TextView) findViewById(R.id.nameApp);
        textViewSummary = (TextView) findViewById(R.id.summary);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(AboutActivity.this);
        navigationView.getMenu().getItem(4).setChecked(true);


        hView = navigationView.getHeaderView(0);
        nav_user = (TextView) hView.findViewById(R.id.userName);
        userEmail = (TextView) hView.findViewById(R.id.userEmail);
        imageView = (ImageView) hView.findViewById(R.id.userImage);


        sharedPreferences = getSharedPreferences("UserLogin", this.MODE_PRIVATE);
        userType = sharedPreferences.getString("type", "").toString();
        image_pathUser = sharedPreferences.getString("image_path", "");
        nameUser = sharedPreferences.getString("name", "");
        emailUser = sharedPreferences.getString("email", "");
        idUser = sharedPreferences.getString("id", "");

        nav_user.setText(nameUser);
        userEmail.setText(emailUser);

        PicassoClient.userImage(AboutActivity.this, image_pathUser, imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentNavigation = new Intent(AboutActivity.this, ProfileActivity.class);
                intentNavigation.putExtra("nameActivity", "ContactUsActivity");
                intentNavigation.putExtra("idUserProfile", idUser);
                startActivity(intentNavigation);
            }
        });

        nameApp = getString(R.string.app_name);
        summary = getString(R.string.summary);
        textToBeColored = nameApp.substring(1, 2);
        htmlText = nameApp.replace("O", "<font color='#fd9b1e'>O</font>")
                .replace("T", "<font color='#73b025'>T</font>");
//        textToBeColored = nameApp.substring(2,3);
//        htmlText = nameApp.replace(textToBeColored,"<font color='#73b025'>T</font>");
        textView.setText(Html.fromHtml(htmlText));

        htmlText = summary.replace("GOT", "<font color='#fd9b1e'>GOT</font>");
        textViewSummary.setText(Html.fromHtml(htmlText));
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
        navigationView.getMenu().getItem(4).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (userType.equals("Admin")) {
            getMenuInflater().inflate(R.menu.admin_menu, menu);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.addCategory:
                startActivity(new Intent(AboutActivity.this, AddCategoryActivity.class));
                return true;

            case R.id.addAds:
                startActivity(new Intent(AboutActivity.this, Add_Ad.class));
                return true;

            case R.id.viewAllCategory:
                startActivity(new Intent(AboutActivity.this, ViewAllCategories_Admin.class));
                return true;

            case R.id.viewAllAds:
                startActivity(new Intent(AboutActivity.this, ViewAllAds_Admin.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Advertisements) {
            intentNavigation = new Intent(AboutActivity.this, HomeActivity.class);
            startActivity(intentNavigation);

        } else if (id == R.id.nav_categories) {
            intentNavigation = new Intent(AboutActivity.this, ViewParentCategories.class);
            startActivity(intentNavigation);

        } else if (id == R.id.nav_profile) {
            intentNavigation = new Intent(AboutActivity.this, ProfileActivity.class);
            intentNavigation.putExtra("nameActivity", "HomeActivity");
            intentNavigation.putExtra("idUserProfile", idUser);
            startActivity(intentNavigation);

        } else if (id == R.id.nav_about) {


        } else if (id == R.id.nav_logout) {
            if (isNetworkOnline()) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear().commit();
                if (ViewParentCategories.booleanCreate) {
                    ViewParentCategories.clearData();
                }
                Intent intent = new Intent(AboutActivity.this, LogIn_SignUp_Main.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                Toast.makeText(this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
                navigationView.getMenu().getItem(5).setChecked(true);
            }

        } else if (id == R.id.nav_saved_ads) {

            Intent intent = new Intent(AboutActivity.this, SavedAdsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_contact) {

            Intent intent = new Intent(AboutActivity.this, ContactUsActivity.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
