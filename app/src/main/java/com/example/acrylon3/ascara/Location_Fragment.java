package com.example.acrylon3.ascara;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;


public class Location_Fragment extends Fragment {

    private static final String LOG_TAG = "MainActivity";

    private Location mylocation;
    Double latitude = 0.0;
    Double longitude = 0.0;
    Double altitude = 0.0;

    private LocationManager locationManager;
    private LocationListener listener;
    private AutoCompleteTextView location_edit;
    private Button setPosition_button;
    private View line;
    private Context applicationContext;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private MainActivity mainActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location, container, false);
        rootView.setVisibility(View.GONE);
        location_edit = rootView.findViewById(R.id.location_editxt);
        setPosition_button = rootView.findViewById(R.id.set_position_btn);
        setPosition_button.setOnClickListener(btnPositionListener);


        return rootView;

//        return super.onCreateView(inflater, container, savedInstanceState);
    }

    final View.OnClickListener btnPositionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };
}