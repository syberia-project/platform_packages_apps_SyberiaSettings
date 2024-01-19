/*
 * Copyright © 2018-2021 Syberia Project
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
import android.provider.SearchIndexableResource;

import android.provider.Settings;
import android.content.Context;
import com.android.settings.R;
import android.content.ContentResolver;
import android.os.UserHandle;

import com.android.settings.development.OverlayCategoryPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import androidx.preference.ListPreference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.syberia.settings.preference.SystemSettingListPreference;
import com.syberia.settings.preference.SecureSettingMasterSwitchPreference;
import com.android.internal.util.syberia.ThemeUtils;

@SearchIndexable
public class QSPanelSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String QUICK_PULLDOWN = "status_bar_quick_qs_pulldown";
    private static final String KEY_QS_UI_STYLE  = "qs_tile_ui_style";

    private ListPreference mQuickPulldown;
    private ListPreference mQsUI;

    private static ThemeUtils mThemeUtils;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.qs_panel_settings);
        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();
        mThemeUtils = new ThemeUtils(getActivity());

        int qpmode = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.STATUS_BAR_QUICK_QS_PULLDOWN, 0, UserHandle.USER_CURRENT);
        mQuickPulldown = (ListPreference) findPreference(QUICK_PULLDOWN);
        mQuickPulldown.setValue(String.valueOf(qpmode));
        mQuickPulldown.setSummary(mQuickPulldown.getEntry());
        mQuickPulldown.setOnPreferenceChangeListener(this);

        String isA11Style = Integer.toString(Settings.System.getIntForUser(resolver,
                Settings.System.QS_TILE_UI_STYLE , 0, UserHandle.USER_CURRENT));

        mQsUI = (ListPreference) findPreference(KEY_QS_UI_STYLE);
        int index = mQsUI.findIndexOfValue(isA11Style);
        mQsUI.setValue(isA11Style);
        mQsUI.setSummary(mQsUI.getEntries()[index]);
        mQsUI.setOnPreferenceChangeListener(this);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mQuickPulldown) {
            int value = Integer.parseInt((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.STATUS_BAR_QUICK_QS_PULLDOWN, value,
                    UserHandle.USER_CURRENT);
            int index = mQuickPulldown.findIndexOfValue((String) newValue);
            mQuickPulldown.setSummary(
                    mQuickPulldown.getEntries()[index]);
            return true;
        } else if (preference == mQsUI) {
            int value = Integer.parseInt((String) newValue);
            int index = mQsUI.findIndexOfValue((String) newValue);
            mQsUI.setValue((String) newValue);
            mQsUI.setSummary(mQsUI.getEntries()[index]);
            Settings.System.putIntForUser(resolver,
                    Settings.System.QS_TILE_UI_STYLE, value, UserHandle.USER_CURRENT);
            updateQsStyle(getActivity());
            return true;
        }
        return false;
    }

    private static void updateQsStyle(Context context) {
        ContentResolver resolver = context.getContentResolver();

        boolean isA11Style = Settings.System.getIntForUser(resolver,
                Settings.System.QS_TILE_UI_STYLE , 0, UserHandle.USER_CURRENT) != 0;

        String qsUIStyleCategory = "android.theme.customization.qs_ui";
        String overlayThemeTarget  = "com.android.systemui";
        String overlayThemePackage  = "com.android.system.qs.ui.A11";

        if (mThemeUtils == null) {
            mThemeUtils = new ThemeUtils(context);
        }

        // reset all overlays before applying
        mThemeUtils.setOverlayEnabled(qsUIStyleCategory, overlayThemeTarget, overlayThemeTarget);

        if (isA11Style) {
            mThemeUtils.setOverlayEnabled(qsUIStyleCategory, overlayThemePackage, overlayThemeTarget);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SYBERIA;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.qs_panel_settings);
}
