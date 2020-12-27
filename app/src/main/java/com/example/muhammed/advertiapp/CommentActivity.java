package com.example.muhammed.advertiapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
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
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    EditText comment, editComment, editSubjectMessageEmail, editTextMessageEmail;
    TextView textViewDescription, textRate, textTimeRate, textRateEdit,
            numViews, avgRate, numUsers, companyName, senderMessage, receiverMessage;
    TextView textViewCopy, textViewEmail, textViewSMS, textViewPhone;

    ImageView imageViewAds, videoImageViewAds;
    Button save, update, cancelEdit, submitRate, editRate, editDelete, sendMessageEmail, cancelSend, addToSaved, viewVideoAds;
    LinearLayout mainLinearLayout, mainLinearLayoutRate, mainLinearLayoutRateShow, mainLinearLayoutRateEdit,
            linearLayoutCompanyName;
    LinearLayout linearLayoutPhone, linearLayoutSMS, linearLayoutEmail, linearLayoutCopy;
    Intent intent;
    String idUser, userType, idAds, adsDescription, adsImagePath, adsVideoPath, strComment, textComment, commentId, valueRate, timeRate, phoneUser,
            emailUser, nameUser, rate, numberView, countRate, idUserCompany, emailReceiver, strSubjectMessageEmail, strTextMessageEmail,
            emailSender, passwordSender, savedAdId;

    String[] textComments, timeComments, userNames, userIds, userImagePaths;
    HashMap<String, String> hashMapComment, hashMapView, hashMapAddRate;
    ListView listView;
    ArrayList<HashMap<String, String>> arrayList;
    SharedPreferences sharedPreferences;
    LinearLayout.LayoutParams layoutParams, layoutParamsRate, layoutParamsRateShow, layoutParamsRateEdit;
    TextInputLayout inputLayoutAddComment, inputLayoutEditComment, inputLayoutSubjectMessageEmail, inputLayoutTextMessageEmail;
    SwipeRefreshLayout refreshLayout;
    RatingBar ratingBar, ratingBarShow, ratingBarEdit;
    public ArrayList<AdsClass> adsArrayList;


    ProgressBar progressBar;

    CircleImageView contactCompany;
    Animation fabOpen, fabClose, rotateClock, rotateAntiClock;
    boolean isOpen = false;
    Animation animShake, animRotate;
    Vibrator vib;
    AlertDialog alertDialog, alertDialogSendEmail;

    Session session = null;
    ProgressDialog pdialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        comment = (EditText) findViewById(R.id.comment);
        mainLinearLayout = (LinearLayout) findViewById(R.id.mainLinearLayout);
        mainLinearLayoutRate = (LinearLayout) findViewById(R.id.mainLinearLayoutRate);
        mainLinearLayoutRateShow = (LinearLayout) findViewById(R.id.mainLinearLayoutRateShow);
        mainLinearLayoutRateEdit = (LinearLayout) findViewById(R.id.mainLinearLayoutRateEdit);

        linearLayoutPhone = (LinearLayout) findViewById(R.id.linearLayoutPhone);
        linearLayoutSMS = (LinearLayout) findViewById(R.id.linearLayoutSMS);
        linearLayoutEmail = (LinearLayout) findViewById(R.id.linearLayoutEmail);
        linearLayoutCopy = (LinearLayout) findViewById(R.id.linearLayoutCopy);

        linearLayoutCompanyName = (LinearLayout) findViewById(R.id.linearLayoutCompanyName);
        save = (Button) findViewById(R.id.save);
        submitRate = (Button) findViewById(R.id.submit);
        editRate = (Button) findViewById(R.id.editRate);
        editDelete = (Button) findViewById(R.id.editDelete);
        addToSaved = (Button) findViewById(R.id.addToSaved);
        listView = (ListView) findViewById(R.id.listViewComments);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);
        textRate = (TextView) findViewById(R.id.textRate);
        textRateEdit = (TextView) findViewById(R.id.textRateEdit);
        textTimeRate = (TextView) findViewById(R.id.timeRate);
        imageViewAds = (ImageView) findViewById(R.id.imageViewAds);
        videoImageViewAds = (ImageView) findViewById(R.id.videoImageViewAds);
        viewVideoAds = (Button) findViewById(R.id.viewVideoAds);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.commentRefresh);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBarShow = (RatingBar) findViewById(R.id.ratingBarShow);
        ratingBarEdit = (RatingBar) findViewById(R.id.ratingBarEdit);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        contactCompany = (CircleImageView) findViewById(R.id.contactCompany);

        textViewCopy = (TextView) findViewById(R.id.textViewCopy);
        textViewEmail = (TextView) findViewById(R.id.textViewEmail);
        textViewSMS = (TextView) findViewById(R.id.textViewSMS);
        textViewPhone = (TextView) findViewById(R.id.textViewPhone);


        numViews = (TextView) findViewById(R.id.numViews);
        avgRate = (TextView) findViewById(R.id.avgRate);
        numUsers = (TextView) findViewById(R.id.numUsers);
        companyName = (TextView) findViewById(R.id.companyName);

        animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        animRotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        progressBar.getProgressDrawable().setColorFilter(Color.argb(255, 226, 99, 226), android.graphics.PorterDuff.Mode.SRC_IN);


        View viewEditComment = getLayoutInflater().inflate(R.layout.layout_edit_comment, null);
        View viewAddComment = getLayoutInflater().inflate(R.layout.layout_add_comment, null);
        update = (Button) viewEditComment.findViewById(R.id.update);
        cancelEdit = (Button) viewEditComment.findViewById(R.id.cancelEdit);
        editComment = (EditText) viewEditComment.findViewById(R.id.editComment);

        inputLayoutAddComment = (TextInputLayout) viewAddComment.findViewById(R.id.inputLayoutAddComment);
        inputLayoutEditComment = (TextInputLayout) viewEditComment.findViewById(R.id.inputLayoutEditComment);

        AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this);
        builder.setView(viewEditComment);
        alertDialog = builder.create();


        View viewSendMessage = getLayoutInflater().inflate(R.layout.layout_send_message_to_email, null);
        sendMessageEmail = (Button) viewSendMessage.findViewById(R.id.sendMessageEmail);
        cancelSend = (Button) viewSendMessage.findViewById(R.id.cancelSend);
        editSubjectMessageEmail = (EditText) viewSendMessage.findViewById(R.id.editSubjectMessageEmail);
        editTextMessageEmail = (EditText) viewSendMessage.findViewById(R.id.editTextMessageEmail);
        senderMessage = (TextView) viewSendMessage.findViewById(R.id.senderMessage);
        receiverMessage = (TextView) viewSendMessage.findViewById(R.id.receiverMessage);
        inputLayoutTextMessageEmail = (TextInputLayout) viewSendMessage.findViewById(R.id.inputLayoutTextMessageEmail);
        inputLayoutSubjectMessageEmail = (TextInputLayout) viewSendMessage.findViewById(R.id.inputLayoutSubjectMessageEmail);

        AlertDialog.Builder builderSendEmail = new AlertDialog.Builder(CommentActivity.this);
        builderSendEmail.setView(viewSendMessage);
        alertDialogSendEmail = builderSendEmail.create();

        layoutParams = (LinearLayout.LayoutParams) mainLinearLayout.getLayoutParams();
        layoutParamsRate = (LinearLayout.LayoutParams) mainLinearLayoutRate.getLayoutParams();
        layoutParamsRateShow = (LinearLayout.LayoutParams) mainLinearLayoutRateShow.getLayoutParams();
        layoutParamsRateEdit = (LinearLayout.LayoutParams) mainLinearLayoutRateEdit.getLayoutParams();
        fabOpen = AnimationUtils.loadAnimation(CommentActivity.this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(CommentActivity.this, R.anim.fab_close);
        rotateClock = AnimationUtils.loadAnimation(CommentActivity.this, R.anim.rotate_clock);
        rotateAntiClock = AnimationUtils.loadAnimation(CommentActivity.this, R.anim.rotate_anticlock);
        intent = getIntent();


        adsArrayList = new ArrayList<>();

        sharedPreferences = getSharedPreferences("UserLogin", this.MODE_PRIVATE);
        userType = sharedPreferences.getString("type", "").toString();
        idUser = sharedPreferences.getString("id", "");
        emailSender = sharedPreferences.getString("email", "");
        passwordSender = sharedPreferences.getString("password", "");


        idAds = intent.getStringExtra("adsId").toString();
        adsDescription = intent.getStringExtra(Config.TAG_DESCRIPTION).toString();
        adsImagePath = intent.getStringExtra(Config.TAG_IMAGE_PATH).toString();
        adsVideoPath = intent.getStringExtra("video_path").toString();

        phoneUser = intent.getStringExtra("phoneUser").toString().trim();
        emailUser = intent.getStringExtra("emailUser").toString().trim();

        idUserCompany = intent.getStringExtra("idUserCompany").toString().trim();
        nameUser = intent.getStringExtra("nameUser").toString().trim();
        rate = intent.getStringExtra("rate").toString().trim();
        numberView = intent.getStringExtra("numberView").toString().trim();
        countRate = intent.getStringExtra("countRate").toString().trim();


        int integer = Integer.valueOf(numberView) + 1;
        numViews.setText(integer + "");
        avgRate.setText(rate);
        numUsers.setText(countRate);
        companyName.setText(nameUser);

        textViewPhone.setText(getString(R.string.Call) + phoneUser);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        textViewDescription.setText(adsDescription);
        PicassoClient.downloadimage(CommentActivity.this, adsImagePath, imageViewAds);
        PicassoClient.frameImage(CommentActivity.this, adsVideoPath, videoImageViewAds);


        imageViewAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommentActivity.this, FullScreenImage.class);
                intent.putExtra("imagePath", adsImagePath);
                startActivity(intent);
            }
        });

        viewVideoAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommentActivity.this, FullScreenVideo.class);
                intent.putExtra("videoPath", adsVideoPath);
                startActivity(intent);
            }
        });


        hashMapComment = new HashMap<>();
        hashMapView = new HashMap<>();
        hashMapAddRate = new HashMap<>();

        arrayList = new ArrayList<HashMap<String, String>>();
        registerForContextMenu(listView);

        layoutParams.height = 0;
        layoutParams.width = 0;
        mainLinearLayout.setLayoutParams(layoutParams);


        layoutParamsRate.height = 0;
        layoutParamsRate.width = 0;
        mainLinearLayoutRate.setLayoutParams(layoutParamsRate);

        layoutParamsRateShow.height = 0;
        layoutParamsRateShow.width = 0;
        mainLinearLayoutRateShow.setLayoutParams(layoutParamsRateShow);

        layoutParamsRateEdit.height = 0;
        layoutParamsRateEdit.width = 0;
        mainLinearLayoutRateEdit.setLayoutParams(layoutParamsRateEdit);

        comment.setText("");
        comment.setEnabled(false);
        save.setEnabled(false);

        submitRate.setEnabled(false);
        submitRate.setBackgroundColor(Color.TRANSPARENT);
        editRate.setEnabled(false);
        editRate.setBackgroundColor(Color.TRANSPARENT);


        // this data of my company
        emailReceiver = emailUser;


        if (Locale.getDefault().getDisplayLanguage().equals(getString(R.string.arabic))) {
            save.setAnimation(animRotate);
            save.startAnimation(animRotate);
        }
        refreshLayout.setColorSchemeResources(R.color.purple, R.color.colorPrimary, R.color.colorPrimaryDark);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        getJSON();
                    }
                }, 3000);
            }
        });

        ratingBarEdit.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                // Hated it, Disliked it, It's OK, Liked it, Loved it

                editRate.setEnabled(true);
                editRate.setBackgroundResource(R.color.colorPrimary);
                if (rating == 0.5 || rating == 1) {
                    textRateEdit.setText(getString(R.string.rate1));
                } else if (rating == 1.5 || rating == 2) {
                    textRateEdit.setText(getString(R.string.rate2));
                } else if (rating == 2.5 || rating == 3) {
                    textRateEdit.setText(getString(R.string.rate3));
                } else if (rating == 3.5 || rating == 4) {
                    textRateEdit.setText(getString(R.string.rate4));
                } else if (rating == 4.5 || rating == 5) {
                    textRateEdit.setText(getString(R.string.rate5));
                }
                valueRate = String.valueOf(rating);
            }
        });


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                // Hated it, Disliked it, It's OK, Liked it, Loved it

                submitRate.setEnabled(true);
                submitRate.setBackgroundResource(R.color.colorPrimary);
                if (rating == 0.5 || rating == 1) {
                    textRate.setText(getString(R.string.rate1));
                } else if (rating == 1.5 || rating == 2) {
                    textRate.setText(getString(R.string.rate2));
                } else if (rating == 2.5 || rating == 3) {
                    textRate.setText(getString(R.string.rate3));
                } else if (rating == 3.5 || rating == 4) {
                    textRate.setText(getString(R.string.rate4));
                } else if (rating == 4.5 || rating == 5) {
                    textRate.setText(getString(R.string.rate5));
                }
                valueRate = String.valueOf(rating);
            }
        });

        submitRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hashMapAddRate.put("value_rate", valueRate);
                hashMapAddRate.put("user_id", idUser);
                hashMapAddRate.put("ads_id", idAds);
                hashMapAddRate.put("function", "addRate");
                getJSONRate();
            }
        });

        editDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(CommentActivity.this, (View) editDelete);
                MenuInflater menuInflater = popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.popup_menu, popupMenu.getMenu());
                PopupMenuEvent popupMenuEvent = new PopupMenuEvent(CommentActivity.this);
                popupMenu.setOnMenuItemClickListener(popupMenuEvent);
                popupMenu.show();
            }
        });
        editRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hashMapAddRate = new HashMap<>();
                hashMapAddRate.put("value_rate", valueRate);
                hashMapAddRate.put("user_id", idUser);
                hashMapAddRate.put("ads_id", idAds);
                hashMapAddRate.put("function", "updateRate");

                getJSONRate();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strComment = comment.getText().toString();
                submitAdd();

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strComment = editComment.getText().toString();
                submitEdit();

            }
        });
        cancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
                inputLayoutEditComment.setErrorEnabled(false);
            }
        });
        cancelSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogSendEmail.dismiss();
                editSubjectMessageEmail.setText("");
                editTextMessageEmail.setText("");
                inputLayoutSubjectMessageEmail.setErrorEnabled(false);
            }
        });

        sendMessageEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strSubjectMessageEmail = editSubjectMessageEmail.getText().toString();
                strTextMessageEmail = editTextMessageEmail.getText().toString();

                submitSendMessageEmail();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(CommentActivity.this, ProfileActivity.class);
                intent.putExtra("nameActivity", "CommentActivity");
                intent.putExtra("idUserProfile", userIds[position]);
                startActivity(intent);

            }
        });

        contactCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpen) {

                    linearLayoutPhone.startAnimation(fabClose);
                    linearLayoutEmail.startAnimation(fabClose);
                    linearLayoutSMS.startAnimation(fabClose);
                    linearLayoutCopy.startAnimation(fabClose);

                    linearLayoutPhone.setClickable(false);
                    linearLayoutEmail.setClickable(false);
                    linearLayoutSMS.setClickable(false);
                    linearLayoutCopy.setClickable(false);

                    contactCompany.startAnimation(rotateAntiClock);
                    isOpen = false;
                } else {

                    linearLayoutPhone.startAnimation(fabOpen);
                    linearLayoutEmail.startAnimation(fabOpen);
                    linearLayoutSMS.startAnimation(fabOpen);
                    linearLayoutCopy.startAnimation(fabOpen);

                    linearLayoutPhone.setClickable(true);
                    linearLayoutEmail.setClickable(true);
                    linearLayoutSMS.setClickable(true);
                    linearLayoutCopy.setClickable(true);

                    contactCompany.startAnimation(rotateClock);
                    isOpen = true;
                }
            }
        });

        linearLayoutPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phoneUser));
                startActivity(callIntent);
            }
        });

        linearLayoutEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogSendEmail.show();
                receiverMessage.setText(getString(R.string.to) + emailReceiver);
                senderMessage.setText(getString(R.string.from) + emailSender);
            }
        });

        linearLayoutSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Uri uri = Uri.parse("smsto:" + phoneUser);
                    // No permisison needed
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
                    // Set the message to be sent
                    startActivity(smsIntent);
                } catch (Exception e) {
                    Toast.makeText(CommentActivity.this, "SMS failed, please try again later!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        linearLayoutCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied", phoneUser.toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(CommentActivity.this, getString(R.string.copyNumber), Toast.LENGTH_LONG).show();
            }
        });


        linearLayoutCompanyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CommentActivity.this, ViewSomeAds.class);
                intent.putExtra("typeShow", nameUser);
                intent.putExtra("idUserCompany", idUserCompany);
                startActivity(intent);
            }
        });


        //to save ad in saved
        addToSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (savedAdId.contains("," + idAds + ",")) {

                    deleteSaveAd();
                } else {
                    saveAd();

                }
            }
        });

        getJSON();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.copy:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied", textComments[info.position].toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(CommentActivity.this, getString(R.string.copy), Toast.LENGTH_LONG).show();
                return true;

            case R.id.delete:
                if (userIds[info.position].equals(idUser)) {
                    confirmDeleteComment();
                } else {

                    Toast.makeText(CommentActivity.this, getString(R.string.deleteComment), Toast.LENGTH_LONG).show();
                }
                return true;

            case R.id.edit:
                if (userIds[info.position].equals(idUser)) {
                    alertDialog.show();


                    editComment.setText(textComment.toString());

                } else {
                    Toast.makeText(CommentActivity.this, getString(R.string.editComment), Toast.LENGTH_LONG).show();
                }

                return true;

            case R.id.cancel:
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }


    private void sendMessage() {
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
        alertDialogSendEmail.dismiss();

        pdialog = ProgressDialog.show(CommentActivity.this, "", getString(R.string.Sending), true);

        class RetreiveFeedTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                try {
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(emailSender));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailReceiver));
                    message.setSubject(strSubjectMessageEmail);
                    message.setContent(strTextMessageEmail, "text/html; charset=utf-8");
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
                Toast.makeText(CommentActivity.this, getString(R.string.doneSend), Toast.LENGTH_LONG).show();
            }
        }

        RetreiveFeedTask task = new RetreiveFeedTask();
        task.execute();
    }

    private void submitAdd() {

        inputLayoutAddComment.setErrorEnabled(false);
        if (!checkAddComment()) {
            comment.setAnimation(animShake);
            comment.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }

        hashMapComment.put("function", "addComment");
        hashMapComment.put("textComment", strComment);
        hashMapComment.put("idUser", idUser);
        hashMapComment.put("idAds", idAds);

        getJSONComment();
    }

    private boolean checkAddComment() {
        if (strComment.isEmpty()) {
            inputLayoutAddComment.setErrorEnabled(true);
            inputLayoutAddComment.setError(getString(R.string.addComment));
            comment.setError(getString(R.string.errorInput));
            requestFocus(comment);
            return false;
        }
        inputLayoutAddComment.setErrorEnabled(false);
        return true;
    }


    private void submitEdit() {

        inputLayoutEditComment.setErrorEnabled(false);

        if (!checkEditComment()) {
            editComment.setAnimation(animShake);
            editComment.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }

        hashMapComment = new HashMap<>();
        hashMapComment.put("function", "updateComment");
        hashMapComment.put("textComment", strComment);
        hashMapComment.put("idUser", idUser);
        hashMapComment.put("idAds", idAds);

        getJSONComment();

    }

    private boolean checkEditComment() {
        if (strComment.isEmpty()) {
            inputLayoutEditComment.setErrorEnabled(true);
            inputLayoutEditComment.setError(getString(R.string.addComment));
            return false;
        }
        inputLayoutEditComment.setErrorEnabled(false);
        alertDialog.dismiss();
        return true;
    }

    private void submitSendMessageEmail() {

        if (strSubjectMessageEmail.isEmpty() && strTextMessageEmail.isEmpty()) {
            vib.vibrate(120);
            confirmSendMessageEmail();
        } else {
            sendMessage();
        }


    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void confirmDeleteComment() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.confirmDeleteComment));

        alertDialogBuilder.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        hashMapComment = new HashMap<>();
                        hashMapComment.put("function", "deleteComment");
                        hashMapComment.put("idUser", idUser);
                        hashMapComment.put("idAds", idAds);
                        getJSONComment();
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

    private void confirmSendMessageEmail() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.confirmSendMessageEmail));

        alertDialogBuilder.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {


                        sendMessage();
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


    private void getJSON() {

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_ADVERTI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                showAllData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                hashMapView.put("function", "getComment_Rate");
                hashMapView.put("userId", idUser);
                hashMapView.put("adsId", idAds);
                return hashMapView;
            }


        };

        Singleton.getSingleton(this).setRequestQue(stringRequest);

    }

    public void showAllData(String json) {

        getComment(json);
        getAllComments(json);
        getRate(json);
        showSavedAd(json);
    }

    public void getComment(String json) {
        try {
            JSONObject jsonObjectParent = new JSONObject(json);
            JSONArray jsonArray = jsonObjectParent.getJSONArray("resultComment");

            if (jsonArray.length() == 0) {
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
                ;
                mainLinearLayout.setLayoutParams(layoutParams);

                comment.setEnabled(true);
                save.setEnabled(true);
            } else {
                JSONObject jsonObjectChild = jsonArray.getJSONObject(0);
                textComment = jsonObjectChild.getString("text");
                commentId = jsonObjectChild.getString("id");

                comment.setText("");
                comment.setEnabled(false);
                save.setEnabled(false);

                layoutParams.height = 0;
                layoutParams.width = 0;
                mainLinearLayout.setLayoutParams(layoutParams);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getJSONComment() {

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_ADVERTI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);

                if (!response.startsWith("Could")) {
                    getComment(response);
                    getAllComments(response);
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
                return hashMapComment;
            }

        };

        Singleton.getSingleton(this).setRequestQue(stringRequest);

    }

    public void getAllComments(String json) {
        try {
            JSONObject jsonObjectParent = new JSONObject(json);
            JSONArray jsonArray = jsonObjectParent.getJSONArray("resultAllComments");
            JSONObject jsonObjectChild = null;
            int count = 0;
            textComments = new String[jsonArray.length()];
            timeComments = new String[jsonArray.length()];
            userIds = new String[jsonArray.length()];
            userNames = new String[jsonArray.length()];
            userImagePaths = new String[jsonArray.length()];
            arrayList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObjectChild = jsonArray.getJSONObject(i);
                textComments[count] = jsonObjectChild.getString("text");
                timeComments[count] = jsonObjectChild.getString("time");
                userIds[count] = jsonObjectChild.getString("user_id");
                userNames[count] = jsonObjectChild.getString("name");
                userImagePaths[count] = jsonObjectChild.getString("image_path");
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("text", textComments[count]);
                hashMap.put("time", timeComments[count]);
                hashMap.put("name", userNames[count]);
                hashMap.put("image_path", userImagePaths[count]);
                count++;
                arrayList.add(hashMap);

            }

            Adapter_Comment customSimpleAdapter = new Adapter_Comment(CommentActivity.this, arrayList);
            listView.setAdapter(customSimpleAdapter);
            Helper.getListViewSize(listView);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getJSONRate() {


        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_ADVERTI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                if (!response.startsWith("Could")) {
                    getRate(response);
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
                return hashMapAddRate;
            }

        };

        Singleton.getSingleton(this).setRequestQue(stringRequest);

    }


    public void getRate(String json) {

        layoutParamsRateEdit.height = 0;
        layoutParamsRateEdit.width = 0;
        mainLinearLayoutRateEdit.setLayoutParams(layoutParamsRateEdit);

        try {
            JSONObject jsonObjectParent = new JSONObject(json);
            JSONArray jsonArray = jsonObjectParent.getJSONArray("resultRate");

            if (jsonArray.length() == 0) {

                layoutParamsRate.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                layoutParamsRate.width = LinearLayout.LayoutParams.MATCH_PARENT;
                ;
                mainLinearLayoutRate.setLayoutParams(layoutParamsRate);

                layoutParamsRateShow.height = 0;
                layoutParamsRateShow.width = 0;
                mainLinearLayoutRateShow.setLayoutParams(layoutParamsRateShow);

            } else {

                layoutParamsRate.height = 0;
                layoutParamsRate.width = 0;
                mainLinearLayoutRate.setLayoutParams(layoutParamsRate);

                layoutParamsRateShow.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                layoutParamsRateShow.width = LinearLayout.LayoutParams.MATCH_PARENT;
                mainLinearLayoutRateShow.setLayoutParams(layoutParamsRateShow);

                JSONObject jsonObjectChild = jsonArray.getJSONObject(0);
                valueRate = jsonObjectChild.getString("value_rate").toString();
                timeRate = jsonObjectChild.getString("time").toString();

                ratingBarShow.setRating(Float.valueOf(valueRate));
                textTimeRate.setText(getString(R.string.timeRate) + timeRate.substring(0, 10));
                ratingBarShow.setEnabled(false);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateRate() {

        if (ratingBarShow.getStepSize() == 0.5 || ratingBarShow.getStepSize() == 1) {
            textRateEdit.setText(getString(R.string.rate1));
        } else if (ratingBarShow.getStepSize() == 1.5 || ratingBarShow.getStepSize() == 2) {
            textRateEdit.setText(getString(R.string.rate2));
        } else if (ratingBarShow.getStepSize() == 2.5 || ratingBarShow.getStepSize() == 3) {
            textRateEdit.setText(getString(R.string.rate3));
        } else if (ratingBarShow.getStepSize() == 3.5 || ratingBarShow.getStepSize() == 4) {
            textRateEdit.setText(getString(R.string.rate4));
        } else if (ratingBarShow.getStepSize() == 4.5 || ratingBarShow.getStepSize() == 5) {
            textRateEdit.setText(getString(R.string.rate5));
        }

        ratingBarEdit.setRating(Float.valueOf(valueRate));
        layoutParamsRateShow.height = 0;
        layoutParamsRateShow.width = 0;
        mainLinearLayoutRateShow.setLayoutParams(layoutParamsRateShow);

        layoutParamsRateEdit.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        layoutParamsRateEdit.width = LinearLayout.LayoutParams.MATCH_PARENT;
        mainLinearLayoutRateEdit.setLayoutParams(layoutParamsRateEdit);
    }

    public void deleteRate() {

        hashMapAddRate = new HashMap<>();
        hashMapAddRate.put("user_id", idUser);
        hashMapAddRate.put("ads_id", idAds);
        hashMapAddRate.put("function", "deleteRate");

        getJSONRate();
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
            if (savedAdId.contains("," + idAds + ",")) {
                addToSaved.setBackgroundResource(R.drawable.ic_saved_ad);
            } else {
                addToSaved.setBackgroundResource(R.drawable.ic_save_ads);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveAd() {

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_ADVERTI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressBar.setVisibility(View.GONE);
                addToSaved.setBackgroundResource(R.drawable.ic_saved_ad);
                Toast.makeText(CommentActivity.this, getString(R.string.saved), Toast.LENGTH_LONG).show();
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

        Singleton.getSingleton(this).setRequestQue(stringRequest);
    }

    private void deleteSaveAd() {

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_ADVERTI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressBar.setVisibility(View.GONE);
                addToSaved.setBackgroundResource(R.drawable.ic_save_ads);
                Toast.makeText(CommentActivity.this, getString(R.string.Unsaved), Toast.LENGTH_LONG).show();
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

        Singleton.getSingleton(this).setRequestQue(stringRequest);
    }


    public class PopupMenuEvent implements android.support.v7.widget.PopupMenu.OnMenuItemClickListener {

        Context context;

        public PopupMenuEvent(Context context) {
            this.context = context;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.updateRate) {
                updateRate();
                return true;
            } else if (item.getItemId() == R.id.deleteRate) {
                deleteRate();
                return true;
            }

            return false;
        }
    }
}
