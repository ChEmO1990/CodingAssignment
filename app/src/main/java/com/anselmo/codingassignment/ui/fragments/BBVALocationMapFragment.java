package com.anselmo.codingassignment.ui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anselmo.codingassignment.R;
import com.anselmo.codingassignment.http.HttpUtility;
import com.anselmo.codingassignment.models.BBVALocation;
import com.anselmo.codingassignment.models.HourFlag;
import com.anselmo.codingassignment.ui.DetailActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new DrawGeoJson().execute();
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
    }

    private class DrawGeoJson extends AsyncTask<Void, Void, List<BBVALocation>> {
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

            String jsonResponse = httpUtility.makeHttpCall("https://maps.googleapis.com/maps/api/place/textsearch/json?query=BBVA+Compass&location=32.777154, -96.799225&radius=10000&key=AIzaSyA-kXdyFNpT5E8M305fgQgcXXoF3KBJl0g");

            Log.i("ANSELMIOO", jsonResponse);

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

                    itemLocation.setFormatted_address(nodeItems.getString("formatted_address"));
                    itemLocation.setName(nodeItems.getString("name"));
                    itemLocation.setIcon(nodeItems.getString("icon"));


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

}
