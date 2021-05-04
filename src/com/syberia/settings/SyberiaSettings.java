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

package com.syberia.settings;

import android.os.Bundle;
import android.content.Context;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.internal.logging.nano.MetricsProto;

import androidx.preference.Preference;

import com.syberia.settings.Utils;

public class SyberiaSettings extends SettingsPreferenceFragment {

    private static final String KEY_AMBIENT_DISPLAY_CUSTOM = "ambient_display_custom";
    private Preference mCustomDoze;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.syberia_settings);

        mCustomDoze = (Preference) findPreference(KEY_AMBIENT_DISPLAY_CUSTOM);
        if (!Utils.isCustomDoze(getActivity().getApplicationContext())) {
            getPreferenceScreen().removePreference(mCustomDoze);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SYBERIA;
    }
}
