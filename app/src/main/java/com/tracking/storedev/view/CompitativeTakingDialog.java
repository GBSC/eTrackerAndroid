package com.tracking.storedev.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.tracking.storedev.R;
import com.tracking.storedev.adapter.OrderListAdapter;
import com.tracking.storedev.bean.Stocks;

import java.util.ArrayList;

/**
 * Created by ZASS on 3/22/2018.
 */

public class CompitativeTakingDialog extends Dialog {

    private Context context;
    private Button addBtn;
    private ImageView closeBtn;
    private long storeID;
    private long storeVisitID;
    private OrderListAdapter orderListAdapter;
    private CompitativeCallback orderCallback;

    public CompitativeTakingDialog(Context context, long storeID, long storeVisitID, CompitativeCallback compitativeCallback) {
        super(context);
        this.context = context;
        this.storeID = storeID;
        this.storeVisitID = storeVisitID;
        this.orderCallback = compitativeCallback;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.compitative_dialog);

        closeBtn =(ImageView)findViewById(R.id.closeBtn);
        addBtn =(Button)findViewById(R.id.addBtn);

        ListView listView = (ListView)findViewById(R.id.listview);
        orderListAdapter = new OrderListAdapter(context, getItemList());

        listView.setAdapter(orderListAdapter);

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
                ArrayList<Stocks> stringArrayList = orderListAdapter.getStocksList();
                if(stringArrayList.size() == 0){
                    Toast.makeText(context, R.string.please_put_compitative_stock_with_qtry, Toast.LENGTH_SHORT).show();
                }else {
                    orderCallback.CompitativeGetter(stringArrayList);
                    dismiss();
                }
            }
        });

        setCancelable(false);

    }


    public ArrayList<Stocks> getItemList(){
        ArrayList<Stocks> stringArrayList = new ArrayList<>();
        Stocks stocks = new Stocks();
        stocks.Product = "Eva Cooking Oil – 01 Litre";
        stocks.Qty = 0;
        stringArrayList.add(stocks);

        stocks = new Stocks();
        stocks.Product = "Eva Cooking Oil – 05 Litre";
        stocks.Qty = 0;
        stringArrayList.add(stocks);

        stocks = new Stocks();
        stocks.Product = "Eva Cooking Oil (1Ltr X 5)";
        stocks.Qty = 0;
        stringArrayList.add(stocks);

        stocks = new Stocks();
        stocks.Product = "Eva Cooking Oil (1Ltr X 10)";
        stocks.Qty = 0;
        stringArrayList.add(stocks);
        stocks = new Stocks();
        stocks.Product = "Eva Cooking Oil (2Ltr X 10)";
        stocks.Qty = 0;
        stringArrayList.add(stocks);

        stocks = new Stocks();
        stocks.Product = "Eva Cooking Oil (2Ltr X 15)";
        stocks.Qty = 0;
        stringArrayList.add(stocks);

        return stringArrayList;
    }

    public interface CompitativeCallback{
        public void CompitativeGetter(ArrayList<Stocks> orderItem);
    }
}

