package com.digiomega.hangrymatesrestaurants.Notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.digiomega.hangrymatesrestaurants.AllConstant.AllConstants;
import com.digiomega.hangrymatesrestaurants.AllConstant.PreferenceClass;
import com.digiomega.hangrymatesrestaurants.MainActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;


public class CustomNotificationReceiver extends BroadcastReceiver {

   String title,message,type;

    DatabaseReference rootref;

    public Context context;


    SharedPreferences sharedPreferences;

    @Override
    public void onReceive(final Context context, Intent intent) {

        this.context=context;

        sharedPreferences=context.getSharedPreferences(PreferenceClass.user,Context.MODE_PRIVATE);

        Bundle extras = intent.getExtras();
        rootref= FirebaseDatabase.getInstance().getReference();

        if (extras!=null) {

              if (sharedPreferences.getBoolean(PreferenceClass.IS_LOGIN,false)) {

                  JSONObject jsonObject = new JSONObject();
                  Set<String> keys = extras.keySet();
                  for (String key : keys) {
                      try {
                          jsonObject.put(key, JSONObject.wrap(extras.get(key)));
                      } catch(JSONException e) {
                      }
                  }

                  Log.d(AllConstants.tag,jsonObject.toString());

                    title = extras.getString("title");
                    message = extras.getString("body");

                    if(title.contains("received a new order")) {
                        Intent i = new Intent(context, MainActivity.class);
                        i.putExtra("order_id", extras.getString("order_id"));
                        i.putExtra("type", "new");
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }
                    }

           }

        }

    }