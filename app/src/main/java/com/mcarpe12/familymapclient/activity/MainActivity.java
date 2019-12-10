package com.mcarpe12.familymapclient.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.mcarpe12.familymapclient.fragment.LoginFragment;
import com.mcarpe12.familymapclient.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Iconify.with(new FontAwesomeModule());

        // Add the login fragment to the activity
        FragmentManager fm = this.getSupportFragmentManager();
        LoginFragment loginFragment = (LoginFragment) fm.findFragmentById(R.id.fragment_container_main);
        if (loginFragment == null) {
            loginFragment = createLoginFragment("LOGIN FRAGMENT");
            fm.beginTransaction()
                    .add(R.id.fragment_container_main, loginFragment)
                    .commit();
        }
    }

    private LoginFragment createLoginFragment(String title) {
        LoginFragment fragment = new LoginFragment();

        Bundle args = new Bundle();
        args.putString(LoginFragment.ARG_TITLE, title);
        fragment.setArguments(args);

        return fragment;
    }
}
