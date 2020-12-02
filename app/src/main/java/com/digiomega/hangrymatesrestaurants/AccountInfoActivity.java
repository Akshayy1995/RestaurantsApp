package com.digiomega.hangrymatesrestaurants;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.digiomega.hangrymatesrestaurants.AllConstant.Font;
import com.digiomega.hangrymatesrestaurants.AllConstant.PreferenceClass;
import com.digiomega.hangrymatesrestaurants.Utils.ContextWrapper;
import com.digiomega.hangrymatesrestaurants.Utils.FontHelper;
import com.digiomega.hangrymatesrestaurants.R;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Dinosoftlabs on 10/18/2019.
 */

public class AccountInfoActivity extends AppCompatActivity {
    ImageView back_icon;
    TextView user_f_name,user_l_name,user_contact_number,rider_mail;

    SharedPreferences sPref;

    @Override
    protected void attachBaseContext(Context newBase) {

        String[] language_array = newBase.getResources().getStringArray(R.array.language_code);
        List<String> language_code = Arrays.asList(language_array);
        sPref = newBase.getSharedPreferences(PreferenceClass.user, MODE_PRIVATE);
        String language = sPref.getString(PreferenceClass.selected_language, PreferenceClass.default_language);

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
        setContentView(R.layout.activity_account_info);
        sPref = getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);
        FrameLayout frameLayout = findViewById(R.id.account_main_container);

        FontHelper.applyFont(this,frameLayout, Font.verdana);
        init();
    }

    public void init(){

        String userF_name= sPref.getString(PreferenceClass.pre_first,"");
        String userL_name = sPref.getString(PreferenceClass.pre_last,"");
        String phone_number = sPref.getString(PreferenceClass.pre_contact,"");
        String rider_email = sPref.getString(PreferenceClass.pre_email,"");


        back_icon = findViewById(R.id.back_icon);
        user_f_name = findViewById(R.id.user_f_name);
        user_l_name = findViewById(R.id.user_l_name);
        user_contact_number = findViewById(R.id.user_contact_number);
        rider_mail = findViewById(R.id.rider_mail);

        user_f_name.setText(userF_name);
        user_l_name.setText(userL_name);
        user_contact_number.setText(phone_number);
        rider_mail.setText(rider_email);

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }



}
