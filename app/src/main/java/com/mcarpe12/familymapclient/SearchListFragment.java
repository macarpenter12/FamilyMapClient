package com.mcarpe12.familymapclient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.mcarpe12.familymapclient.service.DataCache;

import java.util.ArrayList;
import java.util.List;

import familymap.Event;
import familymap.Person;

public class SearchListFragment extends Fragment
        implements SearchView.OnQueryTextListener {
    public static final String ARG_TITLE = "title";

    private SearchView mSearchView;
    private RecyclerView mSearchRecyclerView;
    private SearchAdapter mAdapter;

    private List<Person> mPersons = new ArrayList<>();
    private List<Event> mEvents = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mSearchView = view.findViewById(R.id.search_bar);
        mSearchView.setOnQueryTextListener(this);
        mSearchRecyclerView = view.findViewById(R.id.search_recycler_view);
        mSearchRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new SearchAdapter(mPersons, mEvents);
        mSearchRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        updateUI(mSearchView.getQuery().toString());
        return true;
    }

    private void updateUI(String term) {
        List<Person> persons = new ArrayList<>(DataCache.getInstance().getPersons());
        persons = DataCache.getInstance().applyPersonFilters(persons);
        List<Event> events = new ArrayList<>(DataCache.getInstance().getEvents());
        events = DataCache.sortEvents(events);
        events = DataCache.getInstance().applyEventFilters(events);
        mPersons.clear();
        mEvents.clear();

        // Add all matching Persons
        for (Person person : persons) {
            if (person.getFirstName().toLowerCase().contains(term)
                    || person.getLastName().toLowerCase().contains(term)) {
                mPersons.add(person);
            }
        }

        // Add all matching Events
        for (Event event : events) {
            Person person = DataCache.getInstance().findPerson(event.getPersonID());
            if (event.getType().toLowerCase().contains(term)
                    || event.getCity().toLowerCase().contains(term)
                    || event.getCountry().toLowerCase().contains(term)
                    || person.getFirstName().toLowerCase().contains(term)
                    || person.getLastName().toLowerCase().contains(term)) {
                mEvents.add(event);
            }
        }

        // Update Recycler View
        mAdapter.notifyDataSetChanged();
    }

    private class SearchHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final TextView mItemTopText;
        private final TextView mItemBottomText;
        private final ImageView mItemImage;

        private Person mPerson;
        private Event mEvent;

        public SearchHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item, parent, false));
            itemView.setOnClickListener(this);

            mItemTopText = itemView.findViewById(R.id.item_top_text);
            mItemBottomText = itemView.findViewById(R.id.item_bottom_text);
            mItemImage = itemView.findViewById(R.id.item_image);
        }

        void bind(Person person) {
            mPerson = person;
            String name = person.getFirstName() + " " + person.getLastName();
            mItemTopText.setText(name);
            mItemBottomText.setText("");

            // Generate gender image
            int iconColor;
            FontAwesomeIcons iconGender;
            if (person.getGender().equals("m")) {
                iconColor = R.color.male_icon;
                iconGender = FontAwesomeIcons.fa_male;
            } else {
                iconColor = R.color.female_icon;
                iconGender = FontAwesomeIcons.fa_female;
            }

            // Set image for family members
            Drawable genderIcon = new IconDrawable(getActivity(), iconGender)
                    .colorRes(iconColor).sizeDp(40);
            mItemImage.setImageDrawable(genderIcon);
        }

        void bind(Event event) {
            mEvent = event;
            String eventText = event.getType().toUpperCase() + ": "
                    + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")";
            Person person = DataCache.getInstance().findPerson(event.getPersonID());
            String name = person.getFirstName() + " " + person.getLastName();
            mItemTopText.setText(eventText);
            mItemBottomText.setText(name);

            // Generate event marker image
            Bitmap markerIcon = DataCache.getInstance().getEventMarkerIcon(getActivity(), event);
            BitmapDrawable bd = new BitmapDrawable(getResources(), markerIcon);

            mItemImage.setImageDrawable(bd);
        }

        @Override
        public void onClick(View v) {
            if (this.getAdapterPosition() < mPersons.size()) {
                String personID = mPerson.getPersonID();
                Intent intent = new Intent(getActivity(), PersonActivity.class);
                intent.putExtra(PersonActivity.EXTRA_PERSON_ID, personID);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getActivity(), EventActivity.class);
                intent.putExtra(EventActivity.EXTRA_EVENT_ID, mEvent.getEventID());
                startActivity(intent);
            }
        }
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchHolder> {

        private final List<Person> mPersons;
        private final List<Event> mEvents;

        SearchAdapter(List<Person> persons, List<Event> events) {
            this.mPersons = persons;
            this.mEvents = events;
        }

        @NonNull
        @Override
        public SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new SearchHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchHolder holder, int position) {
            if (position < mPersons.size()) {
                Person person = mPersons.get(position);
                holder.bind(person);
            } else {
                Event event = mEvents.get(position - mPersons.size());
                holder.bind(event);
            }
        }

        @Override
        public int getItemCount() {
            return mPersons.size() + mEvents.size();
        }
    }
}
