package com.example.amandafernandes.mobrec;

import android.app.Fragment;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class GmapsFragment extends Fragment {
    // Specifies the drawMarker() to draw the marker with default color
    private static final float UNDEFINED_COLOR = -1;
    /** Google Places API variables (PLACES KEY IS SERVER KEY) **/
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final String PLACES_API_RADIUS = "150";
    private static final String PLACES_API_KEY = "AIzaSyDbyAkQGz9tSBdyHPagbcaDDRfxl9czPJc";

    private static GoogleMap mGoogleMap;

    // Stores near by places
    static Place[] mPlaces = null;
    // Links marker id and place object
    static HashMap<String, Place> mHMReference = new HashMap<String, Place>();

    private static double mLatitude = 0;
    private static double mLongitude = 0;
    private static String type;
    private EditText searchText = null;

    /** Constructor **/
    public GmapsFragment(String type){ GmapsFragment.type = type; }

    /** Creates the main view and sets markers and geolocation on map **/
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gmaps, container, false);

        searchText = (EditText) rootView.findViewById(R.id.searchText);

        // Checking Google Play and Internet availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity().getBaseContext());
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        // The app only works if there is a connection with Google Play Services
        if(status != ConnectionResult.SUCCESS || netInfo == null || !netInfo.isConnected()) {
            Toast.makeText(getActivity().getBaseContext(), "Internet Connection Error. Please, connect to working Internet connection.",
                    Toast.LENGTH_LONG).show();
        } else {
            setUpMapIfNeeded(savedInstanceState);
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mGoogleMap != null) {
            MapsActivity.fragmentManager.beginTransaction()
                    .remove(MapsActivity.fragmentManager.findFragmentById(R.id.map)).commit();
            mGoogleMap = null;
        }
    }

    /** Sets up the map if it is possible to do so **/
    public void setUpMapIfNeeded(Bundle savedInstanceState) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mGoogleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mGoogleMap = ((SupportMapFragment) MapsActivity.fragmentManager
                    .findFragmentById(R.id.map)).getMap();
            MapsActivity.fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {

                }
            });
            // Check if we were successful in obtaining the map.
            if (mGoogleMap != null)
                setUpMap(savedInstanceState);
        }
    }

    private void setUpMap(Bundle savedInstanceState) {
        mGoogleMap.setMyLocationEnabled(true);

        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);
        // Getting Current Location From GPS
        //Location location = locationManager.getLastKnownLocation(provider);
        Location location = new Location(provider);
        location.setLatitude(51.524680);
        location.setLongitude(-0.039388);

        if(location != null) {
            onLocationChanged(location);
        }

        // Handling screen rotation
        if(savedInstanceState != null) {
            // Removes all the existing links from marker id to place object
            mHMReference.clear();

            //If nearby places are already saved
            if(savedInstanceState.containsKey("places")) {
                // Retrieving the array of place objects
                mPlaces = (Place[]) savedInstanceState.getParcelableArray("places");

                // Traversing through each near by place object
                for (Place mPlace : mPlaces) {
                    // Getting latitude and longitude of the i-th place
                    LatLng point = new LatLng(Double.parseDouble(mPlace.mLat),
                            Double.parseDouble(mPlace.mLng));

                    // Drawing the marker corresponding to the i-th place
                    Marker m = drawMarker(point, UNDEFINED_COLOR);

                    // Linking the place to its consecutive marker id
                    mHMReference.put(m.getId(), mPlace);
                }
            }
        }

        // Without any filter, the map will show all places near the user
        setMarkers();

        // Search click listener
        searchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    String search = searchText.getText().toString();

                    GmapsFragment.type = search;
                    setMarkers();
                }
                return false;
            }
        });

        // Marker click listener
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // If touched at User input location
                if(!mHMReference.containsKey(marker.getId()))
                    return false;

                // Getting place object corresponding to the currently clicked Marker
                Place place = mHMReference.get(marker.getId());

                // Creating an instance of DisplayMetrics
                DisplayMetrics dm = new DisplayMetrics();

                // Getting the screen display metrics
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

                // Creating a dialog fragment to display the photo
                PlaceDialogFragment dialogFragment = new PlaceDialogFragment(place,dm);

                // Getting a reference to Fragment Manager
                //FragmentManager fragmentManager = myContext.getSupportFragmentManager();

                // Starting Fragment Transaction
                FragmentTransaction fragmentTransaction = MapsActivity.fragmentManager.beginTransaction();
                // Adding the dialog fragment to the transaction
                fragmentTransaction.add(dialogFragment, "TAG");
                // Committing the fragment transaction
                fragmentTransaction.commit();

                return false;
            }
        });
    }

    //@Override
    //public void onConnected(Bundle savedInstanceState) {
    //}

    //TODO Put onConnected bundle HERE
    /*

     */

    // Sets the camera at the user location
    public static void onLocationChanged(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        LatLng latLng = new LatLng(mLatitude, mLongitude);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
    }

    /** Function that sets the markers in the map depending of the user request */
    public void setMarkers() {
        // Clears the map from any other markers
        mGoogleMap.clear();

        // String that will request the places from the API
        StringBuilder googlePlacesUrl = new StringBuilder(PLACES_API_BASE);
        googlePlacesUrl.append("location="+mLatitude+","+mLongitude);
        googlePlacesUrl.append("&radius=" + PLACES_API_RADIUS);
        googlePlacesUrl.append("&types=" + this.type);
        googlePlacesUrl.append("&key=" + PLACES_API_KEY);
        googlePlacesUrl.append("&sensor=true");

        // Creating a new non-ui thread task to download Google place json data
        PlacesTask placesTask = new PlacesTask();

        // Invokes the "doInBackground()" method of the class PlaceTask
        placesTask.execute(googlePlacesUrl.toString());
    }

    /** A class, to download Google Places */
    private class PlacesTask extends AsyncTask<String, Integer, String> {
        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch(Exception e) {
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask();

            // Start parsing the Google places in JSON format
            // Invokes the "doInBackground()" method of ParserTask
            parserTask.execute(result);
        }
    }

    /** A method to download json data from argument url */
    private static String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer stringBuffer = new StringBuffer();

            String line = "";
            while((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            data = stringBuffer.toString();
            bufferedReader.close();
        } catch(Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, Place[]> {
        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected Place[] doInBackground(String... jsonData) {
            Place[] places = null;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);
                /** Getting the parsed data as a List construct */
                places = placeJsonParser.parse(jObject);

            } catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(Place[] places){
            mPlaces = places;

            if (places.length != 0) {
                for (Place place : places) {
                    // Getting location of the place
                    double lat = Double.parseDouble(place.mLat);
                    double lng = Double.parseDouble(place.mLng);
                    LatLng latLng = new LatLng(lat, lng);

                    Marker m = drawMarker(latLng, UNDEFINED_COLOR);

                    // Adding place reference to HashMap with marker id as HashMap key
                    // to get its reference in infowindow click event listener
                    mHMReference.put(m.getId(), place);
                }
            } else {
                Toast.makeText(getActivity().getBaseContext(), "No results were found. Please, try a different one.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /** Drawing marker at latLng with color */
    private static Marker drawMarker(LatLng latLng, float color){
        // Creating a marker
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting the position for the marker
        markerOptions.position(latLng);

        if (color != UNDEFINED_COLOR)
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(color));

        // Placing a marker on the touched position
        return mGoogleMap.addMarker(markerOptions);
    }

    /** A callback function, executed on screen rotation */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Saving all the near by places objects
        if(mPlaces!=null)
            outState.putParcelableArray("places", mPlaces);

        super.onSaveInstanceState(outState);
    }
}