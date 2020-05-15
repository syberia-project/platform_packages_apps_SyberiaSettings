/*
 * Copyright © 2018-2019 Syberia Project
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
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceFragmentCompat;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import androidx.preference.ListPreference;
import androidx.preference.SwitchPreference;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.util.hwkeys.ActionUtils;
import androidx.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import com.android.internal.util.hwkeys.ActionUtils;

import com.android.internal.logging.nano.MetricsProto;

public class NavBarSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String NAVBAR_VISIBILITY = "navbar_visibility";

    private SwitchPreference mNavbarVisibility;
    private boolean mIsNavSwitchingMode = false;
    private Handler mHandler;
    private boolean needsNavbar;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.navbar_settings);
        needsNavbar = ActionUtils.hasNavbarByDefault(getActivity());
        final PreferenceScreen prefScreen = getPreferenceScreen();
        mNavbarVisibility = (SwitchPreference) findPreference(NAVBAR_VISIBILITY);
        if (!needsNavbar) {
            boolean showing = Settings.System.getInt(getContentResolver(),
                    Settings.System.FORCE_SHOW_NAVBAR,
                    ActionUtils.hasNavbarByDefault(getActivity()) ? 1 : 0) != 0;
            updateBarVisibleAndUpdatePrefs(showing);
            mNavbarVisibility.setOnPreferenceChangeListener(this);

            mHandler = new Handler();
        } else {
            prefScreen.removePreference(mNavbarVisibility);
            Settings.System.putInt(getContentResolver(), Settings.System.FORCE_SHOW_NAVBAR, 1);
        }
    }

    private void updateBarVisibleAndUpdatePrefs(boolean showing) {
        mNavbarVisibility.setChecked(showing);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (preference.equals(mNavbarVisibility)) {
            if (mIsNavSwitchingMode && needsNavbar) {
                return false;
            }
            mIsNavSwitchingMode = true;
            boolean showing = ((Boolean)newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.FORCE_SHOW_NAVBAR,
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
