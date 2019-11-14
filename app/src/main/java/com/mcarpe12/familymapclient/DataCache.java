package com.mcarpe12.familymapclient;

import familymap.Event;
import familymap.Person;

public class DataCache {
    private static DataCache instance;
    private Event[] events;
    private Person[] persons;

    public static DataCache getInstance() {
        if (instance == null) {
            instance = new DataCache();
        }

        return instance;
    }

    private DataCache() {

    }
}
