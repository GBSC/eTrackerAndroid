package com.tracking.storedev.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tracking.storedev.App;
import com.tracking.storedev.R;
import com.tracking.storedev.ShopProfileAtivity;
import com.tracking.storedev.StoreDetailActivity;
import com.tracking.storedev.adapter.JourneyAdapter;
import com.tracking.storedev.adapter.ShopSectionRecyclerAdapter;
import com.tracking.storedev.bean.SectionHeader;
import com.tracking.storedev.db.Store;
import com.tracking.storedev.dbcontroller.DBHandler;
import com.tracking.storedev.util.Constants;
import com.tracking.storedev.util.PrefManager;
import com.tracking.storedev.util.Util;
import com.tracking.storedev.view.FilterDialog;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Irfan Ali on 2/28/2018.
 */

public class StoreVisitsFragment extends Fragment {

    private DBHandler dbHandler = DBHandler.getInstance();
    private App appInstance = App.getAppInstance();
    private Util utilInstance = Util.getInstance();
    private PrefManager prefManager = PrefManager.getPrefInstance();

    FilterDialog filterDialog;
    private RecyclerView recyclerView;
    private TextView emptyElement;
    private ShopSectionRecyclerAdapter journeyAdapter;
    List<SectionHeader> orderList;
    private String status = "Target";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_journey_cycle, container, false);
        appInstance.storeVisitsFragment = this;

        String filterStr = (String)prefManager.get(Constants.Filter, new String());
        if(!filterStr.equals(""))
            status = filterStr;

        String currentDay = utilInstance.getDayOfWeek()+"";
        orderList =  dbHandler.getOrderByStoreID(currentDay, status);


        recyclerView = (RecyclerView)view.findViewById(R.id.journey_recycler_view);
        emptyElement = (TextView)view.findViewById(R.id.emptyElement);
        recyclerView.setHasFixedSize(true);

        if(orderList.size() == 0){
            String label = "";
            if(status.equals("Target"))
                label = "Targets"+" are not assign on "+utilInstance.getDayByDayID();
            else if(status.equals("Achieved"))
                label = "You have not achieve target";
            else
                label = "You have not Extra";

            emptyElement.setText(label);
            emptyElement.setVisibility(View.VISIBLE);

        }else{
            emptyElement.setVisibility(View.GONE);
        }
        if(journeyAdapter == null){
            journeyAdapter = new ShopSectionRecyclerAdapter(getActivity(), orderList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(journeyAdapter);
        }else{
            //journeyAdapter.refreshStoreVisist(storeList);
        }

        /*journeyAdapter.setOnItemClickListener(new JourneyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int positioon) {
                try{
                    Store store = storeList.get(positioon);
                    StoreDetailActivity.storeInfo = store;
                    StoreDetailActivity.Status = status;
                    ShopProfileAtivity.storeInfo = store;
                    ShopProfileAtivity.Status = status;
                    startActivity(new Intent(getActivity(), ShopProfileAtivity.class));
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });*/
        return view;
    }

    public void refreshStoreVisit(Store store){
        //journeyAdapter.add(store);
    }

    public void updateStoreVisit(Store store){
      //journeyAdapter.update(store);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.main, menu);//Menu Resource, Menu

       /* MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);

        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.box_background_color));
        searchAutoComplete.setTextColor(getResources().getColor(R.color.white));
        searchAutoComplete.setHint("Search Store...");
        searchAutoComplete.setTextSize(14);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                journeyAdapter.getFilter().filter(newText);
                return false;
            }
        });*/

    }

    public void popupForFilter(){
        filterDialog = new FilterDialog(getActivity(), new FilterDialog.SelectionCallBack() {

            @Override
            public void selection(String selection) {
                String currentDay = utilInstance.getDayOfWeek()+"";
                if(selection.equals("Target")){
                    status = "Target";
                    orderList =  dbHandler.getOrderByStoreID(currentDay, status);
                }else if(selection.equals("Achieved")){
                    status = "Achieved";
                    orderList =  dbHandler.getOrderByStoreID(currentDay, status);
                }else{
                    status = "Extra";
                    orderList =  dbHandler.getOrderByStoreID(currentDay, status);
                }

                if(orderList.size() == 0){
                    String label = "";
                    if(status.equals("Target"))
                        label = "You have no more targets on "+utilInstance.getDayByDayID();
                    else if(status.equals("Achieved"))
                        label = "Achieved target list is empty";
                    else
                        label = "Extra list is empty";

                    emptyElement.setText(label);
                    emptyElement.setVisibility(View.VISIBLE);
                }else{
                    emptyElement.setVisibility(View.GONE);
                }

                journeyAdapter.refreshStoreVisist(orderList);
                filterDialog.dismiss();
            }
        });
        filterDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                popupForFilter();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        if(status.equals("Target")){
            String currentDate = utilInstance.getDayOfWeek()+"";
            //orderList = dbHandler.getOrderByStoreID(currentDate, "Target");
            if(journeyAdapter != null){
               // journeyAdapter.refreshStoreVisist(orderList);
            }
        }

        super.onResume();
    }


}
