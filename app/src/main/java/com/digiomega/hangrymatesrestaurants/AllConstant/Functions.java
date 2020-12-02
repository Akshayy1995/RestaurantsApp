package com.digiomega.hangrymatesrestaurants.AllConstant;

import android.content.Context;
import android.content.DialogInterface;


import com.digiomega.hangrymatesrestaurants.R;

import java.text.DecimalFormat;

public class Functions {

    public static void Show_Options(Context context, CharSequence[] options, final  Callback callback) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.AlertDialogCustom);

        builder.setTitle(null);

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                callback.Responce(""+options[item]);

            }

        });

        builder.show();

    }

    public static Double Roundoff_decimal(Double value){
        return  Double.valueOf(new DecimalFormat("##.##").format(value));

    }

}
