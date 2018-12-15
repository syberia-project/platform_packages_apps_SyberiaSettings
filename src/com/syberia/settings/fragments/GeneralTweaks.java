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

import android.content.ContentResolver;
import android.os.Bundle;
import android.content.Context;
import android.content.res.Resources;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.ListPreference;
import androidx.preference.Preference.OnPreferenceChangeListener;

import com.android.settings.R;

import android.provider.SearchIndexableResource;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.syberia.settings.preference.AppMultiSelectListPreference;
import com.syberia.settings.preference.ScrollAppsViewPreference;
import com.syberia.settings.preference.CustomSeekBarPreference;
import com.syberia.settings.preference.SecureSettingSwitchPreference;

public class GeneralTweaks extends SettingsPreferenceFragment implements Indexable, OnPreferenceChangeListener {

	private static final String SCREEN_OFF_ANIMATION = "screen_off_animation";
	private static final String KEY_ASPECT_RATIO_APPS_ENABLED = "aspect_ratio_apps_enabled";
	private static final String KEY_ASPECT_RATIO_APPS_LIST = "aspect_ratio_apps_list";
	private static final String KEY_ASPECT_RATIO_CATEGORY = "aspect_ratio_category";
	private static final String KEY_ASPECT_RATIO_APPS_LIST_SCROLLER = "aspect_ratio_apps_list_scroller";
	private static final String SYSUI_ROUNDED_SIZE = "sysui_rounded_size";
	private static final String SYSUI_ROUNDED_CONTENT_PADDING = "sysui_rounded_content_padding";
	private static final String SYSUI_ROUNDED_FWVALS = "sysui_rounded_fwvals";
	private ListPreference mScreenOffAnimation;
	private ListPreference mVelocityFriction;
	private ListPreference mPositionFriction;
	private ListPreference mVelocityAmplitude;
	private AppMultiSelectListPreference mAspectRatioAppsSelect;
	private ScrollAppsViewPreference mAspectRatioApps;
	private CustomSeekBarPreference mCornerRadius;
	private CustomSeekBarPreference mContentPadding;
	private SecureSettingSwitchPreference mRoundedFwvals;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.general_tweaks);
		ContentResolver resolver = getActivity().getContentResolver();
        mScreenOffAnimation = (ListPreference) findPreference(SCREEN_OFF_ANIMATION);
        int screenOffStyle = Settings.System.getInt(resolver, Settings.System.SCREEN_OFF_ANIMATION, 0);
        mScreenOffAnimation.setValue(String.valueOf(screenOffStyle));
        mScreenOffAnimation.setSummary(mScreenOffAnimation.getEntry());
        mScreenOffAnimation.setOnPreferenceChangeListener(this);

        float velFriction = Settings.System.getFloatForUser(resolver,
                    Settings.System.STABILIZATION_VELOCITY_FRICTION,
                    0.1f,
                    UserHandle.USER_CURRENT);
        mVelocityFriction = (ListPreference) findPreference("stabilization_velocity_friction");
	mVelocityFriction.setValue(Float.toString(velFriction));
	mVelocityFriction.setSummary(mVelocityFriction.getEntry());
	mVelocityFriction.setOnPreferenceChangeListener(this);

	float posFriction = Settings.System.getFloatForUser(resolver,
                    Settings.System.STABILIZATION_POSITION_FRICTION,
                    0.1f,
                    UserHandle.USER_CURRENT);
            mPositionFriction = (ListPreference) findPreference("stabilization_position_friction");
	mPositionFriction.setValue(Float.toString(posFriction));
	mPositionFriction.setSummary(mPositionFriction.getEntry());
	mPositionFriction.setOnPreferenceChangeListener(this);

	int velAmplitude = Settings.System.getIntForUser(resolver,
                    Settings.System.STABILIZATION_VELOCITY_AMPLITUDE,
                    8000,
                    UserHandle.USER_CURRENT);
            mVelocityAmplitude = (ListPreference) findPreference("stabilization_velocity_amplitude");
	mVelocityAmplitude.setValue(Integer.toString(velAmplitude));
	mVelocityAmplitude.setSummary(mVelocityAmplitude.getEntry());
	mVelocityAmplitude.setOnPreferenceChangeListener(this);
	
	final PreferenceCategory aspectRatioCategory =
                (PreferenceCategory) getPreferenceScreen().findPreference(KEY_ASPECT_RATIO_CATEGORY);
        final boolean supportMaxAspectRatio =
                getResources().getBoolean(com.android.internal.R.bool.config_haveHigherAspectRatioScreen);
        if (!supportMaxAspectRatio) {
                getPreferenceScreen().removePreference(aspectRatioCategory);
        } else {
        mAspectRatioAppsSelect =
                (AppMultiSelectListPreference) findPreference(KEY_ASPECT_RATIO_APPS_LIST);
        mAspectRatioApps =
                (ScrollAppsViewPreference) findPreference(KEY_ASPECT_RATIO_APPS_LIST_SCROLLER);
        final String valuesString = Settings.System.getString(getContentResolver(),
                Settings.System.ASPECT_RATIO_APPS_LIST);
        List<String> valuesList = new ArrayList<String>();
        if (!TextUtils.isEmpty(valuesString)) {
            valuesList.addAll(Arrays.asList(valuesString.split(":")));
            mAspectRatioApps.setVisible(true);
            mAspectRatioApps.setValues(valuesList);
        } else {
            mAspectRatioApps.setVisible(false);
        }
        mAspectRatioAppsSelect.setValues(valuesList);
        mAspectRatioAppsSelect.setOnPreferenceChangeListener(this);
        }

	Resources res = null;
        Context ctx = getContext();
        float density = Resources.getSystem().getDisplayMetrics().density;

        try {
            res = ctx.getPackageManager().getResourcesForApplication("com.android.systemui");
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        // Rounded Corner Radius
        mCornerRadius = (CustomSeekBarPreference) findPreference(SYSUI_ROUNDED_SIZE);
        int resourceIdRadius = (int) ctx.getResources().getDimension(com.android.internal.R.dimen.rounded_corner_radius);
        int cornerRadius = Settings.Secure.getIntForUser(ctx.getContentResolver(), Settings.Secure.SYSUI_ROUNDED_SIZE,
                ((int) (resourceIdRadius / density)), UserHandle.USER_CURRENT);
        mCornerRadius.setValue(cornerRadius);
        mCornerRadius.setOnPreferenceChangeListener(this);

        // Rounded Content Padding
        mContentPadding = (CustomSeekBarPreference) findPreference(SYSUI_ROUNDED_CONTENT_PADDING);
        int resourceIdPadding = res.getIdentifier("com.android.systemui:dimen/rounded_corner_content_padding", null,
                null);
        int contentPadding = Settings.Secure.getIntForUser(ctx.getContentResolver(),
                Settings.Secure.SYSUI_ROUNDED_CONTENT_PADDING,
                (int) (res.getDimension(resourceIdPadding) / density), UserHandle.USER_CURRENT);
        mContentPadding.setValue(contentPadding);
        mContentPadding.setOnPreferenceChangeListener(this);

        // Rounded use Framework Values
        mRoundedFwvals = (SecureSettingSwitchPreference) findPreference(SYSUI_ROUNDED_FWVALS);
        mRoundedFwvals.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
		ContentResolver resolver = getActivity().getContentResolver();
		if (preference == mScreenOffAnimation) {
			String value = (String) newValue;
			Settings.System.putInt(resolver,
			Settings.System.SCREEN_OFF_ANIMATION, Integer.valueOf(value));
			int valueIndex = mScreenOffAnimation.findIndexOfValue(value);
			mScreenOffAnimation.setSummary(mScreenOffAnimation.getEntries()[valueIndex]);
			return true;
		} else if (preference == mVelocityFriction) {
			String value = (String) newValue;
			Settings.System.putFloatForUser(resolver,
			Settings.System.STABILIZATION_VELOCITY_FRICTION, Float.valueOf(value), UserHandle.USER_CURRENT);
			int valueIndex = mVelocityFriction.findIndexOfValue(value);
			mVelocityFriction.setSummary(mVelocityFriction.getEntries()[valueIndex]);
			} else if (preference == mPositionFriction) {
			String value = (String) newValue;
			Settings.System.putFloatForUser(resolver,
			Settings.System.STABILIZATION_POSITION_FRICTION, Float.valueOf(value), UserHandle.USER_CURRENT);
			int valueIndex = mPositionFriction.findIndexOfValue(value);
			mPositionFriction.setSummary(mPositionFriction.getEntries()[valueIndex]);
		} else if (preference == mVelocityAmplitude) {
			String value = (String) newValue;
			Settings.System.putFloatForUser(resolver,
			Settings.System.STABILIZATION_VELOCITY_AMPLITUDE, Float.valueOf(value), UserHandle.USER_CURRENT);
			int valueIndex = mVelocityAmplitude.findIndexOfValue(value);
			mVelocityAmplitude.setSummary(mVelocityAmplitude.getEntries()[valueIndex]);
			return true;
		} else if (preference == mAspectRatioAppsSelect) {
			Collection<String> valueList = (Collection<String>) newValue;
			mAspectRatioApps.setVisible(false);
			if (valueList != null) {
			    Settings.System.putString(getContentResolver(),
                                    Settings.System.ASPECT_RATIO_APPS_LIST, TextUtils.join(":", valueList));
                            mAspectRatioApps.setVisible(true);
                            mAspectRatioApps.setValues(valueList);
                       } else {
                            Settings.System.putString(getContentResolver(),
                            Settings.System.ASPECT_RATIO_APPS_LIST, "");
                       }
                       return true;
		} else if (preference == mCornerRadius) {
                        Settings.Secure.putIntForUser(getContext().getContentResolver(), Settings.Secure.SYSUI_ROUNDED_SIZE,
                                (int) objValue, UserHandle.USER_CURRENT);
                        return true;
                } else if (preference == mContentPadding) {
                        Settings.Secure.putIntForUser(getContext().getContentResolver(), Settings.Secure.SYSUI_ROUNDED_CONTENT_PADDING,
                                (int) objValue, UserHandle.USER_CURRENT);
                        eturn true;
                } else if (preference == mRoundedFwvals) {
                    restoreCorners();
                    return true;
		}        
    	return false;
    }
    
    private void restoreCorners() {
        Resources res = null;
        float density = Resources.getSystem().getDisplayMetrics().density;
        Context ctx = getContext();

        try {
            res = ctx.getPackageManager().getResourcesForApplication("com.android.systemui");
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        int resourceIdRadius = (int) ctx.getResources().getDimension(com.android.internal.R.dimen.rounded_corner_radius);
        int resourceIdPadding = res.getIdentifier("com.android.systemui:dimen/rounded_corner_content_padding", null, null);
        mCornerRadius.setValue((int) (resourceIdRadius / density));
        mContentPadding.setValue((int) (res.getDimension(resourceIdPadding) / density));
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
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    final ArrayList<SearchIndexableResource> result = new ArrayList<>();

                    final SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.general_tweaks;
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