package com.digiomega.hangrymatesrestaurants;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.digiomega.hangrymatesrestaurants.AllConstant.ApiRequest;
import com.digiomega.hangrymatesrestaurants.AllConstant.Callback;
import com.digiomega.hangrymatesrestaurants.AllConstant.Config;
import com.digiomega.hangrymatesrestaurants.AllConstant.PreferenceClass;
import com.digiomega.hangrymatesrestaurants.R;
import com.digiomega.hangrymatesrestaurants.Utils.ContextWrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by Dinosoftlabs on 10/18/2019.
 */

public class LoginActivity extends AppCompatActivity {


    SharedPreferences sPref;

    Context context;

    ProgressBar progressBar;

    Button log_in_now;

    TextView loginText,tv_email,tv_pass;

    EditText ed_email,ed_password;

    CheckBox terms_condition;
    TextView txt_terms,txt_privacy_policy;
    Boolean check_terms_condition = false;


    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );




    SharedPreferences sharedPreferences;

    @Override
    protected void attachBaseContext(Context newBase) {

        String[] language_array = newBase.getResources().getStringArray(R.array.language_code);
        List<String> language_code = Arrays.asList(language_array);
        sharedPreferences = newBase.getSharedPreferences(PreferenceClass.user, MODE_PRIVATE);
        String language = sharedPreferences.getString(PreferenceClass.selected_language, PreferenceClass.default_language);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && language_code.contains(language)) {
            Locale newLocale = new Locale(language);
            Context context = ContextWrapper.wrap(newBase, newLocale);
            super.attachBaseContext(context);
        }
        else {
            super.attachBaseContext(newBase);
        }

    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorBlack));
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        context = LoginActivity.this;
        sPref =getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);

        tv_email = (TextView)findViewById(R.id.tv_email);
        tv_pass = (TextView)findViewById(R.id.tv_password);


        progressBar =   findViewById(R.id.pbar);

        loginText = (TextView)findViewById(R.id.login_title);



        ed_email = (EditText)findViewById(R.id.ed_email);
        ed_password =(EditText)findViewById(R.id.ed_password);
        log_in_now = (Button)findViewById(R.id.btn_login);

        log_in_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean valid = checkEmail(ed_email.getText().toString());

                if (ed_email.getText().toString().trim().equals("")) {

                    Toast.makeText(context, "Enter Email!", Toast.LENGTH_SHORT).show();

                } else if (ed_password.getText().toString().trim().equals("")) {

                    Toast.makeText(context, "Enter Password!", Toast.LENGTH_SHORT).show();
                }else if (ed_password.getText().toString().length()<6) {

                    Toast.makeText(context, "Enter Password Atleat 6 Charaters!", Toast.LENGTH_SHORT).show();
                }
                else if (!valid) {

                    Toast.makeText(context, "Enter Valid Email!", Toast.LENGTH_SHORT).show();
                }else if (!check_terms_condition){

                    Toast.makeText(LoginActivity.this, "Please Accept Terms of Use & Privacy Policy", Toast.LENGTH_SHORT).show();
                }else {

                    login();

                }
            }
        });

        terms_condition = findViewById(R.id.checkbox_terms_condition);
        txt_terms = findViewById(R.id.txt_terms);
        txt_privacy_policy = findViewById(R.id.txt_privacy_policy);

        terms_condition.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    // perform logic
                    check_terms_condition = true;
                }else {
                    check_terms_condition = false;
                }
            }
        });

        txt_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this,TermsConditionActivity.class);
                intent.putExtra("type","Terms of Use");
                startActivity(intent);

            }
        });

        txt_privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this,TermsConditionActivity.class);
                intent.putExtra("type","Privacy Policy");
                startActivity(intent);

            }
        });





    }


    private void login(){
        final String email = ed_email.getText().toString().trim();
        final String password = ed_password.getText().toString().trim();
        String device_tocken = sPref.getString(PreferenceClass.device_token,"");

        String url = Config.LOGIN_URL;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            jsonObject.put("device_token", ""+device_tocken);
            jsonObject.put("role","hotel");
            jsonObject.put("lat","");
            jsonObject.put("long","");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressBar.setVisibility(View.VISIBLE);
        log_in_now.setVisibility(View.GONE);
        ApiRequest.Call_Api(context, url, jsonObject, new Callback() {
            @Override
            public void Responce(String resp) {

                progressBar.setVisibility(View.GONE);
                log_in_now.setVisibility(View.VISIBLE);

                try {
                    JSONObject jsonResponse = new JSONObject(resp);

                    int code_id  = Integer.parseInt(jsonResponse.optString("code"));

                    if(code_id == 200) {

                        JSONObject json = new JSONObject(jsonResponse.toString());
                        JSONObject resultObj = json.getJSONObject("msg");
                        JSONObject json1 = new JSONObject(resultObj.toString());
                        JSONObject resultObj1 = json1.getJSONObject("UserInfo");
                        JSONObject resultObj2 = json1.getJSONObject("User");

                        SharedPreferences.Editor editor = sPref.edit();
                        editor.putString(PreferenceClass.pre_email, ed_email.getText().toString());
                        editor.putString(PreferenceClass.pre_pass, ed_password.getText().toString());
                        editor.putString(PreferenceClass.pre_first, resultObj1.optString("first_name"));
                        editor.putString(PreferenceClass.pre_last, resultObj1.optString("last_name"));
                        editor.putString(PreferenceClass.pre_contact, resultObj1.optString("phone"));
                        editor.putString(PreferenceClass.pre_user_id, resultObj1.optString("user_id"));
                        String user_id=resultObj1.optString("user_id");
                        editor.putString(PreferenceClass.ADMIN_USER_ID,user_id);
                        editor.putBoolean(PreferenceClass.IS_LOGIN, true);
                        editor.commit();


                        if(resultObj2.optString("role").equalsIgnoreCase("hotel")){

                            editor.putString(PreferenceClass.USER_TYPE,resultObj2.optString("role"));
                            editor.commit();
                            startActivity(new Intent(context,MainActivity.class));
                            finish();

                        }

                    }else{

                        JSONObject json = new JSONObject(jsonResponse.toString());
                        Toast.makeText(context,json.optString("msg"), Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });


    }

    private boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

}
