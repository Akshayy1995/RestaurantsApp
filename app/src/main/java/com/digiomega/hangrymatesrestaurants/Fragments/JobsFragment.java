package com.digiomega.hangrymatesrestaurants.Fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.percentlayout.widget.PercentRelativeLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.text.Html;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.digiomega.hangrymatesrestaurants.AllConstant.AllConstants;
import com.digiomega.hangrymatesrestaurants.AllConstant.ApiRequest;
import com.digiomega.hangrymatesrestaurants.AllConstant.Callback;
import com.digiomega.hangrymatesrestaurants.AllConstant.Functions;
import com.digiomega.hangrymatesrestaurants.MainActivity;
import com.gmail.samehadar.iosdialog.CamomileSpinner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.digiomega.hangrymatesrestaurants.Adapters.ExpandableListAdapter;
import com.digiomega.hangrymatesrestaurants.AllConstant.Config;
import com.digiomega.hangrymatesrestaurants.AllConstant.PreferenceClass;
import com.digiomega.hangrymatesrestaurants.Models.MenuItemExtraModel;
import com.digiomega.hangrymatesrestaurants.Models.MenuItemModel;
import com.digiomega.hangrymatesrestaurants.Models.NewOrderModelClass;
import com.digiomega.hangrymatesrestaurants.R;
import com.digiomega.hangrymatesrestaurants.Utils.CustomExpandableListView;
import com.digiomega.hangrymatesrestaurants.Utils.FirebaseRecyclerAdapterCustom;
import com.digiomega.hangrymatesrestaurants.Utils.FontHelper;

import com.leerybit.escpos.Ticket;
import com.leerybit.escpos.TicketBuilder;
import com.leerybit.escpos.widgets.TicketPreview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Dinosoftlabs on 10/18/2019.
 */

public class JobsFragment extends Fragment {

    public static boolean ACCEPTED_ORDER, ACCPT_DEC_FLAG, HANDLER_FLAG;

    SharedPreferences sPre;

    public static String serverkey;

    LinearLayout main_layout;
    SwipeRefreshLayout refresh_layout;

    PercentRelativeLayout no_job_div;

    TextView inst_tv, hotel_name_tv, hotel_phone_number_tv, hotel_add_tv,sub_total_amount_tv, total_amount_tv, payment_method_tv,
            order_user_name_tv, order_user_address_tv, order_user_number_tv, total_delivery_fee_tv,total_tip_tv, tax_tv, total_tex_tv, ordr_nmbr;
    ExpandableListAdapter listAdapter;
    CustomExpandableListView customExpandableListView;
    ArrayList<MenuItemModel> listDataHeader;
    ArrayList<MenuItemExtraModel> listChildData;
    private ArrayList<ArrayList<MenuItemExtraModel>> ListChild;
    String order_id;
    String preparation_time = "";

    private RelativeLayout accept_div, decline_div, order_detail_layout;
    public static boolean FLAG_ACCEPT;
    ScrollView scrolView;
    LinearLayout hotel_btn_div;
    RelativeLayout orders_div;

    boolean mIsReceiverRegistered = false;
    NewOrderBroadCast mReceiver = null;

    DatabaseReference mDatabase;
    FirebaseDatabase firebaseDatabase;

    RecyclerView current_order_list,pending_order_list;
    View view;

    RelativeLayout transparent_layer,progressDialog;
    CamomileSpinner dealsProgressBar;

    String user_id;
    SearchView searchView;


    FireBaseFilterAdapter fRadapter;

    FireBaseFilterAdapterAccepted pendingadapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        view = inflater.inflate(R.layout.jobs_frag, container, false);


        orders_div = view.findViewById(R.id.orders_div);
        FrameLayout frameLayout = view.findViewById(R.id.order_fragment_container);
        FontHelper.applyFont(getContext(), frameLayout, com.digiomega.hangrymatesrestaurants.AllConstant.Font.verdana);
        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        sPre = getContext().getSharedPreferences(PreferenceClass.user, getContext().MODE_PRIVATE);

        initUI(view);
        orders_div.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        return view;
    }

    private void initUI( View v) {
        user_id = sPre.getString(PreferenceClass.pre_user_id, "");
        LinearLayoutManager manager=new LinearLayoutManager(getContext());
        manager.setReverseLayout(true);
        current_order_list = (RecyclerView) view.findViewById(R.id.list_of_messages);
        current_order_list.setLayoutManager(manager);
        current_order_list.setNestedScrollingEnabled(false);
        current_order_list.setHasFixedSize(false);

        LinearLayoutManager manager2=new LinearLayoutManager(getContext());
        manager2.setReverseLayout(true);
        pending_order_list = (RecyclerView) view.findViewById(R.id.accepted_order_recyclerview);
        pending_order_list.setLayoutManager(manager2);
        pending_order_list.setNestedScrollingEnabled(false);
        pending_order_list.setHasFixedSize(false);

        dealsProgressBar = view.findViewById(R.id.dealsProgress);
        dealsProgressBar.start();
        progressDialog = view.findViewById(R.id.progressDialog);
        transparent_layer = view.findViewById(R.id.transparent_layer);
        transparent_layer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });




        getAllOrderParser();
        getAllPendingOrderParser();

        no_job_div = v.findViewById(R.id.no_job_div);
        refresh_layout = v.findViewById(R.id.refresh_layout);
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                orders_div.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                HANDLER_FLAG = true;
                scrolView.setVisibility(View.GONE);
                order_detail_layout.setVisibility(View.GONE);

                refresh_layout.setRefreshing(false);

            }
        });


        main_layout = v.findViewById(R.id.main_layout);
        initViewMenuOrderExtraItem(v,true);

        searchView = v.findViewById(R.id.floating_search_view);
        searchView.setQueryHint(Html.fromHtml("<font color = #dddddd>" + "Search Restaurant Menu" + "</font>"));
        TextView searchText = (TextView)
                v.findViewById(androidx.appcompat.R.id.search_src_text);
        searchText.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        searchText.setPadding(0,0,0,0);
        searchText.setTextColor(getResources().getColor(R.color.or_color_name));
        LinearLayout searchEditFrame = (LinearLayout) searchView.findViewById(R.id.search_edit_frame);
        ((LinearLayout.LayoutParams) searchEditFrame.getLayoutParams()).leftMargin = 5;
        Search(searchView);

    }


    private void Search(final androidx.appcompat.widget.SearchView searchView) {


        searchView.setFocusable(false);

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText.equalsIgnoreCase("")){

                    getAllOrderParser();
                    getAllPendingOrderParser();

                    InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


                }
                else {

                    fRadapter.getFilter().filter(newText);
                    pendingadapter.getFilter().filter(newText);


                }
                return true;
            }
        });


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {

                Log.i("SearchView:", "onClose");
                searchView.onActionViewCollapsed();

                return false;
            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.onActionViewExpanded();

            }
        });

        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {

            @Override
            public void onViewDetachedFromWindow(View arg0) {
                 }

            @Override
            public void onViewAttachedToWindow(View arg0) {

            }
        });
    }


    public void initViewMenuOrderExtraItem(View v,boolean isfrom_first) {


        order_id = sPre.getString(PreferenceClass.ORDER_ID, "");

        customExpandableListView = (CustomExpandableListView) v.findViewById(R.id.custon_list_order_items);
        customExpandableListView.setExpanded(true);
        customExpandableListView.setGroupIndicator(null);

        customExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return true; // This way the expander cannot be collapsed
            }
        });

        String order_title = sPre.getString(PreferenceClass.ORDER_HEADER, "");
        String order_inst = sPre.getString(PreferenceClass.ORDER_INS, "");
        String order_number = sPre.getString(PreferenceClass.ORDER_ID, "");

        hotel_btn_div = v.findViewById(R.id.hotel_btn_div);
        accept_div = v.findViewById(R.id.accept_div);
        decline_div = v.findViewById(R.id.decline_div);
        scrolView = v.findViewById(R.id.scrolView);
        ordr_nmbr = v.findViewById(R.id.ordr_nmbr);
        ordr_nmbr.setText("Order #" + order_number);
        /// All Text from API
        hotel_name_tv = v.findViewById(R.id.order_hotel_name);
        hotel_add_tv = v.findViewById(R.id.order_hotel_address);
        hotel_phone_number_tv = v.findViewById(R.id.order_hotel_number);
        payment_method_tv = v.findViewById(R.id.payment_method_tv);
        sub_total_amount_tv=v.findViewById(R.id.sub_total_amount_tv);
        total_amount_tv = v.findViewById(R.id.total_amount_tv);
        inst_tv = v.findViewById(R.id.inst_tv);
        inst_tv.setText(order_inst);
        order_user_name_tv = v.findViewById(R.id.order_user_name_tv);
        order_user_address_tv = v.findViewById(R.id.order_user_address_tv);
        order_user_number_tv = v.findViewById(R.id.order_user_number_tv);
        total_delivery_fee_tv = v.findViewById(R.id.total_delivery_fee_tv);
        total_tip_tv=v.findViewById(R.id.total_tip_tv);
        tax_tv = v.findViewById(R.id.tax_tv);
        total_tex_tv = v.findViewById(R.id.total_tex_tv);

        order_detail_layout = v.findViewById(R.id.order_detail_layout);

        if(isfrom_first){
            scrolView.setVisibility(View.GONE);
            order_detail_layout.setVisibility(View.GONE);
        }else {
            getserverkey();
            getOrderDetailItems();
            scrolView.setVisibility(View.VISIBLE);
            order_detail_layout.setVisibility(View.VISIBLE);
        }


        if (ACCEPTED_ORDER) {
            hotel_btn_div.setVisibility(View.VISIBLE);
          //  view.findViewById(R.id.print_div).setVisibility(View.GONE);
            ACCEPTED_ORDER = false;
        } else {
         //   view.findViewById(R.id.print_div).setVisibility(View.VISIBLE);
            hotel_btn_div.setVisibility(View.GONE);
        }

        accept_div.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FLAG_ACCEPT = true;
                customDialog();
                ACCPT_DEC_FLAG = true;

               Stop_ring_tone();

            }
        });

        decline_div.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FLAG_ACCEPT = false;
                customDialog();
                ACCPT_DEC_FLAG = true;

                Stop_ring_tone();

            }
        });

      /*  view.findViewById(R.id.print_div).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Print_order(false,AllConstants.UTF_8_charset);
            }
        });*/

    }



    private void getAllOrderParser() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = firebaseDatabase.getReference().child("restaurant").child(user_id).child("CurrentOrders");
        Query query=mDatabase.orderByKey();
        FirebaseRecyclerOptions<NewOrderModelClass> options =
                new FirebaseRecyclerOptions.Builder<NewOrderModelClass>()
                        .setQuery(query, NewOrderModelClass.class)
                        .build();


        fRadapter = new FireBaseFilterAdapter(options);


        fRadapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                fRadapter.notifyDataSetChanged();

            }
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                super.onItemRangeChanged(positionStart, itemCount, payload);
                fRadapter.notifyDataSetChanged();
            }
        });


        fRadapter.startListening();

        current_order_list.setAdapter(fRadapter);
        fRadapter.notifyDataSetChanged();

    }

    private void ChangeStatus(String order_id) {
        DatabaseReference mref=FirebaseDatabase.getInstance().getReference();

        final Query query2 =mref.child("restaurant").child(user_id).child("CurrentOrders").orderByChild("order_id").equalTo(order_id);
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot nodeDataSnapshot : dataSnapshot.getChildren()) {
                    String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                    String path = "restaurant"+ "/" +user_id+ "/" + "CurrentOrders" + "/" + key;
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("status", "1");
                    mref.child(path).updateChildren(result);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void getAllPendingOrderParser() {

        Query query=FirebaseDatabase.getInstance().getReference().child("restaurant").child(user_id).child("PendingOrders");
        FirebaseRecyclerOptions<NewOrderModelClass> options2 =
                new FirebaseRecyclerOptions.Builder<NewOrderModelClass>()
                        .setQuery(query, NewOrderModelClass.class)
                        .build();


        pendingadapter = new FireBaseFilterAdapterAccepted(options2);



        pendingadapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                pendingadapter.notifyDataSetChanged();

            }
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                super.onItemRangeChanged(positionStart, itemCount, payload);
                pendingadapter.notifyDataSetChanged();
            }
        });

        pendingadapter.startListening();

        pending_order_list.setAdapter(pendingadapter);
        pendingadapter.notifyDataSetChanged();

    }

    public void getserverkey(){
        DatabaseReference mref=FirebaseDatabase.getInstance().getReference();

        final Query query2 =mref.child("restaurant").child(user_id).child("CurrentOrders").orderByChild("order_id").equalTo(order_id);
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot nodeDataSnapshot : dataSnapshot.getChildren()) {
                    serverkey = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void getOrderDetailItems(){

        transparent_layer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);
        listDataHeader = new ArrayList<MenuItemModel>();
        ListChild = new ArrayList<>();

        JSONObject orderJsonObject = new JSONObject();
        try {
            orderJsonObject.put("order_id",order_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ApiRequest.Call_Api(getContext(), Config.SHOW_ORDER_DETAIL, orderJsonObject, new Callback() {
            @Override
            public void Responce(String resp) {

                try {
                    JSONObject jsonResponse = new JSONObject(resp);
                    int code_id  = Integer.parseInt(jsonResponse.optString("code"));

                    if(code_id == 200) {

                        JSONObject json = new JSONObject(jsonResponse.toString());
                        JSONArray jsonArray = json.getJSONArray("msg");

                        for (int i=0;i<jsonArray.length();i++){

                            JSONObject allJsonObject = jsonArray.getJSONObject(i);
                            JSONObject orderJsonObject = allJsonObject.getJSONObject("Order");
                            JSONObject userInfoObj = allJsonObject.getJSONObject("UserInfo");
                            JSONObject userAddressObj = allJsonObject.getJSONObject("Address");
                            JSONObject restaurantJsonObject = allJsonObject.getJSONObject("Restaurant");
                            JSONObject restaurantCurrencuObj = restaurantJsonObject.getJSONObject("Currency");
                            String currency_symbol= restaurantCurrencuObj.optString("symbol");

                            preparation_time = restaurantJsonObject.getString("preparation_time");
                            Log.d("preparation_time",restaurantJsonObject.getString("preparation_time"));
                            String first_name = userInfoObj.optString("first_name");
                            String last_name = userInfoObj.optString("last_name");
                            order_user_name_tv.setText(first_name+" "+last_name);
                            order_user_number_tv.setText(userInfoObj.optString("phone"));
                            String street_user = userAddressObj.optString("street");
                            String city_user = userAddressObj.optString("city");
                            String delivery = orderJsonObject.optString("delivery");
                            if(delivery.equalsIgnoreCase("0")){
                                order_user_address_tv.setText("Pick Up");
                            }
                            else {
                                order_user_address_tv.setText(street_user + " " + city_user);
                            }


                            inst_tv.setText(orderJsonObject.optString("instructions"));
                            total_amount_tv.setText(currency_symbol+orderJsonObject.optString("price"));

                            String getPaymentMethodTV = orderJsonObject.optString("cod");
                            if(getPaymentMethodTV.equalsIgnoreCase("0")) {
                                payment_method_tv.setText("Credit Card");
                            }
                            else {
                                payment_method_tv.setText("Cash On Delivery");
                            }

                            hotel_name_tv.setText(restaurantJsonObject.optString("name"));
                            hotel_phone_number_tv.setText(restaurantJsonObject.optString("phone"));
                            JSONObject restaurantAddress = restaurantJsonObject.getJSONObject("RestaurantLocation");
                            String street = restaurantAddress.optString("street");
                            String city = restaurantAddress.optString("city");

                            hotel_add_tv.setText(street+", "+city);


                            String rider_tip = orderJsonObject.optString("rider_tip");
                            if(rider_tip.equalsIgnoreCase("")){
                                rider_tip = "0.0";
                            }
                            total_tip_tv.setText(currency_symbol+" "+rider_tip);


                            String tax = orderJsonObject.optString("tax");
                            String delivery_fee = orderJsonObject.optString("delivery_fee");
                            total_delivery_fee_tv.setText(currency_symbol+delivery_fee);
                            String tax_free = restaurantJsonObject.optString("tax_free");
                            if(tax_free.equalsIgnoreCase("1")) {
                                tax_tv.setText("(" + "0" + "%)");
                            }


                            total_tex_tv.setText(tax);
                            String subTotal = orderJsonObject.optString("sub_total");
                            sub_total_amount_tv.setText(subTotal);



                            JSONArray menuItemArray = allJsonObject.getJSONArray("OrderMenuItem");

                            for (int j=0;j<menuItemArray.length();j++) {

                                JSONObject alljsonJsonObject2 = menuItemArray.getJSONObject(j);
                                MenuItemModel menuItemModel = new MenuItemModel();
                                menuItemModel.setItem_name(alljsonJsonObject2.optString("name"));
                                double price=Integer.parseInt(alljsonJsonObject2.optString("quantity","1"))*Double.parseDouble(alljsonJsonObject2.optString("price"));
                                menuItemModel.setItem_price(""+Functions.Roundoff_decimal(price));
                                menuItemModel.setId(alljsonJsonObject2.optString("id"));
                                menuItemModel.setOrder_id(alljsonJsonObject2.optString("order_id"));
                                menuItemModel.setOrder_quantity(alljsonJsonObject2.optString("quantity"));
                                menuItemModel.setCurrency(currency_symbol);

                                listDataHeader.add(menuItemModel);

                                listChildData = new ArrayList<>();

                                JSONArray extramenuItemArray = alljsonJsonObject2.getJSONArray("OrderMenuExtraItem");
                                if(extramenuItemArray!=null&& extramenuItemArray.length()>0){
                                    for (int k = 0; k < extramenuItemArray.length(); k++) {
                                        if (extramenuItemArray.length() != 0) {
                                            JSONObject allJsonObject3 = extramenuItemArray.getJSONObject(k);
                                            MenuItemExtraModel menuItemExtraModel = new MenuItemExtraModel();

                                            menuItemExtraModel.setExtra_item_name(allJsonObject3.optString("name"));
                                            menuItemExtraModel.setPrice(allJsonObject3.optString("price","0.0"));
                                            menuItemExtraModel.setQuantity(allJsonObject3.optString("quantity","1"));

                                            menuItemExtraModel.setQuantity(""+(Integer.parseInt(menuItemExtraModel.getQuantity())* Integer.parseInt(menuItemModel.getOrder_quantity())));

                                            double item_price=Integer.parseInt(menuItemExtraModel.getQuantity())* Double.parseDouble(menuItemExtraModel.getPrice());
                                            menuItemExtraModel.setPrice(""+Functions.Roundoff_decimal(item_price));

                                            menuItemExtraModel.setCurrency(currency_symbol);

                                            listChildData.add(menuItemExtraModel);

                                        }

                                    }

                                }
                                ListChild.add(listChildData);
                            }
                        }

                        listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, ListChild);
                        transparent_layer.setVisibility(View.GONE);
                        progressDialog.setVisibility(View.GONE);

                        // setting list adapter
                        customExpandableListView.setAdapter(listAdapter);
                        for(int l=0; l < listAdapter.getGroupCount(); l++)
                            if(ListChild.size()!=0) {
                                customExpandableListView.expandGroup(l);
                            }

                    }



                }catch (Exception e){
                    e.getMessage();
                }


            }
        });




    }



    public void customDialog(){

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dialog);

        // set the custom dialog components - text, image and button
        TextView title = (TextView)dialog.findViewById(R.id.title);


        final EditText reasonTxt = (EditText)dialog.findViewById(R.id.ed_message);
        Button done_btn = (Button)dialog.findViewById(R.id.done_btn);
        Button cancel_btn = (Button)dialog.findViewById(R.id.cancel_btn);

        if(FLAG_ACCEPT){

            if (preparation_time.isEmpty() || preparation_time==null) {
                reasonTxt.setHint("Type rider instructions (Optional)");
            }else {

                reasonTxt.setText("Restaurants food preparation time is "+preparation_time+" Min");
            }
        }
        else {
            reasonTxt.setHint("Type  Reject Reason here");
            title.setText("Reject Reason");
        }

        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                transparent_layer.setVisibility(View.VISIBLE);
                progressDialog.setVisibility(View.VISIBLE);

                if (!FLAG_ACCEPT) {
                    FLAG_ACCEPT = false;
                    if(reasonTxt.getText().toString().isEmpty()){
                        transparent_layer.setVisibility(View.GONE);
                        progressDialog.setVisibility(View.GONE);
                        Toast.makeText(getContext(),"Type rider instructions",Toast.LENGTH_LONG).show();

                    }
                    else {
                        DatabaseReference delete_order=FirebaseDatabase.getInstance().getReference();

                        delete_order.child("restaurant").child(user_id).child("CurrentOrders").child(serverkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                setAccDecStatus(reasonTxt.getText().toString());
                                dialog.dismiss();
                            }
                        });
                    }
                }
                else {
                    DatabaseReference add_to_onother=FirebaseDatabase.getInstance().getReference()
                            .child("restaurant").child(user_id);
                    add_to_onother.child("CurrentOrders").child(serverkey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            NewOrderModelClass modelClass=dataSnapshot.getValue(NewOrderModelClass.class);
                            HashMap<String,String> map=new HashMap<>();
                            map.put("deal",modelClass.getDeal());
                            map.put("order_id",modelClass.getOrder_id());
                            map.put("status",modelClass.getStatus());
                            map.put("type",modelClass.getType());

                            add_to_onother.child("PendingOrders").child(serverkey)
                                    .setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {


                                    add_to_onother.child("CurrentOrders").child(serverkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            setAccDecStatus(reasonTxt.getText().toString());
                                            dialog.dismiss(); }
                                    });

                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                FLAG_ACCEPT = false;
                ACCPT_DEC_FLAG = false;
            }
        });

        dialog.show();

    }

    public void setAccDecStatus(String text){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("order_id", order_id);
            jsonObject.put("key",serverkey);
            jsonObject.put("time", currentDateandTime);
            if (FLAG_ACCEPT) {
                jsonObject.put("status", "1");
                jsonObject.put("rejected_reason", "");
                jsonObject.put("accepted_reason", text);

            }
            else {
                jsonObject.put("status", "2");
                jsonObject.put("rejected_reason", text);
                jsonObject.put("accepted_reason", "");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(getContext(), Config.ACCEPT_DECLINE_STATUS, jsonObject, new Callback() {
            @Override
            public void Responce(String resp) {


                getAllOrderParser();

                if(FLAG_ACCEPT){
                    Print_order(true,AllConstants.cp1252_charset);

                }

                try {

                    JSONObject jsonObject1 = new JSONObject(resp);
                    int code = Integer.parseInt(jsonObject1.optString("code"));

                    if(code==200){
                        HANDLER_FLAG = true;
                        transparent_layer.setVisibility(View.GONE);
                        progressDialog.setVisibility(View.GONE);
                        scrolView.setVisibility(View.GONE);
                        order_detail_layout.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    transparent_layer.setVisibility(View.GONE);
                    progressDialog.setVisibility(View.GONE);
                }

            }
        });


    }



    public void Print_order(boolean from_accept_order,String charset){
        MainActivity.printer.setCharsetName(charset);
        Date date=new Date();
        TicketBuilder ticket_builder = new TicketBuilder(MainActivity.printer)
                .header("Foodies")
                .text("Date: " + DateFormat.format("dd.MM.yyyy", date).toString())
                .text("Time: " + DateFormat.format("HH:mm", date).toString())
                .text("Order_ID: "+order_id)
                .divider();

        for (int i = 0; i < listDataHeader.size(); i++) {

            double price;
            MenuItemModel menuItemModel=listDataHeader.get(i);
            ticket_builder.text(menuItemModel.getOrder_quantity()+"x["+menuItemModel.getItem_name().replaceAll("&amp;", "&")+"] ("+ menuItemModel.getCurrency()+""+menuItemModel.getItem_price()+")", TicketBuilder.TextAlignment.LEFT);
            price=Double.parseDouble(menuItemModel.getItem_price());
            ArrayList<MenuItemExtraModel> child_list=ListChild.get(i);
            for (int j = 0; j < child_list.size(); j++){
                MenuItemExtraModel menuItemExtraModel=child_list.get(j);
                ticket_builder.text(menuItemExtraModel.getQuantity()+"x"+menuItemExtraModel.getExtra_item_name().replaceAll("&amp;", "&")+" "+menuItemModel.getCurrency()+""+menuItemExtraModel.getPrice());
                price=price+Double.parseDouble(menuItemExtraModel.getPrice());
            }

            ticket_builder.right(menuItemModel.getCurrency()+" "+price);

            ticket_builder.text("     ");
        }
        ticket_builder.menuLine("Tip für Rider",total_tip_tv.getText().toString());
        ticket_builder.menuLine("Delivery Fee",total_delivery_fee_tv.getText().toString());
        ticket_builder.menuLine("Tax",total_tex_tv.getText().toString());

        ticket_builder.divider();
        ticket_builder.center("Customer Details");
        ticket_builder.text("     ");
        ticket_builder.text(order_user_name_tv.getText().toString());
        ticket_builder.text(order_user_address_tv.getText().toString());
        ticket_builder.text(order_user_number_tv.getText().toString());
        ticket_builder.text("     ");
        ticket_builder.text(inst_tv.getText().toString());
        ticket_builder.divider();

        ticket_builder.right(payment_method_tv.getText().toString());
        ticket_builder.text("     ");
        ticket_builder.menuLine("Total", total_amount_tv.getText().toString())
                .divider()
                .text("THANK YOU", TicketBuilder.TextAlignment.CENTER)
                .feedLine(2)
                .isCyrillic(true);

        Ticket ticket=ticket_builder.build();

        Log.d(AllConstants.tag,ticket.getFiscalPreview());
        Log.d(AllConstants.tag,ticket.getTicketPreview());

        if(from_accept_order) {

            if(MainActivity.printer!=null && MainActivity.printer.isConnected()) {

                MainActivity.printer.send(ticket);
            }

        }
        else
            open_preview(ticket);

    }


    public void open_preview(Ticket ticket) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_print_preview_dialog);

        TicketPreview ticketPreview=dialog.findViewById(R.id.ticket_preview);
        ticketPreview.setTicket(ticket);

        dialog.findViewById(R.id.preview_print_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.printer!=null && MainActivity.printer.isConnected()) {
                    dialog.cancel();
                    MainActivity.printer.send(ticket);
                }
                else {
                    Toast.makeText(getContext(), "Printer not connected", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        layoutParams.copyFrom(dialog.getWindow().getAttributes());

        int dialogWindowWidth = (int) (displayWidth * 0.9f);
        layoutParams.width = dialogWindowWidth;
        dialog.getWindow().setAttributes(layoutParams);
    }





    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh_icon, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mIsReceiverRegistered) {
            if (mReceiver == null)
                mReceiver = new NewOrderBroadCast();
            getActivity().registerReceiver(mReceiver, new IntentFilter("newOrder"));
            mIsReceiverRegistered = true;
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        Stop_ring_tone();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mIsReceiverRegistered) {
            getActivity().unregisterReceiver(mReceiver);
            mReceiver = null;
            mIsReceiverRegistered = false;
        }
    }


    Ringtone ringtoneSound;
    private class NewOrderBroadCast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            if(ringtoneSound!=null && ringtoneSound.isPlaying()) {

            }else {
                ringtoneSound = RingtoneManager.getRingtone(context, ringtoneUri);
                ringtoneSound.play();

                Click_on_order(intent.getStringExtra("order_id"));
            }

          }
    }


    public void Stop_ring_tone(){
        if(ringtoneSound!=null && ringtoneSound.isPlaying()){
            ringtoneSound.stop();
        }

        ringtoneSound=null;
    }




    public class FireBaseFilterAdapter extends FirebaseRecyclerAdapterCustom<NewOrderModelClass, FireBaseFilterAdapter.Orderviewholder> {


        private RecycleItemClick recycleItemClick;
        private static final String TAG = "PeopleListAdapter";

        public FireBaseFilterAdapter(FirebaseRecyclerOptions<NewOrderModelClass> options) {

            super(options, true);
        }



        public void setRecycleItemClick(RecycleItemClick recycleItemClick) {
            this.recycleItemClick = recycleItemClick;
        }

        @Override
        public Orderviewholder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_item_orders, parent, false);
            return new Orderviewholder(view);
        }

        @Override
        protected void onBindViewHolder(Orderviewholder holder, int position, NewOrderModelClass model) {

            if(model.getType().equalsIgnoreCase("0")){
                holder.order_id.setText("Order# "+model.getOrder_id()+" (Pickup)");
            }
            else {
                holder.order_id.setText("Order# "+model.getOrder_id()+" (Delivery)");
            }
            Log.d("order_type",model.getType()+" "+model.getOrder_id());
            if(model.getStatus().equals("0")){
                holder.deal_image.setVisibility(View.GONE);
                holder.status_text.setVisibility(View.VISIBLE);
                holder.status_text.setText("New");
            }else {
                if(model.getDeal().equals("1")){
                    holder.deal_image.setVisibility(View.VISIBLE);
                    holder.status_text.setVisibility(View.GONE);
                    holder.deal_image.setImageDrawable(getContext().getResources().getDrawable(R.drawable.deal));
                }
                else {
                    holder.status_text.setVisibility(View.GONE);
                }
            }
            holder.main_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Click_on_order(model.getOrder_id());

                }
            });


        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            transparent_layer.setVisibility(View.GONE);
            progressDialog.setVisibility(View.GONE);

        }

        @Override
        protected void onChildUpdate(NewOrderModelClass model,
                                     ChangeEventType type,
                                     DataSnapshot snapshot,
                                     int newIndex,
                                     int oldIndex) {
            model.setOrder_id(String.valueOf(snapshot.child("order_id").getValue()));
            model.setDeal(String.valueOf(snapshot.child("deal").getValue()));
            model.setStatus(String.valueOf(snapshot.child("status").getValue()));
            model.setType(String.valueOf(snapshot.child("type").getValue()));
            super.onChildUpdate(model, type, snapshot, newIndex, oldIndex);
        }

        @Override
        protected boolean filterCondition(NewOrderModelClass model, String filterPattern) {
            return model.getOrder_id().toLowerCase().contains(filterPattern);
        }


        class Orderviewholder extends RecyclerView.ViewHolder{
            TextView order_id,status_text;
            ImageView deal_image;
            View view;
            RelativeLayout main_layout;
            public Orderviewholder(View itemView) {
                super(itemView);
                view=itemView;
                this.order_id=(TextView) view.findViewById(R.id.order_id);
                this.deal_image=view.findViewById(R.id.deal_image);
                this.status_text=(TextView) view.findViewById(R.id.text_view);
                this.main_layout=view.findViewById(R.id.main_layout);
            }

        }

    }

    public void Click_on_order(String order_id){
        ACCEPTED_ORDER = true;
        transparent_layer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);
        ChangeStatus(order_id);
        fRadapter.notifyDataSetChanged();
        SharedPreferences.Editor editor = sPre.edit();
        editor.putString(PreferenceClass.ORDER_ID,order_id);
        editor.putBoolean("Current_order",true);
        editor.commit();
        initViewMenuOrderExtraItem(view,false);
    }

    public interface RecycleItemClick {
        void onItemClick(String userId, NewOrderModelClass user, int position);
    }


    public class FireBaseFilterAdapterAccepted extends FirebaseRecyclerAdapterCustom<NewOrderModelClass, FireBaseFilterAdapterAccepted.Orderviewholder> {


        private RecycleItemClick recycleItemClick;
        private static final String TAG = "PeopleListAdapter";

        public FireBaseFilterAdapterAccepted(FirebaseRecyclerOptions<NewOrderModelClass> options) {
            super(options, true);
        }



        public void setRecycleItemClick(RecycleItemClick recycleItemClick) {
            this.recycleItemClick = recycleItemClick;
        }

        @Override
        public Orderviewholder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_item_orders, parent, false);
            return new Orderviewholder(view);
        }

        @Override
        protected void onBindViewHolder(Orderviewholder holder, int position, NewOrderModelClass model) {

            if(model.getStatus().equals("0")){
                holder.deal_image.setVisibility(View.GONE);
                holder.status_text.setVisibility(View.GONE);
            }else {
                if(model.getDeal().equals("1")){
                    holder.deal_image.setVisibility(View.VISIBLE);
                    holder.status_text.setVisibility(View.GONE);
                    holder.deal_image.setImageDrawable(getContext().getResources().getDrawable(R.drawable.deal));
                }
            }

            if(model.getType().equalsIgnoreCase("0")){
                holder.order_id.setText("Order # "+model.getOrder_id()+" (Pickup)");
            }
            else {
                holder.order_id.setText("Order # "+model.getOrder_id());
            }

            holder.main_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    transparent_layer.setVisibility(View.VISIBLE);
                    progressDialog.setVisibility(View.VISIBLE);
                    // ChangeStatus(model.getOrder_id());
                    SharedPreferences.Editor editor = sPre.edit();
                    editor.putString(PreferenceClass.ORDER_ID,model.getOrder_id());
                    editor.putBoolean("Current_order",true);
                    editor.commit();
                    initViewMenuOrderExtraItem(view,false);


                }
            });


        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            transparent_layer.setVisibility(View.GONE);
            progressDialog.setVisibility(View.GONE);

        }

        @Override
        protected void onChildUpdate(NewOrderModelClass model,
                                     ChangeEventType type,
                                     DataSnapshot snapshot,
                                     int newIndex,
                                     int oldIndex) {
            model.setOrder_id(String.valueOf(snapshot.child("order_id").getValue()));
            model.setDeal(String.valueOf(snapshot.child("deal").getValue()));
            model.setStatus(String.valueOf(snapshot.child("status").getValue()));
            model.setType(String.valueOf(snapshot.child("type").getValue()));
            super.onChildUpdate(model, type, snapshot, newIndex, oldIndex);
        }

        @Override
        protected boolean filterCondition(NewOrderModelClass model, String filterPattern) {
            return model.getOrder_id().toLowerCase().contains(filterPattern);
        }


        class Orderviewholder extends RecyclerView.ViewHolder{
            TextView order_id,status_text;
            ImageView deal_image;
            View view;
            RelativeLayout main_layout;
            public Orderviewholder(View itemView) {
                super(itemView);
                view=itemView;
                this.order_id=(TextView) view.findViewById(R.id.order_id);
                this.deal_image=view.findViewById(R.id.deal_image);
                this.status_text=(TextView) view.findViewById(R.id.text_view);
                this.main_layout=view.findViewById(R.id.main_layout);
            }

        }

    }


}
