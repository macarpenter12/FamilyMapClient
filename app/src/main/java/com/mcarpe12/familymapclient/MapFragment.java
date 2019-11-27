package com.mcarpe12.familymapclient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.mcarpe12.familymapclient.service.DataCache;

import familymap.Event;
import familymap.Person;

public class MapFragment extends Fragment
        implements OnMapReadyCallback,
        GoogleMap.OnMapLoadedCallback,
        GoogleMap.OnMarkerClickListener {
    public static final String ARG_TITLE = "title";
    public static final String TAG = "MapFragment";
    public static final String EXTRA_LAYOUT_RESOURCE = "com.mcarpe12.familymapclient.map_layout";
    public static final String EXTRA_INIT_EVENT_ID = "com.mcarpe12.familymapclient.init_event_id";
    private GoogleMap map;
    private TextView mMapText;


    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        int layout = getArguments().getInt(EXTRA_LAYOUT_RESOURCE);
        View view = layoutInflater.inflate(layout, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mMapText = view.findViewById(R.id.mapTextView);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        for (Event event : DataCache.getInstance().getEvents()) {
            addEventMarker(event);
        }

        // Initialize view to given location (user's birth, unless specified)
        String initEventID = getArguments().getString(EXTRA_INIT_EVENT_ID);
        Event initEvent = DataCache.getInstance().findEvent(initEventID);
        LatLng initLocation = new LatLng(initEvent.getLatitude(), initEvent.getLongitude());
        map.animateCamera(CameraUpdateFactory.newLatLng(initLocation));

        map.setOnMarkerClickListener(this);
        mMapText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Event event = (Event) mMapText.getTag();
                        String personID = event.getPersonID();
                        Intent intent = new Intent(getActivity(), PersonActivity.class);
                        intent.putExtra(PersonActivity.EXTRA_PERSON_ID, personID);
                        startActivity(intent);
                    }
                }
        );
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Event event = (Event) marker.getTag();
        Person person = DataCache.getInstance().findPerson(event.getPersonID());
        String text = person.getFirstName() + " " + person.getLastName() + "\n"
                + event.getType().toUpperCase() + ": "
                + event.getCity() + ", " + event.getCountry()
                + " (" + event.getYear() + ")";
        mMapText.setText(text);

        int iconColor;
        FontAwesomeIcons iconGender;
        if (person.getGender().equals("m")) {
            iconColor = R.color.male_icon;
            iconGender = FontAwesomeIcons.fa_male;
        } else {
            iconColor = R.color.female_icon;
            iconGender = FontAwesomeIcons.fa_female;
        }

        Drawable genderIcon = new IconDrawable(getActivity(), iconGender)
                .colorRes(iconColor).sizeDp(40);
        mMapText.setCompoundDrawablesWithIntrinsicBounds(genderIcon, null, null, null);
        mMapText.setTag(event);

        map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        return true;
    }

    @Override
    public void onMapLoaded() {

    }

    private void addEventMarker(Event event) {
        LatLng eventLocation = new LatLng(event.getLatitude(), event.getLongitude());

        float color = DataCache.getInstance().getEventColor(event);
        Marker marker = map.addMarker(new MarkerOptions().position(eventLocation)
                .icon(BitmapDescriptorFactory.fromBitmap(getEventMarkerIcon(event)))
        );
        marker.setTag(event);
    }

    public Bitmap getEventMarkerIcon(Event event) {
        int color = DataCache.getInstance().getEventColor(event);
        Bitmap bitmap = null;

        FontAwesomeIcons iconMapMarker = FontAwesomeIcons.fa_map_marker;
        Drawable d = new IconDrawable(getActivity(), FontAwesomeIcons.fa_map_marker).colorRes(color);

        bitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);
        return bitmap;
    }
}
