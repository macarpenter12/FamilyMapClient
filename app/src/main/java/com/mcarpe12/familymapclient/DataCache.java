package com.mcarpe12.familymapclient;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import familymap.Event;
import familymap.Person;

public class DataCache {
    private static DataCache instance;
    private Map<String, Event> eventMap = new HashMap<>();
    private Map<String, List<Event>> eventsByPerson = new HashMap<>();
    private Map<String, Person> personMap = new HashMap<>();
    private Map<String, Person> childrenMap = new HashMap<>();
    private Event[] events;
    private Person[] persons;
    private String authToken;
    private String userPersonID;

    private HashMap<String, Float> eventTypes = new HashMap<>();
    int colorIndex = 0;
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
        return eventMap.get(eventID);
    }

    public List<Event> getEventsByPerson(String personID) {
        return eventsByPerson.get(personID);
    }

    public void setEvents(Event[] events) {
        this.events = events;

        for (Event event : events) {
            String key = event.getPersonID();

            // To find event by eventID
            eventMap.put(event.getEventID(), event);

            // To find all events by personID
            if (eventsByPerson.containsKey(key)) {
                eventsByPerson.get(key).add(event);
            } else {
                List<Event> list = new ArrayList<>();
                list.add(event);
                eventsByPerson.put(key, list);
            }
        }
    }

    public Person[] getPersons() {
        return persons;
    }

    public Person findPerson(String personID) {
        return personMap.get(personID);
    }

    /**
     * Finds the child of the given person with associated personID.
     *
     * @param personID ID of person, used to find child of that person.
     * @return Child of given parent.
     */
    public Person findChild(String personID) {
        return childrenMap.get(personID);
    }

    public void setPersons(Person[] persons) {
        this.persons = persons;

        for (Person person : persons) {
            // Lookup person by ID
            personMap.put(person.getPersonID(), person);

            // Store children relationships
            if (person.getFatherID() != null) {
                childrenMap.put(person.getFatherID(), person);
            }
            if (person.getMotherID() != null) {
                childrenMap.put(person.getMotherID(), person);
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
    public float getEventColor(Event event) {
        if (!eventTypes.containsKey(event.getType())) {
            eventTypes.put(event.getType(), markerColors[colorIndex % markerColors.length]);
            colorIndex++;
        }

        return eventTypes.get(event.getType());
    }
}
