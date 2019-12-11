package com.mcarpe12.familymapclient;

import com.mcarpe12.familymapclient.fragment.SearchListFragment;
import com.mcarpe12.familymapclient.service.DataCache;
import com.mcarpe12.familymapclient.service.Proxy;

import org.junit.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import familymap.Event;
import familymap.Person;
import request.LoginRequest;
import request.RegisterRequest;
import response.LoginResponse;
import response.RegisterResponse;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class SearchTest {
    private String SERVER_URL = "http://localhost:8080";
    private final String PASSWORD = "parker";
    private final String FIRST_NAME = "Sheila";
    private final String LAST_NAME = "Parker";
    private final String EMAIL = "sparker@email.com";
    private final String GENDER = "f";

    @Test
    public void searchPersons_valid() throws Exception {
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
        DataCache.getInstance().setAuthToken(token);
        String personID = logRes.getPersonID();
        String onePersonURL = SERVER_URL + "/person/" + personID;
        url = new URL(onePersonURL);
        Person person = Proxy.getOnePerson(url);
        assertNotNull(token);
        DataCache.getInstance().setUserPerson(person);

        // Get Events
        String eventURL = SERVER_URL + "/event";
        url = new URL(eventURL);
        Event[] events = Proxy.getEvents(url);
        assertNotNull(events);
        DataCache.getInstance().setEvents(events);

        // Get Persons
        String personsURL = SERVER_URL + "/person";
        url = new URL(personsURL);
        Person[] persons = Proxy.getPersons(url);
        assertNotNull(persons);
        DataCache.getInstance().setPersons(persons);

        // Search for our registered user
        List<Person> personList = SearchListFragment.searchPersons(FIRST_NAME);
        assertNotNull(personList);
        assertEquals(personList.get(0).getPersonID(), personID);
        assertEquals(personList.get(0).getFirstName(), FIRST_NAME);
        personList = SearchListFragment.searchPersons(LAST_NAME);
        assertNotNull(personList);
        assertEquals(personList.get(0).getPersonID(), personID);
        assertEquals(personList.get(0).getFirstName(), FIRST_NAME);
        assertEquals(personList.get(0).getLastName(), LAST_NAME);
    }

    @Test
    public void searchEvents_valid() throws Exception {
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
        DataCache.getInstance().setAuthToken(token);
        String personID = logRes.getPersonID();
        String onePersonURL = SERVER_URL + "/person/" + personID;
        url = new URL(onePersonURL);
        Person person = Proxy.getOnePerson(url);
        assertNotNull(token);
        DataCache.getInstance().setUserPerson(person);

        // Get Events
        String eventURL = SERVER_URL + "/event";
        url = new URL(eventURL);
        Event[] events = Proxy.getEvents(url);
        assertNotNull(events);
        DataCache.getInstance().setEvents(events);

        // Get Persons
        String personsURL = SERVER_URL + "/person";
        url = new URL(personsURL);
        Person[] persons = Proxy.getPersons(url);
        assertNotNull(persons);
        DataCache.getInstance().setPersons(persons);

        // Search within our search results for our user's events that exist in the DataCache
        List<Event> cacheEvents = DataCache.getInstance().getEventsByPerson(personID);
        List<Event> searchEvents = SearchListFragment.searchEvents(FIRST_NAME);
        boolean found = false;
        for (Event eCache : cacheEvents) {
            found = false;
            for (Event eSearch : searchEvents) {
                if (eCache.getEventID().equals(eSearch.getEventID())) {
                    found = true;
                }
            }
            assertTrue(found);
        }
    }

    @Test
    public void searchPersons_invalid() throws Exception {
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
        DataCache.getInstance().setAuthToken(token);
        String personID = logRes.getPersonID();
        String onePersonURL = SERVER_URL + "/person/" + personID;
        url = new URL(onePersonURL);
        Person person = Proxy.getOnePerson(url);
        assertNotNull(token);
        DataCache.getInstance().setUserPerson(person);

        // Get Events
        String eventURL = SERVER_URL + "/event";
        url = new URL(eventURL);
        Event[] events = Proxy.getEvents(url);
        assertNotNull(events);
        DataCache.getInstance().setEvents(events);

        // Get Persons
        String personsURL = SERVER_URL + "/person";
        url = new URL(personsURL);
        Person[] persons = Proxy.getPersons(url);
        assertNotNull(persons);
        DataCache.getInstance().setPersons(persons);

        // Search for invalid person, should return empty
        List<Person> personList = SearchListFragment.searchPersons("Not a valid person");
        assertNotNull(personList);
        assertFalse(personList.size() > 0);
    }

    @Test
    public void searchEvents_invalid() throws Exception {
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
        DataCache.getInstance().setAuthToken(token);
        String personID = logRes.getPersonID();
        String onePersonURL = SERVER_URL + "/person/" + personID;
        url = new URL(onePersonURL);
        Person person = Proxy.getOnePerson(url);
        assertNotNull(token);
        DataCache.getInstance().setUserPerson(person);

        // Get Events
        String eventURL = SERVER_URL + "/event";
        url = new URL(eventURL);
        Event[] events = Proxy.getEvents(url);
        assertNotNull(events);
        DataCache.getInstance().setEvents(events);

        // Get Persons
        String personsURL = SERVER_URL + "/person";
        url = new URL(personsURL);
        Person[] persons = Proxy.getPersons(url);
        assertNotNull(persons);
        DataCache.getInstance().setPersons(persons);

        // Search within our search results for our user's events that exist in the DataCache
        List<Event> cacheEvents = DataCache.getInstance().getEventsByPerson(personID);
        List<Event> searchEvents = SearchListFragment.searchEvents("This will not find any events");
        boolean found = false;
        for (Event eCache : cacheEvents) {
            found = false;
            for (Event eSearch : searchEvents) {
                if (eCache.getEventID().equals(eSearch.getEventID())) {
                    found = true;
                }
            }
            assertFalse(found);
        }
    }
}
