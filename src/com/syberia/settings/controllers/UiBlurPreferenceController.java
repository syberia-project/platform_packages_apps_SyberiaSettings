/*
 * Copyright (C) 2021 Syberia Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.syberia.settings.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.provider.Settings;
import androidx.preference.SwitchPreference;
import androidx.preference.Preference;

import com.android.settings.DisplaySettings;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.R;
import com.android.settingslib.core.AbstractPreferenceController;

import static android.provider.Settings.System.UI_BACKGROUND_BLUR;

public class UiBlurPreferenceController extends AbstractPreferenceController implements
        PreferenceControllerMixin, Preference.OnPreferenceChangeListener {

    private static final String KEY_UI_BLUR = "ui_blur";

    private static boolean mBlurAvailable = SystemProperties
            .getBoolean("ro.surface_flinger.supports_background_blur", false);

    public UiBlurPreferenceController(Context context) {
        super(context);
    }

    @Override
    public String getPreferenceKey() {
        return KEY_UI_BLUR;
    }

    @Override
    public void updateState(Preference preference) {
        int uiBlurValue = Settings.System.getInt(mContext.getContentResolver(),
                UI_BACKGROUND_BLUR, 0);
        ((SwitchPreference) preference).setChecked(uiBlurValue != 0);
    }

    @Override
    public boolean isAvailable() {
        return mBlurAvailable;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean uiBlurValue = (Boolean) newValue;
        Settings.System.putInt(mContext.getContentResolver(), UI_BACKGROUND_BLUR, uiBlurValue ? 1 : 0);
        return true;
    }
}
