package com.example.acrylon3.ascara;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
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


public class Location_Fragment extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        com.google.android.gms.location.LocationListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private static final String LOG_TAG = "Location_Fragment";
    private static final int GOOGLE_API_CLIENT_ID = R.string.key_api;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(0, 0), new LatLng(0, 0));

    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationAddressResultReceiver addressResultReceiver;
    private Location currentLocation;
    private String currentAdd = null;
    private String myAddress;
    Double latitude = 0.0;
    Double longitude = 0.0;
    Double altitude = 0.0;


    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private LocationManager locationManager;
    private LocationListener listener;
    private AutoCompleteTextView mAutocompleteTextView;
    private Button setPosition_button;
    private View line;
    private Context applicationContext;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private SearchFragment searchFragment;
    private ProgressBar progressBar;


    View fragment_location;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_location, container, false);
        setHasOptionsMenu(true);
        mAutocompleteTextView = rootView.findViewById(R.id.location_editxt);
        setPosition_button = rootView.findViewById(R.id.set_position_btn);
        setPosition_button.setOnClickListener(btnPositionListener);
        progressBar = rootView.findViewById(R.id.progressBar);


        if (mGoogleApiClient == null) {
            rebuildGoogleApiClient();
        }

        mAutocompleteTextView.setThreshold(3);
        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);

        addressResultReceiver = new Location_Fragment.LocationAddressResultReceiver(new Handler());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                currentLocation = locationResult.getLocations().get(0);
                getAddress();
            }
        };
        startLocationUpdates();

        return rootView;

    }

    final View.OnClickListener btnPositionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showResults(currentAdd);
            while (currentAdd.isEmpty()) {
                progressBar = new ProgressBar(getContext());
                progressBar.setVisibility(View.VISIBLE);
            }
            progressBar.setVisibility(View.GONE);

        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_done, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.action_menu_done:

                myAddress = mAutocompleteTextView.getText().toString();
                rootView.setVisibility(View.GONE);
                fragment = new SearchFragment();
                final Bundle bundle = new Bundle();
                bundle.putString("Address", myAddress);
                fragment.setArguments(bundle);
                replaceFragment(fragment);
                onPause();
                break;
        }
        return true;
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi((LocationServices.API))
                .enableAutoManage(getActivity(), GOOGLE_API_CLIENT_ID, this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();
//        mGoogleApiClient.connect();
    }

    private void rebuildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity()).enableAutoManage(this.getActivity(), GOOGLE_API_CLIENT_ID, this).addConnectionCallbacks(this).addApi(Places.GEO_DATA_API).build();
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
            }
        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(getActivity(),
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getContext(),
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

    private void getAddress() {

        if (!Geocoder.isPresent()) {
            Toast.makeText(getActivity(),
                    "Can't find current address, ",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(getActivity(), GetAddressIntentService.class);
        intent.putExtra("add_receiver", addressResultReceiver);
        intent.putExtra("add_location", currentLocation);
        getActivity().startService(intent);
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
                    Toast.makeText(getActivity(), "Location permission not granted, " +
                                    "restart the app if you want the feature",
                            Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
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
                Toast.makeText(getActivity(),
                        "Address not found, ",
                        Toast.LENGTH_SHORT).show();
            }

            currentAdd = resultData.getString("address_result");

        }
    }

    private void showResults(String currentAdd) {
        mAutocompleteTextView.setText(currentAdd);
    }


    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }
}