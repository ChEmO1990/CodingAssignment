package com.anselmo.codingassignment.ui.activities;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.anselmo.codingassignment.R;
import com.anselmo.codingassignment.ui.common.BaseActivity;
import com.anselmo.codingassignment.ui.fragments.BBVALocationListFragment;
import com.anselmo.codingassignment.ui.fragments.BBVALocationMapFragment;


/**
 * Created by chemo on 8/24/17.
 */

public class HomeActivity extends BaseActivity {
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private FragmentTransaction transaction;
    private BBVALocationMapFragment mapFragment = new BBVALocationMapFragment();
    private BBVALocationListFragment listFragment = new BBVALocationListFragment();
    private static final int REQUEST_ACCESS_FINE_LOCATION = 1;
    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = getToolbar();

        transaction = getFragmentManager().beginTransaction();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( flag == 0 ) {
                    flag = 1;
                    toolbar.setTitle(getString(R.string.home_listview));
                    openFragment(listFragment);
                } else if( flag == 1 ){
                    flag = 0;
                    toolbar.setTitle(getString(R.string.home_mapview));
                    openFragment(mapFragment);
                }
            }
        });

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            toolbar.setTitle(getString(R.string.home_mapview));
            openFragment(mapFragment);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    finish();
                    Intent i = new Intent(this, HomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                } else {
                    Toast.makeText(this, getString(R.string.no_permissions), Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }
        }
    }

    private void openFragment(final Fragment fragment)   {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}