package com.mcarpe12.familymapclient.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.mcarpe12.familymapclient.fragment.MapFragment;
import com.mcarpe12.familymapclient.R;

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
