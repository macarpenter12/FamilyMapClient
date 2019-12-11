package com.mcarpe12.familymapclient;

import com.google.gson.Gson;
import com.mcarpe12.familymapclient.service.DataCache;
import com.mcarpe12.familymapclient.service.Proxy;

import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import familymap.Person;
import familymap.User;
import request.LoadRequest;
import request.LoginRequest;
import request.RegisterRequest;
import response.LoginResponse;
import response.RegisterResponse;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class RelationshipTest {
    private String SERVER_URL = "http://localhost:8080";
    private String PASSWORD = "parker";
    private String FIRST_NAME = "Sheila";
    private String LAST_NAME = "Parker";
    private String EMAIL = "sparker@email.com";
    private String GENDER = "f";

    @Test
    public void relationship_valid() throws Exception {
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
        String onePersonURL = SERVER_URL + "/person/" + personID;
        url = new URL(onePersonURL);
        Person userPerson = Proxy.getOnePerson(url);
        Person father = null;
        Person mother = null;

        assertNotNull(userPerson);
        assertNotNull(persons);

        // Find father and mother
        for (Person person : persons) {
            if (person.getPersonID().equals(userPerson.getFatherID())) {
                father = person;
            } else if (person.getPersonID().equals(userPerson.getMotherID())) {
                mother = person;
            }
        }
        assertNotNull(father);
        assertNotNull(mother);

        // Father and mother are married to each other
        assertEquals(father.getSpouseID(), mother.getPersonID());
        assertEquals(mother.getSpouseID(), father.getPersonID());
        assertEquals(father.getLastName(), LAST_NAME);
    }

    @Test
    public void relationship_invalid() throws Exception {
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
        String onePersonURL = SERVER_URL + "/person/" + personID;
        url = new URL(onePersonURL);
        Person userPerson = Proxy.getOnePerson(url);

        // Should not generate spouse for user
        assertNull(userPerson.getSpouseID());

        // Attempt to find person with invalid personID
        String incorrectPersonID = UUID.randomUUID().toString();
        incorrectPersonID = incorrectPersonID.substring(0, 7);
        boolean match = false;
        for (Person person : persons) {
            if (person.getPersonID().equals(incorrectPersonID)) {
                match = true;
            }
        }
        assertFalse(match);
    }
}
