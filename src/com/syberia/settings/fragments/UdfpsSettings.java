/*
 * Copyright (C) 2017-2022 The Project-Xtended
 * Copyright (C) 2019-2022 The Syberia OS Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.syberia.settings.fragments;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;

import androidx.preference.ListPreference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.syberia.SyberiaUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.syberia.settings.preference.SystemSettingListPreference;
import com.syberia.settings.preference.SystemSettingSwitchPreference;

public class UdfpsSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String UDFPS_CUSTOMIZATION = "udfps_customization";
    private static final String UDFPS_SCREEN_OFF = "screen_off_fod";
    private static final String UDFPS_HAPTIC_FEEDBACK = "udfps_haptic_feedback";

    private static final int REQUEST_PICK_IMAGE = 0;

    private PreferenceCategory mUdfpsCustomization;
    private SystemSettingSwitchPreference mUdfpsScreenOff;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.udfps_settings);

        final PreferenceScreen prefSet = getPreferenceScreen();
        Resources resources = getResources();

        final boolean udfpsResPkgInstalled = SyberiaUtils.isPackageInstalled(getContext(),
                "com.syberia.udfps.resources");
	mUdfpsCustomization = (PreferenceCategory) findPreference(UDFPS_CUSTOMIZATION);
        if (!udfpsResPkgInstalled) {
            prefSet.removePreference(mUdfpsCustomization);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SYBERIA;
    }
}
