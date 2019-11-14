package com.mcarpe12.familymapclient;

import familymap.AuthToken;
import familymap.Event;
import familymap.Person;
import request.LoginRequest;
import request.RegisterRequest;
import response.LoginResponse;
import response.RegisterResponse;

public class Proxy {

    public LoginResponse login(LoginRequest req) {
        return null;
    }

    public RegisterResponse register(RegisterRequest req) {
        return null;
    }

    Event[] getEvents(AuthToken token) {
        return null;
    }

    Person[] getPersons(AuthToken token) {
        return null;
    }
}
