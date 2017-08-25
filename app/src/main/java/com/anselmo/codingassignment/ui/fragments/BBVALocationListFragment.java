package com.anselmo.codingassignment.ui.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anselmo.codingassignment.R;
import com.anselmo.codingassignment.adapters.LocationAdapter;
import com.anselmo.codingassignment.models.BBVALocation;
import com.anselmo.codingassignment.models.HourFlag;
import com.anselmo.codingassignment.utils.Constants;
import com.anselmo.codingassignment.utils.Prefs;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

public class BBVALocationListFragment extends Fragment {
    private RecyclerView recycler;
    private List<BBVALocation> itemsList;
    private LocationAdapter mAdapter;
    private View mView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemsList = new ArrayList<>();
        mAdapter = new LocationAdapter(getActivity(), itemsList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.bbva_location_list_fragment, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recycler = (RecyclerView) mView.findViewById(R.id.recycler);

        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recycler.setHasFixedSize(true);
        recycler.setItemViewCacheSize(20);
        recycler.setDrawingCacheEnabled(true);
        recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recycler.setAdapter(mAdapter);

        new ParserJsonDummy().execute();
    }

    private class ParserJsonDummy extends AsyncTask<Void, Void, List<BBVALocation>> {
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
            String jsonResponse = Prefs.getString(Constants.JSON_RESPONSE, null);

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
        protected void onPostExecute( List<BBVALocation> items) {
            super.onPostExecute(items);

            itemsList.clear();
            itemsList.addAll(items);
            mAdapter.notifyDataSetChanged();
        }
    }

}
