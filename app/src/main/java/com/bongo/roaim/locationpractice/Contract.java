package com.bongo.roaim.locationpractice;

import android.content.IntentSender;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Hridoy on 04-Dec-17.
 */

public interface Contract {
    interface View {
        String TAG = "MainActivity";
        int REQ_CODE_PERMISSION_LOCATION = 565;
        int REQUEST_CHECK_SETTINGS = 555;

        void onInitGoogleMap(OnMapReadyCallback mapReadyCallback);
        void setGoogleMap(GoogleMap map);
        void addMarkerAtBongo(LatLng latLng);
        void onMoveCamera(LatLng latLng);

        FusedLocationProviderClient getFusedLocation();

        boolean isLocationPermissionGranted();

        void onReqLocationPermission();
        
        GoogleApiClient getGoogleApiClient();

        void onDisplayLocationRequiredDialog(Status status) throws IntentSender.SendIntentException;

        void onShowLocationSettingsFailedMsg();

        void addCircleGpsAccuracy(LatLng latLng, CircleOptions circleOptions);
    }

    interface Presenter {
        void initGoogleMap();

        void moveCamera(LatLng latLng);

        void getLastKnownLocation();

        void onLocationPermissionRequestResult(int requestCode, String[] permissions, int[] grantResults);
    }

    interface Interactor extends OnMapReadyCallback {

        LatLng getBongoLatLng();

        void getLastKnownLocation(FusedLocationProviderClient fusedLocation);

        void getLocationSettingsStatus(GoogleApiClient googleApiClient, LocationSettingsRequest locationSettingsRequest);
    }

    interface InteractorListener {

        void onGoogleMapReady(GoogleMap map);

        void onFailureLastKnownLocation(Exception e);

        void displayLocationSettingsRequest();

        void updateCamera(Location location);

        void onLocationSettingsResolutionRequired(Status status) throws IntentSender.SendIntentException;

        void onLocationSettingsFailed();

        void onLocationSettingsSuccess();
    }
}
