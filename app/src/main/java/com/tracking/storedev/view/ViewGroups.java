package com.tracking.storedev.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;

import com.tracking.storedev.R;

/**
 * Created by ZASS on 4/14/2018.
 */

public  class ViewGroups {
    private static final ViewGroups ourInstance = new ViewGroups();

    public static ViewGroups getInstance() {
        return ourInstance;
    }

    public Bitmap loadBitmapFromView(View v) {
        if (v.getMeasuredHeight() <= 0) {
            int width = 500; // v.getMeasuredWidth();
            int height = 500;  //v.getMeasuredHeight();

            v.measure(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.layout(0, 0, width, height);
            v.draw(c);
            return b;
        }
        return null;
    }

    public void showSnackMessage(View view, String msg, int duration) {
        Snackbar snackbar = Snackbar.make(view, "" + msg,
                Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.color_highlight));
        snackbar.setDuration(duration);
        snackbar.show();
    }

    public ProgressDialog progressDialog(Context context, boolean cancelable) {
        ProgressDialog progressDialog = ProgressDialog.show(context, null, null, false, true );
        progressDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        progressDialog.setContentView( R.layout.progress_dialog );
        progressDialog.setCancelable(cancelable);
        return progressDialog;
    }
}
