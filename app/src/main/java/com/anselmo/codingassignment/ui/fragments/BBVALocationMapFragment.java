package com.anselmo.codingassignment.ui.fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anselmo.codingassignment.R;
import com.anselmo.codingassignment.http.HttpUtility;
import com.anselmo.codingassignment.models.BBVALocation;
import com.anselmo.codingassignment.models.HourFlag;
import com.anselmo.codingassignment.ui.activities.DetailActivity;
import com.anselmo.codingassignment.utils.Constants;
import com.anselmo.codingassignment.utils.Prefs;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chemo on 8/24/17.
 */
public class BBVALocationMapFragment extends Fragment implements OnMapReadyCallback {
    //Current Tag
    private static final String TAG = BBVALocationMapFragment.class.getSimpleName();

    //GoogleMaps instance
    private GoogleMap mGoogleMap;

    //MapView inside layout
    private MapView mMapView;

    //Inflate view
    private View mView;

    private static final int REQUEST_ACCESS_FINE_LOCATION = 1;

    private LatLng currentCoordinates = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.bbva_location_map_fragment, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = (MapView) mView.findViewById(R.id.mainMapView);
        if( mMapView != null ) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getActivity().getApplicationContext());
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        getMyLocation();
    }

    private class DrawMark extends AsyncTask<Void, Void, List<BBVALocation>> {
        //Http Utility
        private HttpUtility httpUtility = null;

        //Items with the data
        private ArrayList<BBVALocation> locations;

        //Mark Icon
        private ArrayList<BitmapDescriptor> bitmaps = null;

        //Single Item
        private BBVALocation itemLocation = null;

        //Types
        private ArrayList<String> types;

        @Override
        protected List<BBVALocation> doInBackground(Void... voids) {
            httpUtility = new HttpUtility();

            //Hardcode
            String jsonResponse = httpUtility.makeHttpCall("https://maps.googleapis.com/maps/api/place/textsearch/json?query=BBVA+Compass&location=" + Prefs.getString(Constants.LATITUDE, null) + "," + Prefs.getString(Constants.LONGITUDE, null) + "&radius=10000&key=" + getString(R.string.google_maps_key));

            //Save json
            Prefs.putString(Constants.JSON_RESPONSE, jsonResponse);

            /**
             * --READ ME--
             *
             * The json response is so big... I'm not going parsing all the response. Im going to get only some important values.
             */
            try {
                //HomeActivity
                JSONObject jsonObj = new JSONObject(jsonResponse);

                //Getting json array node
                JSONArray results = jsonObj.getJSONArray("results");

                locations = new ArrayList<>(results.length());
                bitmaps = new ArrayList<>();

                //looping through all results
                for (int i = 0; i < results.length(); i++) {
                    //Create new item
                    itemLocation = new BBVALocation();

                    //Node with single item
                    JSONObject nodeItems = results.getJSONObject(i);

                    if( nodeItems.has("opening_hours") ) {
                        JSONObject hoursNode = nodeItems.getJSONObject("opening_hours");

                        if( hoursNode.getString("open_now").equals("true") ) {
                            itemLocation.setOpen_now(HourFlag.TRUE);
                        } else {
                            itemLocation.setOpen_now(HourFlag.FALSE);
                        }

                    } else {
                        itemLocation.setOpen_now(HourFlag.INFO_NO_AVAILABLE);
                    }

                    if( nodeItems.has("rating") ) {
                        itemLocation.setRating(nodeItems.getString("rating"));
                    } else {
                        itemLocation.setRating("0");
                    }

                    if( nodeItems.has("formatted_address")) {
                        itemLocation.setFormatted_address(nodeItems.getString("formatted_address"));
                    }

                    if( nodeItems.has("name")) {
                        itemLocation.setName(nodeItems.getString("name"));
                    }

                    if( nodeItems.has("icon")) {
                        itemLocation.setIcon(nodeItems.getString("icon"));
                    }

                    JSONObject geometry = nodeItems.getJSONObject("geometry");
                    JSONObject nodeLocation = geometry.getJSONObject("location");

                    itemLocation.setLat(nodeLocation.getString("lat"));
                    itemLocation.setLng(nodeLocation.getString("lng"));

                    JSONArray nodeTypes = nodeItems.getJSONArray("types");
                    types = new ArrayList<>();

                    for( int j = 0; j < nodeTypes.length(); j++ ) {
                        types.add( nodeTypes.getString(j) );
                    }

                    itemLocation.setTypes(types);
                    locations.add(itemLocation);

                    bitmaps.add(BitmapDescriptorFactory.fromBitmap(Picasso.with(getActivity()).load(itemLocation.getIcon()).get()));
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return locations;
        }

        @Override
        protected void onPostExecute(final List<BBVALocation> items) {
            super.onPostExecute(items);

            for( int i = 0; i < items.size(); i++ ) {
                mGoogleMap.addMarker( new MarkerOptions().position(new LatLng(Double.parseDouble(items.get(i).getLat()), Double.parseDouble(items.get(i).getLng()))).icon(bitmaps.get(i)).title(items.get(i).getName())
                        .snippet(items.get(i).getFormatted_address()));

                mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Intent i = new Intent(getActivity(), DetailActivity.class);
                        BBVALocation loc = items.get(0);
                        i.putExtra("current_location", loc);
                        getActivity().startActivity(i);
                    }
                });
            }
        }
    }

    private void handleLatLng(double latitude, double longitude) {
        Log.v("TAG", "(" + latitude + "," + longitude + ")");
        Prefs.putString(Constants.LATITUDE, String.valueOf(latitude));
        Prefs.putString(Constants.LONGITUDE, String.valueOf(longitude));

        currentCoordinates = new LatLng(Double.parseDouble(Prefs.getString(Constants.LATITUDE, null)), Double.parseDouble(Prefs.getString(Constants.LONGITUDE, null)));

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentCoordinates, 15));

        mGoogleMap.animateCamera(CameraUpdateFactory.zoomIn());

        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentCoordinates)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mGoogleMap.addMarker(new MarkerOptions().position(currentCoordinates).title(getString(R.string.your_location)));
        new DrawMark().execute();
    }

    private class Listener implements LocationListener {
        public void onLocationChanged(Location location) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            handleLatLng(latitude, longitude);
        }

        public void onProviderDisabled(String provider){}
        public void onProviderEnabled(String provider){}
        public void onStatusChanged(String provider, int status, Bundle extras){}
    }

    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_ACCESS_FINE_LOCATION);

            return;
        }

        // Get user location
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if(location != null) {
            handleLatLng(location.getLatitude(), location.getLongitude());
        }
    }

}
