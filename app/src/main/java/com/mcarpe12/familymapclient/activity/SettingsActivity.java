package com.mcarpe12.familymapclient.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.mcarpe12.familymapclient.R;
import com.mcarpe12.familymapclient.service.DataCache;

public class SettingsActivity extends AppCompatActivity
        implements Switch.OnCheckedChangeListener,
        TextView.OnClickListener {
    private Switch mLifeStoryLines;
    private Switch mFamilyTreeLines;
    private Switch mSpouseLines;
    private Switch mFilterFather;
    private Switch mFilterMother;
    private Switch mFilterMale;
    private Switch mFilterFemale;
    private View mLogoutItem;

    public static final String EXTRA_SETTINGS_CHANGED = "com.mcarpe12.familymapclient.settings_changed";
    private boolean settingsChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize settings switches
        mLifeStoryLines = findViewById(R.id.switch_life_story_lines);
        mFamilyTreeLines = findViewById(R.id.switch_family_tree_lines);
        mSpouseLines = findViewById(R.id.switch_spouse_lines);
        mFilterFather = findViewById(R.id.switch_filter_father);
        mFilterMother = findViewById(R.id.switch_filter_mother);
        mFilterMale = findViewById(R.id.switch_filter_male);
        mFilterFemale = findViewById(R.id.switch_filter_female);
        mLogoutItem = findViewById(R.id.clayout_logout);

        mLifeStoryLines.setChecked(DataCache.getInstance().isLifeStoryLines());
        mFamilyTreeLines.setChecked(DataCache.getInstance().isFamilyTreeLines());
        mSpouseLines.setChecked(DataCache.getInstance().isSpouseLines());
        mFilterFather.setChecked(DataCache.getInstance().isFilterFatherSide());
        mFilterMother.setChecked(DataCache.getInstance().isFilterMotherSide());
        mFilterMale.setChecked(DataCache.getInstance().isFilterMaleEvents());
        mFilterFemale.setChecked(DataCache.getInstance().isFilterFemaleEvents());

        mLifeStoryLines.setOnCheckedChangeListener(this);
        mFamilyTreeLines.setOnCheckedChangeListener(this);
        mSpouseLines.setOnCheckedChangeListener(this);
        mFilterFather.setOnCheckedChangeListener(this);
        mFilterMother.setOnCheckedChangeListener(this);
        mFilterMale.setOnCheckedChangeListener(this);
        mFilterFemale.setOnCheckedChangeListener(this);

        mLogoutItem.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // Update the settings to reflect the switches
        DataCache.getInstance().setLifeStoryLines(mLifeStoryLines.isChecked());
        DataCache.getInstance().setFamilyTreeLines(mFamilyTreeLines.isChecked());
        DataCache.getInstance().setSpouseLines(mSpouseLines.isChecked());
        DataCache.getInstance().setFilterFatherSide(mFilterFather.isChecked());
        DataCache.getInstance().setFilterMotherSide(mFilterMother.isChecked());
        DataCache.getInstance().setFilterMaleEvents(mFilterMale.isChecked());
        DataCache.getInstance().setFilterFemaleEvents(mFilterFemale.isChecked());

        settingsChanged = true;

        Intent data = new Intent();
        data.putExtra(EXTRA_SETTINGS_CHANGED, settingsChanged);
        setResult(RESULT_OK, data);
    }

    @Override
    public void onClick(View v) {
        DataCache.clear();
        Intent logoutIntent = new Intent(this, MainActivity.class);
        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(logoutIntent);
    }
}
