package com.tracking.storedev.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.tracking.storedev.R;

/**
 * Created by ZASS on 4/28/2018.
 */

public class TProgressDialog {

    public ProgressDialog progressDialog;

    public ProgressDialog createTProgressDialog(Context context){
        progressDialog = ProgressDialog.show(context, null, null, false, true );
        progressDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        progressDialog.setContentView( R.layout.progress_dialog );
        progressDialog.setCancelable(true);

        return progressDialog;
    }
}
