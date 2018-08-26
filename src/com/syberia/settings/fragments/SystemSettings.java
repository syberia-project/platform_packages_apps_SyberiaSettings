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
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.internal.logging.nano.MetricsProto;

public class SystemSettings extends SettingsPreferenceFragment {

	private static final String CATEGORY_TWEAKS = "general_tweaks";
	private static final String CATEGORY_ANIMATIONS = "animations";
	private static final String CATEGORY_RESENT = "recents_ui";
	private static final String CATEGORY_SYS_APP_REMOVER = "system_app_remover";
	private static final String CATEGORY_DIALER = "dialer";

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.system_settings);

        PreferenceScreen preferenceScreen = getPreferenceScreen();
		preferenceScreen.removePreference(findPreference(CATEGORY_TWEAKS));
		preferenceScreen.removePreference(findPreference(CATEGORY_ANIMATIONS));
		preferenceScreen.removePreference(findPreference(CATEGORY_RESENT));
		preferenceScreen.removePreference(findPreference(CATEGORY_SYS_APP_REMOVER));
		preferenceScreen.removePreference(findPreference(CATEGORY_DIALER));
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SYBERIA;
    }
}