package com.mcarpe12.familymapclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.mcarpe12.familymapclient.service.DataCache;

import familymap.Event;

public class EventActivity extends AppCompatActivity {
    public static final String EXTRA_EVENT_ID = "com.mcarpe12.familymapclient.event_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        String eventID = getIntent().getStringExtra(EXTRA_EVENT_ID);

        FragmentManager fm = this.getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.fragment_container_event);
        if (mapFragment == null) {
            mapFragment = createMapFragment("EVENT MAP FRAGMENT", R.layout.fragment_event, eventID);
            fm.beginTransaction()
                    .add(R.id.fragment_container_event, mapFragment)
                    .commit();
        }
    }

    private MapFragment createMapFragment(String title, int layout, String eventID) {
        MapFragment fragment = new MapFragment();

        Bundle args = new Bundle();
        args.putString(MapFragment.ARG_TITLE, title);
        args.putInt(MapFragment.EXTRA_LAYOUT_RESOURCE, layout);
        args.putString(MapFragment.EXTRA_INIT_EVENT_ID, eventID);
        fragment.setArguments(args);

        return fragment;
    }
}
