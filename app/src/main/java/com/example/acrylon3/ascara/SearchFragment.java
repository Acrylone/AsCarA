package com.example.acrylon3.ascara;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;

import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements View.OnClickListener {
    private static final String LOG_TAG = "MainActivity";

    private Location mylocation;
    Double latitude = 0.0;
    Double longitude = 0.0;
    Double altitude = 0.0;

    private Button location, start, end, search;


    private LocationManager locationManager;
    private LocationListener listener;
    private AutoCompleteTextView location_edit;
    private Button setPosition_button;
    private View line;
    private Context applicationContext;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private MainActivity mainActivity;
    CustomDateTimePicker customStart;
    CustomDateTimePicker customEnd;


    View fragment_location;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        setHasOptionsMenu(true);
        location = rootView.findViewById(R.id.location_btn);
        start = rootView.findViewById(R.id.start_location_btn);
        end = rootView.findViewById(R.id.end_location_btn);
        search = rootView.findViewById(R.id.search_btn);

        location.setOnClickListener(this);
        start.setOnClickListener(this);
        end.setOnClickListener(this);
        search.setOnClickListener(this);

        Bundle bundle = getArguments();
        if (bundle != null)
        {
            String currentAddress = bundle.getString("Address", "");
            location.setText(currentAddress);
            if(currentAddress.isEmpty())
                location.setText(R.string.txt_btn_location);
        }

        return rootView;
//        return inflater.inflate(R.layout.fragment_search, container, false);
    }


    @Override
    public void onClick(View view) {
        Fragment fragment = null;
        switch (view.getId()) {
            case R.id.location_btn:
                fragment = new Location_Fragment();
                replaceFragment(fragment);
                break;

            case R.id.start_location_btn:
                startLocation();
                customStart.showDialog();
                break;

            case R.id.end_location_btn:
                endLocation();
                customEnd.showDialog();
                break;
        }
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void startLocation() {
        customStart = new CustomDateTimePicker(getActivity(),
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

    }

    public void endLocation() {
        customEnd = new CustomDateTimePicker(getActivity(),
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
                        end.setText("");
                        end.setText(weekDayShortName + " " + calendarSelected.get(Calendar.DAY_OF_MONTH) + " " + monthShortName + "." + "\n"
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
        customEnd.set24HourFormat(true);
        /**
         * Pass Directly current data and time to show when it pop up
         */
        customEnd.setDate(Calendar.getInstance());

    }

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
