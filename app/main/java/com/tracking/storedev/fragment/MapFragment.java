package com.tracking.storedev.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.config.GoogleDirectionConfiguration;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.request.DirectionDestinationRequest;
import com.akexorcist.googledirection.request.DirectionOriginRequest;
import com.akexorcist.googledirection.request.DirectionRequest;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.liefery.android.icon_badge.IconBadge;
import com.tracking.storedev.App;
import com.tracking.storedev.MainActivity;
import com.tracking.storedev.R;
import com.tracking.storedev.ShopProfileAtivity;
import com.tracking.storedev.StoreDetailActivity;
import com.tracking.storedev.bean.ShopMark;
import com.tracking.storedev.db.Store;
import com.tracking.storedev.dbcontroller.DBHandler;
import com.tracking.storedev.util.DistanceFromMeComparator;
import com.tracking.storedev.util.PrefManager;
import com.tracking.storedev.util.Util;
import com.tracking.storedev.view.TProgressDialog;
import com.tracking.storedev.web.WebCore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import im.delight.android.location.SimpleLocation;

/**
 * Created by Irfan Ali on 2/28/2018.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback, DirectionCallback {

    private App appInstance = App.getAppInstance();
    private DBHandler dbHandler = DBHandler.getInstance();
    private Util utilInstance = Util.getInstance();
    private WebCore webCore = WebCore.getInstance();
    private PrefManager prefManager = PrefManager.getPrefInstance();
    private ArrayList<ShopMark> locationList;
    private GoogleMap googleMap;
    LatLng currentLatLng = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        appInstance.mapFragment = this;
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (utilInstance.isNetworkConnected(getActivity())) {
            requestDirection();
        }

        return view;
    }

    public void requestDirection() {
        if(googleMap != null)
            googleMap.clear();
        locationList = dbHandler.getShopsLocation();

        if (locationList.size() > 0 || true) {
            try {
                SimpleLocation location = MainActivity.locationInstance;
                if (location == null)
                    return;

                double currentLat = location.getLatitude();
                double currentLng = location.getLongitude();
                currentLatLng = new LatLng(currentLat, currentLng);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (currentLatLng == null)
                return;

            if (currentLatLng.latitude != 0.0 && currentLatLng.longitude != 0.0) {

                //Add user marke
                ShopMark shopMark = new ShopMark();
                shopMark.latLng = currentLatLng;
                locationList.add(shopMark);
                Collections.sort(locationList, new DistanceFromMeComparator(currentLatLng));


                GoogleDirectionConfiguration.getInstance().setLogEnabled(true);
                DirectionOriginRequest directionOriginRequest = GoogleDirection.withServerKey(getResources().getString(R.string.google_map_server_key));

                DirectionDestinationRequest directionDestinationRequest = directionOriginRequest.from(currentLatLng);
                for (int index = 0; index < locationList.size(); index++) {
                    ShopMark shopMark1 = locationList.get(index);
                    if (index == locationList.size() - 1) {
                        DirectionRequest directionRequest = directionDestinationRequest.to(shopMark1.latLng);
                        directionRequest.transitMode(TransportMode.DRIVING);
                        directionRequest.execute(this);
                    } else {
                        directionDestinationRequest.and(shopMark1.latLng);
                    }
                }
            } else {
                Toast.makeText(getActivity(), "Getting Current Location Failed", Toast.LENGTH_SHORT).show();
            }
        }else{

        }
    }

    public void refreshMap(){
        requestDirection();
    }

    public Bitmap getCurrentLocationBadge() {
        Drawable circleDrawable = getResources().getDrawable(R.mipmap.ic_marker);
        IconBadge badge = new IconBadge(getActivity());
        badge.setBackgroundShapeCircle();
        badge.setBackgroundShapeColor(getResources().getColor(R.color.login_background));
        badge.setElevation(getResources().getDimension(
                R.dimen.marker_shadow));
        badge.setForegroundDrawable(circleDrawable);
        badge.setForegroundShapeColor(getResources().getColor(R.color.white));
        Bitmap bitmap = badge.export(80);
        return bitmap;
    }

    public Bitmap getShopMakerBadge() {
        Drawable circleDrawable = getResources().getDrawable(R.mipmap.ic_shop_marker);
        IconBadge badge = new IconBadge(getActivity());
        badge.setBackgroundShapeCircle();
        badge.setBackgroundShapeColor(Color.BLACK);
        badge.setElevation(getResources().getDimension(
                R.dimen.marker_shadow));
        badge.setForegroundDrawable(circleDrawable);
        badge.setForegroundShapeColor(getResources().getColor(R.color.white));
        Bitmap bitmap = badge.export(80);
        return bitmap;
    }


    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        try {

            if (direction.isOK()) {
                Route route = direction.getRouteList().get(0);

                int legCount = route.getLegList().size();
                for (int index = 0; index < legCount; index++) {
                    final int i = index;
                    Leg leg = route.getLegList().get(index);
                    final Store storeInfo = locationList.get(index).store;

                    BitmapDescriptor icon;

                    if (index == 0) {
                        Bitmap iconBadge = getCurrentLocationBadge();
                        icon = BitmapDescriptorFactory.fromBitmap(iconBadge);
                        Marker marker = googleMap.addMarker(new MarkerOptions().title("Its Me").snippet("").anchor(.5f, .5f).icon(icon).position(leg.getStartLocation().getCoordination()));
                        marker.setTag("0");
                        marker.showInfoWindow();
                    } else if (index == legCount - 1) {
                        Bitmap iconBadge = getShopMakerBadge();
                        icon = BitmapDescriptorFactory.fromBitmap(iconBadge);
                        Marker marker = googleMap.addMarker(new MarkerOptions().title(storeInfo.ShopName).snippet(storeInfo.ShopKeeper).anchor(.5f, .5f).icon(icon).position(leg.getEndLocation().getCoordination()));
                        marker.setTag("" + storeInfo.StoreID);
                        marker.showInfoWindow();
                    } else {
                        Bitmap iconBadge = getShopMakerBadge();
                        icon = BitmapDescriptorFactory.fromBitmap(iconBadge);
                        Marker marker = googleMap.addMarker(new MarkerOptions().title(storeInfo.ShopName).snippet(storeInfo.ShopKeeper).anchor(.5f, .5f).icon(icon).position(leg.getEndLocation().getCoordination()));
                        marker.setTag("" + storeInfo.StoreID);
                        marker.showInfoWindow();
                    }

                    List<Step> stepList = leg.getStepList();
                    ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(getActivity(), stepList, 2, getResources().getColor(R.color.black), 2, Color.BLUE);
                    for (PolylineOptions polylineOption : polylineOptionList) {
                        googleMap.addPolyline(polylineOption);
                    }
                    googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            String tag = marker.getTag().toString();
                            int shopID = Integer.parseInt(tag);
                            Store storeDetail = dbHandler.getStoreDetail(shopID);
                            if (storeDetail != null) {
                                StoreDetailActivity.storeInfo = storeDetail;
                                ShopProfileAtivity.storeInfo = storeDetail;
                                ShopProfileAtivity.latlng = currentLatLng;
                                ShopProfileAtivity.Status = "Target";
                                startActivity(new Intent(getActivity(), ShopProfileAtivity.class));
                            }
                        }
                    });



                }
                setCameraWithCoordinationBounds(route);
            } else {
                Toast.makeText(getActivity(), "Google map loading failed", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(southwest, (float) 15));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh, menu);//Menu Resource, Menu
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                popupForFilter();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        Toast.makeText(getActivity(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
    }

    public void popupForFilter(){
        final ProgressDialog progressDialog = new TProgressDialog().createTProgressDialog(getActivity());

        int userID = prefManager.getUserID();
        webCore.pullFromServer(getActivity(), userID, progressDialog);

        webCore.pushToWeb(getActivity(), new WebCore.WebEqecuteCallBack() {
            @Override
            public void finishResponse() {
                offlineCheckInCheckOut(progressDialog);
            }

            @Override
            public void updateToDateResponse() {
                offlineCheckInCheckOut(progressDialog);
            }
        });

        if (utilInstance.isNetworkConnected(getActivity())) {
            requestDirection();
        }
    }

    public void offlineCheckInCheckOut(final ProgressDialog progressDialog ){
        webCore.pushStoreCheckOutToServer(getActivity(), new WebCore.WebEqecuteCallBack() {
            @Override
            public void finishResponse() {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void updateToDateResponse() {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }
}
