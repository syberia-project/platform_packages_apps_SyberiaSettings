/*
 * Copyright © 2018 Syberia Project
 * Date: 13.09.2018
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

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Looper;
import android.provider.SearchIndexableResource;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.android.settings.R;

import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.Utils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;

import com.syberia.settings.preference.IncreasingRingVolumePreference;
import com.syberia.settings.controller.IncreasingRingPreferenceController;
import com.syberia.settings.controller.IncreasingRingVolumePreferenceController;

import com.android.internal.logging.nano.MetricsProto;

import java.util.ArrayList;
import java.util.List;

public class SystemSoundSettings extends DashboardFragment implements Indexable {
	private static final String TAG = "SystemSoundSettings";

	private final IncreasingRingVolumePreferenceCallback mIncreasingRingVolumeCallback = new IncreasingRingVolumePreferenceCallback();

	private static final int SAMPLE_CUTOFF = 2000;
	static final int STOP_SAMPLE = 1;

	final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STOP_SAMPLE:
                    mIncreasingRingVolumeCallback.stopSample();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SYBERIA;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.system_sound_settings;
    }

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, this, getLifecycle());
    }

	@Override
    public void onAttach(Context context) {
        super.onAttach(context);

        IncreasingRingVolumePreferenceController irvpc =
                use(IncreasingRingVolumePreferenceController.class);
        irvpc.setCallback(mIncreasingRingVolumeCallback);
        getLifecycle().addObserver(irvpc);
    }

	@Override
    public void onPause() {
        super.onPause();
        mIncreasingRingVolumeCallback.stopSample();
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(Context context, SystemSoundSettings fragment, Lifecycle lifecycle) {
     	final List<AbstractPreferenceController> controllers = new ArrayList<>();

		controllers.add(new IncreasingRingPreferenceController(context));
        controllers.add(new IncreasingRingVolumePreferenceController(context));        

        return controllers;
    }

    final class IncreasingRingVolumePreferenceCallback implements
            IncreasingRingVolumePreference.Callback {
        private IncreasingRingVolumePreference mPlayingPref;

        @Override
        public void onSampleStarting(IncreasingRingVolumePreference pref) {
            mPlayingPref = pref;
            mHandler.removeMessages(STOP_SAMPLE);
            mHandler.sendEmptyMessageDelayed(STOP_SAMPLE, SAMPLE_CUTOFF);
        }

        public void stopSample() {
            if (mPlayingPref != null) {
                mPlayingPref.stopSample();
                mPlayingPref = null;
            }
        }
    };

    @Override
    protected String getLogTag() {
        return TAG;
    }

    /**
     * For Search.
     */
    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {

                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    final ArrayList<SearchIndexableResource> result = new ArrayList<>();

                    final SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.system_sound_settings;
                    result.add(sir);
                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    final List<String> keys = super.getNonIndexableKeys(context);
                    return keys;
                }
    };
}