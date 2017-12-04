package com.bongo.roaim.locationpractice;

import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Created by Hridoy on 04-Dec-17.
 */

public class LocationInteractor implements Contract.Interactor {
    private static final String TAG = "LocationInteractor";
    private Contract.InteractorListener mListener;

    public LocationInteractor(Contract.InteractorListener listener) {
        mListener = listener;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mListener.onGoogleMapReady(googleMap);
    }

    @Override
    public LatLng getBongoLatLng() {
        //        Bongo/@23.7943427,90.4255612,17z
//        https://www.google.com.bd/maps/place/Bongo/@23.7956145,90.4270297,19z/data=!4m5!3m4!1s0x3755c7ba2db10c9d:0xd463ee11ca3d345b!8m2!3d23.7955872!4d90.4262124?hl=en
//        return new LatLng(23.795686, 90.426195);
        return new LatLng(23.7955872, 90.4262124);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void getLastKnownLocation(FusedLocationProviderClient fusedLocation) {
        fusedLocation.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.d(TAG, "onSuccess() called with: location = [" + location + "]");
                        if (location == null) {
                            mListener.displayLocationSettingsRequest();
                        } else {
                            mListener.updateCamera(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure() called with: e = [" + e + "]");
                        mListener.onFailureLastKnownLocation(e);
                    }
                });
    }

    @Override
    public void getLocationSettingsStatus(GoogleApiClient googleApiClient, LocationSettingsRequest locationSettingsRequest) {
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, locationSettingsRequest);
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        mListener.onLocationSettingsSuccess();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                        try {
                            mListener.onLocationSettingsResolutionRequired(status);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            Log.i(TAG, "LocationSettingsResolutionRequired dialog exception:  "+e);
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        mListener.onLocationSettingsFailed();
                        break;
                }
            }
        });
    }
}
