package com.digiomega.hangrymatesrestaurants;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.digiomega.hangrymatesrestaurants.Adapters.NavigationScreenAdapter;
import com.digiomega.hangrymatesrestaurants.AllConstant.PreferenceClass;
import com.digiomega.hangrymatesrestaurants.Fragments.EarningFragment;
import com.digiomega.hangrymatesrestaurants.Fragments.HistroyFragment;
import com.digiomega.hangrymatesrestaurants.Fragments.JobsFragment;
import com.digiomega.hangrymatesrestaurants.Fragments.ProfileFragment;
import com.digiomega.hangrymatesrestaurants.R;
import com.leerybit.escpos.DeviceCallbacks;
import com.leerybit.escpos.PosPrinter;
import com.leerybit.escpos.PosPrinter60mm;
import com.leerybit.escpos.bluetooth.BTService;
import com.digiomega.hangrymatesrestaurants.Utils.ContextWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;


/**
 * Created by Dinosoftlabs on 10/18/2019.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Integer[] images = {R.drawable.job_icon, R.drawable.hostory_icon,R.drawable.ic_earning, R.drawable.account_icon,R.drawable.logout };
    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    Toolbar toolbar;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    androidx.appcompat.app.ActionBarDrawerToggle mDrawerToggle;
    private Fragment mCurrentFragment;
    TextView toolbar_title;

    Context context;
    SharedPreferences sPref;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    public static boolean isAppRunning;

    TextView printer_status_txt;



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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorBlack));
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_main);
        mTitle = mDrawerTitle = getTitle();
        mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);


        context = MainActivity.this;
        sPref = getSharedPreferences(PreferenceClass.user,MODE_PRIVATE);


        setupToolbar();
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);


        LayoutInflater inflater = getLayoutInflater();
        View listHeaderView = inflater.inflate(R.layout.drawer_header,null, false);

        listHeaderView.findViewById(R.id.printer_btn).setOnClickListener(this);
        printer_status_txt=listHeaderView.findViewById(R.id.printer_status_txt);

        mDrawerList.addHeaderView(listHeaderView);

        mCurrentFragment= new JobsFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame,mCurrentFragment).commit();

        NavigationScreenAdapter adapter = new NavigationScreenAdapter(this, mNavigationDrawerItemTitles, images);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        setupDrawerToggle();



        init_Printer();

        Intent intent=getIntent();
        onNewIntent(intent);


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            set_language_local();
        }
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


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent!=null && intent.hasExtra("order_id")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent intent1=new Intent("newOrder");
                    intent1.putExtra("order_id",intent.getStringExtra("order_id"));
                    sendBroadcast(intent1);
                    setIntent(null);
                }
            },2000);

        }
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.printer_btn:
                if(printer!=null && !printer.isConnected())
                    printer.connect();
                break;
        }

    }

    public static PosPrinter60mm printer;
    public void init_Printer(){
        printer =new PosPrinter60mm(this);

        printer.setDeviceCallbacks(new DeviceCallbacks() {
            @Override
            public void onConnected() {

            }

            @Override
            public void onFailure() {

            }

            @Override
            public void onDisconnected() {

            }
        });

        printer.setStateChangedListener(new PosPrinter.OnStateChangedListener() {
            @Override
            public void onChanged(int state, Message msg) {
                switch (state){
                    case BTService.STATE_NONE:
                        printer_status_txt.setText("NONE");
                        break;
                    case BTService.STATE_CONNECTED:
                        printer_status_txt.setText("Connected");
                        break;
                    case BTService.STATE_CONNECTING:
                        printer_status_txt.setText("Connecting...");
                        break;
                    case BTService.STATE_LISTENING:
                        printer_status_txt.setText("Listening...");
                        break;
                }


            }
        });

    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

    }



    private void selectItem(int position) {

        Fragment fragment = null;

        switch (position) {
            case 1:
                fragment = new JobsFragment();
                break;

            case 2:
                fragment = new HistroyFragment();

                break;
            case 3:
                fragment = new EarningFragment();

                break;
            case 4:
                fragment = new ProfileFragment();

                break;
            case 5:
                logOutUser();
                break;


        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            mDrawerList.setItemChecked(position-1, true);
            mDrawerList.setSelection(position-1);
            setTitle(mNavigationDrawerItemTitles[position-1]);
            mDrawerLayout.closeDrawer(mDrawerList);

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }


    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        toolbar_title.setText(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    void setupToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    void setupDrawerToggle(){
        mDrawerToggle = new androidx.appcompat.app.ActionBarDrawerToggle(this,mDrawerLayout,toolbar, R.string.drawer_open, R.string.drawer_close);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle.syncState();
    }


    private void logOutUser(){
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(PreferenceClass.USER_TYPE,"");
        editor.putString(PreferenceClass.pre_email, "");
        editor.putString(PreferenceClass.pre_pass, "");
        editor.putString(PreferenceClass.pre_first, "");
        editor.putString(PreferenceClass.pre_last, "");
        editor.putString(PreferenceClass.pre_contact, "");
        editor.putString(PreferenceClass.pre_user_id, "");
        editor.putString(PreferenceClass.ADMIN_USER_ID,"");

        editor.putBoolean(PreferenceClass.IS_LOGIN, false);
        editor.commit();
        startActivity(new Intent(context, LoginActivity.class));
        finish();

    }

    @Override
    public void onBackPressed() {

        if(mDrawerLayout.isDrawerOpen(mDrawerList)){
            mDrawerLayout.closeDrawer(mDrawerList);
        }
        else {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                super.onBackPressed();
                MainActivity.this.finish();
                return;
            } else {
                Toast.makeText(getBaseContext(), "Tap Again To Exit", Toast.LENGTH_SHORT).show();


                mBackPressed = System.currentTimeMillis();

            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAppRunning = false;
    }



}
