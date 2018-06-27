package com.tracking.storedev.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.tracking.storedev.R;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by ZASS on 3/22/2018.
 */

public class StoreVisitsDialog extends Dialog {



    private Context context;
    private Button addBtn;
    private ImageView closeBtn;

    private CheckBox checkBoxMa;
    private CheckBox checkBoxTu;
    private CheckBox checkBoxWe;
    private CheckBox checkBoxTh;
    private CheckBox checkBoxFr;
    private CheckBox checkBoxSa;
    private CheckBox checkBoxSu;

    private OrderCallback orderCallback;

    private JSONArray jsonArray = null;

    public StoreVisitsDialog(Context context, OrderCallback orderCallback) {
        super(context);
        this.context = context;
        this.orderCallback = orderCallback;
        jsonArray = new JSONArray();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.frequency_dialog);

        closeBtn =(ImageView)findViewById(R.id.closeBtn);
        addBtn =(Button)findViewById(R.id.addBtn);

        checkBoxMa = (CheckBox)findViewById(R.id.checkBoxMonday);
        checkBoxTu = (CheckBox)findViewById(R.id.checkBoxTuesday);
        checkBoxWe = (CheckBox)findViewById(R.id.checkBoxWednesday);
        checkBoxTh = (CheckBox)findViewById(R.id.checkBoxThursday);
        checkBoxFr = (CheckBox)findViewById(R.id.checkFriday);
        checkBoxSa = (CheckBox)findViewById(R.id.checkSaturday);
        checkBoxSu = (CheckBox)findViewById(R.id.checkSunday);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = null;
                try{
                    if(checkBoxMa.isChecked()){
                        jsonObject = new JSONObject();
                        jsonObject.put("day", 2);
                        jsonArray.put(jsonObject);
                    }
                    if(checkBoxTu.isChecked()){
                        jsonObject = new JSONObject();
                        jsonObject.put("day", 3);
                        jsonArray.put(jsonObject);
                    }
                    if(checkBoxWe.isChecked()){
                        jsonObject = new JSONObject();
                        jsonObject.put("day", 4);
                        jsonArray.put(jsonObject);
                    }
                    if(checkBoxTh.isChecked()){
                        jsonObject = new JSONObject();
                        jsonObject.put("day", 5);
                        jsonArray.put(jsonObject);
                    }
                    if(checkBoxFr.isChecked()){
                        jsonObject = new JSONObject();
                        jsonObject.put("day", 6);
                        jsonArray.put(jsonObject);
                    }
                    if(checkBoxSa.isChecked()){
                        jsonObject = new JSONObject();
                        jsonObject.put("day", 7);
                        jsonArray.put(jsonObject);
                    }
                    if(checkBoxSu.isChecked()){
                        jsonObject = new JSONObject();
                        jsonObject.put("day", 1);
                        jsonArray.put(jsonObject);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

                orderCallback.OrderGetter(jsonArray);
                dismiss();
            }
        });

        setCancelable(false);

    }



    public interface OrderCallback{
        public void OrderGetter(JSONArray jsonArray);
    }
}

