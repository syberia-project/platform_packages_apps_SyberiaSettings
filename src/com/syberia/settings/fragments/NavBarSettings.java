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
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import android.provider.Settings;

import com.android.internal.logging.nano.MetricsProto;

import com.android.internal.util.hwkeys.ActionUtils;

public class NavBarSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String NAVBAR_VISIBILITY = "navbar_visibility";
    private SwitchPreference mNavbarVisibility;
    private boolean mIsNavSwitchingMode = false;
    private Handler mHandler;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.navbar_settings);

	mNavbarVisibility = (SwitchPreference) findPreference(NAVBAR_VISIBILITY);
         boolean showing = Settings.Secure.getInt(getContentResolver(),
                Settings.Secure.NAVIGATION_BAR_VISIBLE,
                ActionUtils.hasNavbarByDefault(getActivity()) ? 1 : 0) != 0;
        updateBarVisibleAndUpdatePrefs(showing);
        mNavbarVisibility.setOnPreferenceChangeListener(this);
        mHandler = new Handler();
    }

    private void updateBarVisibleAndUpdatePrefs(boolean showing) {
        mNavbarVisibility.setChecked(showing);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.equals(mNavbarVisibility)) {
            if (mIsNavSwitchingMode) {
                return false;
            }
            mIsNavSwitchingMode = true;
            boolean showing = ((Boolean)newValue);
            Settings.Secure.putInt(getContentResolver(), Settings.Secure.NAVIGATION_BAR_VISIBLE,
                    showing ? 1 : 0);
            updateBarVisibleAndUpdatePrefs(showing);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIsNavSwitchingMode = false;
                }
            }, 1500);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SYBERIA;
    }
}