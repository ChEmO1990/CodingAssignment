package com.anselmo.codingassignment.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.anselmo.codingassignment.R;
import com.anselmo.codingassignment.models.BBVALocation;
import com.anselmo.codingassignment.ui.common.BaseActivity;
import com.anselmo.codingassignment.utils.Constants;
import com.anselmo.codingassignment.utils.Prefs;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import java.io.IOException;

/**
 * Created by chemo on 8/24/17.
 */

public class DetailActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener {
    private BBVALocation currentLocation = null;
    private LatLng currentCoordinates = null;
    private BitmapDescriptor bitmapDescriptor = null;

    //UI
    private SupportMapFragment mapFragment = null;
    private TextView address;
    private TextView hour;
    private TextView rating;
    private TextView type;
    private Button btnMaps;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        toolbar = getToolbar();
        toolbar.setNavigationIcon(R.mipmap.ic_up);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnMaps  = (Button) findViewById(R.id.btnGoogleMaps);
        address  = (TextView) findViewById(R.id.lbl_address);
        hour     = (TextView) findViewById(R.id.lbl_hour);
        rating   = (TextView) findViewById(R.id.lbl_rating);
        type     = (TextView) findViewById(R.id.lbl_type);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.detailMapView);

        if( getIntent().getExtras() != null ) {
            Bundle data = getIntent().getExtras();
            currentLocation = data.getParcelable("current_location");
            currentCoordinates = new LatLng(Double.parseDouble(currentLocation.getLat()), Double.parseDouble(currentLocation.getLng()));

            switch ( currentLocation.getOpen_now() ) {
                case 1:
                    hour.setText(getString(R.string.placeholder_open_now) + " " + getString(R.string.yes));
                    break;
                case 2:
                    hour.setText(getString(R.string.placeholder_open_now) + " " + getString(R.string.no));
                    break;
                case 3:
                    hour.setText(getString(R.string.placeholder_open_now) + " " + getString(R.string.no_description));
                    break;
            }

            try {
                toolbar.setTitle(currentLocation.getName());
                address.setText(getString(R.string.placeholder_address) + " " + currentLocation.getFormatted_address());
                rating.setText(getString(R.string.placeholder_rating) + " " + currentLocation.getRating());
                type.setText(getString(R.string.placeholder_type) + " " + currentLocation.getTypes().get(0));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        btnMaps.setOnClickListener(this);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onClick(View v) {
        if( v.getId() == R.id.btnGoogleMaps ) {
            String geo = "geo:" + Prefs.getString(Constants.LATITUDE, null) + "," + Prefs.getString(Constants.LONGITUDE, null);
            Uri gmmIntentUri = Uri.parse(geo);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(Picasso.with(DetailActivity.this).load(currentLocation.getIcon()).get());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(currentLocation.getLat()), Double.parseDouble(currentLocation.getLng())))
                                .icon(bitmapDescriptor)
                                .title(currentLocation.getName())
                                .snippet(currentLocation.getFormatted_address()));
                    }
                });
            }
        }).start();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentCoordinates, 15));
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentCoordinates)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
