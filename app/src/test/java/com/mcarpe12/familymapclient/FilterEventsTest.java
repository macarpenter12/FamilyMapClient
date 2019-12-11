package com.mcarpe12.familymapclient;

import com.mcarpe12.familymapclient.service.DataCache;
import com.mcarpe12.familymapclient.service.Proxy;

import org.junit.Test;

import java.net.URL;
import java.util.Arrays;
import java.util.UUID;

import familymap.Event;
import familymap.Person;
import request.LoginRequest;
import request.RegisterRequest;
import response.LoginResponse;
import response.RegisterResponse;
import java.util.List;
import java.util.ArrayList;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertFalse;

public class FilterEventsTest {
    private String SERVER_URL = "http://localhost:8080";
    private final String PASSWORD = "parker";
    private final String FIRST_NAME = "Sheila";
    private final String LAST_NAME = "Parker";
    private final String EMAIL = "sparker@email.com";
    private final String GENDER = "f";

    @Test
    public void filterEvents_male() throws Exception {
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
        String onePersonURL = SERVER_URL + "/person/" + personID;
        url = new URL(onePersonURL);
        Person person = Proxy.getOnePerson(url);
        assertNotNull(token);
        DataCache.getInstance().setAuthToken(token);
        DataCache.getInstance().setUserPerson(person);

        // Get Events
        String eventURL = SERVER_URL + "/event";
        url = new URL(eventURL);
        Event[] events = Proxy.getEvents(url);
        assertNotNull(events);
        List<Event> eventList = new ArrayList<>(Arrays.asList(events));

        // Get Persons
        String personsURL = SERVER_URL + "/person";
        url = new URL(personsURL);
        Person[] persons = Proxy.getPersons(url);
        assertNotNull(persons);
        DataCache.getInstance().setPersons(persons);

        // Filter out male events
        DataCache.getInstance().setFilterFemaleEvents(true);
        DataCache.getInstance().setFilterMaleEvents(false);
        eventList = DataCache.getInstance().applyEventFilters(eventList);
        boolean match = false;
        // Search for male events
        for (Event event : eventList) {
            if (DataCache.getInstance().findPerson(event.getPersonID())
                    .getGender().equals("m")) {
                match = true;
            }
        }
        assertFalse(match);
    }

    @Test
    public void filterEvents_female() throws Exception {
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
        String onePersonURL = SERVER_URL + "/person/" + personID;
        url = new URL(onePersonURL);
        Person person = Proxy.getOnePerson(url);
        assertNotNull(token);
        DataCache.getInstance().setAuthToken(token);
        DataCache.getInstance().setUserPerson(person);

        // Get Events
        String eventURL = SERVER_URL + "/event";
        url = new URL(eventURL);
        Event[] events = Proxy.getEvents(url);
        assertNotNull(events);
        List<Event> eventList = new ArrayList<>(Arrays.asList(events));

        // Get Persons
        String personsURL = SERVER_URL + "/person";
        url = new URL(personsURL);
        Person[] persons = Proxy.getPersons(url);
        assertNotNull(persons);
        DataCache.getInstance().setPersons(persons);

        // Filter out female events
        DataCache.getInstance().setFilterMaleEvents(true);
        DataCache.getInstance().setFilterFemaleEvents(false);
        eventList = DataCache.getInstance().applyEventFilters(eventList);
        boolean match = false;
        // Search for female events
        for (Event event : eventList) {
            if (DataCache.getInstance().findPerson(event.getPersonID()).getGender().equals("f")) {
                match = true;
            }
        }
        assertFalse(match);
    }
}
