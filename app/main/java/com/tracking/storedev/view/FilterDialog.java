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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tracking.storedev.R;
import com.tracking.storedev.util.Constants;
import com.tracking.storedev.util.PrefManager;

/**
 * Created by ZASS on 3/22/2018.
 */

public class FilterDialog extends Dialog {

    private Context context;
    PrefManager prefManager = PrefManager.getPrefInstance();
    private String filterStr;
    private SelectionCallBack selectionCallBack;

    private CheckBox checkBoxMa;
    private CheckBox checkBoxTu;
    private CheckBox checkBoxWe;
    private CheckBox checkBoxTh;
    private CheckBox checkBoxFr;
    private CheckBox checkBoxSa;
    private CheckBox checkBoxSu;

    public FilterDialog(Context context, SelectionCallBack selectionCallBack) {
        super(context);
        this.context = context;
        this.selectionCallBack = selectionCallBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.filter_dialog);

        ImageView closeBtn =(ImageView)findViewById(R.id.closeBtn);
        Button applyBtn =(Button)findViewById(R.id.applyBtn);
        final TextView btn_target =(TextView)findViewById(R.id.btn_target);
        final TextView btn_achieved =(TextView)findViewById(R.id.btn_achieved);
        final TextView btn_extra =(TextView)findViewById(R.id.btn_extra);
        final LinearLayout days_selection_layout = (findViewById(R.id.days_selection_layout));

        checkBoxView();

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        filterStr = (String)prefManager.get(Constants.Filter, new String());

        if(filterStr.equals("Target")){
            btn_target.setBackgroundResource(R.drawable.button_small_curve);
            days_selection_layout.setVisibility(View.GONE);
        }else if(filterStr.equals("Achieved")){
            btn_achieved.setBackgroundResource(R.drawable.button_small_curve);
            days_selection_layout.setVisibility(View.GONE);
        }else{
            btn_extra.setBackgroundResource(R.drawable.button_small_curve);
            days_selection_layout.setVisibility(View.VISIBLE);
        }

        btn_target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefManager.put(Constants.Filter, "Target");
                selectionCallBack.selection("Target", "");
                days_selection_layout.setVisibility(View.GONE);
            }
        });

        btn_achieved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefManager.put(Constants.Filter, "Achieved");
                selectionCallBack.selection("Achieved", "");
                days_selection_layout.setVisibility(View.GONE);
            }
        });

        btn_extra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefManager.put(Constants.Filter, "Extra");
                btn_target.setBackgroundResource(R.drawable.edittext_form);
                btn_achieved.setBackgroundResource(R.drawable.edittext_form);
                btn_extra.setBackgroundResource(R.drawable.button_small_curve);
                days_selection_layout.setVisibility(View.VISIBLE);
            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String days = "";
                String daysName = "";
                if(checkBoxMa.isChecked()){
                    days += 2+",";
                    daysName += checkBoxMa.getText().toString()+", ";
                }
                if(checkBoxTu.isChecked()){
                    daysName += checkBoxTu.getText().toString()+", ";
                    days += 3+",";
                }
                if(checkBoxWe.isChecked()){
                    daysName += checkBoxWe.getText().toString()+", ";
                    days += 4+",";
                }
                if(checkBoxTh.isChecked()){
                    daysName += checkBoxTh.getText().toString()+", ";
                    days += 5+",";
                }
                if(checkBoxFr.isChecked()){
                    daysName += checkBoxFr.getText().toString()+", ";
                    days += 6+",";
                }
                if(checkBoxSa.isChecked()){
                    daysName += checkBoxSa.getText().toString()+", ";
                    days += 7+",";
                }
                if(checkBoxSu.isChecked()){
                    daysName += checkBoxSu.getText().toString()+", ";
                    days += 1+",";
                }
                selectionCallBack.selection(days, daysName);
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });

        setCancelable(false);

    }

    public void checkBoxView(){
        checkBoxMa = (CheckBox)findViewById(R.id.checkBoxMonday);
        checkBoxTu = (CheckBox)findViewById(R.id.checkBoxTuesday);
        checkBoxWe = (CheckBox)findViewById(R.id.checkBoxWednesday);
        checkBoxTh = (CheckBox)findViewById(R.id.checkBoxThursday);
        checkBoxFr = (CheckBox)findViewById(R.id.checkFriday);
        checkBoxSa = (CheckBox)findViewById(R.id.checkSaturday);
        checkBoxSu = (CheckBox)findViewById(R.id.checkSunday);
    }

    public interface SelectionCallBack{
        public void selection(String selection, String daysName);
    }

}

