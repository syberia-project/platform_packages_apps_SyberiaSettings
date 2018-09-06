/*
 * Copyright © 2018 Syberia Project
 * Date: 22.08.2018
 * Time: 21:21
 * Author: @alexxxdev <alexxxdev@ya.ru>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.syberia.settings.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v14.preference.SwitchPreference;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.syberia.settings.preference.CustomSeekBarPreference;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

import com.android.internal.logging.nano.MetricsProto;

public class LockScreenSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

private static final String LOCK_CLOCK_FONTS = "lock_clock_fonts";
private static final String LOCK_DATE_FONTS = "lock_date_fonts";
private static final String CLOCK_FONT_SIZE = "lockclock_font_size";
private static final String DATE_FONT_SIZE = "lockdate_font_size";

private static final String LOCK_SCREEN_VISUALIZER_CUSTOM_COLOR = "lock_screen_visualizer_custom_color";

ListPreference mLockClockFonts;
ListPreference mLockDateFonts;
private CustomSeekBarPreference mClockFontSize;
private CustomSeekBarPreference mDateFontSize;
private ColorPickerPreference mVisualizerColor;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.lockscreen_settings);

	ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();
        Resources resources = getResources();

        // Visualizer custom color
        mVisualizerColor = (ColorPickerPreference) findPreference(LOCK_SCREEN_VISUALIZER_CUSTOM_COLOR);
        int visColor = Settings.System.getInt(resolver,
                Settings.System.LOCK_SCREEN_VISUALIZER_CUSTOM_COLOR, 0xff1976D2);
        String visColorHex = String.format("#%08x", (0xff1976D2 & visColor));
        mVisualizerColor.setSummary(visColorHex);
        mVisualizerColor.setNewPreviewColor(visColor);
        mVisualizerColor.setAlphaSliderEnabled(true);
        mVisualizerColor.setOnPreferenceChangeListener(this);

        // Lockscren Clock Fonts
        mLockClockFonts = (ListPreference) findPreference(LOCK_CLOCK_FONTS);
        mLockClockFonts.setValue(String.valueOf(Settings.System.getInt(
                getContentResolver(), Settings.System.LOCK_CLOCK_FONTS, 0)));
        mLockClockFonts.setSummary(mLockClockFonts.getEntry());
        mLockClockFonts.setOnPreferenceChangeListener(this);

	// Lockscren Date Fonts
        mLockDateFonts = (ListPreference) findPreference(LOCK_DATE_FONTS);
        mLockDateFonts.setValue(String.valueOf(Settings.System.getInt(
                getContentResolver(), Settings.System.LOCK_DATE_FONTS, 0)));
        mLockDateFonts.setSummary(mLockDateFonts.getEntry());
        mLockDateFonts.setOnPreferenceChangeListener(this);

 	// Lock Clock Size
        mClockFontSize = (CustomSeekBarPreference) findPreference(CLOCK_FONT_SIZE);
        mClockFontSize.setValue(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKCLOCK_FONT_SIZE, 64));
        mClockFontSize.setOnPreferenceChangeListener(this);

        // Lock Date Size
        mDateFontSize = (CustomSeekBarPreference) findPreference(DATE_FONT_SIZE);
        mDateFontSize.setValue(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKDATE_FONT_SIZE,16));
        mDateFontSize.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
		ContentResolver resolver = getActivity().getContentResolver();
		if (preference == mLockClockFonts) {
            		Settings.System.putInt(getContentResolver(), Settings.System.LOCK_CLOCK_FONTS,
                    	Integer.valueOf((String) newValue));
            		mLockClockFonts.setValue(String.valueOf(newValue));
            		mLockClockFonts.setSummary(mLockClockFonts.getEntry());
	return true; 
	}       else if (preference == mLockDateFonts) {
			Settings.System.putInt(getContentResolver(), Settings.System.LOCK_DATE_FONTS,
			Integer.valueOf((String) newValue));
			mLockDateFonts.setValue(String.valueOf(newValue));
			mLockDateFonts.setSummary(mLockDateFonts.getEntry());
        		return true;
	}      else if (preference == mClockFontSize) {
			int top = (Integer) newValue;
			Settings.System.putInt(getContentResolver(),
			Settings.System.LOCKCLOCK_FONT_SIZE, top*1);
			return true;
        }      else if (preference == mDateFontSize) {
			int top = (Integer) newValue;
			Settings.System.putInt(getContentResolver(),
			Settings.System.LOCKDATE_FONT_SIZE, top*1);
			return true;
	}      else if (preference == mVisualizerColor) {
			String hex = ColorPickerPreference.convertToARGB(
			Integer.valueOf(String.valueOf(newValue)));
			int intHex = ColorPickerPreference.convertToColorInt(hex);
			Settings.System.putInt(resolver,
			Settings.System.LOCK_SCREEN_VISUALIZER_CUSTOM_COLOR, intHex);
			preference.setSummary(hex);
			return true;
        }
    	return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SYBERIA;
    }
}
