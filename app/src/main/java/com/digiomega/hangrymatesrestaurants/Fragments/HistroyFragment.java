package com.digiomega.hangrymatesrestaurants.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.percentlayout.widget.PercentRelativeLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.digiomega.hangrymatesrestaurants.Adapters.ExpandableListAdapter;
import com.digiomega.hangrymatesrestaurants.Adapters.OrderAdapter;
import com.digiomega.hangrymatesrestaurants.AllConstant.ApiRequest;
import com.digiomega.hangrymatesrestaurants.AllConstant.Callback;
import com.digiomega.hangrymatesrestaurants.AllConstant.Config;
import com.digiomega.hangrymatesrestaurants.AllConstant.PreferenceClass;
import com.digiomega.hangrymatesrestaurants.Models.MenuItemExtraModel;
import com.digiomega.hangrymatesrestaurants.Models.MenuItemModel;
import com.digiomega.hangrymatesrestaurants.Models.OrderModelClass;

import com.digiomega.hangrymatesrestaurants.R;
import com.digiomega.hangrymatesrestaurants.Utils.CustomExpandableListView;
import com.digiomega.hangrymatesrestaurants.Utils.CustomRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by Dinosoftlabs on 10/18/2019.
 */

public class HistroyFragment extends Fragment {

    SharedPreferences sPre;

    CustomRecyclerView order_history_recyclerview;

    RecyclerView.LayoutManager recyclerViewlayoutManager;
    OrderAdapter recyclerViewadapter;

    LinearLayout main_layout;
    ProgressBar orderHistoryProgress;

    ArrayList<OrderModelClass> orderArrayList;
    PercentRelativeLayout no_job_div;
    ImageView filter_search;


    /// Order Detail Views

    TextView inst_tv,hotel_name_tv,hotel_phone_number_tv,hotel_add_tv,total_amount_tv,payment_method_tv,
            order_user_name_tv,order_user_address_tv,order_user_number_tv,total_delivery_fee_tv,tax_tv,total_tex_tv;
    ExpandableListAdapter listAdapter;
    CustomExpandableListView customExpandableListView;
    ArrayList<MenuItemModel> listDataHeader;
    ArrayList<MenuItemExtraModel> listChildData;
    private ArrayList<ArrayList<MenuItemExtraModel>> ListChild;

    String order_id;

    private RelativeLayout order_detail_layout,tax_div;

    ScrollView scrolView;
    LinearLayout hotel_btn_div;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View view = inflater.inflate(R.layout.history_frag, container, false);
        initUI(view);

        return view;
    }

    public void initUI(View v){

        order_history_recyclerview = v.findViewById(R.id.order_history_recyclerview);
        order_history_recyclerview.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(getContext());
        order_history_recyclerview.setLayoutManager(recyclerViewlayoutManager);

        orderHistoryProgress = v.findViewById(R.id.orderHistoryProgress);
        main_layout = v.findViewById(R.id.main_layout);
        no_job_div = v.findViewById(R.id.no_job_div);
        tax_div = v.findViewById(R.id.tax_div);
        sPre = getContext().getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);
        getAllOrderParser();

    }


    public void initViewMenuOrderExtraItem(View v){

        order_id = sPre.getString(PreferenceClass.ORDER_ID,"");

        customExpandableListView = (CustomExpandableListView) v.findViewById(R.id.custon_list_order_items);
        customExpandableListView .setExpanded(true);
        customExpandableListView.setGroupIndicator(null);

        customExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return true; // This way the expander cannot be collapsed
            }
        });



        String order_inst = sPre.getString(PreferenceClass.ORDER_INS,"");

        hotel_btn_div = v.findViewById(R.id.hotel_btn_div);

        scrolView = v.findViewById(R.id.scrolView);

        hotel_name_tv = v.findViewById(R.id.order_hotel_name);
        hotel_add_tv = v.findViewById(R.id.order_hotel_address);
        hotel_phone_number_tv = v.findViewById(R.id.order_hotel_number);
        payment_method_tv = v.findViewById(R.id.payment_method_tv);
        total_amount_tv = v.findViewById(R.id.total_amount_tv);
        inst_tv = v.findViewById(R.id.inst_tv);
        inst_tv.setText(order_inst);
        order_user_name_tv = v.findViewById(R.id.order_user_name_tv);
        order_user_address_tv = v.findViewById(R.id.order_user_address_tv);
        order_user_number_tv = v.findViewById(R.id.order_user_number_tv);
        total_delivery_fee_tv = v.findViewById(R.id.total_delivery_fee_tv);
        tax_tv = v.findViewById(R.id.tax_tv);
        total_tex_tv = v.findViewById(R.id.total_tex_tv);

        order_detail_layout = v.findViewById(R.id.order_detail_layout);
        hotel_btn_div.setVisibility(View.GONE);


        getOrderDetailItems();

    }

    private void getAllOrderParser(){

        orderHistoryProgress.setVisibility(View.VISIBLE);
        orderArrayList = new ArrayList<>();
        String user_id = sPre.getString(PreferenceClass.pre_user_id,"");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss");
        String currentDateandTime = sdf.format(new Date());

        RequestQueue queue = Volley.newRequestQueue(getContext());

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("user_id",user_id);
            jsonObject.put("datetime",currentDateandTime);

            Log.e("Obj",jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(getContext(), Config.SHOW_REST_COMPLETE_ORDER, jsonObject, new Callback() {
            @Override
            public void Responce(String resp) {

                try {
                    JSONObject jsonResponse = new JSONObject(resp);

                    int code_id  = Integer.parseInt(jsonResponse.optString("code"));

                    if(code_id == 200) {

                        JSONObject json = new JSONObject(jsonResponse.toString());
                        JSONArray jsonarray = json.getJSONArray("msg");

                        Log.d("Length",String.valueOf(jsonarray.length()));

                        for (int i = 0; i < jsonarray.length(); i++) {

                            OrderModelClass orderModelClass = new OrderModelClass();
                            JSONObject json1 = jsonarray.getJSONObject(i);

                            JSONObject jsonObjOrder = json1.getJSONObject("Order");
                            JSONObject jsonObjCurrency = jsonObjOrder.getJSONObject("Currency");
                            JSONArray jsonarrayOrder = jsonObjOrder.getJSONArray("OrderMenuItem");

                            JSONObject jsonObjectMenu = jsonarrayOrder.getJSONObject(0);
                            JSONObject jsonObjectExtraMenu = null;
                            if(jsonObjectMenu.getJSONArray("OrderMenuExtraItem")!=null && jsonObjectMenu.getJSONArray("OrderMenuExtraItem").length()>0) {
                                JSONArray jsonarrayExtraOrder = jsonObjectMenu.getJSONArray("OrderMenuExtraItem");
                                jsonObjectExtraMenu = jsonarrayExtraOrder.getJSONObject(0);
                                orderModelClass.setOrder_extra_item_name(jsonObjectExtraMenu.optString("name"));
                            }


                            orderModelClass.setCurrency_symbol(jsonObjCurrency.optString("symbol"));
                            orderModelClass.setOrder_created(jsonObjOrder.optString("created"));

                            orderModelClass.setOrder_id(jsonObjOrder.optString("id"));
                            orderModelClass.setOrder_menu_id(jsonObjectMenu.optString("id"));
                            orderModelClass.setOrder_name(jsonObjectMenu.optString("name"));
                            orderModelClass.setOrder_price(jsonObjOrder.optString("price"));
                            orderModelClass.setInstructions(jsonObjOrder.optString("instructions"));
                            orderModelClass.setRestaurant_name(jsonObjOrder.optString("name"));
                            orderModelClass.setOrder_quantity(jsonObjOrder.optString("quantity"));

                            orderArrayList.add(orderModelClass);
                            // Toast.makeText(getContext(),orderModelClassArrayList.toString(),Toast.LENGTH_SHORT).show();

                        }

                        if (orderArrayList!=null) {

                            if(orderArrayList.size()>0){
                                no_job_div.setVisibility(View.GONE);
                            }
                            else if(orderArrayList.size()==0) {
                                no_job_div.setVisibility(View.VISIBLE);
                            }

                            orderHistoryProgress.setVisibility(View.GONE);
                            recyclerViewadapter = new OrderAdapter(orderArrayList, getActivity());
                            order_history_recyclerview.setAdapter(recyclerViewadapter);
                            recyclerViewadapter.notifyDataSetChanged();

                        }
                        recyclerViewadapter.setOnItemClickListner(new OrderAdapter.OnItemClickListner() {
                            @Override
                            public void OnItemClicked(View view, int position) {

                                SharedPreferences.Editor editor = sPre.edit();
                                editor.putString(PreferenceClass.ORDER_HEADER,orderArrayList.get(position).getOrder_name());
                                editor.putString(PreferenceClass.ORDER_ID,orderArrayList.get(position).getOrder_id());
                                editor.putString(PreferenceClass.ORDER_INS,orderArrayList.get(position).getInstructions());
                                editor.commit();

                                reFreshList(orderArrayList);
                                orderArrayList.get(position).setSelected(true);
                                recyclerViewadapter.notifyDataSetChanged();

                                initViewMenuOrderExtraItem(getView());
                                scrolView.setVisibility(View.VISIBLE);
                                order_detail_layout.setVisibility(View.VISIBLE);

                            }
                        });

                    }else{
                        no_job_div.setVisibility(View.VISIBLE);
                        orderHistoryProgress.setVisibility(View.GONE);
                        JSONObject json = new JSONObject(jsonResponse.toString());
                        Toast.makeText(getContext(),json.optString("msg"), Toast.LENGTH_SHORT).show();
                    }

                    //JSONArray jsonMainNode = jsonResponse.optJSONArray("msg");                            }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });



    }


    public void getOrderDetailItems(){
        orderHistoryProgress.setVisibility(View.VISIBLE);
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
                            JSONObject taxObj = restaurantJsonObject.getJSONObject("Tax");
                            JSONObject restaurantCurrencuObj = restaurantJsonObject.getJSONObject("Currency");
                            String currency_symbol= restaurantCurrencuObj.optString("symbol");

                            String first_name = userInfoObj.optString("first_name");
                            String last_name = userInfoObj.optString("last_name");
                            order_user_name_tv.setText(first_name+" "+last_name);
                            order_user_number_tv.setText(userInfoObj.optString("phone"));
                            String street_user = userAddressObj.optString("street");
                            String zip_user = userAddressObj.optString("zip");
                            String city_user = userAddressObj.optString("city");
                            String state_user = userAddressObj.optString("state");
                            String country_user = userAddressObj.optString("country");

                            // order_user_address_tv.setText(zip_user+", "+city_user+", "+state_user+", "+country_user);

                            order_user_address_tv.setText(street_user + ", " + city_user);

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
                            String zip = restaurantAddress.optString("zip");
                            String city = restaurantAddress.optString("city");
                            String state = restaurantAddress.optString("state");
                            String country = restaurantAddress.optString("country");
/*
                            hotel_add_tv.setText(zip+", "+city+", "+state+", "+country);
                            if(HJobsFragment.FLAG_HJOBS) {*/
                            hotel_add_tv.setText(street + ", " + city);

                            //// Total Payment
                            String tax = taxObj.optString("tax");
                            String sub_total = orderJsonObject.optString("sub_total");
                            String delivery_fee = orderJsonObject.optString("delivery_fee");
                            total_delivery_fee_tv.setText(currency_symbol+delivery_fee);
                            tax_tv.setText("("+tax+"%)");
                            Double getTotalTax = Double.parseDouble(tax)*Double.parseDouble(sub_total)/100;

                            if (String.valueOf(getTotalTax).equals("0.00") || String.valueOf(getTotalTax).equals("0.0") ){

                                tax_div.setVisibility(View.GONE);
                            }else {
                                tax_div.setVisibility(View.VISIBLE);
                            }

                            total_tex_tv.setText(currency_symbol+String.valueOf(getTotalTax));

                            //// End

                            JSONArray menuItemArray = allJsonObject.getJSONArray("OrderMenuItem");

                            for (int j=0;j<menuItemArray.length();j++){

                                JSONObject alljsonJsonObject2 = menuItemArray.getJSONObject(j);
                                MenuItemModel menuItemModel = new MenuItemModel();
                                menuItemModel.setItem_name(alljsonJsonObject2.optString("name"));
                                menuItemModel.setItem_price(currency_symbol+alljsonJsonObject2.optString("price"));
                                menuItemModel.setId(alljsonJsonObject2.optString("id"));
                                menuItemModel.setOrder_id(alljsonJsonObject2.optString("order_id"));
                                menuItemModel.setOrder_quantity(alljsonJsonObject2.optString("quantity"));

                                listDataHeader.add(menuItemModel);

                                listChildData = new ArrayList<>();

                                JSONArray extramenuItemArray = alljsonJsonObject2.getJSONArray("OrderMenuExtraItem");

                                for (int k = 0; k < extramenuItemArray.length(); k++) {
                                    if (extramenuItemArray.length()!=0) {
                                        JSONObject allJsonObject3 = extramenuItemArray.getJSONObject(k);
                                        MenuItemExtraModel menuItemExtraModel = new MenuItemExtraModel();

                                        menuItemExtraModel.setExtra_item_name(allJsonObject3.optString("name"));
                                        menuItemExtraModel.setPrice(allJsonObject3.optString("price"));
                                        menuItemExtraModel.setQuantity(allJsonObject3.optString("quantity"));
                                        menuItemExtraModel.setCurrency(currency_symbol);

                                        listChildData.add(menuItemExtraModel);
                                        ListChild.add(listChildData);

                                    }
                                }

                            }

                            listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, ListChild);
                            orderHistoryProgress.setVisibility(View.GONE);
                            // setting list adapter
                            customExpandableListView.setAdapter(listAdapter);
                            for(int l=0; l < listAdapter.getGroupCount(); l++)
                                if(ListChild.size()!=0) {
                                    customExpandableListView.expandGroup(l);
                                }

                        }

                    }

                }catch (Exception e){
                    e.getMessage();
                }

            }
        });


    }

    public void reFreshList(ArrayList<OrderModelClass> arrayList){

        for(int i=0; i<arrayList.size(); i++)

        {
            arrayList.get(i).setSelected(false);

        }

    }

}
