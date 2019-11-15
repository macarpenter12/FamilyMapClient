package com.mcarpe12.familymapclient;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import familymap.Event;
import familymap.Person;
import request.LoginRequest;
import request.RegisterRequest;
import response.DataResponse;
import response.EventResponse;
import response.LoginResponse;
import response.PersonResponse;
import response.RegisterResponse;

public class Proxy {


    public static LoginResponse login(URL url, LoginRequest req) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(5000);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.connect();

        String reqData = serialize(req);
        OutputStream reqBody = connection.getOutputStream();
        writeString(reqData, reqBody);

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream resBody = connection.getInputStream();
            String resData = readString(resBody);
            LoginResponse loginRes = deserialize(resData, LoginResponse.class);
            return loginRes;
        } else {
            return new LoginResponse("error: " + connection.getResponseCode(), false);
        }
    }

    public static RegisterResponse register(String host, String port, RegisterRequest req) throws IOException {
        return null;
    }

    public static Event[] getEvents(URL url) throws IOException {
        String token = DataCache.getInstance().getAuthToken();

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(5000);
        connection.setRequestMethod("GET");
        connection.addRequestProperty("Authorization", token);
        connection.connect();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream resBody = connection.getInputStream();
            String resData = readString(resBody);
            EventResponse eventRes = deserialize(resData, EventResponse.class);
            Event[] events = eventRes.getData();
            return events;
        } else {
            return null;
        }
    }

    public static Person[] getPersons(URL url) throws IOException {
        String token = DataCache.getInstance().getAuthToken();

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(5000);
        connection.setRequestMethod("GET");
        connection.addRequestProperty("Authorization", token);
        connection.connect();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream resBody = connection.getInputStream();
            String resData = readString(resBody);
            PersonResponse personRes = deserialize(resData, PersonResponse.class);
            Person[] persons = personRes.getData();
            return persons;
        } else {
            return null;
        }
    }

    public static Person getOnePerson(URL url) throws IOException {
        String token = DataCache.getInstance().getAuthToken();

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(5000);
        connection.setRequestMethod("GET");
        connection.addRequestProperty("Authorization", token);
        connection.connect();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream resBody = connection.getInputStream();
            String resData = readString(resBody);
            Person personRes = deserialize(resData, Person.class);
            return personRes;
        } else {
            return null;
        }
    }


    public static <T> T deserialize(String value, Class<T> returnType) {
        return (new Gson()).fromJson(value, returnType);
    }

    public static String serialize(Object obj) {
        return (new Gson()).toJson(obj);
    }

    public static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(sw);
        bw.write(str);
        bw.flush();
    }

    public static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }
}
