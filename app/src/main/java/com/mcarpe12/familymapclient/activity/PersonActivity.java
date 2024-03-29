package com.mcarpe12.familymapclient.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.mcarpe12.familymapclient.R;
import com.mcarpe12.familymapclient.service.DataCache;

import java.util.ArrayList;
import java.util.List;

import familymap.Event;
import familymap.Person;

public class PersonActivity extends AppCompatActivity {
    private static final int LIFE_EVENTS_POSITION = 0;
    private static final int FAMILY_POSITION = 1;

    public static final String EXTRA_PERSON_ID = "com.mcarpe12.familymapclient.person_id";

    private TextView mFirstName;
    private TextView mLastName;
    private TextView mGender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        String personID = getIntent().getStringExtra(EXTRA_PERSON_ID);
        Person person = DataCache.getInstance().findPerson(personID);
        Person father = null;
        Person mother = null;
        Person spouse = null;
        Person child = DataCache.getInstance().findChild(personID);

        // Filter family members by active settings
        List<Person> filteredPersons = new ArrayList<>(DataCache.getInstance().getPersons());
        for (Person p : filteredPersons) {
            if (p.getPersonID().equals(person.getFatherID())) {
                father = p;
            } else if (p.getPersonID().equals(person.getMotherID())) {
                mother = p;
            } else if (p.getPersonID().equals(person.getSpouseID())) {
                spouse = p;
            }
        }

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);

        // Add all events associated with given person and apply filters
        List<Event> lifeEvents = new ArrayList<>(DataCache.getInstance().getEventsByPerson(personID));
        lifeEvents = DataCache.sortEvents(lifeEvents);
        lifeEvents = DataCache.getInstance().applyEventFilters(lifeEvents);

        // Add the existing (non-null) family members in this order:
        // Father, Mother, Spouse, Child
        List<Person> familyMembers = new ArrayList<>();
        if (father != null) {
            familyMembers.add(father);
        }
        if (mother != null) {
            familyMembers.add(mother);
        }
        if (spouse != null) {
            familyMembers.add(spouse);
        }
        if (child != null) {
            familyMembers.add(child);
        }

        expandableListView.setAdapter(new ExpandableListAdapter(lifeEvents, familyMembers, person));

        mFirstName = findViewById(R.id.person_first_name);
        mFirstName.setText(person.getFirstName());
        mLastName = findViewById(R.id.person_last_name);
        mLastName.setText(person.getLastName());
        mGender = findViewById(R.id.person_gender);
        if (person.getGender().equals("m")) {
            mGender.setText("Male");
        } else {
            mGender.setText("Female");
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

    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        private final List<Event> lifeEvents;
        private final List<Person> familyMembers;
        private final Person person;

        ExpandableListAdapter(List<Event> lifeEvents, List<Person> familyMembers, Person person) {
            this.lifeEvents = lifeEvents;
            this.familyMembers = familyMembers;
            this.person = person;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case LIFE_EVENTS_POSITION:
                    return lifeEvents.size();
                case FAMILY_POSITION:
                    return familyMembers.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch (groupPosition) {
                case LIFE_EVENTS_POSITION:
                    return (R.string.life_events_title);
                case FAMILY_POSITION:
                    return (R.string.family_title);
                default:
                    throw new IllegalArgumentException("Unrecognized group postition " + groupPosition);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition) {
                case LIFE_EVENTS_POSITION:
                    return lifeEvents.get(childPosition);
                case FAMILY_POSITION:
                    return familyMembers.get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrecognized group postition " + groupPosition);
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.list_title);

            switch (groupPosition) {
                case LIFE_EVENTS_POSITION:
                    titleView.setText(R.string.life_events_title);
                    break;
                case FAMILY_POSITION:
                    titleView.setText(R.string.family_title);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch (groupPosition) {
                case LIFE_EVENTS_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
                    initializeLifeEventView(itemView, childPosition);
                    break;
                case FAMILY_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
                    initializeFamilyView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        private void initializeLifeEventView(View listItemView, final int childPosition) {
            final Event event = lifeEvents.get(childPosition);
            final String eventText = event.getType().toUpperCase() + ": "
                    + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")";
            String fullName = person.getFirstName() + " " + person.getLastName();

            TextView itemTopTextView = listItemView.findViewById(R.id.item_top_text);
            itemTopTextView.setText(eventText);

            TextView itemBottomTextView = listItemView.findViewById(R.id.item_bottom_text);
            itemBottomTextView.setText(fullName);

            // Generate event marker image
            ImageView mItemImage = listItemView.findViewById(R.id.item_image);
            Bitmap markerIcon = DataCache.getInstance().getEventMarkerIcon(PersonActivity.this, event);
            BitmapDrawable bd = new BitmapDrawable(getResources(), markerIcon);

            mItemImage.setImageDrawable(bd);

            listItemView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(PersonActivity.this, EventActivity.class);
                            intent.putExtra(EventActivity.EXTRA_EVENT_ID, event.getEventID());
                            startActivity(intent);
                        }
                    }
            );
        }

        private void initializeFamilyView(View listItemView, final int childPosition) {
            final Person familyMember = familyMembers.get(childPosition);
            final String fullName = familyMember.getFirstName() + " " + familyMember.getLastName();
            String familyMemberID = familyMember.getPersonID();
            String relationship;

            TextView itemTopTextView = listItemView.findViewById(R.id.item_top_text);
            itemTopTextView.setText(fullName);

            if (familyMemberID.equals(person.getFatherID())) {
                relationship = "Father";
            } else if (familyMemberID.equals(person.getMotherID())) {
                relationship = "Mother";
            } else if (familyMemberID.equals(person.getSpouseID())) {
                relationship = "Spouse";
            } else {
                relationship = "Child";
            }

            TextView itemBottomTextView = listItemView.findViewById(R.id.item_bottom_text);
            itemBottomTextView.setText(relationship);

            // Generate gender image
            ImageView mItemImage = listItemView.findViewById(R.id.item_image);
            int iconColor;
            FontAwesomeIcons iconGender;
            if (familyMember.getGender().equals("m")) {
                iconColor = R.color.male_icon;
                iconGender = FontAwesomeIcons.fa_male;
            } else {
                iconColor = R.color.female_icon;
                iconGender = FontAwesomeIcons.fa_female;
            }

            // Set image for family members
            Drawable genderIcon = new IconDrawable(PersonActivity.this, iconGender)
                    .colorRes(iconColor).sizeDp(40);
            mItemImage.setImageDrawable(genderIcon);

            // If user clicks on a person, launch another PersonActivity for that person
            listItemView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String personID = familyMember.getPersonID();
                            Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
                            intent.putExtra(PersonActivity.EXTRA_PERSON_ID, personID);
                            startActivity(intent);
                        }
                    }
            );
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
