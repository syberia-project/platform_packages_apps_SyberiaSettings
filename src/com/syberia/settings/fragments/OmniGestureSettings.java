/*
 *  Copyright (C) 2018 The OmniROM Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.syberia.settings.fragments;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.syberia.settings.preference.CustomSeekBarPreference;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v14.preference.SwitchPreference;
import com.android.internal.util.hwkeys.ActionUtils;

import android.provider.SearchIndexableResource;
import android.provider.Settings;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;

import java.util.ArrayList;
import java.util.List;

public class OmniGestureSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {

    private static final String TAG = "OmniGestureSettings";
    private static final String KEY_OMNI_GESTURES_ENABLED = "use_bottom_gesture_navigation";
    private static final String KEY_SWIPE_LENGTH = "gesture_swipe_length";
    private static final String KEY_SWIPE_TIMEOUT = "gesture_swipe_timeout";

    private CustomSeekBarPreference mSwipeTriggerLength;
    private CustomSeekBarPreference mSwipeTriggerTimeout;
    private SwitchPreference mOmniGesturesEnabled;

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.SYBERIA;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.omni_gesture_settings);
        mFooterPreferenceMixin.createFooterPreference().setTitle(R.string.gesture_settings_info);
 
        mOmniGesturesEnabled = (SwitchPreference) findPreference(KEY_OMNI_GESTURES_ENABLED);
        mOmniGesturesEnabled.setOnPreferenceChangeListener(this);

        mSwipeTriggerLength = (CustomSeekBarPreference) findPreference(KEY_SWIPE_LENGTH);
        int value = Settings.System.getInt(getContentResolver(),
                Settings.System.OMNI_BOTTOM_GESTURE_SWIPE_LIMIT,
                getSwipeLengthInPixel(getResources().getInteger(com.android.internal.R.integer.nav_gesture_swipe_min_length)));

        mSwipeTriggerLength.setMin(getSwipeLengthInPixel(40));
        mSwipeTriggerLength.setMax(getSwipeLengthInPixel(80));
        mSwipeTriggerLength.setValue(value);
        mSwipeTriggerLength.setOnPreferenceChangeListener(this);

        mSwipeTriggerTimeout = (CustomSeekBarPreference) findPreference(KEY_SWIPE_TIMEOUT);
        value = Settings.System.getInt(getContentResolver(),
                 Settings.System.OMNI_BOTTOM_GESTURE_TRIGGER_TIMEOUT,
                 getResources().getInteger(com.android.internal.R.integer.nav_gesture_swipe_timout));
        mSwipeTriggerTimeout.setValue(value);
        mSwipeTriggerTimeout.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        if (preference == mOmniGesturesEnabled) {
            int enabled = ((boolean) objValue) ? 1 : 0;
            if (enabled == 1) {
                Settings.Secure.putInt(getContentResolver(),
                        Settings.Secure.NAVIGATION_BAR_VISIBLE, 0);
                Settings.Secure.putInt(getContentResolver(),
                        Settings.Secure.EDGE_GESTURES_ENABLED, 0);
            } else {
                if (ActionUtils.hasNavbarByDefault(getPrefContext())) {
                    Settings.Secure.putInt(getContentResolver(),
                            Settings.Secure.NAVIGATION_BAR_VISIBLE,
                            1);
                }
            }
        } else if (preference == mSwipeTriggerLength) {
            int value = (Integer) objValue;
            Settings.System.putInt(getContentResolver(),
                     Settings.System.OMNI_BOTTOM_GESTURE_SWIPE_LIMIT, value);
        } else if (preference == mSwipeTriggerTimeout) {
             int value = (Integer) objValue;
             Settings.System.putInt(getContentResolver(),
                    Settings.System.OMNI_BOTTOM_GESTURE_TRIGGER_TIMEOUT, value);
        } else {
            return false;
        }
        return true;
    }

    private int getSwipeLengthInPixel(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    /**
     * For Search.
     */
    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {

                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    final ArrayList<SearchIndexableResource> result = new ArrayList<>();

                    final SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.omni_gesture_settings;
                    result.add(sir);
                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    final List<String> keys = super.getNonIndexableKeys(context);
                    return keys;
                }
    };
}