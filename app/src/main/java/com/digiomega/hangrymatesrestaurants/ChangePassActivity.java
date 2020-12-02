package com.digiomega.hangrymatesrestaurants;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

/**
 * Created by Dinosoftlabs on 10/18/2019.
 */

public class ChangePassActivity extends AppCompatActivity {
    ImageView back_icon;
    EditText old_password,new_password,confirm_password;
    Button btn_change_pass;
    SharedPreferences sharedPreferences;
    Context context;
    ProgressBar changePassProgress;



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
        setContentView(R.layout.activity_change_pass);
        sharedPreferences = getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);
        context = ChangePassActivity.this;

        initUI();

    }

    public void initUI(){
        btn_change_pass = findViewById(R.id.btn_change_pass);
        changePassProgress = findViewById(R.id.changePassProgress);
        old_password = findViewById(R.id.ed_old_pass);
        new_password = findViewById(R.id.ed_new_pass);
        confirm_password = findViewById(R.id.ed_confirm_pass);

        btn_change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (old_password.getText().toString().trim().equals("") || old_password.getText().toString().length()<6) {

                    Toast.makeText(context, "Check password length can not be shorter than 6!", Toast.LENGTH_SHORT).show();
                    old_password.setError("Check password length can not be shorter than 6!");

                } else if (new_password.getText().toString().trim().equals("") || new_password.getText().toString().length()<6) {

                    Toast.makeText(context, "Check password length can not be shorter than 6!", Toast.LENGTH_SHORT).show();
                    new_password.setError("Check password length can not be shorter than 6!");
                }else if (confirm_password.getText().toString().trim().equals("") || confirm_password.getText().toString().length()<6) {

                    Toast.makeText(context, "Check password length can not be shorter than 6!", Toast.LENGTH_SHORT).show();
                    confirm_password.setError("Check password length can not be shorter than 6!");
                }
                else {
                    if (new_password.getText().toString().equals(confirm_password.getText().toString())) {

                        changePasswordVollyRequest();
                    } else {
                        Toast.makeText(context, "Password does not match", Toast.LENGTH_LONG).show();
                        confirm_password.setError("Password does not match");
                        new_password.setError("Password does not match");
                        //passwords not matching.please try again
                    }
                }
            }
        });
        back_icon = findViewById(R.id.back_icon);

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try  {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {

                }


               finish();

            }
        });

    }

    public void changePasswordVollyRequest(){

        String getUser_id = sharedPreferences.getString(PreferenceClass.pre_user_id,"");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id",getUser_id);
            jsonObject.put("old_password",old_password.getText().toString());
            jsonObject.put("new_password",new_password.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        btn_change_pass.setVisibility(View.GONE);
        changePassProgress.setVisibility(View.VISIBLE);

        ApiRequest.Call_Api(context, Config.CHANGE_PASSWORD, jsonObject, new Callback() {
            @Override
            public void Responce(String resp) {


                btn_change_pass.setVisibility(View.VISIBLE);
                changePassProgress.setVisibility(View.GONE);

                try {
                    JSONObject jsonResponse = new JSONObject(resp);

                    int code_id  = Integer.parseInt(jsonResponse.optString("code"));
                    if (code_id==200){
                        changePassProgress.setVisibility(View.GONE);
                        Toast.makeText(context,"Password Changed Successfully",Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else {
                        changePassProgress.setVisibility(View.GONE);
                        Toast.makeText(context,resp,Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

    }

}
