package com.mcarpe12.familymapclient;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.mcarpe12.familymapclient.service.DataCache;

import java.util.List;

import familymap.Event;
import familymap.Person;

public class SearchListFragment extends Fragment {
    private RecyclerView mSearchRecyclerView;

    private class SearchHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private Person mPerson;
        private Event mEvent;

        private final TextView mItemTopText;
        private final TextView mItemBottomText;
        private final ImageView mItemImage;


        public SearchHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item, parent, false));
            itemView.setOnClickListener(this);

            mItemTopText = itemView.findViewById(R.id.item_top_text);
            mItemBottomText = itemView.findViewById(R.id.item_bottom_text);
            mItemImage = itemView.findViewById(R.id.item_image);
        }

        void bind(Person person) {
            mPerson = person;
            String name = mPerson.getFirstName() + " " + mPerson.getLastName();
            mItemTopText.setText(name);

            // Generate gender image
            int iconColor;
            FontAwesomeIcons iconGender;
            if (mPerson.getGender().equals("m")) {
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
