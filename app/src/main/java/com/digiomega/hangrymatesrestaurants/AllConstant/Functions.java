package com.digiomega.hangrymatesrestaurants.AllConstant;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Window;


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

    public static Dialog dialog;
    public static void Show_loader(Context context, boolean outside_touch, boolean cancleable) {

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_progress_dialog_layout);


        if(!outside_touch)
            dialog.setCanceledOnTouchOutside(false);

        if(!cancleable)
            dialog.setCancelable(false);

        dialog.show();

    }

    public static void cancel_loader(){
        if(dialog!=null){
            dialog.cancel();
        }
    }
}
