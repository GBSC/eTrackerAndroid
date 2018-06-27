package com.tracking.storedev.web;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.tracking.storedev.App;
import com.tracking.storedev.R;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;

/**
 * Created by Irfan Ali on 3/1/2018.
 */

public class HttpCaller {

    public static HttpCaller instance;
    public static Context context;
    private static RetryPolicy retryPolicy;

    public static ProgressDialog progressDialog;

    public static HttpCaller getInstance() {
        if (instance == null) {
            instance = new HttpCaller();
        }
        return instance;
    }

    public boolean init(Context context1) {
        context = context1;
        retryPolicy = new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        return true;
    }

    public void setProgressDialog(Context context) {
        progressDialog = ProgressDialog.show(context, null, null, false, true );
        progressDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        progressDialog.setContentView( R.layout.progress_dialog );
        progressDialog.setCancelable(true);
    }

    public void jsonObjectPOSTRequest(final boolean isShowProgress, final Context context, final String webURL, JSONObject jsonbody, final Response.Listener successLisetenr, final Response.ErrorListener errorLisetenr) {
        if (isShowProgress) {
            setProgressDialog(context);
            progressDialog.show();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                webURL,
                jsonbody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        if (isShowProgress) {
                            progressDialog.dismiss();
                        }
                        successLisetenr.onResponse(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError volleyError) {
                        if (isShowProgress) {
                            progressDialog.dismiss();
                        }
                        errorLisetenr.onErrorResponse(volleyError);
                    }
                });
        // Setting retry policy
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        App.requestQueue.add(jsonObjectRequest);
    }

    public void requestArrayGETToServer(final boolean isShowProgress, final Context context, final String webURL, final Response.Listener successLisetenr, final Response.ErrorListener errorLisetenr) {
        if (isShowProgress) {
            setProgressDialog(context);
            progressDialog.show();
        }
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET,
                webURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonObject) {
                        if (isShowProgress) {
                            progressDialog.dismiss();
                        }
                        successLisetenr.onResponse(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (isShowProgress) {
                            progressDialog.dismiss();
                        }
                        errorLisetenr.onErrorResponse(volleyError);
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        App.requestQueue.add(jsonObjectRequest);
    }

    public void request1GETToServer(final boolean isShowProgress, final Context context, final String webURL, final Response.Listener<JSONObject> successLisetenr, final Response.ErrorListener errorLisetenr) {
        if (isShowProgress) {
            setProgressDialog(context);
            progressDialog.show();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                webURL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        if (isShowProgress) {
                            progressDialog.dismiss();
                        }
                        successLisetenr.onResponse(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (isShowProgress) {
                            progressDialog.dismiss();
                        }
                        errorLisetenr.onErrorResponse(volleyError);
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        App.requestQueue.add(jsonObjectRequest);
    }

    public void uploadImage(String BASE_URL, String imagePath, final Response.Listener successLisetenr, final Response.ErrorListener errorLisetenr){
        AndroidNetworking.upload(BASE_URL)
                .addMultipartFile("file",new File(imagePath))
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        successLisetenr.onResponse(response);
                    }
                    @Override
                    public void onError(ANError error) {
                        errorLisetenr.onErrorResponse(null);
                    }
                });
    }

    public void requestToServerForLogin(JSONObject jsonbody, final Response.Listener successLisetenr, final Response.ErrorListener errorLisetenr) {
        String loginURL = WebURL.login;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                loginURL,
                jsonbody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        successLisetenr.onResponse(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError volleyError) {
                        errorLisetenr.onErrorResponse(volleyError);
                    }
                });
        // Setting retry policy
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        App.requestQueue.add(jsonObjectRequest);
    }

}