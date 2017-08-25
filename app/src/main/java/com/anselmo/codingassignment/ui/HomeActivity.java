package com.anselmo.codingassignment.ui;

import android.app.FragmentTransaction;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.anselmo.codingassignment.R;
import com.anselmo.codingassignment.ui.fragments.BBVALocationListFragment;
import com.anselmo.codingassignment.ui.fragments.BBVALocationMapFragment;
import com.anselmo.codingassignment.utils.Constants;
import com.anselmo.codingassignment.utils.Prefs;


/**
 * Created by chemo on 8/24/17.
 */

public class HomeActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    private FragmentTransaction transaction;
    private BBVALocationListFragment some;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        transaction = getFragmentManager().beginTransaction();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this, "Hola", Toast.LENGTH_LONG).show();

                BBVALocationListFragment fra = new BBVALocationListFragment();

                transaction.replace(R.id.fragment_container, fra);
                transaction.addToBackStack(null);

// Commit the transaction
                transaction.commit();
            }
        });

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {


            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            BBVALocationMapFragment firstFragment = new BBVALocationMapFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();


        }

        // Get user location
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Keep track of user location.
        // Use callback/listener since requesting immediately may return null location.
        // IMPORTANT: TO GET GPS TO WORK, MAKE SURE THE LOCATION SERVICES ON YOUR PHONE ARE ON.
        // FOR ME, THIS WAS LOCATED IN SETTINGS > LOCATION.
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, new Listener());
        // Have another for GPS provider just in case.
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, new Listener());
        // Try to request the location immediately
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location == null){
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (location != null){
            handleLatLng(location.getLatitude(), location.getLongitude());
        }
        Toast.makeText(getApplicationContext(),
                "Trying to obtain GPS coordinates. Make sure you have location services on.",
                Toast.LENGTH_SHORT).show();

        Log.i("Hola", Prefs.getString(Constants.LATITUDE, null) + " + " + Prefs.getString(Constants.LONGITUDE, null));
    }

    /**
     * Handle lat lng.
     */
    private void handleLatLng(double latitude, double longitude){
        Log.v("TAG", "(" + latitude + "," + longitude + ")");
        Prefs.putString(Constants.LATITUDE, String.valueOf(latitude));
        Prefs.putString(Constants.LONGITUDE, String.valueOf(longitude));
    }

    /**
     * Listener for changing gps coords.
     */
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


}
