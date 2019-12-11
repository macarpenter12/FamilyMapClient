package com.mcarpe12.familymapclient;

import com.mcarpe12.familymapclient.service.DataCache;
import com.mcarpe12.familymapclient.service.Proxy;

import org.junit.Test;

import java.net.URL;
import java.util.UUID;

import familymap.Event;
import request.LoginRequest;
import request.RegisterRequest;
import response.LoginResponse;
import response.RegisterResponse;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNull;

public class GetEventsTest {
    private String SERVER_URL = "http://localhost:8080";
    private final String PASSWORD = "parker";
    private final String FIRST_NAME = "Sheila";
    private final String LAST_NAME = "Parker";
    private final String EMAIL = "sparker@email.com";
    private final String GENDER = "f";

    @Test
    public void getEvents_valid() throws Exception {
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
        assertNotNull(token);
        DataCache.getInstance().setAuthToken(token);

        String eventURL = SERVER_URL + "/event";
        url = new URL(eventURL);
        Event[] events = Proxy.getEvents(url);

        // Find an event that matches the user's info
        String personID = logRes.getPersonID();
        boolean match = false;
        for (Event event : events) {
            if (event.getPersonID().equals(personID)) {
                match = true;
            }
        }

        assertTrue(match);
    }

    @Test
    public void getEvents_invalid() throws Exception {
       // Attempt to get events without an auth token
        DataCache.getInstance().setAuthToken(null);
        String eventURL = SERVER_URL + "/event";
        URL url = new URL(eventURL);
        Event[] events = Proxy.getEvents(url);
        assertNull(events);

        // Attempt to get events with an incorrect auth token
        DataCache.getInstance().setAuthToken("Not a real auth token");
        events = Proxy.getEvents(url);
        assertNull(events);
    }
}
