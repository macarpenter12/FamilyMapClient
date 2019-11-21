package com.mcarpe12.familymapclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import familymap.Event;
import familymap.Person;

public class DataCache {
    private static DataCache instance;
    private Map<String, List<Event>> eventsByPerson = new HashMap<>();
    private Map<String, Person> personMap = new HashMap<>();
    private Event[] events;
    private Person[] persons;
    private String authToken;
    private String userPersonID;

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

    public void setEvents(Event[] events) {
        this.events = events;

        for (Event event : events) {
            String key = event.getPersonID();
            if (eventsByPerson.containsKey(key)) {
                eventsByPerson.get(key).add(event);
            } else {
                List<Event> list = new ArrayList<>();
                list.add(event);
                eventsByPerson.put(key, list);
            }
        }
    }

    public Event[] getEventsByPerson(String personID) {
        return (Event[]) (eventsByPerson.get(personID).toArray());
    }

    public Person[] getPersons() {
        return persons;
    }

    public Person findPerson(String personID) {
        return personMap.get(personID);
    }

    public void setPersons(Person[] persons) {
        this.persons = persons;

        for (Person person : persons) {
            personMap.put(person.getPersonID(), person);
        }

//        // Add references to children
//        for (Map.Entry<String, Person> entry : personMap.entrySet()) {
//            Person person = entry.getValue();
//            if (person.getFatherID() != null) {
//
//            }
//            if (person.getMotherID() != null) {
//
//            }
//        }
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
}
