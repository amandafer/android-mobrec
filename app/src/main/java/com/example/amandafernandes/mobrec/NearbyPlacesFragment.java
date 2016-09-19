package com.example.amandafernandes.mobrec;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class NearbyPlacesFragment extends Fragment {
    private Place[] nearbyPlaces = null;
    private ListView listView = null;
    private ArrayList<String> placesNameList = new ArrayList<String>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nearby, container, false);

        listView = (ListView) rootView.findViewById(R.id.listView);

        return rootView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Array of the found places
        nearbyPlaces = GmapsFragment.mPlaces;

        for (Place place:nearbyPlaces) {
            placesNameList.add(place.mPlaceName);
        }

        // Create The Adapter with passing ArrayList as 3rd parameter
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(getActivity(), R.layout.adapter_id, placesNameList);
        listView.setAdapter(arrayAdapter);
    }
}
