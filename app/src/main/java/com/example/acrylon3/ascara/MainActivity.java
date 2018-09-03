package com.example.acrylon3.ascara;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    private static final String LOG_TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;

    private FusedLocationProviderClient fusedLocationClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;

    private LocationAddressResultReceiver addressResultReceiver;

    private TextView currentAddTv;

    private Location currentLocation;

    private LocationCallback locationCallback;

    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private Button setPosition_button;
    CustomDateTimePicker customStart;
    CustomDateTimePicker customEnd;


    private final static int TIME_PICKER_INTERVAL = 5;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(31.794177, 35.187660), new LatLng(31.794177, 35.187660));

    String timeStart = "";
    String timeEnd = "";
    String dateStart = "";
    String dateEnd = "";

    Double latitude = 0.0;
    Double longitude = 0.0;

    private TextView mTextMessage;
    private Button location, start, end, search;

    View fragment_location;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.search_notification:
                    onResume();
                    return true;
                case R.id.rents_navigation:
//                    mTextMessage.setText(R.string.title_middle);
                    return true;
                case R.id.account_notification:
//                    mTextMessage.setText(R.string.title_right);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
//        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.logo));

        fragment_location = findViewById(R.id.fragment_location);
//////////////////////////////GOOGLE API PLACES/////////////////////////////////////////////////////
        mGoogleApiClient = new GoogleApiClient.Builder(getBaseContext())
                .addApi(Places.GEO_DATA_API)
                .addApi((LocationServices.API))
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();
//        mGoogleApiClient.connect();
        mAutocompleteTextView = findViewById(R.id
                .location_editxt);
        mAutocompleteTextView.setThreshold(3);
        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);

////////////////////////////////////////////////////////////////////////////////////////////////////
//        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        addressResultReceiver = new LocationAddressResultReceiver(new Handler());

        currentAddTv = findViewById(R.id.current_address);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                currentLocation = locationResult.getLocations().get(0);
                getAddress();
            };
        };
        startLocationUpdates();
////////////////////////////////////////////////////////////////////////////////////////////////////

        location = findViewById(R.id.location_btn);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment_location.setVisibility(View.VISIBLE);
            }
        });
/////////////////////////////START BUTTON///////////////////////////////////////////////////////////

        customStart = new CustomDateTimePicker(this,
                new CustomDateTimePicker.ICustomDateTimeListener() {

                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                    @Override
                    public void onSet(Dialog dialog, Calendar calendarSelected,
                                      Date dateSelected, int year, String monthFullName,
                                      String monthShortName, int monthNumber, int date,
                                      String weekDayFullName, String weekDayShortName,
                                      int hour24, int hour12, int min, int sec,
                                      String AM_PM) {
                        //                        ((TextInputEditText) findViewById(R.id.edtEventDateTime))
                        start.setText("");
                        start.setText(weekDayShortName + " " + calendarSelected.get(Calendar.DAY_OF_MONTH) + " " + monthShortName + "." + "\n"
                                + hour24 + ":" + String.format("%02d", min));
                    }

                    @Override
                    public void onCancel() {

                    }
                });
        /**
         * Pass Directly current time format it will return AM and PM if you set
         * false
         */
        customStart.set24HourFormat(true);
        /**
         * Pass Directly current data and time to show when it pop up
         */
        customStart.setDate(Calendar.getInstance());
        start = findViewById(R.id.start_location_btn);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customStart.showDialog();

            }
        });
////////////////////////////////////////////////////////////////////////////////////////////////////
        customEnd = new CustomDateTimePicker(this,
                new CustomDateTimePicker.ICustomDateTimeListener() {

                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                    @Override
                    public void onSet(Dialog dialog, Calendar calendarSelected,
                                      Date dateSelected, int year, String monthFullName,
                                      String monthShortName, int monthNumber, int date,
                                      String weekDayFullName, String weekDayShortName,
                                      int hour24, int hour12, int min, int sec,
                                      String AM_PM) {

                        end.setText("");
                        end.setText(weekDayShortName + " " + calendarSelected.get(Calendar.DAY_OF_MONTH) + " " + monthShortName + "." + "\n"
                                + String.format("%02d", hour24) + ":" + String.format("%02d", min));


                    }

                    @Override
                    public void onCancel() {

                    }
                });
        end = findViewById(R.id.end_location_btn);
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customEnd.showDialog();

            }
        });

        customEnd.set24HourFormat(true);
        /**
         * Pass Directly current data and time to show when it pop up
         */
        customEnd.setDate(Calendar.getInstance());

////////////////////////////////////////////////////////////////////////////////////////////////////
        search = findViewById(R.id.search_btn);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
            }
        });

        setPosition_button = findViewById(R.id.set_position_btn);

        setPosition_button.setOnClickListener(btnPositionListener);
    }

    final View.OnClickListener btnPositionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };


    /*******************END OFCREATE***/////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        if(fragment_location.isShown())
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu_done:
                fragment_location.setVisibility(View.GONE);
                break;
        }
        return true;
    }


    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }
    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(2000);
            locationRequest.setFastestInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    null);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getAddress() {

        if (!Geocoder.isPresent()) {
            Toast.makeText(MainActivity.this,
                    "Can't find current address, ",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, GetAddressIntentService.class);
        intent.putExtra("add_receiver", addressResultReceiver);
        intent.putExtra("add_location", currentLocation);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                } else {
                    Toast.makeText(this, "Location permission not granted, " +
                                    "restart the app if you want the feature",
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }
    private class LocationAddressResultReceiver extends ResultReceiver {
        LocationAddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == 0) {
                //Last Location can be null for various reasons
                //for example the api is called first time
                //so retry till location is set
                //since intent service runs on background thread, it doesn't block main thread
                Log.d("Address", "Location null retrying");
                getAddress();
            }

            if (resultCode == 1) {
                Toast.makeText(MainActivity.this,
                        "Address not found, " ,
                        Toast.LENGTH_SHORT).show();
            }

            String currentAdd = resultData.getString("address_result");

            showResults(currentAdd);
        }
    }

    private void showResults(String currentAdd){
        currentAddTv.setText(currentAdd);
    }


    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}
