package com.mcarpe12.familymapclient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import familymap.AuthToken;
import familymap.Event;
import familymap.Person;
import request.LoginRequest;
import request.RegisterRequest;
import response.LoginResponse;
import response.RegisterResponse;
import response.Response;

public class LoginFragment extends Fragment {
    public static final String TAG = "LoginFragment";
    public static final String ARG_TITLE = "title";

    private EditText mHostField;
    private EditText mPortField;
    private EditText mUserNameField;
    private EditText mPasswordField;
    private EditText mFirstNameField;
    private EditText mLastNameField;
    private EditText mEmailField;
    private RadioGroup mGenderRadio;

    private String host = "";
    private String port = "";
    private String userName = "";
    private String password = "";
    private String firstName = "";
    private String lastName = "";
    private String email = "";
    private String gender = "";
    private String serverURL;

    private Button mSignInButton;
    private Button mRegisterButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        // "Sign in" and "Register" buttons disabled until user has entered correct info
        mSignInButton = v.findViewById(R.id.button_sign_in);
        mSignInButton.setEnabled(false);
        mRegisterButton = v.findViewById(R.id.button_register);
        mRegisterButton.setEnabled(false);

        // Find text fields
        mHostField = v.findViewById(R.id.text_server_host);
        mPortField = v.findViewById(R.id.text_server_port);
        mUserNameField = v.findViewById(R.id.text_user_name);
        mPasswordField = v.findViewById(R.id.text_password);
        mFirstNameField = v.findViewById(R.id.text_first_name);
        mLastNameField = v.findViewById(R.id.text_last_name);
        mEmailField = v.findViewById(R.id.text_email);
        mGenderRadio = v.findViewById(R.id.radioGroup_gender);

        addInputListeners();
        addButtonListeners();

        return v;
    }

    // Based on information given by user, allow user to click Login or Register button
    private void buttonsAvailable() {
        // If user has something input for host, port, username, and password fields,
        // allow them to click on the Login button.
        if (host.length() > 0 && port.length() > 0 && userName.length() > 0
                && password.length() > 0) {
            mSignInButton.setEnabled(true);

            // If user has also entered something for first name, last name, email,
            // and gender, allow them to click on the Register button.
            if (firstName.length() > 0 && lastName.length() > 0
                    && email.length() > 0 && gender.length() > 0) {
                mRegisterButton.setEnabled(true);
            } else {
                mRegisterButton.setEnabled(false);
            }
        } else {
            mSignInButton.setEnabled(false);
        }
    }

    private void addButtonListeners() {
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Set default server URL for connections
                    serverURL = host + ":" + port;
                    if (!serverURL.contains("http://")) {
                        serverURL = "http://" + serverURL;
                    }
                    // Create URL for login task
                    URL url = new URL(serverURL + "/user/login");

                    // Call async login task
                    LoginTask loginTask = new LoginTask();
                    loginTask.execute(url);

                } catch (IOException ex) {
                    Toast.makeText(getActivity(),
                            "error: internal server error",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Use input data to generate request
                    RegisterRequest registerReq = new RegisterRequest(userName, password, email,
                            firstName, lastName, gender);
                    // Use request to register with server
                    RegisterResponse registerRes = Proxy.register(host, port, registerReq);

                } catch (IOException ex) {
                    Toast.makeText(getActivity(),
                            "error: internal server error",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void addInputListeners() {
        // Add listener for Server Host Field
        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                host = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                buttonsAvailable();
            }
        };
        mHostField.addTextChangedListener(tw);

        // Add listener for Server Port Field
        tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                port = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                buttonsAvailable();
            }
        };
        mPortField.addTextChangedListener(tw);

        // Add listener for UserName Field
        tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                userName = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                buttonsAvailable();
            }
        };
        mUserNameField.addTextChangedListener(tw);

        // Add listener for Password Field
        tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                password = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                buttonsAvailable();
            }
        };
        mPasswordField.addTextChangedListener(tw);

        // Add listener for First Name Field
        tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                firstName = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                buttonsAvailable();
            }
        };
        mFirstNameField.addTextChangedListener(tw);

        // Add listener for Last Name Field
        tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                lastName = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                buttonsAvailable();
            }
        };
        mLastNameField.addTextChangedListener(tw);

        // Add listener for Email Field
        tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                email = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                buttonsAvailable();
            }
        };
        mEmailField.addTextChangedListener(tw);

        // Add listener for Gender Radio Buttons
        RadioGroup.OnCheckedChangeListener occl = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Determine which radio was checked
                RadioButton checked = mGenderRadio.findViewById(checkedId);
                int index = mGenderRadio.indexOfChild(checked);

                switch (index) {
                    // First radio ("male") was checked
                    case 0:
                        gender = "m";
                        break;
                    // Second radio ("female") was checked
                    case 1:
                        gender = "f";
                        break;
                }
            }
        };
        mGenderRadio.setOnCheckedChangeListener(occl);
    }

    // Async Task that logs the user into the server
    private class LoginTask extends AsyncTask<URL, Integer, LoginResponse> {

        @Override
        protected LoginResponse doInBackground(URL... urls) {
            try {
                LoginRequest loginReq = new LoginRequest(userName, password);
                LoginResponse loginRes = Proxy.login(urls[0], loginReq);
                return loginRes;
            } catch (IOException ex) {
                if (ex.getMessage() != null) {
                    Log.d(TAG, ex.getMessage());
                } else {
                    Log.d(TAG, "Error logging into the server");
                }
                return new LoginResponse("Error logging into the server", false);
            }
        }

        // After user has been logged in, call DataSync to get Events and Persons from the server
        @Override
        protected void onPostExecute(LoginResponse loginRes) {
            if (loginRes.getAuthToken() != null) {
                // Put auth token and PersonID into data cache for later use
                DataCache.getInstance().setAuthToken(loginRes.getAuthToken());
                DataCache.getInstance().setUserPersonID(loginRes.getPersonID());
                SyncDataTask dataTask = new SyncDataTask();
                dataTask.execute(serverURL);
            } else {
                Toast.makeText(getActivity(),
                        loginRes.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    // Gets Events and Persons associated with user from the server
    private class SyncDataTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                URL eventURL = new URL(serverURL + "/event");
                Event[] events = Proxy.getEvents(eventURL);
                if (events != null) {
                    DataCache.getInstance().setEvents(events);
                } else {
                    return false;
                }

                URL personURL = new URL(serverURL + "/person");
                Person[] persons = Proxy.getPersons(personURL);
                if (persons != null) {
                    DataCache.getInstance().setPersons(persons);
                } else {
                    return false;
                }

                URL onePersonURL = new URL(serverURL + "/person/" + DataCache.getInstance().getUserPersonID());
                Person userPerson = Proxy.getOnePerson(onePersonURL);
                if (userPerson != null) {
                    firstName = userPerson.getFirstName();
                    lastName = userPerson.getLastName();
                }

                return true;

            } catch (IOException ex) {
                if (ex.getMessage() != null) {
                    Log.d(TAG, ex.getMessage());
                } else {
                    Log.d(TAG, "Error retrieving data from the server");
                }
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(getActivity(),
                        "Welcome, " + firstName + " " + lastName,
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(),
                        "Error retrieving data from the server",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
