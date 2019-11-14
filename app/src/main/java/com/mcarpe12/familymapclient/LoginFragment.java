package com.mcarpe12.familymapclient;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {
    public static final String ARG_TITLE = "title";
    private EditText mHostField;
    private EditText mPortField;
    private EditText mUserNameField;
    private EditText mPasswordField;
    private Button mSignInButton;
    private Button mRegisterButton;
    private String host = "";
    private String port = "";
    private String userName = "";
    private String password = "";
    private String firstName = "";
    private String lastName = "";
    private String email = "";
    private String gender = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        // Sign in button disabled until user has entered correct info
        mSignInButton = (Button) v.findViewById(R.id.button_sign_in);
        mSignInButton.setEnabled(false);

        mHostField = (EditText) v.findViewById(R.id.text_server_host);



        return v;
    }

    private void addListener(EditText field, String var) {
        field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                var = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                buttonsAvailable();
            }
        });
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
}
