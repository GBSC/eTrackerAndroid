package com.tracking.storedev.fragment;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.tracking.storedev.R;
import com.tracking.storedev.StoreDetailActivity;
import com.tracking.storedev.adapter.CompetitiveAdapter;
import com.tracking.storedev.bean.Stocks;
import com.tracking.storedev.db.CompetitiveItem;
import com.tracking.storedev.db.Store;
import com.tracking.storedev.db.StoreVisit;
import com.tracking.storedev.util.DBToObject;
import com.tracking.storedev.util.Logger;
import com.tracking.storedev.util.Util;
import com.tracking.storedev.view.CompitativeTakingDialog;
import com.tracking.storedev.web.HttpCaller;
import com.tracking.storedev.web.WebURL;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//import com.android.volley.error.VolleyError;

/**
 * Created by Irfan Ali on 2/28/2018.
 */

public class CompitativeStockFragment extends Fragment {

    private Util utilInstance = Util.getInstance();
    private HttpCaller httpCaller = HttpCaller.getInstance();
    private static CompitativeStockFragment orderFragment = new CompitativeStockFragment();
    public DBToObject dbToObject = DBToObject.getInstance();
    private Logger logger = Logger.getInstance();

    private RecyclerView recyclerView;
    private CompetitiveAdapter competitiveAdapter;
    private FloatingActionButton fab;

    private Store store = StoreDetailActivity.storeInfo;
    private  StoreVisit storeVisit = StoreDetailActivity.storeVisit;
    private List<CompetitiveItem> storeList = new ArrayList<>();

    public int indexDuplicateItem = 0;


    public static CompitativeStockFragment getInstance() {
        return orderFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.store_taking_fragment, container, false);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        TextView textViewTitle = view.findViewById(R.id.txt_title);
        textViewTitle.setText("Competitor Brand Inventory");

        final long storeID = store.StoreID;

        recyclerView = (RecyclerView) view.findViewById(R.id.journey_recycler_view);
        competitiveAdapter = new CompetitiveAdapter(storeList, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(competitiveAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final long storeVisitID = storeVisit.StoreVisitID;
                CompitativeTakingDialog orderDialog = new CompitativeTakingDialog(getActivity(), storeID, storeVisitID, new CompitativeTakingDialog.CompitativeCallback() {
                    @Override
                    public void CompitativeGetter(ArrayList<Stocks> orderItems) {

                        boolean isInternetConnection = utilInstance.isNetworkConnected(getActivity());

                        List<CompetitiveItem> orderList = new ArrayList<>();
                        for(Stocks stocks : orderItems){
                            CompetitiveItem competitiveItem = new CompetitiveItem();
                            competitiveItem.Product = stocks.Product;
                            competitiveItem.IsSync = isInternetConnection;
                            competitiveItem.Qty = stocks.Qty;
                            competitiveItem.StoreVisitID = storeVisitID;
                            orderList.add(competitiveItem);
                        }

                        JSONObject jsonObject = dbToObject.compitativeStockToJSONObject(orderList);
                        if(isInternetConnection)
                            postCompitativeTaken(jsonObject, orderItems);
                        else{
                            logger.setLog(null, orderList.size() + " Competitive added successfully Offline", "Success");
                            refreshCompitativeList(orderItems, isInternetConnection);
                        }
                    }
                });
                orderDialog.show();
            }
        });
        return view;
    }


    public void postCompitativeTaken(JSONObject jsonObject, final ArrayList<Stocks> orderItems) {
        String url = WebURL.addMultipleCompetativeStock;
        httpCaller.jsonObjectPOSTRequest(true, getActivity(), url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    logger.setLog(null, orderItems.size() + " Competitive successfully synced to web", "Success");
                    refreshCompitativeList(orderItems, true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError error) {
                logger.setLog(null, orderItems.size() + " Competitive failed to web", "Failed");
                Toast.makeText(getActivity(), "All Competitive stocks Added failed", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }


    public void refreshCompitativeList(final ArrayList<Stocks> orderItems, boolean isInternetConnect){
        ArrayList<CompetitiveItem> competitiveItemList = new ArrayList<>();

        for (Stocks stock : orderItems) {
            long storeVisitID = storeVisit.StoreVisitID;
            CompetitiveItem orderItem = getDuplicateOrder(stock.Product, storeVisitID);
            if(orderItem != null){
                orderItem.Qty = stock.Qty+orderItem.Qty;
                storeList.set(indexDuplicateItem, orderItem);
                competitiveAdapter.notifyDataSetChanged();
            }else{
                orderItem = new CompetitiveItem();
                orderItem.StoreVisitID = storeVisit.StoreVisitID;
                orderItem.StoreID = storeVisitID;
                orderItem.Product = stock.Product;
                orderItem.Qty = stock.Qty;
                orderItem.IsSync = isInternetConnect;
                orderItem.save();
                competitiveAdapter.refrehAdapter(orderItem);
            }
        }
        Toast.makeText(getActivity(), "All competitive stocks Successfully Added", Toast.LENGTH_SHORT).show();

    }

    public CompetitiveItem getDuplicateOrder(String product, long storeVisitID){
        for(int a=0;a<storeList.size() ; a++){
            CompetitiveItem orderItem = storeList.get(a);
            if(orderItem.Product.equals(""+product) && orderItem.StoreVisitID == storeVisitID ){
                indexDuplicateItem = a;
                return orderItem;
            }
        }
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}
