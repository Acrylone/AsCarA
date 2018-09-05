package com.example.acrylon3.ascara;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;


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

    View fragment_location;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_location, container, false);
        setHasOptionsMenu(true);
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
                rootView.setVisibility(View.GONE);
                fragment = new SearchFragment();
                replaceFragment(fragment);
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
}