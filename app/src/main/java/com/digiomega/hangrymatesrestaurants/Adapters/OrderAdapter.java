package com.digiomega.hangrymatesrestaurants.Adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.digiomega.hangrymatesrestaurants.Models.OrderModelClass;
import com.digiomega.hangrymatesrestaurants.R;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by Dinosoftlabs on 10/18/2019.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder>  {

    ArrayList<OrderModelClass> getDataAdapter;
    Context context;
    OrderAdapter.OnItemClickListner onItemClickListner;

    public OrderAdapter(ArrayList<OrderModelClass> getDataAdapter, Context context){
        super();
        this.getDataAdapter = getDataAdapter;
        this.context = context;

    }

    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_order, parent, false);

        OrderAdapter.ViewHolder viewHolder = new OrderAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        OrderModelClass getDataAdapter1 =  getDataAdapter.get(position);



        holder.order_number.setText("#"+getDataAdapter1.getOrder_id());
      //  String time = date_time.substring(11,19);
        String date_time = getDataAdapter1.getOrder_created();
        //  String date = date_time.substring(0,10);

        StringTokenizer tk = new StringTokenizer(date_time);
        String date = tk.nextToken();
        String time = tk.nextToken();

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm a");
        Date dt;
        try {
            dt = sdf.parse(time);
            System.out.println("Time Display: " + sdfs.format(dt));
            String finalTime = sdfs.format(dt);
            holder.order_time.setText(finalTime);
            // <-- I got result here
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String symbol = getDataAdapter1.getCurrency_symbol();

        holder.menu_item_name.setText(getDataAdapter1.getOrder_name());
        holder.order_date.setText(date);

        holder.order_price.setText(symbol+ getDataAdapter1.getOrder_price());

        holder.order_item_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    onItemClickListner.OnItemClicked(v,position);

                }catch (NullPointerException e){
                    e.printStackTrace();
                }

            }
        });

        if (getDataAdapter1.isSelected()) {
            holder.order_item_main.setBackgroundColor(context.getResources().getColor(R.color.colorBG));
        } else {
            holder.order_item_main.setBackgroundColor(Color.WHITE);
        }


    }


    @Override
    public int getItemCount() {
        return getDataAdapter.size() ;
    }



    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView menu_item_name,retaurant_name,order_quantity, order_date, order_time, order_price,order_number;

        RelativeLayout order_item_main;


        public ViewHolder(View itemView) {

            super(itemView);
            menu_item_name = (TextView)itemView.findViewById(R.id.deal_name);
          //  retaurant_name = (TextView)itemView.findViewById(R.id.hotal_name_tv);
         //   order_quantity = (TextView) itemView.findViewById(R.id.order_quantity) ;

            order_date = (TextView)itemView.findViewById(R.id.date_deal_tv);
            order_time = (TextView)itemView.findViewById(R.id.time_deal_tv);
            order_price = (TextView) itemView.findViewById(R.id.price_deal_tv) ;
            order_number = (TextView)itemView.findViewById(R.id.order_number);

            order_item_main = (RelativeLayout) itemView.findViewById(R.id.order_item_main_div);



        }
    }

    public interface OnItemClickListner {
        void OnItemClicked(View view, int position);
    }

    public void setOnItemClickListner(OnItemClickListner onItemClickListner) {
        this.onItemClickListner = onItemClickListner;
    }

}
