package com.example.acrylon3.ascara;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class Location_Fragment extends Fragment implements LocationListener{

    private static final String LOG_TAG = "MainActivity";

    private Location mylocation;
    Double latitude = 0.0;
    Double longitude = 0.0;
    Double altitude = 0.0;


    private AutoCompleteTextView location_edit;
    private Button setPosition_button;
    private View line;
    private Context applicationContext;

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

    private View.OnClickListener btnPositionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, Location_Fragment.this);
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(bestProvider);

            if (location == null) {
                Toast.makeText(getApplicationContext(), "GPS signal not found", Toast.LENGTH_SHORT).show();
            }
            if (location != null) {
                Log.e("locatin", "location--" + location);

                Log.e("latitude at beginning",
                        "@@@@@@@@@@@@@@@" + location.getLatitude());
                onLocationChanged(location);
            }
        }
    };

    public void onLocationChanged(Location location) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Log.e("latitude", "latitude--" + latitude);
        try {
            Log.e("latitude", "inside latitude--" + latitude);
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
//                String postalCode = addresses.get(0).getPostalCode();
//                String knownName = addresses.get(0).getFeatureName();

                location_edit.setText(address + ", " +state + " , " + city + " , " + country + "\n");
//                String locationS = location_edit.getText().toString();
//                Intent newActivityIntent=new Intent(getActivity(),MainActivity.class);
//                newActivityIntent.putExtra("locationS",location_edit.getText());
//                startActivity(newActivityIntent);
                }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.i(TAG, "Provider " + s + " has now status: " + i);

    }

    @Override
    public void onProviderEnabled(String s) {
        Log.i(TAG, "Provider " + s + " is enabled");

    }

    @Override
    public void onProviderDisabled(String s) {
        Log.i(TAG, "Provider " + s + " is disabled");

    }

    private static final String TAG = "LocationAddress";


    public Context getApplicationContext() {
        return applicationContext;
    }
}
