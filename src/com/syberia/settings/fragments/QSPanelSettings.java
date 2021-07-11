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

import android.provider.SearchIndexableResource;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.content.Context;
import com.android.settings.R;

import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;
import androidx.preference.ListPreference;
import androidx.preference.SwitchPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.syberia.settings.preference.SystemSettingListPreference;

@SearchIndexable
public class QSPanelSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

	private static final String SLIDER_STYLE  = "slider_style";

	private Handler mHandler;

	private SystemSettingListPreference mSlider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.qs_panel_settings);
        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mSlider = (SystemSettingListPreference) findPreference(SLIDER_STYLE);
        mCustomSettingsObserver.observe();
    }

    private CustomSettingsObserver mCustomSettingsObserver = new CustomSettingsObserver(mHandler);
    private class CustomSettingsObserver extends ContentObserver {

        CustomSettingsObserver(Handler handler) {
            super(handler);
        }

        void observe() {
            Context mContext = getContext();
            ContentResolver resolver = mContext.getContentResolver();
            resolver.registerContentObserver(Settings.System.getUriFor(
                    Settings.System.SLIDER_STYLE),
                    false, this, UserHandle.USER_ALL);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if (uri.equals(Settings.System.getUriFor(Settings.System.SLIDER_STYLE))) {
                updateSlider();
            }
        }
    }

    private void updateSlider() {
        ContentResolver resolver = getActivity().getContentResolver();

        boolean sliderDefault = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SLIDER_STYLE , 0, UserHandle.USER_CURRENT) == 0;
        boolean sliderOOS = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SLIDER_STYLE , 0, UserHandle.USER_CURRENT) == 1;
        boolean sliderAosp = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SLIDER_STYLE , 0, UserHandle.USER_CURRENT) == 2;
        boolean sliderRUI = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SLIDER_STYLE , 0, UserHandle.USER_CURRENT) == 3;
        boolean sliderA12 = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SLIDER_STYLE , 0, UserHandle.USER_CURRENT) == 4;

        if (sliderDefault) {
            setDefaultSlider(mOverlayService);
        } else if (sliderOOS) {
            enableSlider(mOverlayService, "com.android.theme.systemui_slider_oos");
        } else if (sliderAosp) {
            enableSlider(mOverlayService, "com.android.theme.systemui_slider.aosp");
        } else if (sliderRUI) {
            enableSlider(mOverlayService, "com.android.theme.systemui_slider.rui");
        } else if (sliderA12) {
            enableSlider(mOverlayService, "com.android.theme.systemui_slider.a12");
        }
    }

    public static void setDefaultSlider(IOverlayManager overlayManager) {
        for (int i = 0; i < SLIDERS.length; i++) {
            String sliders = SLIDERS[i];
            try {
                overlayManager.setEnabled(sliders, false, USER_SYSTEM);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static void enableSlider(IOverlayManager overlayManager, String overlayName) {
        try {
            for (int i = 0; i < SLIDERS.length; i++) {
                String sliders = SLIDERS[i];
                try {
                    overlayManager.setEnabled(sliders, false, USER_SYSTEM);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            overlayManager.setEnabled(overlayName, true, USER_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static final String[] SLIDERS = {
        "com.android.theme.systemui_slider_oos",
        "com.android.theme.systemui_slider.aosp",
        "com.android.theme.systemui_slider.rui",
        "com.android.theme.systemui_slider.a12"
    };

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference == mSlider) {
		mCustomSettingsObserver.observe();
        return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SYBERIA;
    }


    /**
     * For Search.
     */
    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {

                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(
                        Context context, boolean enabled) {
                    final SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.qs_panel_settings;
                    return Arrays.asList(sir);
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    final List<String> keys = super.getNonIndexableKeys(context);
                    return keys;
                }
    };
}