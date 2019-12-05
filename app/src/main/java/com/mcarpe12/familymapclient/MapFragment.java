package com.mcarpe12.familymapclient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.mcarpe12.familymapclient.service.DataCache;

import java.util.ArrayList;
import java.util.List;

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
    private String context;
    private GoogleMap map;
    private TextView mMapText;
    private List<Polyline> polylines = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        int layout = getArguments().getInt(EXTRA_LAYOUT_RESOURCE);
        if (layout == R.layout.fragment_event) {
            context = "event";
            setHasOptionsMenu(false);
        } else {
            context = "main";
            setHasOptionsMenu(true);
        }
        View view = layoutInflater.inflate(layout, container, false);
        // TODO: Use a keyword to determine the context of this fragment, then make changes accordingly.

        // Determine the ID of the map fragment contained in the layout we are using
        int fragmentID = 0;
        if (context.equals("event")) {
            fragmentID = R.id.eventViewMap;
        } else {
            fragmentID = R.id.map;
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(fragmentID);
        mapFragment.getMapAsync(this);

        // Determine the ID of the TextView at the bottom of the fragment based on the layout we are using
        int textViewID = 0;
        if (context.equals("event")) {
            textViewID = R.id.eventTextView;
        } else {
            textViewID = R.id.mapTextView;
        }
        mMapText = view.findViewById(textViewID);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.searchMenuItem);
        searchMenuItem.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_search)
                .colorRes(R.color.menu_icon_white)
                .actionBarSize());
        MenuItem settingsMenuItem = menu.findItem(R.id.settingsMenuItem);
        settingsMenuItem.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_gear)
                .colorRes(R.color.menu_icon_white)
                .actionBarSize());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.searchMenuItem:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.settingsMenuItem:
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }
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
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(initLocation, 5));

        // If this is an event activity, set the footer to the current event
        if (context.equals("event")) {
            setMapText(initEvent);
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
        setMapText((Event) marker.getTag());
        removeLines();
        drawLines((Event) marker.getTag(), 8);

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

    private void setMapText(Event event) {
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
    }

    private void drawLines(Event event, int width) {
        if (event == null) {
            return;
        }

        Person person = DataCache.getInstance().findPerson(event.getPersonID());

        // Draw spouse line
        Event sBirth = getBirth(person.getSpouseID());
        drawOneLine(event, sBirth, width, Color.GREEN);

        // Draw father line
        Event fBirth = getBirth(person.getFatherID());
        drawOneLine(event, fBirth, width, Color.BLUE);

        // Draw mother line
        Event mBirth = getBirth(person.getMotherID());
        drawOneLine(event, mBirth, width, Color.RED);

        // Recursively draw parent lines
        drawLines(fBirth, width - 2);
        drawLines(mBirth, width - 2);
    }

    private void drawOneLine(Event event1, Event event2, int width, int color) {
        if (event1 == null || event2 == null) {
            return;
        }
        if (width < 0) {
            width = 2;
        }

        Polyline line = map.addPolyline(new PolylineOptions()
                .add(new LatLng(event1.getLatitude(), event1.getLongitude()),
                        new LatLng(event2.getLatitude(), event2.getLongitude()))
                .width(width)
                .color(color));
        polylines.add(line);
    }

    private void removeLines() {
        for (Polyline line : polylines) {
            line.remove();
        }
        polylines.clear();
    }

    /**
     * Get given Person's birth event. If there is no birth, get the first chronological event.
     * Reuses PersonActivity's sorting method to organize life events.
     *
     * @param personID ID of person to get birth event for.
     * @return Birth event (or first chronological event) of given person.
     */
    private Event getBirth(String personID) {
        Event birth = null;
        List<Event> lifeEvents = DataCache.getInstance().getEventsByPerson(personID);
        if (lifeEvents != null) {
            List<Event> eventList = new ArrayList<>(lifeEvents);
            eventList = PersonActivity.sortEvents(eventList);
            if (eventList.size() > 0) {
                birth = eventList.get(0);
            }
        }
        return birth;
    }
}
