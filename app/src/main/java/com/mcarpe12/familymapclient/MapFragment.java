package com.mcarpe12.familymapclient;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import familymap.Event;
import familymap.Person;

public class MapFragment extends Fragment
        implements OnMapReadyCallback,
        GoogleMap.OnMapLoadedCallback,
        GoogleMap.OnMarkerClickListener {
    public static final String ARG_TITLE = "title";
    public static final String TAG = "MapFragment";
    private GoogleMap map;

    private TextView mMapText;

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        View view = layoutInflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mMapText = view.findViewById(R.id.mapTextView);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        for (Event event : DataCache.getInstance().getEvents()) {
            LatLng eventLocation = new LatLng(event.getLatitude(), event.getLongitude());
            map.addMarker(new MarkerOptions().position(eventLocation).title(event.getEventID()));
        }

        map.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String eventID = marker.getTitle();
        Event event = DataCache.getInstance().findEvent(eventID);
        Person person = DataCache.getInstance().findPerson(event.getPersonID());
        String text = person.getFirstName() + " " + person.getLastName() + "\n"
                + event.getType().toUpperCase() + ": "
                + event.getCity() + ", " + event.getCountry()
                + " (" + event.getYear() + ")";
        mMapText.setText(text);

        int iconColor;
        if (person.getGender().equals("m")) {
            iconColor = R.color.male_icon;
        } else {
            iconColor = R.color.female_icon;
        }


        map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        return true;
    }

    @Override
    public void onMapLoaded() {

    }
}
