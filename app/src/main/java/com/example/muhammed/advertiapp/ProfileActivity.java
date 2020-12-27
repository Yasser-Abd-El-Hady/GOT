package com.example.muhammed.advertiapp;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String userType;
    NavigationView navigationView;


    private TextView nameTv, emailTv, addressTv, birthdayTv, typeTv, phoneTv, emailValidation;
    private EditText nameEditText, birthdayEditText, addressEditText, phoneEditText, emailEditText, currentPassword, newPassword,
            editTextAddCode, newPasswordByCode, editTextAddCodeValidation;
    CircleImageView user_img_update;
    CircleImageView user_img;
    Button editProfileBtn, saveBtn, cancelBtn, editPassword, cancelChange, saveChangePassword, sendCode, cancelContinue, addCode,
            cancelContinue2, saveNewPassword, cancelContinue3, addCodeValidation, cancelValidation;
    RadioButton radioButtonSMS, radioButtonEmail;

    private String user_id, idUser, nameActivity, image_pathUser, nameUser, emailUser;
    ProgressDialog progressDialog;
    public SharedPreferences shrd;
    SharedPreferences.Editor editor;
    Intent intent, intentGet;
    LinearLayout mainLinearLayout, mainLinearLayout2, linearLayoutImage, linearLayoutPassword, linearLayoutForgotPassword,
            linearLayoutForgotPassword2, linearLayoutForgotPassword3;
    LinearLayout.LayoutParams updateLayout, showUSerProfileLayout, layoutParamsImage, layoutParamsPassword, layoutParamsForgotPassword,
            layoutParamsForgotPassword2, layoutParamsForgotPassword3;
    Uri imageUri;

    String name, birthday, address, phone, email, password, type, nameString, birthdayString, addressString, phoneString, emailString, typeString, strImageProfilePath,
            strImageProfileName, strEmail, newPasswordstr, waySendstr, numberLayout, emailReceiver, emailSender, passwordSender, subject,
            textMessage, code, codeValidation, subjectValidation, textMessageValidation, nameEditStr, birthdayEditStr, addressEditStr,
            phoneEditStr, image_path;


    private static final int SELECTED_PICTURE = 1, CROP_PICTURE = 2, PLACE_PICKER_REQUEST = 100;

    Map<String, String> mapDataEdit;
    SwipeRefreshLayout refreshLayout;


    View hView;
    TextView nav_user, userEmail, forgotPassword, email_number_forgot;
    CircleImageView imageView;
    Session session = null;
    ProgressDialog pdialog = null;
    Animation animShake;
    Vibrator vib;
    TextInputLayout inputLayoutName, inputLayoutAddress, inputLayoutPhone, inputLayoutEditEmail, inputLayoutBirthday, inputLayoutCurrentPassword, inputLayoutNewPassword,
            inputLayoutAddCode, inputLayoutNewPasswordByCode, inputLayoutAddCodeValidation;

    ProgressBar progressBar;
    AlertDialog alertDialog;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        gatherControls();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.profileRefresh);
        setSupportActionBar(toolbar);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        hView = navigationView.getHeaderView(0);
        nav_user = (TextView) hView.findViewById(R.id.userName);
        userEmail = (TextView) hView.findViewById(R.id.userEmail);
        imageView = (CircleImageView) hView.findViewById(R.id.userImage);

        editPassword = (Button) findViewById(R.id.edit_password);
        forgotPassword = (TextView) findViewById(R.id.forgotPassword);


        inputLayoutName = (TextInputLayout) findViewById(R.id.inputLayoutEditName);
        inputLayoutAddress = (TextInputLayout) findViewById(R.id.inputLayoutEditAddress);
        inputLayoutPhone = (TextInputLayout) findViewById(R.id.inputLayoutEditPhone);
        inputLayoutBirthday = (TextInputLayout) findViewById(R.id.inputLayoutEditBirthday);
        inputLayoutCurrentPassword = (TextInputLayout) findViewById(R.id.inputLayoutCurrentPassword);
        inputLayoutNewPassword = (TextInputLayout) findViewById(R.id.inputLayoutNewPassword);
        inputLayoutAddCode = (TextInputLayout) findViewById(R.id.inputLayoutAddCode);
        inputLayoutNewPasswordByCode = (TextInputLayout) findViewById(R.id.inputLayoutNewPasswordByCode);
        inputLayoutEditEmail = (TextInputLayout) findViewById(R.id.inputLayoutEditEmail);


        birthdayEditText.setFocusable(false);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        progressBar.getProgressDrawable().setColorFilter(Color.argb(255, 226, 99, 226), android.graphics.PorterDuff.Mode.SRC_IN);

        shrd = getSharedPreferences("UserLogin", this.MODE_PRIVATE);
        editor = shrd.edit();
        userType = shrd.getString("type", "").toString();
        image_pathUser = shrd.getString("image_path", "");
        nameUser = shrd.getString("name", "");
        emailUser = shrd.getString("email", "");

        nav_user.setText(nameUser);
        userEmail.setText(emailUser);
        PicassoClient.userImage(ProfileActivity.this, image_pathUser, imageView);

        if (!userType.equals("Admin")) {

        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                if (isNetworkOnline()) {
                    user_img.setEnabled(true);
                    editProfileBtn.setEnabled(true);
                    editPassword.setEnabled(true);

                    refreshLayout.setRefreshing(true);
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.setRefreshing(false);
                            getUSerProfileData();
                        }
                    }, 3000);
                } else {
                    refreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);

                    user_img.setEnabled(false);
                    editProfileBtn.setEnabled(false);
                    editPassword.setEnabled(false);
                    Toast.makeText(ProfileActivity.this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
                }

            }
        });

        progressDialog = new ProgressDialog(this);
        intentGet = getIntent();
        idUser = intentGet.getStringExtra("idUserProfile").toString();
        nameActivity = intentGet.getStringExtra("nameActivity").toString();
        getUserId();

        animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        View viewSendMessage = getLayoutInflater().inflate(R.layout.layout_verify_email, null);
        addCodeValidation = (Button) viewSendMessage.findViewById(R.id.addCodeValidation);
        cancelValidation = (Button) viewSendMessage.findViewById(R.id.cancelValidation);
        editTextAddCodeValidation = (EditText) viewSendMessage.findViewById(R.id.editTextAddCodeValidation);
        emailValidation = (TextView) viewSendMessage.findViewById(R.id.emailValidation);
        inputLayoutAddCodeValidation = (TextInputLayout) viewSendMessage.findViewById(R.id.inputLayoutAddCodeValidation);


        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setView(viewSendMessage);
        alertDialog = builder.create();

        if (isNetworkOnline()) {
            getUSerProfileData();
        } else {
            refreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);

            user_img.setEnabled(false);
            editProfileBtn.setEnabled(false);
            editPassword.setEnabled(false);
        }

        refreshLayout.setColorSchemeResources(R.color.purple, R.color.colorPrimary, R.color.colorPrimaryDark);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetworkOnline()) {
                    user_img.setEnabled(true);
                    editProfileBtn.setEnabled(true);
                    editPassword.setEnabled(true);

                    refreshLayout.setRefreshing(true);
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.setRefreshing(false);
                            getUSerProfileData();
                        }
                    }, 3000);
                } else {
                    refreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);

                    user_img.setEnabled(false);
                    editProfileBtn.setEnabled(false);
                    editPassword.setEnabled(false);
                    Toast.makeText(ProfileActivity.this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
                }

            }
        });


        user_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, FullScreenImage.class);
                intent.putExtra("imagePath", image_path);
                startActivity(intent);
            }
        });

        birthdayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialog dialog = new DateDialog(v, false);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "DatePicker");

                birthdayEditText.setFocusable(false);
                birthdayEditText.setClickable(true);
            }
        });

        addressEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                Intent intent;
                try {
                    intent = builder.build(ProfileActivity.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameEditText.setText(name);
                birthdayEditText.setText(birthday);
                addressEditText.setText(address);
                phoneEditText.setText(phone);
                emailEditText.setText(email);

                Picasso.with(ProfileActivity.this)
                        .load(image_path)
                        .placeholder(R.mipmap.ic_profile_picture_user)
                        .resize(200, 200).into(user_img_update);


                showUSerProfileLayout.height = 0;
                showUSerProfileLayout.width = 0;
                mainLinearLayout.setLayoutParams(showUSerProfileLayout);

                updateLayout.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                updateLayout.width = LinearLayout.LayoutParams.MATCH_PARENT;
                ;
                mainLinearLayout2.setLayoutParams(updateLayout);
                numberLayout = "1";
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nameEditStr = nameEditText.getText().toString().trim();
                birthdayEditStr = birthdayEditText.getText().toString().trim();
                addressEditStr = addressEditText.getText().toString().trim();
                phoneEditStr = phoneEditText.getText().toString().trim();
                strEmail = emailEditText.getText().toString().trim();

                submitForm();

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputLayoutName.setErrorEnabled(false);
                inputLayoutBirthday.setErrorEnabled(false);
                inputLayoutAddress.setErrorEnabled(false);
                inputLayoutPhone.setErrorEnabled(false);

                updateLayout.height = 0;
                updateLayout.width = 0;
                mainLinearLayout2.setLayoutParams(updateLayout);

                showUSerProfileLayout.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                showUSerProfileLayout.width = LinearLayout.LayoutParams.MATCH_PARENT;
                ;
                mainLinearLayout.setLayoutParams(showUSerProfileLayout);

                numberLayout = "0";
            }
        });

        addCodeValidation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFormAddCodeValidation();
            }
        });

        cancelValidation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                emailValidation.setText(getString(R.string.sentCode));
                editTextAddCodeValidation.setText("");
            }
        });


        user_img_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(photoPickerIntent, getString(R.string.selectImage)), SELECTED_PICTURE);
            }
        });

        editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUSerProfileLayout.height = 0;
                showUSerProfileLayout.width = 0;
                mainLinearLayout.setLayoutParams(showUSerProfileLayout);

                layoutParamsPassword.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                layoutParamsPassword.width = LinearLayout.LayoutParams.MATCH_PARENT;
                linearLayoutPassword.setLayoutParams(layoutParamsPassword);

                numberLayout = "2";
            }
        });

        cancelChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputLayoutCurrentPassword.setErrorEnabled(false);
                inputLayoutNewPassword.setErrorEnabled(false);

                showUSerProfileLayout.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                showUSerProfileLayout.width = LinearLayout.LayoutParams.MATCH_PARENT;
                mainLinearLayout.setLayoutParams(showUSerProfileLayout);

                layoutParamsPassword.height = 0;
                layoutParamsPassword.width = 0;
                linearLayoutPassword.setLayoutParams(layoutParamsPassword);
                currentPassword.setText("");
                newPassword.setText("");

                numberLayout = "0";
            }
        });
        saveChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFormChangePassword();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layoutParamsForgotPassword.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                ;
                layoutParamsForgotPassword.width = LinearLayout.LayoutParams.MATCH_PARENT;
                ;
                linearLayoutForgotPassword.setLayoutParams(layoutParamsForgotPassword);

                layoutParamsPassword.height = 0;
                layoutParamsPassword.width = 0;
                linearLayoutPassword.setLayoutParams(layoutParamsPassword);
                currentPassword.setText("");
                newPassword.setText("");
                radioButtonSMS.setText(getString(R.string.SendToSmS) + "\n" + phone);
                radioButtonEmail.setText(getString(R.string.SendToEmail) + "\n" + email);

                numberLayout = "3";
            }
        });

        radioButtonSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                waySendstr = "Email";
            }
        });
        radioButtonEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                waySendstr = "SMS";
            }
        });

        cancelContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layoutParamsForgotPassword.height = 0;
                layoutParamsForgotPassword.width = 0;
                linearLayoutForgotPassword.setLayoutParams(layoutParamsForgotPassword);

                layoutParamsPassword.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                layoutParamsPassword.width = LinearLayout.LayoutParams.MATCH_PARENT;
                linearLayoutPassword.setLayoutParams(layoutParamsPassword);

                radioButtonSMS.setChecked(true);
                waySendstr = "SMS";

                numberLayout = "2";
            }
        });

        sendCode.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StringFormatMatches")
            @Override
            public void onClick(View v) {

                final int min = 100000;
                final int max = 999999;
                final int random = new Random().nextInt((max - min) + 1) + min;

                code = String.valueOf(random);
                subject = code + getString(R.string.subjectMessage);
                textMessage = getString(R.string.textMessage, name) + getString(R.string.textMessageArabic, name);

                if (waySendstr.equals("SMS")) {
                    email_number_forgot.setText(getString(R.string.sentCode) + phone);

                } else if (waySendstr.equals("Email")) {
                    email_number_forgot.setText(getString(R.string.sentCode) + email);

                    emailReceiver = email;


                    Properties props = new Properties();
                    props.put("mail.smtp.host", "smtp.gmail.com");
                    props.put("mail.smtp.socketFactory.port", "465");
                    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.port", "465");

                    session = Session.getDefaultInstance(props, new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(emailSender, passwordSender);
                        }
                    });
                    sendMessage();

                }


                waySendstr = "SMS";

                numberLayout = "4";
            }
        });

        cancelContinue2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputLayoutAddCode.setErrorEnabled(false);

                layoutParamsForgotPassword2.height = 0;
                layoutParamsForgotPassword2.width = 0;
                linearLayoutForgotPassword2.setLayoutParams(layoutParamsForgotPassword2);

                layoutParamsForgotPassword.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                layoutParamsForgotPassword.width = LinearLayout.LayoutParams.MATCH_PARENT;
                linearLayoutForgotPassword.setLayoutParams(layoutParamsForgotPassword);

                numberLayout = "3";
            }
        });

        addCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submitFormAddCode();
                //check your code is correct or incorrect
            }
        });

        cancelContinue3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputLayoutNewPasswordByCode.setErrorEnabled(false);

                layoutParamsForgotPassword3.height = 0;
                layoutParamsForgotPassword3.width = 0;
                linearLayoutForgotPassword3.setLayoutParams(layoutParamsForgotPassword3);

                layoutParamsForgotPassword2.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                layoutParamsForgotPassword2.width = LinearLayout.LayoutParams.MATCH_PARENT;
                linearLayoutForgotPassword2.setLayoutParams(layoutParamsForgotPassword2);

                numberLayout = "4";
            }
        });

        saveNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submitFormChangePasswordByCode();
            }
        });
    }

    private void sendMessage() {
        pdialog = ProgressDialog.show(ProfileActivity.this, "", getString(R.string.Sending), true);

        class RetreiveFeedTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                try {
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(emailSender));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailReceiver));
                    message.setSubject(subject);
                    message.setContent(textMessage, "text/html; charset=utf-8");
                    Transport.send(message);
                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                pdialog.dismiss();

                layoutParamsForgotPassword.height = 0;
                layoutParamsForgotPassword.width = 0;
                linearLayoutForgotPassword.setLayoutParams(layoutParamsForgotPassword);


                layoutParamsForgotPassword2.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                layoutParamsForgotPassword2.width = LinearLayout.LayoutParams.MATCH_PARENT;
                linearLayoutForgotPassword2.setLayoutParams(layoutParamsForgotPassword2);

                radioButtonSMS.setChecked(true);

                Toast.makeText(getApplicationContext(), getString(R.string.doneSend), Toast.LENGTH_LONG).show();
            }
        }

        RetreiveFeedTask task = new RetreiveFeedTask();
        task.execute();
    }

    private void gatherControls() {
        nameTv = (TextView) findViewById(R.id.user_name_tv);
        emailTv = (TextView) findViewById(R.id.user_email_tv);
        addressTv = (TextView) findViewById(R.id.user_address_tv);
        birthdayTv = (TextView) findViewById(R.id.user_birthday_tv);
        typeTv = (TextView) findViewById(R.id.user_type_tv);
        phoneTv = (TextView) findViewById(R.id.user_phone_tv);
        user_img = (CircleImageView) findViewById(R.id.user_profile_imgView);
        user_img_update = (CircleImageView) findViewById(R.id.user_profile_imgView_update);
        editProfileBtn = (Button) findViewById(R.id.edit_profile_btn);


        //update UserClass data layout controls
        nameEditText = (EditText) findViewById(R.id.name_update_editText);
        birthdayEditText = (EditText) findViewById(R.id.birthday_update_editText);
        addressEditText = (EditText) findViewById(R.id.address_update_editText);
        phoneEditText = (EditText) findViewById(R.id.phone_update_editText);
        emailEditText = (EditText) findViewById(R.id.email_update_editText);
        saveBtn = (Button) findViewById(R.id.saveBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);

        saveChangePassword = (Button) findViewById(R.id.saveChangePassword);
        cancelChange = (Button) findViewById(R.id.cancelChange);
        currentPassword = (EditText) findViewById(R.id.currentPassword);
        newPassword = (EditText) findViewById(R.id.newPassword);

        sendCode = (Button) findViewById(R.id.sendCode);
        cancelContinue = (Button) findViewById(R.id.cancelContinue);
        radioButtonSMS = (RadioButton) findViewById(R.id.radioButtonSMS);
        radioButtonEmail = (RadioButton) findViewById(R.id.radioButtonEmail);

        email_number_forgot = (TextView) findViewById(R.id.email_number);
        editTextAddCode = (EditText) findViewById(R.id.editTextAddCode);
        addCode = (Button) findViewById(R.id.addCode);
        cancelContinue2 = (Button) findViewById(R.id.cancelContinue2);

        mainLinearLayout = (LinearLayout) findViewById(R.id.showUserLayout);
        mainLinearLayout2 = (LinearLayout) findViewById(R.id.updateLayout);
        showUSerProfileLayout = (LinearLayout.LayoutParams) mainLinearLayout.getLayoutParams();
        updateLayout = (LinearLayout.LayoutParams) mainLinearLayout2.getLayoutParams();

        newPasswordByCode = (EditText) findViewById(R.id.newPasswordByCode);
        saveNewPassword = (Button) findViewById(R.id.saveNewPassword);
        cancelContinue3 = (Button) findViewById(R.id.cancelContinue3);

        updateLayout.height = 0;
        updateLayout.width = 0;
        mainLinearLayout2.setLayoutParams(updateLayout);


        linearLayoutImage = (LinearLayout) findViewById(R.id.linearLayoutImage);
        layoutParamsImage = (LinearLayout.LayoutParams) linearLayoutImage.getLayoutParams();

        linearLayoutPassword = (LinearLayout) findViewById(R.id.changePasswordLayout);
        layoutParamsPassword = (LinearLayout.LayoutParams) linearLayoutPassword.getLayoutParams();

        layoutParamsPassword.height = 0;
        layoutParamsPassword.width = 0;
        linearLayoutPassword.setLayoutParams(layoutParamsPassword);

        linearLayoutForgotPassword = (LinearLayout) findViewById(R.id.forgotPasswordLayout);
        layoutParamsForgotPassword = (LinearLayout.LayoutParams) linearLayoutForgotPassword.getLayoutParams();


        layoutParamsForgotPassword.height = 0;
        layoutParamsForgotPassword.width = 0;
        linearLayoutForgotPassword.setLayoutParams(layoutParamsForgotPassword);

        linearLayoutForgotPassword2 = (LinearLayout) findViewById(R.id.forgotPasswordLayout2);
        layoutParamsForgotPassword2 = (LinearLayout.LayoutParams) linearLayoutForgotPassword2.getLayoutParams();


        layoutParamsForgotPassword2.height = 0;
        layoutParamsForgotPassword2.width = 0;
        linearLayoutForgotPassword2.setLayoutParams(layoutParamsForgotPassword2);

        linearLayoutForgotPassword3 = (LinearLayout) findViewById(R.id.forgotPasswordLayout3);
        layoutParamsForgotPassword3 = (LinearLayout.LayoutParams) linearLayoutForgotPassword3.getLayoutParams();


        layoutParamsForgotPassword3.height = 0;
        layoutParamsForgotPassword3.width = 0;
        linearLayoutForgotPassword3.setLayoutParams(layoutParamsForgotPassword3);

        nameString = getString(R.string.name);
        birthdayString = getString(R.string.birthday);
        addressString = getString(R.string.address);
        phoneString = getString(R.string.phone);
        emailString = getString(R.string.email);
        typeString = getString(R.string.type);

        waySendstr = "SMS";
        numberLayout = "0";

// this data of my company
        emailSender = Config.Account_App;
        passwordSender = Config.Password_App;

        mapDataEdit = new HashMap<>();
        mapDataEdit.put("function", "editProfileWithOutImage");
    }

    private void getUserId() {
        SharedPreferences.Editor editor = shrd.edit();
        user_id = shrd.getString("id", "");

        if (idUser.equals(user_id)) {
            editProfileBtn.setVisibility(View.VISIBLE);
            editPassword.setVisibility(View.VISIBLE);
            editProfileBtn.setEnabled(true);
            editPassword.setEnabled(true);
        } else {
            editProfileBtn.setVisibility(View.GONE);
            editPassword.setVisibility(View.GONE);
            editProfileBtn.setEnabled(false);
            editPassword.setEnabled(false);

        }
        user_id = idUser;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
                user_img_update.setImageBitmap(selectedImage);

                layoutParamsImage.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                layoutParamsImage.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                linearLayoutImage.setLayoutParams(layoutParamsImage);

                ByteArrayOutputStream byteArrayOutputStreamObject;
                byteArrayOutputStreamObject = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);
                byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
                strImageProfileName = "GOT_IMG_" + String.valueOf(System.currentTimeMillis());
                strImageProfilePath = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);


                mapDataEdit.remove("function");
                mapDataEdit.put("function", "editProfileWithImage");
                mapDataEdit.put("userImageProfileName", strImageProfileName);
                mapDataEdit.put("userImageProfilePath", strImageProfilePath);

            } else {
                Toast.makeText(ProfileActivity.this, getString(R.string.cropImage), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == PLACE_PICKER_REQUEST) {

            if (data != null) {
                Place place = PlacePicker.getPlace(data, this);
                String addressFromMap = String.valueOf(place.getAddress());
                addressEditText.setText(addressFromMap);
            } else {
                Toast.makeText(ProfileActivity.this, getString(R.string.selectAddress), Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(ProfileActivity.this, getString(R.string.pickedImage), Toast.LENGTH_LONG).show();
        }

    }

    public void getUSerProfileData() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_ADVERTI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("user_result");
                        if (!(jsonArray.isNull(0))) {
                            JSONObject user_object = jsonArray.getJSONObject(0);
                            name = user_object.getString("name");
                            birthday = user_object.getString("birthday");
                            address = user_object.getString("address");
                            phone = user_object.getString("phone_number");
                            strEmail = email = user_object.getString("email");
                            password = user_object.getString("password");
                            type = user_object.getString("type");
                            nameTv.setText(nameString + name);
                            birthdayTv.setText(birthdayString + birthday);
                            addressTv.setText(addressString + address);
                            phoneTv.setText(phoneString + phone);
                            emailTv.setText(emailString + email);
                            typeTv.setText(typeString + type);
                            image_path = user_object.getString("image_path");
                            Picasso.with(ProfileActivity.this)
                                    .load(image_path)
                                    .placeholder(R.mipmap.ic_profile_picture_user)
                                    .resize(200, 200).into(user_img);

                            editSharedPreferences(image_path, name, email);
                            nav_user.setText(name);
                            userEmail.setText(email);
                            Picasso.with(ProfileActivity.this)
                                    .load(image_path)
                                    .placeholder(R.mipmap.ic_profile_picture_user)
                                    .resize(200, 200).into(imageView);


                            progressBar.setVisibility(View.GONE);
                        } else {
                            progressBar.setVisibility(View.GONE);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> mapData = new HashMap<>();
                mapData = new HashMap<>();
                mapData.put("user_id", user_id);
                mapData.put("function", "viewProfile");
                return mapData;

            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);
        requestQueue.add(stringRequest);


    }


    public void editSharedPreferences(String image_path, String name, String email) {
        editor.putString("image_path", image_path);
        editor.putString("name", name);
        editor.putString("email", email);
        editor.commit();
        emailUser = shrd.getString("email", "");
    }

    public void editSharedPreferences(String password) {
        editor.putString("password", password);
        editor.commit();
    }

    private void submitFormChangePasswordByCode() {
        inputLayoutNewPasswordByCode.setErrorEnabled(false);

        if (!checkNewPasswordByCode()) {
            newPasswordByCode.setAnimation(animShake);
            newPasswordByCode.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }


        newPasswordstr = newPasswordByCode.getText().toString().trim();
        changePassword();
        layoutParamsForgotPassword3.height = 0;
        layoutParamsForgotPassword3.width = 0;
        linearLayoutForgotPassword3.setLayoutParams(layoutParamsForgotPassword3);

        showUSerProfileLayout.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        showUSerProfileLayout.width = LinearLayout.LayoutParams.MATCH_PARENT;
        mainLinearLayout.setLayoutParams(showUSerProfileLayout);

        newPasswordByCode.setText("");

        numberLayout = "0";


    }

    private void submitFormAddCode() {
        inputLayoutAddCode.setErrorEnabled(false);

        if (!checkAddCode()) {
            editTextAddCode.setAnimation(animShake);
            editTextAddCode.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }

        if (editTextAddCode.getText().toString().trim().equals(code)) {

            layoutParamsForgotPassword2.height = 0;
            layoutParamsForgotPassword2.width = 0;
            linearLayoutForgotPassword2.setLayoutParams(layoutParamsForgotPassword2);

            layoutParamsForgotPassword3.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParamsForgotPassword3.width = LinearLayout.LayoutParams.MATCH_PARENT;
            linearLayoutForgotPassword3.setLayoutParams(layoutParamsForgotPassword3);

            numberLayout = "5";
        } else {
            Toast.makeText(ProfileActivity.this, getString(R.string.incorrectCode), Toast.LENGTH_LONG).show();
        }
    }

    private void submitFormChangePassword() {

        inputLayoutCurrentPassword.setErrorEnabled(false);
        inputLayoutNewPassword.setErrorEnabled(false);

        if (!checkCurrentPassword()) {
            currentPassword.setAnimation(animShake);
            currentPassword.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }
        if (!checkNewPassword()) {
            newPassword.setAnimation(animShake);
            newPassword.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }

        if (password.equals(currentPassword.getText().toString().trim())) {

            newPasswordstr = newPassword.getText().toString().trim();
            changePassword();
            showUSerProfileLayout.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            showUSerProfileLayout.width = LinearLayout.LayoutParams.MATCH_PARENT;
            mainLinearLayout.setLayoutParams(showUSerProfileLayout);

            layoutParamsPassword.height = 0;
            layoutParamsPassword.width = 0;
            linearLayoutPassword.setLayoutParams(layoutParamsPassword);

            currentPassword.setText("");
            newPassword.setText("");

            numberLayout = "0";
        } else {
            Toast.makeText(ProfileActivity.this, getString(R.string.incorrectPassword), Toast.LENGTH_LONG).show();
        }
    }

    private void submitForm() {
        inputLayoutName.setErrorEnabled(false);
        inputLayoutBirthday.setErrorEnabled(false);
        inputLayoutAddress.setErrorEnabled(false);
        inputLayoutPhone.setErrorEnabled(false);
        inputLayoutEditEmail.setErrorEnabled(false);


        if (!checkName()) {
            nameEditText.setAnimation(animShake);
            nameEditText.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }
        if (!checkBirthday()) {
            birthdayEditText.setAnimation(animShake);
            birthdayEditText.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }
        if (!checkAddress()) {
            addressEditText.setAnimation(animShake);
            addressEditText.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }
        if (!checkPhone()) {
            phoneEditText.setAnimation(animShake);
            phoneEditText.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }
        if (!checkEmail()) {
            emailEditText.setAnimation(animShake);
            emailEditText.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }


        if (emailUser.equals(strEmail)) {
            mapDataEdit.put("user_id", user_id);
            mapDataEdit.put("user_name", nameEditStr);
            mapDataEdit.put("user_birthday", birthdayEditStr);
            mapDataEdit.put("user_address", addressEditStr);
            mapDataEdit.put("user_phone", phoneEditStr);
            mapDataEdit.put("user_email", strEmail);

            updateUserData();
            updateLayout.height = 0;
            updateLayout.width = 0;
            mainLinearLayout2.setLayoutParams(updateLayout);

            showUSerProfileLayout.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            showUSerProfileLayout.width = LinearLayout.LayoutParams.MATCH_PARENT;
            ;
            mainLinearLayout.setLayoutParams(showUSerProfileLayout);

            numberLayout = "0";
        } else {
            sendCode();
        }

    }

    private boolean checkNewPasswordByCode() {
        if (newPasswordByCode.getText().toString().isEmpty()) {
            inputLayoutNewPasswordByCode.setErrorEnabled(true);
            inputLayoutNewPasswordByCode.setError(getString(R.string.err_msg_new_password));
            return false;
        }

        inputLayoutNewPasswordByCode.setErrorEnabled(false);
        return true;
    }

    private boolean checkAddCode() {
        if (editTextAddCode.getText().toString().isEmpty()) {
            inputLayoutAddCode.setErrorEnabled(true);
            inputLayoutAddCode.setError(getString(R.string.err_msg_add_code));
            return false;
        }

        inputLayoutAddCode.setErrorEnabled(false);
        return true;
    }

    private boolean checkCurrentPassword() {
        if (currentPassword.getText().toString().isEmpty()) {
            inputLayoutCurrentPassword.setErrorEnabled(true);
            inputLayoutCurrentPassword.setError(getString(R.string.err_msg_current_password));
            return false;
        }

        inputLayoutCurrentPassword.setErrorEnabled(false);
        return true;
    }


    private boolean checkNewPassword() {
        if (newPassword.getText().toString().isEmpty()) {
            inputLayoutNewPassword.setErrorEnabled(true);
            inputLayoutNewPassword.setError(getString(R.string.err_msg_new_password));
            return false;
        }
        inputLayoutNewPassword.setErrorEnabled(false);
        return true;
    }


    private boolean checkName() {
        if (nameEditStr.isEmpty()) {
            inputLayoutName.setErrorEnabled(true);
            inputLayoutName.setError(getString(R.string.err_msg_name));

            return false;
        }
        inputLayoutName.setErrorEnabled(false);
        return true;
    }

    private boolean checkBirthday() {
        if (birthdayEditStr.isEmpty()) {
            inputLayoutBirthday.setErrorEnabled(true);
            inputLayoutBirthday.setError(getString(R.string.err_msg_birthday));

            return false;
        }
        inputLayoutBirthday.setErrorEnabled(false);
        return true;
    }

    private boolean checkAddress() {
        if (addressEditStr.isEmpty()) {
            inputLayoutAddress.setErrorEnabled(true);
            inputLayoutAddress.setError(getString(R.string.err_msg_address));

            return false;
        }
        inputLayoutAddress.setErrorEnabled(false);
        return true;
    }

    private boolean checkPhone() {
        if (phoneEditStr.isEmpty()) {
            inputLayoutPhone.setErrorEnabled(true);
            inputLayoutPhone.setError(getString(R.string.err_msg_phone));

            return false;
        }
        inputLayoutPhone.setErrorEnabled(false);
        return true;
    }

    private boolean checkEmail() {
        if (strEmail.isEmpty()) {
            Toast.makeText(this, getString(R.string.selectAccount), Toast.LENGTH_SHORT).show();

            return false;
        }
        return true;
    }

    public void updateUserData() {
        progressDialog.setMessage("Update Data");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_ADVERTI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    if (response.equals("Success")) {
                        Toast.makeText(ProfileActivity.this, getString(R.string.updatedSuccessfully), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        getUSerProfileData();
                    } else if (response.equals("Failed")) {
                        Toast.makeText(ProfileActivity.this, getString(R.string.updateFailed), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(ProfileActivity.this, getString(R.string.accountUsed), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                return mapDataEdit;

            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);
        requestQueue.add(stringRequest);

    }


    public void changePassword() {
        progressDialog.setMessage("Change Password");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_ADVERTI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    if (response.equals("Success")) {
                        Toast.makeText(ProfileActivity.this, getString(R.string.updatedSuccessfully), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        password = newPasswordstr;
                        editSharedPreferences(password);
                    } else {
                        Toast.makeText(ProfileActivity.this, getString(R.string.updateFailed), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                mapDataEdit.clear();
                mapDataEdit.put("function", "changePassword");
                mapDataEdit.put("user_id", user_id);
                mapDataEdit.put("user_password", newPasswordstr);

                return mapDataEdit;

            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);
        requestQueue.add(stringRequest);

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
        navigationView.getMenu().getItem(2).setChecked(true);
        if (!isNetworkOnline()) {
            refreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);

            user_img.setEnabled(false);
            editProfileBtn.setEnabled(false);
            editPassword.setEnabled(false);
            Toast.makeText(this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
        } else {
            user_img.setEnabled(true);
            editProfileBtn.setEnabled(true);
            editPassword.setEnabled(true);
            getUSerProfileData();
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (numberLayout.equals("0")) {
                if (nameActivity.equals("CommentActivity")) {
                    super.onBackPressed();
                } else {
                    super.onBackPressed();
                }
            } else if (numberLayout.equals("1")) {
                numberLayout = "0";


                inputLayoutName.setErrorEnabled(false);
                inputLayoutBirthday.setErrorEnabled(false);
                inputLayoutAddress.setErrorEnabled(false);
                inputLayoutPhone.setErrorEnabled(false);

                updateLayout.height = 0;
                updateLayout.width = 0;
                mainLinearLayout2.setLayoutParams(updateLayout);

                showUSerProfileLayout.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                showUSerProfileLayout.width = LinearLayout.LayoutParams.MATCH_PARENT;
                ;
                mainLinearLayout.setLayoutParams(showUSerProfileLayout);
            } else if (numberLayout.equals("2")) {
                numberLayout = "0";

                layoutParamsPassword.height = 0;
                layoutParamsPassword.width = 0;
                linearLayoutPassword.setLayoutParams(layoutParamsPassword);

                showUSerProfileLayout.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                showUSerProfileLayout.width = LinearLayout.LayoutParams.MATCH_PARENT;
                ;
                mainLinearLayout.setLayoutParams(showUSerProfileLayout);

                inputLayoutCurrentPassword.setErrorEnabled(false);
                inputLayoutNewPassword.setErrorEnabled(false);
            } else if (numberLayout.equals("3")) {
                layoutParamsForgotPassword.height = 0;
                layoutParamsForgotPassword.width = 0;
                linearLayoutForgotPassword.setLayoutParams(layoutParamsForgotPassword);

                layoutParamsPassword.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                layoutParamsPassword.width = LinearLayout.LayoutParams.MATCH_PARENT;
                linearLayoutPassword.setLayoutParams(layoutParamsPassword);

                radioButtonSMS.setChecked(true);
                waySendstr = "SMS";

                numberLayout = "2";
            } else if (numberLayout.equals("4")) {


                inputLayoutAddCode.setErrorEnabled(false);

                layoutParamsForgotPassword2.height = 0;
                layoutParamsForgotPassword2.width = 0;
                linearLayoutForgotPassword2.setLayoutParams(layoutParamsForgotPassword2);

                layoutParamsForgotPassword.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                layoutParamsForgotPassword.width = LinearLayout.LayoutParams.MATCH_PARENT;
                linearLayoutForgotPassword.setLayoutParams(layoutParamsForgotPassword);

                numberLayout = "3";
            } else if (numberLayout.equals("5")) {

                inputLayoutNewPasswordByCode.setErrorEnabled(false);

                layoutParamsForgotPassword3.height = 0;
                layoutParamsForgotPassword3.width = 0;
                linearLayoutForgotPassword3.setLayoutParams(layoutParamsForgotPassword3);

                layoutParamsForgotPassword2.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                layoutParamsForgotPassword2.width = LinearLayout.LayoutParams.MATCH_PARENT;
                linearLayoutForgotPassword2.setLayoutParams(layoutParamsForgotPassword2);

                numberLayout = "4";
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        if (userType.equals("Admin")) {
            menuInflater.inflate(R.menu.admin_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.addCategory:
                startActivity(new Intent(ProfileActivity.this, AddCategoryActivity.class));
                return true;

            case R.id.addAds:
                startActivity(new Intent(ProfileActivity.this, Add_Ad.class));
                return true;

            case R.id.viewAllCategory:
                startActivity(new Intent(ProfileActivity.this, ViewAllCategories_Admin.class));
                return true;

            case R.id.viewAllAds:
                startActivity(new Intent(ProfileActivity.this, ViewAllAds_Admin.class));
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
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_categories) {

            Intent intent = new Intent(ProfileActivity.this, ViewParentCategories.class);
            startActivity(intent);


        } else if (id == R.id.nav_profile) {
            if (isNetworkOnline()) {
                user_img.setEnabled(true);
                editProfileBtn.setEnabled(true);
                editPassword.setEnabled(true);
                refreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        getUSerProfileData();
                    }
                }, 3000);
            } else {
                refreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);

                user_img.setEnabled(false);
                editProfileBtn.setEnabled(false);
                editPassword.setEnabled(false);
                Toast.makeText(ProfileActivity.this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
            }

        } else if (id == R.id.nav_about) {

            Intent intent = new Intent(ProfileActivity.this, AboutActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {
            if (isNetworkOnline()) {

                SharedPreferences.Editor editor = shrd.edit();
                editor.clear().commit();
                if (ViewParentCategories.booleanCreate) {
                    ViewParentCategories.clearData();
                }
                Intent intent = new Intent(ProfileActivity.this, LogIn_SignUp_Main.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                Toast.makeText(this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
                navigationView.getMenu().getItem(2).setChecked(true);
            }

        } else if (id == R.id.nav_saved_ads) {

            Intent intent = new Intent(ProfileActivity.this, SavedAdsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_contact) {

            Intent intent = new Intent(ProfileActivity.this, ContactUsActivity.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sendCode() {

        final int min = 100000;
        final int max = 999999;
        final int random = new Random().nextInt((max - min) + 1) + min;

        codeValidation = String.valueOf(random);
        subjectValidation = codeValidation + getString(R.string.subjectMessageRegistration);
        textMessageValidation = getString(R.string.textMessageRegistration, nameEditStr) + getString(R.string.textMessageArabicRegistration, nameEditStr);


        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailSender, passwordSender);
            }
        });
        sendMessageValidation();

    }

    private void sendMessageValidation() {
        pdialog = ProgressDialog.show(ProfileActivity.this, "", getString(R.string.Sending), true);

        class RetreiveFeedTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                try {
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(emailSender));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailReceiver));
                    message.setSubject(subjectValidation);
                    message.setContent(textMessageValidation, "text/html; charset=utf-8");
                    Transport.send(message);
                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                pdialog.dismiss();

                alertDialog.show();
                emailValidation.setText(getString(R.string.sentCode) + strEmail);
                emailReceiver = strEmail;
                Toast.makeText(getApplicationContext(), getString(R.string.doneSend), Toast.LENGTH_LONG).show();

            }
        }

        RetreiveFeedTask task = new RetreiveFeedTask();
        task.execute();
    }

    private void submitFormAddCodeValidation() {
        inputLayoutAddCodeValidation.setErrorEnabled(false);

        if (!checkAddCodeValidation()) {
            editTextAddCodeValidation.setAnimation(animShake);
            editTextAddCodeValidation.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }

        if (editTextAddCodeValidation.getText().toString().trim().equals(codeValidation)) {
            mapDataEdit.put("user_id", user_id);
            mapDataEdit.put("user_name", nameEditStr);
            mapDataEdit.put("user_birthday", birthdayEditStr);
            mapDataEdit.put("user_address", addressEditStr);
            mapDataEdit.put("user_phone", phoneEditStr);
            mapDataEdit.put("user_email", strEmail);

            alertDialog.dismiss();
            updateUserData();
            updateLayout.height = 0;
            updateLayout.width = 0;
            mainLinearLayout2.setLayoutParams(updateLayout);

            showUSerProfileLayout.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            showUSerProfileLayout.width = LinearLayout.LayoutParams.MATCH_PARENT;
            ;
            mainLinearLayout.setLayoutParams(showUSerProfileLayout);

            numberLayout = "0";
        } else {
            Toast.makeText(ProfileActivity.this, getString(R.string.incorrectCode), Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkAddCodeValidation() {
        if (editTextAddCodeValidation.getText().toString().isEmpty()) {
            inputLayoutAddCodeValidation.setErrorEnabled(true);
            inputLayoutAddCodeValidation.setError(getString(R.string.err_msg_add_code));
            return false;
        }

        inputLayoutAddCodeValidation.setErrorEnabled(false);
        return true;
    }
}
