package com.mcarpe12.familymapclient.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.mcarpe12.familymapclient.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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

    private Person userPerson;

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
    private boolean filterFatherSide = true;
    private boolean filterMotherSide = true;
    private boolean filterMaleEvents = true;
    private boolean filterFemaleEvents = true;

    public static DataCache getInstance() {
        if (instance == null) {
            instance = new DataCache();
        }

        return instance;
    }

    public static void clear() {
        instance = new DataCache();
    }

    private DataCache() {

    }

    public boolean isLifeStoryLines() {
        return lifeStoryLines;
    }

    public void setLifeStoryLines(boolean lifeStoryLines) {
        this.lifeStoryLines = lifeStoryLines;
    }

    public boolean isFamilyTreeLines() {
        return familyTreeLines;
    }

    public void setFamilyTreeLines(boolean familyTreeLines) {
        this.familyTreeLines = familyTreeLines;
    }

    public boolean isSpouseLines() {
        return spouseLines;
    }

    public void setSpouseLines(boolean spouseLines) {
        this.spouseLines = spouseLines;
    }

    public boolean isFilterFatherSide() {
        return filterFatherSide;
    }

    public void setFilterFatherSide(boolean filterFatherSide) {
        this.filterFatherSide = filterFatherSide;
    }

    public boolean isFilterMotherSide() {
        return filterMotherSide;
    }

    public void setFilterMotherSide(boolean filterMotherSide) {
        this.filterMotherSide = filterMotherSide;
    }

    public boolean isFilterMaleEvents() {
        return filterMaleEvents;
    }

    public void setFilterMaleEvents(boolean filterMaleEvents) {
        this.filterMaleEvents = filterMaleEvents;
    }

    public boolean isFilterFemaleEvents() {
        return filterFemaleEvents;
    }

    public void setFilterFemaleEvents(boolean filterFemaleEvents) {
        this.filterFemaleEvents = filterFemaleEvents;
    }

    public List<Event> getEvents() {
        return Arrays.asList(events);
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

    public static List<Event> sortEvents(List<Event> events) {
        if (events == null) {
            return null;
        }
        ArrayList<Event> eventsCopy = new ArrayList<>(events);

        // Ensure that birth event is always first and death event is always last
        Event birth = null;
        Event death = null;
        for (Event event : eventsCopy) {
            if (event.getType().toLowerCase().equals("birth")) {
                birth = event;
            }
            if (event.getType().toLowerCase().equals("death")) {
                death = event;
            }
        }
        eventsCopy.remove(birth);
        eventsCopy.remove(death);

        List<Event> sortedEvents = new ArrayList<>();
        if (birth != null) {
            sortedEvents.add(birth);
        }

        while (eventsCopy.size() > 0) {
            Event minEvent = eventsCopy.get(0);
            if (minEvent == null) {
                if (death != null) {
                    sortedEvents.add(death);
                }
                return sortedEvents;
            }
            for (Event event : eventsCopy) {
                if (event.getYear() < minEvent.getYear()) {
                    minEvent = event;
                } else if (event.getYear() == minEvent.getYear()) {
                    if (event.getType().compareToIgnoreCase(minEvent.getType()) == -1) {
                        minEvent = event;
                    }
                }
            }
            eventsCopy.remove(minEvent);
            sortedEvents.add(minEvent);
        }
        if (death != null) {
            sortedEvents.add(death);
        }
        return sortedEvents;
    }

    /**
     * Applies the active filters to the given set of events, filtering them by
     *
     * @param events
     */
    public List<Event> applyEventFilters(List<Event> events) {
        Person father = findPerson(DataCache.getInstance().getUserPerson().getFatherID());
        Person mother = findPerson(DataCache.getInstance().getUserPerson().getMotherID());

        if (!DataCache.getInstance().isFilterFatherSide()) {
            events = removeAncestorEvents(father, events);
        }
        if (!DataCache.getInstance().isFilterMotherSide()) {
            events = removeAncestorEvents(mother, events);
        }
        if (!DataCache.getInstance().isFilterMaleEvents()) {
            events = removeEventsByGender("m", events);
        }
        if (!DataCache.getInstance().isFilterFemaleEvents()) {
            events = removeEventsByGender("f", events);
        }
        return events;
    }

    /**
     * Recursively removes events of the given person and the events of that peron's ancestors
     *
     * @param person Person for whom to delete their events and their ancestor events
     * @param events List of events to filter
     * @return Filtered list of events after ancestor events have been removed
     */
    public List<Event> removeAncestorEvents(Person person, List<Event> events) {
        if (events == null || person == null) {
            return events;
        }

        // Remove the given person's events
        List<Event> eventsToRemove = new ArrayList<>();
        for (Event event : events) {
            if (event.getPersonID().equals(person.getPersonID())) {
                eventsToRemove.add(event);
            }
        }
        events.removeAll(eventsToRemove);

        // Recursively remove all ancestor events
        Person father = DataCache.getInstance().findPerson(person.getFatherID());
        Person mother = DataCache.getInstance().findPerson(person.getMotherID());
        events = removeAncestorEvents(father, events);
        events = removeAncestorEvents(mother, events);
        return events;
    }

    public List<Event> removeEventsByGender(String gender, List<Event> events) {
        if (events == null) {
            return null;
        }
        gender = gender.toLowerCase();

        // Remove any events associated with persons of given gender
        List<Event> eventsToRemove = new ArrayList<>();
        for (Event event : events) {
            if (findPerson(event.getPersonID()).getGender().equals(gender)) {
                eventsToRemove.add(event);
            }
        }
        events.removeAll(eventsToRemove);

        return events;
    }

    public List<Person> getPersons() {
        return Arrays.asList(persons);
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
        Person child = childrenByPersonID.get(personID);
        if (child == null) {
             return null;
        }

        if (child.getGender().equals("m") && isFilterMaleEvents()) {
            return child;
        } else if (child.getGender().equals("f") && isFilterFemaleEvents()) {
            return child;
        } else {
            return null;
        }
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

    public List<Person> applyPersonFilters(List<Person> persons) {
        Person father = findPerson(DataCache.getInstance().getUserPerson().getFatherID());
        Person mother = findPerson(DataCache.getInstance().getUserPerson().getMotherID());

        if (!DataCache.getInstance().isFilterFatherSide()) {
            persons = removeAncestors(father, persons);
        }
        if (!DataCache.getInstance().isFilterMotherSide()) {
            persons = removeAncestors(mother, persons);
        }
        if (!DataCache.getInstance().isFilterMaleEvents()) {
            persons = removePersonsByGender("m", persons);
        }
        if (!DataCache.getInstance().isFilterFemaleEvents()) {
            persons = removePersonsByGender("f", persons);
        }

        return persons;
    }

    public List<Person> removeAncestors(Person person, List<Person> persons) {
        if (person == null || persons == null) {
            return persons;
        }

        persons.remove(person);

        // Recursively remove all ancestors
        Person father = DataCache.getInstance().findPerson(person.getFatherID());
        Person mother = DataCache.getInstance().findPerson(person.getMotherID());
        removeAncestors(father, persons);
        removeAncestors(mother, persons);

        return persons;
    }

    public List<Person> removePersonsByGender(String gender, List<Person> persons) {
        if (persons == null) {
            return null;
        }
        gender = gender.toLowerCase();

        // Remove all persons matching given gender
        List<Person> personsToRemove = new ArrayList<>();
        for(Person p : persons) {
            if (p.getGender().equals(gender)) {
                personsToRemove.add(p);
            }
        }
        persons.removeAll(personsToRemove);

        return persons;
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

    public Person getUserPerson() {
        return userPerson;
    }

    public void setUserPerson(Person userPerson) {
        this.userPerson = userPerson;
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
