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

public class OrderListDialog extends Dialog {



    private Context context;
    private Button addBtn;
    private ImageView closeBtn;
    private long storeID;
    private OrderListAdapter orderListAdapter;
    private OrderCallback orderCallback;

    public OrderListDialog(Context context, long storeID, long storeVisitID, OrderCallback orderCallback) {
        super(context);
        this.context = context;
        this.storeID = storeID;
        this.orderCallback = orderCallback;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.order_dialog);

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
                    Toast.makeText(context, R.string.put_order_with_Qty, Toast.LENGTH_SHORT).show();
                }else{
                    orderCallback.OrderGetter(stringArrayList);
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

    public interface OrderCallback{
        public void OrderGetter(ArrayList<Stocks> orderItem);
    }
}

