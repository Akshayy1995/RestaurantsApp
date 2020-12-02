package com.digiomega.hangrymatesrestaurants.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.digiomega.hangrymatesrestaurants.AllConstant.Callback;
import com.digiomega.hangrymatesrestaurants.AllConstant.Font;
import com.digiomega.hangrymatesrestaurants.AllConstant.Functions;
import com.digiomega.hangrymatesrestaurants.AllConstant.PreferenceClass;
import com.digiomega.hangrymatesrestaurants.ChangePassActivity;

import com.digiomega.hangrymatesrestaurants.AccountInfoActivity;
import com.digiomega.hangrymatesrestaurants.R;
import com.digiomega.hangrymatesrestaurants.SplashActivity;
import com.digiomega.hangrymatesrestaurants.Utils.FontHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Dinosoftlabs on 10/18/2019.
 */

public class ProfileFragment extends Fragment {

    RelativeLayout profile_div,change_password_div;
    SharedPreferences sharedPreferences;
    TextView language_txt;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.profile_frag, container, false);
        sharedPreferences = getContext().getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);

        FrameLayout frameLayout = v.findViewById(R.id.profile_main_container);
        FontHelper.applyFont(getContext(),frameLayout, Font.verdana);
        init(v);

        return v;
    }

    public void init(View v){
        profile_div = v.findViewById(R.id.profile_div);


        change_password_div = v.findViewById(R.id.change_password_div);



        profile_div.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getContext(), AccountInfoActivity.class));

            }
        });



        change_password_div.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ChangePassActivity.class));
            }
        });


        language_txt=v.findViewById(R.id.language_txt);

        List<String> language_names= Arrays.asList(getResources().getStringArray(R.array.language_names_for_show));
        List <String> language_code= Arrays.asList(getResources().getStringArray(R.array.language_code));

        String language= Locale.getDefault().getLanguage();
        if(sharedPreferences.getString(PreferenceClass.selected_language,null)!=null)
            language = sharedPreferences.getString(PreferenceClass.selected_language, language_code.get(0));

        if(language_code.contains(language)){
            language_txt.setText(language_names.get(language_code.indexOf(language)));
        }
        else {
            language_txt.setText("english");
        }


        v.findViewById(R.id.language_div).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Open_Language_dialog();
            }
        });
    }


    public void Open_Language_dialog(){
        List<String> language_names=  Arrays.asList(getResources().getStringArray(R.array.language_names_for_show));
        List <String> language_code=  Arrays.asList(getResources().getStringArray(R.array.language_code));

        final CharSequence[] options = getResources().getStringArray(R.array.language_names_for_show);
        Functions.Show_Options(getContext(), options, new Callback() {
            @Override
            public void Responce(String resp) {
                sharedPreferences.edit().putString(PreferenceClass.selected_language,language_code.get(language_names.indexOf(resp))).commit();

                startActivity(new Intent(getActivity(),SplashActivity.class));
                getActivity().finish();

            }
        });
    }



}

