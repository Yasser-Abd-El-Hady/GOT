package com.example.muhammed.advertiapp;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

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


public class RegistrationActivity extends AppCompatActivity {

    EditText name, address, phone, birthday, email, password, editTextAddCodeValidation;

    CircleImageView imageProfile;
    private static final int SELECTED_PICTURE = 1, CROP_PICTURE = 2;
    TextView emailValidation;
    String strName, strAddress, strEmail, strPhone, strBirthday, strPassword, strType, strImageProfileName, strImageProfilePath, emailReceiver,
            emailSender, passwordSender, codeValidation, subjectValidation, textMessageValidation;
    Button signUp, addCodeValidation, cancelValidation;
    SharedPreferences sharedPreferences;
    HashMap<String, String> hashMap;
    Spinner type;
    TextInputLayout inputLayoutName, inputLayoutAddress, inputLayoutPhone, inputLayoutBirthday, inputLayoutType, inputLayoutEmail,
            inputLayoutPassword, inputLayoutAddCodeValidation;
    Vibrator vib;
    Animation animShake;
    Uri imageUri;

    private static final int PLACE_PICKER_REQUEST = 100;

    ProgressDialog progressDialog;
    AlertDialog alertDialog;
    Session session = null;
    ProgressDialog pdialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        name = (EditText) findViewById(R.id.name);
        birthday = (EditText) findViewById(R.id.birthday);
        address = (EditText) findViewById(R.id.address);
        phone = (EditText) findViewById(R.id.phone);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.userPassword);
        type = (Spinner) findViewById(R.id.type);
        imageProfile = (CircleImageView) findViewById(R.id.imageProfileUser);
        signUp = (Button) findViewById(R.id.userSignUp);
        inputLayoutName = (TextInputLayout) findViewById(R.id.inputLayoutName);
        inputLayoutAddress = (TextInputLayout) findViewById(R.id.inputLayoutAddress);
        inputLayoutPhone = (TextInputLayout) findViewById(R.id.inputLayoutPhone);
        inputLayoutBirthday = (TextInputLayout) findViewById(R.id.inputLayoutBirthday);
        inputLayoutType = (TextInputLayout) findViewById(R.id.inputLayoutType);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.inputLayoutEmail);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.inputLayoutPassword);


        birthday.setFocusable(false);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.createAccount));
        progressDialog.setMessage(getString(R.string.wait));


        animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        sharedPreferences = getSharedPreferences("UserLogin", this.MODE_PRIVATE);

        String[] persons = new String[]{"Type", "Company", "User"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.layout_spinner_item, R.id.textViewSpinnerItem, persons);
        type.setAdapter(adapter);
        hashMap = new HashMap<>();
        strEmail = "";
        strImageProfilePath = "";


        View viewSendMessage = getLayoutInflater().inflate(R.layout.layout_verify_email, null);
        addCodeValidation = (Button) viewSendMessage.findViewById(R.id.addCodeValidation);
        cancelValidation = (Button) viewSendMessage.findViewById(R.id.cancelValidation);
        editTextAddCodeValidation = (EditText) viewSendMessage.findViewById(R.id.editTextAddCodeValidation);
        emailValidation = (TextView) viewSendMessage.findViewById(R.id.emailValidation);
        inputLayoutAddCodeValidation = (TextInputLayout) viewSendMessage.findViewById(R.id.inputLayoutAddCodeValidation);

        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
        builder.setView(viewSendMessage);
        alertDialog = builder.create();

        cancelValidation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                emailValidation.setText(getString(R.string.sentCode));
                editTextAddCodeValidation.setText("");
                inputLayoutAddCodeValidation.setErrorEnabled(false);
            }
        });


        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(photoPickerIntent, getString(R.string.selectImage)), SELECTED_PICTURE);
            }
        });

        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialog dialog = new DateDialog(v, false);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "DatePicker");

                birthday.setFocusable(false);
                birthday.setClickable(true);
            }
        });

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                Intent intent;
                try {
                    intent = builder.build(RegistrationActivity.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        strType = "";
                        break;
                    case 1:
                        strType = "Company";
                        break;
                    case 2:
                        strType = "User";
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                strType = "Admin";
            }
        });

        // When the user press the Submit button, App will check all the data required
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strName = name.getText().toString().trim();
                strBirthday = birthday.getText().toString().trim();
                strAddress = address.getText().toString().trim();
                strPhone = phone.getText().toString().trim();
                strEmail = email.getText().toString().trim();
                strPassword = password.getText().toString().trim();

                submitForm();
            }
        });

        addCodeValidation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFormAddCodeValidation();
            }
        });

        emailSender = Config.Account_App;
        passwordSender = Config.Password_App;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECTED_PICTURE && resultCode == RESULT_OK) {
            try {
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
            } catch (ActivityNotFoundException ex) {

            }
        } else if (requestCode == CROP_PICTURE) {

            if (data != null) {
                Bundle bundle = data.getExtras();
                Bitmap selectedImage = bundle.getParcelable("data");
                imageProfile.setImageBitmap(selectedImage);
                ByteArrayOutputStream byteArrayOutputStreamObject;
                byteArrayOutputStreamObject = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);
                byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
                strImageProfileName = "GOT_IMG_" + String.valueOf(System.currentTimeMillis());
                strImageProfilePath = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

            } else {
                Toast.makeText(RegistrationActivity.this, getString(R.string.cropImage), Toast.LENGTH_LONG).show();
            }

        } else if (requestCode == PLACE_PICKER_REQUEST) {

            if (data != null) {
                Place place = PlacePicker.getPlace(data, this);
                String addressFromMap = String.valueOf(place.getAddress());
                address.setText(addressFromMap);
            } else {
                Toast.makeText(RegistrationActivity.this, getString(R.string.selectAddress), Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(RegistrationActivity.this, getString(R.string.pickedImage), Toast.LENGTH_LONG).show();
        }

    }

    private void submitForm() {
        inputLayoutName.setErrorEnabled(false);
        inputLayoutBirthday.setErrorEnabled(false);
        inputLayoutAddress.setErrorEnabled(false);
        inputLayoutPhone.setErrorEnabled(false);
        inputLayoutType.setErrorEnabled(false);
        inputLayoutEmail.setErrorEnabled(false);
        inputLayoutPassword.setErrorEnabled(false);


        if (!checkImage()) {
            return;
        }
        if (!checkName()) {
            name.setAnimation(animShake);
            name.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }
        if (!checkBirthday()) {
            birthday.setAnimation(animShake);
            birthday.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }
        if (!checkAddress()) {
            address.setAnimation(animShake);
            address.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }
        if (!checkPhone()) {
            phone.setAnimation(animShake);
            phone.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }
        if (!checkEmail()) {
            email.setAnimation(animShake);
            email.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }
        if (!checkPassword()) {
            password.setAnimation(animShake);
            password.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }
        if (!checkType()) {
            type.setAnimation(animShake);
            type.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }

        sendCode();

    }

    private boolean checkImage() {
        if (strImageProfilePath.isEmpty()) {
            Toast.makeText(this, getString(R.string.enterImage), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkName() {
        if (strName.isEmpty()) {
            inputLayoutName.setErrorEnabled(true);
            inputLayoutName.setError(getString(R.string.err_msg_name));
            name.setError(getString(R.string.err_msg_required));
            requestFocus(name);
            return false;
        }
        inputLayoutName.setErrorEnabled(false);
        return true;
    }

    private boolean checkBirthday() {
        if (strBirthday.isEmpty()) {
            inputLayoutBirthday.setErrorEnabled(true);
            inputLayoutBirthday.setError(getString(R.string.err_msg_birthday));
            birthday.setError(getString(R.string.err_msg_required));
            requestFocus(birthday);
            return false;
        }
        inputLayoutBirthday.setErrorEnabled(false);
        return true;
    }

    private boolean checkAddress() {
        if (strAddress.isEmpty()) {
            inputLayoutAddress.setErrorEnabled(true);
            inputLayoutAddress.setError(getString(R.string.err_msg_address));
            address.setError(getString(R.string.err_msg_required));
            requestFocus(address);
            return false;
        }
        inputLayoutAddress.setErrorEnabled(false);
        return true;
    }

    private boolean checkPhone() {
        if (strPhone.isEmpty()) {
            inputLayoutPhone.setErrorEnabled(true);
            inputLayoutPhone.setError(getString(R.string.err_msg_phone));
            phone.setError(getString(R.string.err_msg_required));
            requestFocus(phone);
            return false;
        }
        inputLayoutPhone.setErrorEnabled(false);
        return true;
    }

    private boolean checkType() {
        if (strType.isEmpty()) {
            inputLayoutType.setErrorEnabled(true);
            inputLayoutType.setError(getString(R.string.err_msg_required));
            requestFocus(type);
            return false;
        }
        inputLayoutType.setErrorEnabled(false);
        return true;
    }

    private boolean checkEmail() {
        if (strEmail.isEmpty()) {
            inputLayoutType.setErrorEnabled(true);
            inputLayoutType.setError(getString(R.string.err_msg_email));
            email.setError(getString(R.string.err_msg_required));
            requestFocus(email);
            return false;
        }
        inputLayoutEmail.setErrorEnabled(false);
        return true;
    }

    private boolean checkPassword() {
        if (password.getText().toString().startsWith(" ") || password.getText().toString().endsWith(" ")) {
            inputLayoutPassword.setErrorEnabled(true);
            password.setError(getString(R.string.err_msg_required));
            requestFocus(password);
            if (password.getText().toString().isEmpty()) {
                inputLayoutPassword.setError(getString(R.string.err_msg_password));
            } else if (password.getText().toString().startsWith(" ")) {
                inputLayoutPassword.setError(getString(R.string.err_msg_password_beginning_space));
            } else {
                inputLayoutPassword.setError(getString(R.string.err_msg_password_end_spaces));
            }
            return false;
        }
        if (password.getText().toString().isEmpty()) {
            inputLayoutPassword.setErrorEnabled(true);
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            password.setError(getString(R.string.err_msg_required));
            return false;
        }
        inputLayoutPassword.setErrorEnabled(false);
        return true;
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    // When the user inserts all the correct data required and press the Submit button will store all data in database
    private void addUser() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_ADVERTI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                if (response.equals("FALSE")) {
                    Toast.makeText(RegistrationActivity.this, response, Toast.LENGTH_LONG).show();
                } else if (response.equals("This account is used previously")) {
                    Toast.makeText(RegistrationActivity.this, getString(R.string.accountUsed), Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email", strEmail).commit();
                    editor.putString("password", strPassword).commit();
                    editor.putString("id", response).commit();
                    editor.putString("image_path", Config.URL + strImageProfileName + ".png").commit();
                    editor.putString("name", strName).commit();
                    editor.putString("type", strType).commit();
                    startActivity(intent);
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
                hashMap.put("function", "addUser");
                return hashMap;
            }
        };
        Singleton.getSingleton(RegistrationActivity.this).setRequestQue(stringRequest);
    }


    private void sendCode() {

        final int min = 100000;
        final int max = 999999;
        final int random = new Random().nextInt((max - min) + 1) + min;

        codeValidation = String.valueOf(random);
        subjectValidation = codeValidation + getString(R.string.subjectMessageRegistration);
        textMessageValidation = getString(R.string.textMessageRegistration, strName) + getString(R.string.textMessageArabicRegistration, strName);


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

    private void sendMessage() {
        pdialog = ProgressDialog.show(RegistrationActivity.this, "", getString(R.string.Sending), true);
        alertDialog.show();

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
            hashMap.put("userImageProfileName", strImageProfileName);
            hashMap.put("userImageProfilePath", strImageProfilePath);
            hashMap.put("userName", strName);
            hashMap.put("userBirthday", strBirthday);
            hashMap.put("userAddress", strAddress);
            hashMap.put("userPhone", strPhone);
            hashMap.put("userEmail", strEmail);
            hashMap.put("userPassword", strPassword);
            hashMap.put("userType", strType);

            addUser();
        } else {
            Toast.makeText(RegistrationActivity.this, getString(R.string.incorrectCode), Toast.LENGTH_LONG).show();
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
