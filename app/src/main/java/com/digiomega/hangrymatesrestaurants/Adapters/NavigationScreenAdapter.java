package com.digiomega.hangrymatesrestaurants.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.digiomega.hangrymatesrestaurants.R;


/**
 * Created by Dinosoftlabs on 10/18/2019.
 */
public class NavigationScreenAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final Integer[] imgid;

    public NavigationScreenAdapter(Activity context, String[] itemname, Integer[] imgid) {
        super(context, R.layout.row_item_navigation_item, itemname);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.itemname = itemname;
        this.imgid = imgid;
    }


    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.row_item_navigation_item, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.textViewName);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageViewIcon);
        //  TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);

        txtTitle.setText(itemname[position]);
        imageView.setImageResource(imgid[position]);
        //  extratxt.setText("Description "+itemname[position]);
        return rowView;

    };
}
