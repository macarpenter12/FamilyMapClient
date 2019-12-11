package com.mcarpe12.familymapclient;

import com.mcarpe12.familymapclient.service.DataCache;
import com.mcarpe12.familymapclient.service.Proxy;

import org.junit.Test;


import java.net.URL;
import java.util.UUID;

import familymap.Person;
import request.LoginRequest;
import request.RegisterRequest;
import response.LoginResponse;
import response.RegisterResponse;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNull;

public class GetPersonsTest {
    private String SERVER_URL = "http://localhost:8080";
    private String PASSWORD = "parker";
    private String FIRST_NAME = "Sheila";
    private String LAST_NAME = "Parker";
    private String EMAIL = "sparker@email.com";
    private String GENDER = "f";

    @Test
    public void getPersons_valid() throws Exception {
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

        String personURL = SERVER_URL + "/person";
        url = new URL(personURL);
        Person[] persons = Proxy.getPersons(url);

        // Find the person that matches user's info
        boolean match = false;
        for (int i = 0; i < persons.length; ++i) {
            if (persons[i].getFirstName().equals(FIRST_NAME)
                    && persons[i].getLastName().equals(LAST_NAME)) {
                match = true;
                persons[i] = null;
                break;
            }
        }
        assertTrue(match);
        match = false;

        // Find another person with the same last name
        for (Person person : persons) {
            if (person != null) {
                if (person.getLastName().equals(LAST_NAME)) {
                    match = true;
                    break;
                }
            }
        }
        assertTrue(match);
    }

    @Test
    public void getPersons_invalid() throws Exception {
        // Attempt to get persons without an auth token
        DataCache.getInstance().setAuthToken(null);
        String personURL = SERVER_URL + "/person";
        URL url = new URL(personURL);
        Person[] persons = Proxy.getPersons(url);
        assertNull(persons);

        // Attempt to get persons with an incorrect auth token
        DataCache.getInstance().setAuthToken("This token will not work");
        persons = Proxy.getPersons(url);
        assertNull(persons);
    }
}
