package com.mcarpe12.familymapclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Add the search fragment to the activity
        FragmentManager fm = this.getSupportFragmentManager();
        SearchListFragment searchListFragment = (SearchListFragment) fm.findFragmentById(R.id.fragment_container_main);
        if (searchListFragment == null) {
            searchListFragment = createSearchListFragment("SEARCH LIST FRAGMENT");
            fm.beginTransaction()
                    .add(R.id.fragment_container_search, searchListFragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private SearchListFragment createSearchListFragment(String title) {
        SearchListFragment fragment = new SearchListFragment();

        Bundle args = new Bundle();
        args.putString(SearchListFragment.ARG_TITLE, title);
        fragment.setArguments(args);

        return fragment;
    }
}
