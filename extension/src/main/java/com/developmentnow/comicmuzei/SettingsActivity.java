package com.developmentnow.comicmuzei;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.MenuItem;
import com.developmentnow.marvelmuzei.R;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingsActivity extends PreferenceActivity {

    public static String KEY_PREF_RANDOM_CHARACTER = "pref_key_input_0";
    public static String KEY_PREF_BY_CHARACTER = "pref_key_input_1";
    public static String KEY_PREF_BY_ARTIST = "pref_key_input_2";
    public static String KEY_PREF_CHARACTERS = "pref_character_input";
    public static String KEY_PREF_ARTISTS = "pref_artist_input";
    public static final String SEPARATOR = "OV=I=XseparatorX=I=VO";
    private HashMap<String, String> artists = new HashMap<String, String>();
    private HashMap<String, String> characters = new HashMap<String, String>();
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        addPreferencesFromResource(R.xml.preferences);
        findPreference("pref_key_input_0").setOnPreferenceChangeListener(CheckboxChanger);
        findPreference("pref_key_input_1").setOnPreferenceChangeListener(CheckboxChanger);
        findPreference("pref_key_input_2").setOnPreferenceChangeListener(CheckboxChanger);
        findPreference("pref_character_input").setOnPreferenceChangeListener(ListChanger);
        findPreference("pref_artist_input").setOnPreferenceChangeListener(ListChanger);

        String[] character_list_name = getResources().getStringArray(R.array.character_names);
        String[] character_list_id = getResources().getStringArray(R.array.character_values);
        String[] artist_list_name = getResources().getStringArray(R.array.artist_names);
        String[] artist_list_id = getResources().getStringArray(R.array.artist_values);
        for (int i = 0; i < artist_list_id.length; i++) {
            artists.put(artist_list_id[i], artist_list_name[i]);
        }
        for (int i = 0; i < character_list_id.length; i++) {
            characters.put(character_list_id[i], character_list_name[i]);
        }

        showCurrentCharacters(null);
        showCurrentArtists(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void showCurrentCharacters(String vals) {
        if (vals == null) {
            vals = prefs.getString(KEY_PREF_CHARACTERS, "");
        }
        String[] character_ids =  TextUtils.split(vals, SEPARATOR);
        if (character_ids.length == 0) {
            findPreference("pref_character_input").setSummary(getResources().getString(R.string.choose_chars) + " " + getResources().getString(R.string.none));
        } else {
            ArrayList<String> names = new ArrayList<String>();
            for (int i = 0; i < character_ids.length; i++) {
                String n = characters.get(character_ids[i]);
                if (n != null)
                    names.add(n);
            }
            findPreference("pref_character_input").setSummary(getResources().getString(R.string.choose_chars) + " " + TextUtils.join(", ", names));
        }
    }
    public void showCurrentArtists(String vals) {
        if (vals == null) {
            vals = prefs.getString(KEY_PREF_ARTISTS, "");
        }
        String[] artist_ids = TextUtils.split(vals, SEPARATOR);
        if (artist_ids.length == 0) {
            findPreference("pref_artist_input").setSummary(getResources().getString(R.string.choose_artists) + " " + getResources().getString(R.string.none));
        } else {
            ArrayList<String> names = new ArrayList<String>();
            for (int i = 0; i < artist_ids.length; i++) {
                String n = artists.get(artist_ids[i]);
                if (n != null)
                    names.add(n);
            }
            findPreference("pref_artist_input").setSummary(getResources().getString(R.string.choose_artists) + " " + TextUtils.join(", ", names));
        }
    }

    public Preference.OnPreferenceChangeListener ListChanger = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String key = preference.getKey();
            if (key.equals("pref_character_input")) {
                ((CheckBoxPreference)findPreference("pref_key_input_0")).setChecked(false);
                ((CheckBoxPreference)findPreference("pref_key_input_1")).setChecked(true);
                ((CheckBoxPreference)findPreference("pref_key_input_2")).setChecked(false);
                showCurrentCharacters(String.valueOf(newValue));
            } else if (key.equals("pref_artist_input")) {
                ((CheckBoxPreference)findPreference("pref_key_input_0")).setChecked(false);
                ((CheckBoxPreference)findPreference("pref_key_input_1")).setChecked(false);
                ((CheckBoxPreference)findPreference("pref_key_input_2")).setChecked(true);
                showCurrentArtists(String.valueOf(newValue));
            }
            if (String.valueOf(newValue).equals("")) {
                ((CheckBoxPreference)findPreference("pref_key_input_0")).setChecked(true);
                ((CheckBoxPreference)findPreference("pref_key_input_1")).setChecked(false);
                ((CheckBoxPreference)findPreference("pref_key_input_2")).setChecked(false);
            }
            return true;
        }
    };
    public Preference.OnPreferenceChangeListener CheckboxChanger = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String key = preference.getKey();
            if (key.equals("pref_key_input_0")) {
                //Reset other items
                ((CheckBoxPreference)findPreference("pref_key_input_1")).setChecked(false);
                ((CheckBoxPreference)findPreference("pref_key_input_2")).setChecked(false);
            }
            else if (key.equals("pref_key_input_1")) {
                //Reset other items
                ((CheckBoxPreference)findPreference("pref_key_input_0")).setChecked(false);
                ((CheckBoxPreference)findPreference("pref_key_input_2")).setChecked(false);
                ((ListPreferenceMultiSelect)findPreference("pref_character_input")).show();
            } else if (key.equals("pref_key_input_2")) {
                ((CheckBoxPreference)findPreference("pref_key_input_0")).setChecked(false);
                ((CheckBoxPreference)findPreference("pref_key_input_1")).setChecked(false);
                ((ListPreferenceMultiSelect)findPreference("pref_artist_input")).show();
            }
            return (Boolean)newValue;
        }
    };

}