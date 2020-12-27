package com.example.muhammed.advertiapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LogIn_SignUp_Main extends AppCompatActivity {


    EditText emailEt, passwordEt;
    Button loginBtn;
    ProgressDialog progressDialog;
    Intent intent;
    SharedPreferences shrd;
    TextInputLayout inputLayoutEmail, inputLayoutPassword;
    Vibrator vib;
    Animation animShake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login_signup);

        emailEt = (EditText) findViewById(R.id.emailEditText);
        passwordEt = (EditText) findViewById(R.id.passwordEditText);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.inputLayoutEmail);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.inputLayoutPassword);

        animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        progressDialog = new ProgressDialog(this);

        shrd = getSharedPreferences("UserLogin", this.MODE_PRIVATE);

        register_from_shrd();
        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                submitForm();
            }
        });

    }

    private void submitForm() {

        if (!checkEmail()) {
            emailEt.setAnimation(animShake);
            emailEt.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }
        if (!checkPassword()) {
            passwordEt.setAnimation(animShake);
            passwordEt.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }

        inputLayoutEmail.setErrorEnabled(false);
        inputLayoutPassword.setErrorEnabled(false);
        progressDialog.setMessage(getString(R.string.wait));
        progressDialog.show();
        user_login();

    }

    private boolean checkEmail() {
        String email = emailEt.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setErrorEnabled(true);
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            emailEt.setError(getString(R.string.err_msg_required));
            requestFocus(emailEt);
            return false;
        }
        inputLayoutEmail.setErrorEnabled(false);
        return true;
    }

    private boolean checkPassword() {
        if (passwordEt.getText().toString().startsWith(" ") || passwordEt.getText().toString().endsWith(" ")) {
            inputLayoutPassword.setErrorEnabled(true);
            passwordEt.setError(getString(R.string.err_msg_required));
            requestFocus(passwordEt);
            if (passwordEt.getText().toString().isEmpty()) {
                inputLayoutPassword.setError(getString(R.string.err_msg_password));
            } else if (passwordEt.getText().toString().startsWith(" ")) {
                inputLayoutPassword.setError(getString(R.string.err_msg_password_beginning_space));
            } else {
                inputLayoutPassword.setError(getString(R.string.err_msg_password_end_spaces));
            }
            return false;
        }
        if (passwordEt.getText().toString().isEmpty()) {
            inputLayoutPassword.setErrorEnabled(true);
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            passwordEt.setError(getString(R.string.err_msg_required));
            return false;
        }
        inputLayoutPassword.setErrorEnabled(false);
        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void save_user_data(String id, String image_path, String name, String type) {
        SharedPreferences.Editor editor = shrd.edit();
        editor.putString("email", emailEt.getText().toString().trim()).commit();
        editor.putString("password", passwordEt.getText().toString().trim()).commit();
        editor.putString("id", id).commit();
        editor.putString("image_path", image_path).commit();
        editor.putString("name", name).commit();
        editor.putString("type", type).commit();
    }

    public void register_from_shrd() {
        SharedPreferences.Editor editor = shrd.edit();
        String email = shrd.getString("email", "");
        String pass = shrd.getString("password", "");
        if (!(TextUtils.isEmpty(email)) && !(TextUtils.isEmpty(pass))) {
            startActivity(new Intent(LogIn_SignUp_Main.this, HomeActivity.class));
            LogIn_SignUp_Main.this.finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void user_login() {
        final String login_email = emailEt.getText().toString().trim();
        final String login_password = passwordEt.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_ADVERTI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        if (!(jsonArray.isNull(0))) {
                            JSONObject user_object = jsonArray.getJSONObject(0);
                            String id = user_object.getString("id");
                            String image_path = user_object.getString("image_path");
                            String name = user_object.getString("name");
                            String type = user_object.getString("type");
                            Toast.makeText(LogIn_SignUp_Main.this, getString(R.string.loginSuccessfully), Toast.LENGTH_LONG).show();
                            Intent user_intent = new Intent(LogIn_SignUp_Main.this, HomeActivity.class);

                            progressDialog.dismiss();
                            save_user_data(id, image_path, name, type);
                            //passwordEt.setText("");
                            startActivity(user_intent);
                        } else {
                            Toast.makeText(LogIn_SignUp_Main.this, getString(R.string.loginFailed), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();

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
                mapData.put("function", "userLogin");
                mapData.put("login_email", login_email);
                mapData.put("login_password", login_password);
                return mapData;

            }
        };

        Singleton.getSingleton(LogIn_SignUp_Main.this).setRequestQue(stringRequest);


    }


    public void signUp(View view) {
        intent = new Intent(LogIn_SignUp_Main.this, RegistrationActivity.class);
        startActivity(intent);
    }


}
