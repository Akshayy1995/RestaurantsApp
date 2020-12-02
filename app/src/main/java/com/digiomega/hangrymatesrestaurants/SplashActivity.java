package com.digiomega.hangrymatesrestaurants;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.digiomega.hangrymatesrestaurants.AllConstant.PreferenceClass;
import com.digiomega.hangrymatesrestaurants.Utils.ContextWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;


/**
 * Created by Dinosoftlabs on 10/18/2019.
 */

public class SplashActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    private static int SPLASH_TIME_OUT = 3000;


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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorBlack));
        }
        setContentView(R.layout.splash_activity);

        sharedPreferences = getSharedPreferences(PreferenceClass.user,MODE_PRIVATE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            set_language_local();
        }

        initUI();

    }

    private void initUI(){
      new Handler().postDelayed(new Runnable() {

        @Override
        public void run() {

            String getUserType = sharedPreferences.getString(PreferenceClass.USER_TYPE,"");
            boolean getLoINSession = sharedPreferences.getBoolean(PreferenceClass.IS_LOGIN,false);

            if(!getLoINSession){
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
            else {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }
    }, SPLASH_TIME_OUT);

}



    public void set_language_local(){
        String [] language_array=getResources().getStringArray(R.array.language_code);
        List<String> language_code= Arrays.asList(language_array);

        String language=sharedPreferences.getString(PreferenceClass.selected_language,PreferenceClass.default_language);


        if(language_code.contains(language)) {
            Locale myLocale = new Locale(language);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = new Configuration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            onConfigurationChanged(conf);

        }


    }


}
