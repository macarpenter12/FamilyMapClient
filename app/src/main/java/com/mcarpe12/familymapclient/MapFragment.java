package com.mcarpe12.familymapclient;

import android.content.Intent;
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

import java.util.HashMap;
import java.util.Map;

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

    private Map<String, Float> eventTypes = new HashMap<>();
    int index = 0;
    private float markerColors[] = {
            BitmapDescriptorFactory.HUE_GREEN,
            BitmapDescriptorFactory.HUE_AZURE,
            BitmapDescriptorFactory.HUE_RED,
            BitmapDescriptorFactory.HUE_YELLOW,
            BitmapDescriptorFactory.HUE_VIOLET,
            BitmapDescriptorFactory.HUE_ORANGE,
            BitmapDescriptorFactory.HUE_CYAN,
            BitmapDescriptorFactory.HUE_ROSE,
            BitmapDescriptorFactory.HUE_MAGENTA
    };

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
            addEventMarker(event);
        }

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

        float color = getEventColor(event);
        Marker marker = map.addMarker(new MarkerOptions().position(eventLocation)
                .icon(BitmapDescriptorFactory.defaultMarker(color))
        );
        marker.setTag(event);
    }

    /**
     * Check the event to see if its event type has been assigned a color. If so, return the
     * HUE_x member of BitmapDescriptorFactory that has been assigned. Else, assign it a color
     * and return that newly assigned color.
     *
     * @param event The event, containing the event.type to find a color for.
     * @return A float, corresponding to a BitmapDescriptorFactory.HUE value.
     */
    private float getEventColor(Event event) {
        if (!eventTypes.containsKey(event.getType())) {
            eventTypes.put(event.getType(), markerColors[index % markerColors.length]);
            index++;
        }

        return eventTypes.get(event.getType());
    }
}
