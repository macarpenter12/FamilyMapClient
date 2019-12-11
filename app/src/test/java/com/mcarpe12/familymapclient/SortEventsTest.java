package com.mcarpe12.familymapclient;

import com.mcarpe12.familymapclient.service.DataCache;
import com.mcarpe12.familymapclient.service.Proxy;

import org.junit.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import familymap.Event;
import familymap.Person;
import request.LoginRequest;
import request.RegisterRequest;
import response.LoginResponse;
import response.RegisterResponse;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class SortEventsTest {
    private String SERVER_URL = "http://localhost:8080";
    private final String PASSWORD = "parker";
    private final String FIRST_NAME = "Sheila";
    private final String LAST_NAME = "Parker";
    private final String EMAIL = "sparker@email.com";
    private final String GENDER = "f";

    @Test
    public void sortEvents_valid() throws Exception {
        // Register
        String userName = UUID.randomUUID().toString();
        userName = userName.substring(0, 7);
        String regURL = SERVER_URL + "/user/register";
        URL url = new URL(regURL);

        RegisterRequest regReq = new RegisterRequest(
                userName, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME, GENDER
        );
        RegisterResponse regRes = Proxy.register(url, regReq);

        // Login
        String loginURL = SERVER_URL + "/user/login";
        url = new URL(loginURL);
        LoginRequest logReq = new LoginRequest(userName, PASSWORD);
        LoginResponse logRes = Proxy.login(url, logReq);
        String token = logRes.getAuthToken();
        String personID = logRes.getPersonID();
        assertNotNull(token);
        DataCache.getInstance().setAuthToken(token);

        String personsURL = SERVER_URL + "/person";
        url = new URL(personsURL);
        Person[] persons = Proxy.getPersons(url);
        assertNotNull(persons);

        // Choose a random person (not the user, who only has birth event)
        // from the list to sort their events
        int rand = Math.abs(new Random().nextInt());
        Person sortPerson = persons[rand % persons.length];
        while (sortPerson.getPersonID().equals(personID)) {
            sortPerson = persons[rand % persons.length];
        }
        String sortPersonID = sortPerson.getPersonID();

        // Import events
        String eventURL = SERVER_URL + "/event";
        url = new URL(eventURL);
        Event[] events = Proxy.getEvents(url);
        assertNotNull(events);

        // Search events for those belonging to our randomly selected person.
        // Two ArrayLists, one is inserted forward and the other backward, to
        // eliminate the chance that we added the events in order already.
        List<Event> eventList = new ArrayList<>();
        List<Event> eventList2 = new ArrayList<>();
        for (Event event : events) {
            if (event.getPersonID().equals(sortPersonID)) {
                eventList.add(0, event);
                eventList2.add(event);
            }
        }

        // Sort the events
        eventList = DataCache.sortEvents(eventList);
        eventList2 = DataCache.sortEvents(eventList2);

        // Check that the events are in order
        // Birth, Marriage, Death
        assertEquals(eventList.get(0).getType(), "birth");
        assertEquals(eventList.get(1).getType(), "marriage");
        assertEquals(eventList.get(2).getType(), "death");
        assertEquals(eventList2.get(0).getType(), "birth");
        assertEquals(eventList2.get(1).getType(), "marriage");
        assertEquals(eventList2.get(2).getType(), "death");
    }

    @Test
    public void sortEvents_invalid() throws Exception {
        // Register
        String userName = UUID.randomUUID().toString();
        userName = userName.substring(0, 7);
        String regURL = SERVER_URL + "/user/register";
        URL url = new URL(regURL);

        RegisterRequest regReq = new RegisterRequest(
                userName, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME, GENDER
        );
        RegisterResponse regRes = Proxy.register(url, regReq);

        // Login
        String loginURL = SERVER_URL + "/user/login";
        url = new URL(loginURL);
        LoginRequest logReq = new LoginRequest(userName, PASSWORD);
        LoginResponse logRes = Proxy.login(url, logReq);
        String token = logRes.getAuthToken();
        String personID = logRes.getPersonID();
        assertNotNull(token);
        DataCache.getInstance().setAuthToken(token);

        String personsURL = SERVER_URL + "/person";
        url = new URL(personsURL);
        Person[] persons = Proxy.getPersons(url);
        assertNotNull(persons);

        // Choose a random person (not the user, who only has birth event)
        // from the list to sort their events
        int rand = Math.abs(new Random().nextInt());
        Person sortPerson = persons[rand % persons.length];
        while (sortPerson.getPersonID().equals(personID)) {
            sortPerson = persons[rand % persons.length];
        }
        String sortPersonID = sortPerson.getPersonID();

        // Import events
        String eventURL = SERVER_URL + "/event";
        url = new URL(eventURL);
        Event[] events = Proxy.getEvents(url);
        assertNotNull(events);
        DataCache.getInstance().setEvents(events);

        // Events have been loaded into our DataCache. Now we will attempt to sort
        // a null event list, which should not return any new events
        List<Event> eventList = null;
        eventList = DataCache.sortEvents(eventList);
        assertNull(eventList);

        // Attempt to sort an empty list, which should return empty
        eventList = new ArrayList<>();
        eventList = DataCache.sortEvents(eventList);
        boolean hasEvents = eventList.size() > 0;
        assertFalse(hasEvents);
    }
}
