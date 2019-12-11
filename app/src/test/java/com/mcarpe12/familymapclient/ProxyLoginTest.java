package com.mcarpe12.familymapclient;

import com.mcarpe12.familymapclient.service.DataCache;
import com.mcarpe12.familymapclient.service.Proxy;

import org.junit.Test;

import java.net.URL;

import familymap.Person;
import request.LoginRequest;
import response.LoginResponse;

import static junit.framework.TestCase.*;

public class ProxyLoginTest {
    private String SERVER_URL = "http://localhost:8080";
    private final String USERNAME = "sheila";
    private final String PASSWORD = "parker";
    private final String FIRST_NAME = "Sheila";
    private final String LAST_NAME = "Parker";

    @Test
    public void login_valid() throws Exception {
        String loginURL = SERVER_URL + "/user/login";
        URL url = new URL(loginURL);

        // Use login function of Proxy
        LoginRequest logReq = new LoginRequest(USERNAME, PASSWORD);
        LoginResponse logRes = Proxy.login(url, logReq);
        DataCache.getInstance().setAuthToken(logRes.getAuthToken());
        String token = logRes.getAuthToken();
        String personID = logRes.getPersonID();

        // Should have received auth token back from Proxy
        assertNotNull(logRes);
        assertNotNull(token);
        assertNotNull(personID);

        // Use auth token to get information from the server
        String queryURL = SERVER_URL + "/person/" + personID;
        url = new URL(queryURL);
        Person person = Proxy.getOnePerson(url);

        // Information we received should match
        assertNotNull(person);
        assertEquals(FIRST_NAME, person.getFirstName());
        assertEquals(LAST_NAME, person.getLastName());
    }

    @Test
    public void login_invalid() throws Exception {
        String loginURL = SERVER_URL + "/user/login";
        URL url = new URL(loginURL);

        // Use login function of Proxy
        LoginRequest logReq1 = new LoginRequest("incorrect", PASSWORD);
        LoginRequest logReq2 = new LoginRequest(USERNAME, "incorrect");
        LoginRequest logReq3 = new LoginRequest(USERNAME.toUpperCase(), PASSWORD);
        LoginRequest logReq5 = new LoginRequest(USERNAME, PASSWORD.toUpperCase());

        // Login Response should not be null, but its data members should be
        LoginResponse logRes = Proxy.login(url, logReq1);
        assertNull(logRes.getAuthToken());

        logRes = Proxy.login(url, logReq2);
        assertNull(logRes.getPersonID());

        logRes = Proxy.login(url, logReq3);
        assertNull(logRes.getUserName());

        logRes = Proxy.login(url, logReq5);
        String message = logRes.getMessage();
        boolean error = message.contains("error");
        assertNotNull(message);
        assertTrue(error);
    }
}
