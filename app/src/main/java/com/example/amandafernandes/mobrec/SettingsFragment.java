package com.example.amandafernandes.mobrec;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;


public class SettingsFragment extends Fragment {
    private Button mBtnApply = null;
    private Button mBtnReset = null;
    private CheckBox cbAirport, cbAtm, cbBank, cbBusStation, cbChurch, cbHospital, cbMovieTheater, cbRestaurant;

    String[] mPlaceType = null;
    StringBuilder mFilterTypes = new StringBuilder("");

    public SettingsFragment(){}

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        // Getting reference to Find Button
        mBtnApply = (Button) rootView.findViewById(R.id.btn_apply);
        mBtnReset = (Button) rootView.findViewById(R.id.btn_reset);
        mPlaceType = getResources().getStringArray(R.array.place_type);

        // Checkboxes
        cbAirport = (CheckBox) rootView.findViewById(R.id.cb_airport);
        cbAtm = (CheckBox) rootView.findViewById(R.id.cb_atm);
        cbBank = (CheckBox) rootView.findViewById(R.id.cb_bank);
        cbBusStation = (CheckBox) rootView.findViewById(R.id.cb_busstation);
        cbChurch = (CheckBox) rootView.findViewById(R.id.cb_church);
        cbHospital = (CheckBox) rootView.findViewById(R.id.cb_hospital);
        cbMovieTheater = (CheckBox) rootView.findViewById(R.id.cb_movietheather);
        cbRestaurant = (CheckBox) rootView.findViewById(R.id.cb_restaurant);

        cbAirport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mFilterTypes.append("|").append("airport");
                }
            }
        });
        cbAtm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mFilterTypes.append("|").append("airport");
                }
            }
        });
        cbBank.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mFilterTypes.append("|").append("bank");
                }
            }
        });
        cbBusStation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mFilterTypes.append("|").append("bus_station");
                }
            }
        });
        cbChurch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mFilterTypes.append("|").append("church");
                }
            }
        });
        cbHospital.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mFilterTypes.append("|").append("hospital");
                }
            }
        });
        cbMovieTheater.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mFilterTypes.append("|").append("movie_theater");
                }
            }
        });
        cbRestaurant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mFilterTypes.append("|").append("restaurant");
                }
            }
        });

        // Setting click event listener for the apply button
        mBtnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment mapFragment = new GmapsFragment(mFilterTypes.toString());

                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, mapFragment).commit();

                // update selected item and title, then close the drawer
                MapsActivity.mDrawerList.setItemChecked(0, true);
                MapsActivity.mDrawerList.setSelection(0);
                MapsActivity.mDrawerLayout.closeDrawer(MapsActivity.mDrawerList);
            }
        });

        // Setting click event listener for the reset button
        mBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment mapFragment = new GmapsFragment("");

                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, mapFragment).commit();

                // update selected item and title, then close the drawer
                MapsActivity.mDrawerList.setItemChecked(0, true);
                MapsActivity.mDrawerList.setSelection(0);
                MapsActivity.mDrawerLayout.closeDrawer(MapsActivity.mDrawerList);
            }
        });

        return rootView;
    }
}