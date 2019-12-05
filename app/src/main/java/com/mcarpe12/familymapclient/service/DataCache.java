package com.mcarpe12.familymapclient.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.mcarpe12.familymapclient.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import familymap.Event;
import familymap.Person;

public class DataCache {
    private static DataCache instance;
    private Map<String, Event> eventsByEventID = new HashMap<>();
    private Map<String, List<Event>> eventsByPersonID = new HashMap<>();
    private Map<String, Person> personsByPersonID = new HashMap<>();
    private Map<String, Person> childrenByPersonID = new HashMap<>();
    private Event[] events;
    private Person[] persons;
    private String authToken;
    private String userPersonID;

    private HashMap<String, Integer> eventTypes = new HashMap<>();
    private int colorIndex = 0;
    private int[] markerColors = {
            R.color.mapmarker_green,
            R.color.mapmarker_yellow,
            R.color.mapmarker_black,
            R.color.mapmarker_blue,
            R.color.mapmarker_red,
            R.color.mapmarker_orange,
            R.color.mapmarker_purple,
            R.color.mapmarker_white,
            R.color.mapmarker_cyan,
            R.color.mapmarker_magenta,
            R.color.mapmerker_pink
    };

    private boolean lifeStoryLines = true;
    private boolean familyTreeLines = true;
    private boolean spouseLines = false;
    private boolean filter_father = true;
    private boolean filter_mother = true;
    private boolean filter_male = true;
    private boolean filter_female = true;

    public static DataCache getInstance() {
        if (instance == null) {
            instance = new DataCache();
        }

        return instance;
    }

    private DataCache() {

    }

    public Event[] getEvents() {
        return events;
    }

    public Event findEvent(String eventID) {
        return eventsByEventID.get(eventID);
    }

    public List<Event> getEventsByPerson(String personID) {
        if (eventsByPersonID.containsKey(personID)) {
            return eventsByPersonID.get(personID);
        } else {
            return null;
        }
    }

    public void setEvents(Event[] events) {
        this.events = events;

        for (Event event : events) {
            String key = event.getPersonID();

            // To find event by eventID
            eventsByEventID.put(event.getEventID(), event);

            // To find all events by personID
            if (eventsByPersonID.containsKey(key)) {
                eventsByPersonID.get(key).add(event);
            } else {
                List<Event> list = new ArrayList<>();
                list.add(event);
                eventsByPersonID.put(key, list);
            }
        }
    }

    public Person[] getPersons() {
        return persons;
    }

    public Person findPerson(String personID) {
        return personsByPersonID.get(personID);
    }

    /**
     * Finds the child of the given person with associated personID.
     *
     * @param personID ID of person, used to find child of that person.
     * @return Child of given parent.
     */
    public Person findChild(String personID) {
        return childrenByPersonID.get(personID);
    }

    public HashMap<String, Integer> getEventTypes() {
        return eventTypes;
    }

    public void setPersons(Person[] persons) {
        this.persons = persons;

        for (Person person : persons) {
            // Lookup person by ID
            personsByPersonID.put(person.getPersonID(), person);

            // Store children relationships
            if (person.getFatherID() != null) {
                childrenByPersonID.put(person.getFatherID(), person);
            }
            if (person.getMotherID() != null) {
                childrenByPersonID.put(person.getMotherID(), person);
            }
        }
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUserPersonID() {
        return userPersonID;
    }

    public void setUserPersonID(String userPersonID) {
        this.userPersonID = userPersonID;
    }

    /**
     * Check the event to see if its event type has been assigned a color. If so, return the
     * HUE_x member of BitmapDescriptorFactory that has been assigned. Else, assign it a color
     * and return that newly assigned color.
     *
     * @param event The event, containing the event.type to find a color for.
     * @return A float, corresponding to a BitmapDescriptorFactory.HUE value.
     */
    public int getEventColor(Event event) {
        if (!eventTypes.containsKey(event.getType())) {
            eventTypes.put(event.getType(), markerColors[colorIndex % markerColors.length]);
            colorIndex++;
        }

        return eventTypes.get(event.getType());
    }

    public Bitmap getEventMarkerIcon(Context context, Event event) {
        int color = getEventColor(event);

        FontAwesomeIcons iconMapMarker = FontAwesomeIcons.fa_map_marker;
        Drawable d = new IconDrawable(context, iconMapMarker).colorRes(color);

        Bitmap bitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);
        return bitmap;
    }
}
