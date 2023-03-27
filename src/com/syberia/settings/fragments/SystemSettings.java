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
import android.content.Context;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import androidx.preference.PreferenceScreen;
import androidx.preference.Preference;

import android.provider.SearchIndexableResource;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.android.internal.util.syberia.UdfpsUtils;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.syberia.SyberiaUtils;

@SearchIndexable
public class SystemSettings extends SettingsPreferenceFragment {

    private static final String UDFPS_SETTINGS = "udfps_settings";

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        final boolean udfpsResPkgInstalled = SyberiaUtils.isPackageInstalled(getContext(),
                "com.syberia.udfps.resources");

        addPreferencesFromResource(R.xml.system_settings);
        final PreferenceScreen prefScreen = getPreferenceScreen();
        if (!UdfpsUtils.hasUdfpsSupport(getContext()) || !udfpsResPkgInstalled) {
            prefScreen.removePreference(findPreference(UDFPS_SETTINGS));
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SYBERIA;
    }

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {

                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(
                        Context context, boolean enabled) {
                    final SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.system_settings;
                    return Arrays.asList(sir);
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    final List<String> keys = super.getNonIndexableKeys(context);
                    return keys;
                }
    };

}
