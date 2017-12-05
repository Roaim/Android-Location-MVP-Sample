package com.bongo.roaim.locationpractice;

import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Hridoy on 04-Dec-17.
 */

public class LocationPresenter implements Contract.Presenter, Contract.InteractorListener {
    private final LocationInteractor mInteractor;
    private Contract.View mView;

    public LocationPresenter(Contract.View view) {
        mView = view;
        mInteractor = new LocationInteractor(this);
    }

    @Override
    public void initGoogleMap() {
        mView.onInitGoogleMap(mInteractor);
    }

    @Override
    public void moveCamera(LatLng latLng) {
        if (latLng==null) mView.onMoveCamera(mInteractor.getBongoLatLng());
        else mView.onMoveCamera(latLng);
    }

    @Override
    public void getLastKnownLocation() {
        if (mView.isLocationPermissionGranted()) {
            mInteractor.getLastKnownLocation(mView.getFusedLocation());
        } else {
            mView.onReqLocationPermission();
        }
    }

    @Override
    public void onLocationPermissionRequestResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            mInteractor.getLastKnownLocation(mView.getFusedLocation());
        } else {
            getLastKnownLocation();
        }
    }

    @Override
    public void onGoogleMapReady(GoogleMap map) {
        mView.setGoogleMap(map);
        mView.addMarkerAtBongo(mInteractor.getBongoLatLng());
    }

    @Override
    public void onFailureLastKnownLocation(Exception e) {

    }

    @Override
    public void displayLocationSettingsRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        locationRequest.setInterval(60*60*1000);
        locationRequest.setFastestInterval(60*60*1000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        mInteractor.getLocationSettingsStatus(mView.getGoogleApiClient(),builder.build());
    }

    @Override
    public void updateCamera(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(location.getAccuracy());
        circleOptions.fillColor(Color.parseColor("#6e00bfa5"));
        circleOptions.strokeColor(Color.parseColor("#6e00bfa5"));
        circleOptions.strokeWidth(1);
        mView.addCircleGpsAccuracy(latLng, circleOptions);
    }

    @Override
    public void onLocationSettingsResolutionRequired(Status status) throws IntentSender.SendIntentException {
        mView.onDisplayLocationRequiredDialog(status);
    }

    @Override
    public void onLocationSettingsFailed() {
        mView.onShowLocationSettingsFailedMsg();
    }

    @Override
    public void onLocationSettingsSuccess() {
        new Awaiter(){
            @Override
            protected long getWaitInterval() {
                return 1000;
            }

            @Override
            protected void onFinished() {
                getLastKnownLocation();
            }
        }.start();
    }
}
