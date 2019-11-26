package com.mcarpe12.familymapclient;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import familymap.Event;
import familymap.Person;

public class PersonActivity extends AppCompatActivity {
    private static final int LIFE_EVENTS_POSITION = 0;
    private static final int FAMILY_POSITION = 1;
    private static final int FATHER_CHILDPOSITION = 0;
    private static final int MOTHER_CHILDPOSITION = 1;
    private static final int SPOUSE_CHILDPOSITION = 2;
    private static final int CHILD_CHILDPOSITION = 3;

    public static final String EXTRA_PERSON_ID = "com.mcarpe12.familymapclient.person_id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        String personID = getIntent().getStringExtra(EXTRA_PERSON_ID);
        Person person = DataCache.getInstance().findPerson(personID);

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);

        // Add all events associated with given person
        List<Event> lifeEvents = DataCache.getInstance().getEventsByPerson(personID);

        // Add the family members in this order:
        // Father, Mother, Spouse, Child
        List<Person> familyMembers = new ArrayList<>();
        familyMembers.add(DataCache.getInstance().findPerson(person.getFatherID()));
        familyMembers.add(DataCache.getInstance().findPerson(person.getMotherID()));
        familyMembers.add(DataCache.getInstance().findPerson(person.getSpouseID()));
        familyMembers.add(DataCache.getInstance().findChild(person.getPersonID()));

        expandableListView.setAdapter(new ExpandableListAdapter(lifeEvents, familyMembers, person));
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
            Event event = lifeEvents.get(childPosition);
            final String eventText = event.getType().toUpperCase() + ": "
                    + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")";
            String fullName = person.getFirstName() + " " + person.getLastName();

            TextView itemTopTextView = listItemView.findViewById(R.id.item_top_text);
            itemTopTextView.setText(eventText);

            TextView itemBottomTextView = listItemView.findViewById(R.id.item_bottom_text);
            itemBottomTextView.setText(fullName);

            listItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(PersonActivity.this, getString(R.string.life_event_toast_text, eventText), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void initializeFamilyView(View listItemView, final int childPosition) {
            Person familyMember = familyMembers.get(childPosition);
            final String fullName = familyMember.getFirstName() + " " + familyMember.getLastName();
            String relationship = "Family";

            TextView itemTopTextView = listItemView.findViewById(R.id.item_top_text);
            itemTopTextView.setText(fullName);

            switch (childPosition) {
                case FATHER_CHILDPOSITION:
                    relationship = "Father";
                    break;
                case MOTHER_CHILDPOSITION:
                    relationship = "Mother";
                    break;
                case SPOUSE_CHILDPOSITION:
                    relationship = "Spouse";
                    break;
                case CHILD_CHILDPOSITION:
                    relationship = "Child";
                    break;
            }
            TextView itemBottomTextView = listItemView.findViewById(R.id.item_bottom_text);
            itemBottomTextView.setText(relationship);

            listItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(PersonActivity.this, getString(R.string.family_toast_text, fullName), Toast.LENGTH_SHORT).show();
                }
            });
        }


        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
