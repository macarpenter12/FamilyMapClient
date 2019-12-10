package com.mcarpe12.familymapclient;

import com.mcarpe12.familymapclient.service.DataCache;
import com.mcarpe12.familymapclient.service.Proxy;

import org.junit.Test;

import java.net.URL;

import familymap.Person;
import request.RegisterRequest;
import response.RegisterResponse;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNull;

public class ProxyRegisterTest {
    private String SERVER_URL = "http://localhost:8080";
    private final String USERNAME = "bob";
    private final String PASSWORD = "parker";
    private final String EMAIL = "bparker@email.com";
    private final String FIRST_NAME = "Bob";
    private final String LAST_NAME = "Parker";
    private final String GENDER = "m";

    @Test
    public void register_valid() throws Exception {
        String registerURL = SERVER_URL + "/user/register";
        URL url = new URL(registerURL);

        RegisterRequest registerReq = new RegisterRequest(
                USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME, GENDER
        );
        RegisterResponse registerRes = Proxy.register(url, registerReq);
        DataCache.getInstance().setAuthToken(registerRes.getAuthToken());
        String token = registerRes.getAuthToken();
        String personID = registerRes.getPersonID();

        assertNotNull(registerRes);
        assertNotNull(token);
        assertNotNull(personID);

        String queryURL = SERVER_URL + "/person/" + personID;
        url = new URL(queryURL);
        Person person = Proxy.getOnePerson(url);

        assertNotNull(person);
        assertEquals(FIRST_NAME, person.getFirstName());
        assertEquals(LAST_NAME, person.getLastName());
    }

    @Test
    public void register_invalid() throws Exception {
        String taken_username = "sheila";

        String registerURL = SERVER_URL + "/user/register";
        URL url = new URL(registerURL);

        // Should return null: username is already taken
        RegisterRequest registerReq = new RegisterRequest(
                taken_username, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME, GENDER
        );
        RegisterResponse registerRes = Proxy.register(url, registerReq);
        DataCache.getInstance().setAuthToken(registerRes.getAuthToken());
        String token = registerRes.getAuthToken();
        String personID = registerRes.getPersonID();

        assertNotNull(registerRes);
        assertNull(token);
        assertNull(personID);
    }
}
