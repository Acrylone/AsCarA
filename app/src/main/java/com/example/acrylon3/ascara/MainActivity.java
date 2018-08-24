package com.example.acrylon3.ascara;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    private static final String LOG_TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;

    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private Location mylocation;

    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    private final static int TIME_PICKER_INTERVAL = 5;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    String timeStart = "";
    String timeEnd = "";
    String dateStart = "";
    String dateEnd = "";

    private DatePickerDialog.OnDateSetListener mDateSetListenerStart;
    private TimePickerDialog.OnTimeSetListener mTimeSetListenerStart;
    private DatePickerDialog.OnDateSetListener mDateSetListenerEnd;
    private TimePickerDialog.OnTimeSetListener mTimeSetListenerEnd;

    Toolbar toolbar;
    private TextView mTextMessage;
    private Button location, start, end, search;

    View fragment_location;
    View fragment_start;
    View fragment_end;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.search_notification:
//                    mTextMessage.setText(R.string.title_left);
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

//        setSupportActionBar(toolbar);
        fragment_location = findViewById(R.id.fragment_location);
        fragment_start = findViewById(R.id.fragment_start);
//////////////////////////////GOOGLE API PLACES/////////////////////////////////////////////////////
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id
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


////////////////////////////////////////////////////////////////////////////////////////////////////

        location = (Button) findViewById(R.id.location_btn);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment_location.setVisibility(View.VISIBLE);
            }
        });
/////////////////////////////START BUTTON///////////////////////////////////////////////////////////

        start = (Button) findViewById(R.id.start_location_btn);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                fragment_start.setVisibility(View.VISIBLE);
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);

                final TimePickerDialog timePickerDialog = new TimePickerDialog(
                        MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                        mTimeSetListenerStart,
                        hour, minute, true);
                timePickerDialog.setTitle("Choose Hour:");
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        // This is hiding the "Cancel" button:
                        timePickerDialog.getButton(Dialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
                    }
                });
                timePickerDialog.show();

                final DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                        mDateSetListenerStart,
                        year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                datePickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        // This is hiding the "Cancel" button:
                        datePickerDialog.getButton(Dialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
                    }
                });
                datePickerDialog.show();

            }
        });

        mDateSetListenerStart = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(LOG_TAG, "onDateSet: dd/mm/yyy: " + day + "/" + month + "/" + year);
                dateStart = day + "/" + month + "/" + year;
            }
        };

        mTimeSetListenerStart = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                Log.d(LOG_TAG, "onTimeSet: HH:MM: " + hour + "/" + minute);
                timeStart = String.format("%02d:%02d",hour , minute);
                start.setText("PICKUP : \n" + dateStart + "\n" + timeStart);
            }
        };






//        start.setText("PICKUP : \n" + date + "\n" + time);

///////////////////////////END BUTTON /////////////////////////////////////////////////////////////
        end = (Button) findViewById(R.id.end_location_btn);
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                        mTimeSetListenerEnd,
                        hour , minute, true);
                timePickerDialog.setTitle("Choose Hour:");
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();

                DatePickerDialog dialog = new DatePickerDialog(
                        MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListenerEnd,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListenerEnd = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(LOG_TAG, "onDateSet: dd/mm/yyy: " + day + "/" + month + "/" + year);
                dateEnd = day + "/" + month + "/" + year;
//                end.setText("RETURN : \n" + date);
            }
        };

        mTimeSetListenerEnd = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                Log.d(LOG_TAG, "onTimeSet: HH:MM: " + hour + "/" + minute);
                timeEnd = String.format("%02d:%02d",hour , minute);
                end.setText("RETURN : \n" + dateEnd + "\n" + timeEnd);
            }
        };
////////////////////////////SEARCH BUTTON///////////////////////////////////////////////////////////
        search = (Button) findViewById(R.id.search_btn);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Location_Fragment.class);
                startActivity(intent);
            }
        });
    }

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
//        checkPermissions();
//        afficherAdresse();

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

//    private synchronized void setUpGClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, 0, this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//        mGoogleApiClient.connect();
//    }

//    public void onLocationChanged(Location location) {
//        mylocation = location;
//        if (mylocation != null) {
//
//            this.mylocation = location;
////            setUpGClient();
//        }
//    }
//
//    private void getMyLocation() {
//        if (mGoogleApiClient != null) {
//            if (mGoogleApiClient.isConnected()) {
//                int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
//                        Manifest.permission.ACCESS_FINE_LOCATION);
//                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
//                    mylocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//                    LocationRequest locationRequest = new LocationRequest();
//                    locationRequest.setInterval(30000);
//                    locationRequest.setFastestInterval(30000);
//                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                            .addLocationRequest(locationRequest);
//                    builder.setAlwaysShow(true);
//                    LocationServices.FusedLocationApi
//                            .requestLocationUpdates(mGoogleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) this);
//                    PendingResult<LocationSettingsResult> result =
//                            LocationServices.SettingsApi
//                                    .checkLocationSettings(mGoogleApiClient, builder.build());
//                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//
//                        @Override
//                        public void onResult(LocationSettingsResult result) {
//                            final Status status = result.getStatus();
//                            switch (status.getStatusCode()) {
//                                case LocationSettingsStatusCodes.SUCCESS:
//                                    // All location settings are satisfied.
//                                    // You can initialize location requests here.
//                                    int permissionLocation = ContextCompat
//                                            .checkSelfPermission(MainActivity.this,
//                                                    Manifest.permission.ACCESS_FINE_LOCATION);
//                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
//                                        mylocation = LocationServices.FusedLocationApi
//                                                .getLastLocation(mGoogleApiClient);
//                                    }
//                                    break;
//                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                                    // Location settings are not satisfied.
//                                    // But could be fixed by showing the user a dialog.
//                                    try {
//                                        // Show the dialog by calling startResolutionForResult(),
//                                        // and check the result in onActivityResult().
//                                        // Ask to turn on GPS automatically
//                                        status.startResolutionForResult(MainActivity.this,
//                                                REQUEST_CHECK_SETTINGS_GPS);
//                                    } catch (IntentSender.SendIntentException e) {
//                                        // Ignore the error.
//                                    }
//                                    break;
//                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                                    // Location settings are not satisfied.
//                                    // However, we have no way
//                                    // to fix the
//                                    // settings so we won't show the dialog.
//                                    // finish();
//                                    break;
//                            }
//                        }
//                    });
//                }
//            }
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case REQUEST_CHECK_SETTINGS_GPS:
//                switch (resultCode) {
//                    case Activity.RESULT_OK:
//                        getMyLocation();
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        finish();
//                        break;
//                }
//                break;
//        }
//    }
//
//    private void checkPermissions() {
//        int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION);
//        List<String> listPermissionsNeeded = new ArrayList<>();
//        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
//            if (!listPermissionsNeeded.isEmpty()) {
//                ActivityCompat.requestPermissions(this,
//                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
//            }
//        } else {
//            getMyLocation();
//        }
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
//                Manifest.permission.ACCESS_FINE_LOCATION);
//        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
//            getMyLocation();
//        }
//    }
//
//    private void afficherAdresse() {
//        setProgressBarIndeterminateVisibility(true);
//
//        //Le geocoder permet de récupérer ou chercher des adresses
//        //gràce à un mot clé ou une position
//        Geocoder geo = new Geocoder(MainActivity.this);
//        try {
//            //Ici on récupère la premiere adresse trouvé gràce à la position que l'on a récupéré
//            List
//                    <Address> adresses = geo.getFromLocation(mylocation.getLatitude(),
//                    mylocation.getLongitude(), 1);
//
//            if (adresses != null && adresses.size() == 1) {
//                Address adresse = adresses.get(0);
//                //Si le geocoder a trouver une adresse, alors on l'affiche
//                ((EditText) findViewById(R.id.location_editxt)).setText(String.format("%s, %s %s",
//                        adresse.getAddressLine(0),
//                        adresse.getPostalCode(),
//                        adresse.getLocality()));
//            } else {
//                //sinon on affiche un message d'erreur
//                ((EditText) findViewById(R.id.location_editxt)).setText("הכתובת לא נכונה");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            ((EditText) findViewById(R.id.location_editxt)).setText("הכתובת לא נכונה");
//        }
//        //on stop le cercle de chargement
//        setProgressBarIndeterminateVisibility(false);
//    }
}
