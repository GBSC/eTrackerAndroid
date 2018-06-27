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
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import java.util.ArrayList;
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

    private RelativeLayout captionLayout;
    FilterDialog filterDialog;
    private RecyclerView recyclerView;
    private TextView emptyElement, textCaption;
    private ImageView imageClose;
    private JourneyAdapter journeyAdapter;
    private List<Store> storeList;
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

        captionLayout = (RelativeLayout) view.findViewById(R.id.captionLayout);
        imageClose = (ImageView) view.findViewById(R.id.imageClose);
        textCaption = (TextView)view.findViewById(R.id.textCaption);
        recyclerView = (RecyclerView)view.findViewById(R.id.journey_recycler_view);
        emptyElement = (TextView)view.findViewById(R.id.emptyElement);

        String filterStr = (String)prefManager.get(Constants.Filter, new String());
        if(!filterStr.equals("")) {
            if(!filterStr.equals("Target") && !filterStr.equals("Achieved")){
                String checkedDays =(String)prefManager.get(Constants.DAYS_CHECKED_SELECTION, "");
                String daysName = utilInstance.getNumberToDays(checkedDays);
                storeList = dbHandler.getStoreList(checkedDays, "");
                //storeList = getStoreBySelectedDays(storeList1, checkedDays);
                textCaption.setText(""+daysName);
            }else{
                status = filterStr;
                textCaption.setText(""+filterStr);

                String currentDay = utilInstance.getDayOfWeek()+"";
                storeList = dbHandler.getStoreList(filterStr,  currentDay);
            }
        }

        if(storeList == null){
            String currentDay = utilInstance.getDayOfWeek()+"";
            storeList = dbHandler.getStoreList("Target",  currentDay);
        }

        if(storeList.size() == 0){
            String label = "";
            if(status.equals("Target"))
                label = "Targets"+" are not assign on "+utilInstance.getDayByDayID();
            else if(status.equals("Achieved"))
                label = "You have not achieve target";
            else
                label = "You have not Extra";

            emptyElement.setText(label);
            emptyElement.setVisibility(View.VISIBLE);
            captionLayout.setVisibility(View.GONE);
        }else{
            if(!filterStr.equals(""))
                captionLayout.setVisibility(View.VISIBLE);
            emptyElement.setVisibility(View.GONE);
        }
        if(journeyAdapter == null){
            journeyAdapter = new JourneyAdapter(storeList,getActivity());
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(journeyAdapter);
        }else{
            journeyAdapter.refreshStoreVisist(storeList);
        }

        journeyAdapter.setOnItemClickListener(new JourneyAdapter.OnItemClickListener() {
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
        });

        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captionLayout.setVisibility(View.GONE);

                storeList = dbHandler.getStoreList("All",  "");
                if(journeyAdapter != null){
                    journeyAdapter.refreshStoreVisist(storeList);
                }

            }
        });
        return view;
    }

    public void refreshStoreVisit(Store store){
        journeyAdapter.add(store);
    }

    public void updateStoreVisit(Store store){
        journeyAdapter.update(store);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.main, menu);//Menu Resource, Menu

        MenuItem search = menu.findItem(R.id.search);
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
        });

    }

    public void popupForFilter(){
        filterDialog = new FilterDialog(getActivity(), new FilterDialog.SelectionCallBack() {

            @Override
            public void selection(String selection, String daysName) {
                String currentDay = utilInstance.getDayOfWeek()+"";
                if(selection.equals("Target")){
                    status = "Target";
                    storeList = dbHandler.getStoreList("Target", currentDay);
                }else if(selection.equals("Achieved")){
                    status = "Achieved";
                    storeList = dbHandler.getStoreList("Achieved", currentDay);
                }else{
                    status = selection;
                    prefManager.put(Constants.DAYS_CHECKED_SELECTION, status);
                    storeList = dbHandler.getStoreList(status, currentDay);
                    //storeList = getStoreBySelectedDays(storeList, status);
                }

                if(storeList.size() == 0){
                    String label = "";
                    if(status.equals("Target"))
                        label = "You have no more targets on "+utilInstance.getDayByDayID();
                    else if(status.equals("Achieved"))
                        label = "Achieved target list is empty";
                    else
                        label = "Extra list is empty";

                    emptyElement.setText(label);
                    emptyElement.setVisibility(View.VISIBLE);
                    captionLayout.setVisibility(View.GONE);
                }else{
                    captionLayout.setVisibility(View.VISIBLE);
                    emptyElement.setVisibility(View.GONE);
                }
                if(!status.equals("Target") && !status.equals("Achieved")) {
                    String selectedDay = daysName.trim().substring(0, daysName.length() - 2);
                    textCaption.setText("" + selectedDay);
                }else{
                    textCaption.setText(""+status);
                }
                journeyAdapter.refreshStoreVisist(storeList);
                filterDialog.dismiss();
            }
        });
        filterDialog.show();
    }

    public List<Store>  getStoreBySelectedDays(List<Store> storeList, String selectedDays){
        List<Store> tempStoreList = new ArrayList<>();
        for(Store store :storeList){
            store.VisitDays = selectedDays;
            tempStoreList.add(store);
        }
        return tempStoreList;
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
        super.onResume();
    }
}
