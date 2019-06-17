/*
 * Copyright (C) 2019 The Potato Open Sauce Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.syberia.settings.fragments;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.SystemProperties;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v14.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import com.android.settings.R;
import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.wrapper.OverlayManagerWrapper;
import com.android.settings.wrapper.OverlayManagerWrapper.OverlayInfo;
import com.syberia.settings.preference.CustomSeekBarPreference;
import com.syberia.settings.preference.SecureSettingSwitchPreference;
import com.syberia.settings.preference.SystemSettingSwitchPreference;

import java.util.ArrayList;
import java.util.List;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class ThemeFragment extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener, Indexable {

    private static final String KEY_BASE_THEME = "base_theme";
    private static final String KEY_SYSUI_THEME = "systemui_theme";
    private static final String BASE_THEME_CATEGORY = "android.base_theme";

    private static final String KEY_SYSTEM_SHAPE = "system_shape";
    private static final String SYSTEM_SHAPE_CATEGORY = "android.shape";

    private static final String SYSUI_ROUNDED_SIZE = "sysui_rounded_size";
    private static final String SYSUI_ROUNDED_CONTENT_PADDING = "sysui_rounded_content_padding";
    private static final String SYSUI_ROUNDED_FWVALS = "sysui_rounded_fwvals";
    private static final String QS_PANEL_ALPHA = "qs_panel_alpha";
    private static final String QS_PANEL_COLOR = "qs_panel_color";
    private static final String QS_PANEL_USE_OREO_STYLE= "qs_panel_use_oreo_style";
    private static final String ACCENT_COLOR = "accent_color";
    private static final String ACCENT_COLOR_PROP = "persist.sys.theme.accentcolor";

    private Handler mHandler;

    private ColorPickerPreference mThemeColor;
    private ListPreference mSystemThemeBase;
    private Fragment mCurrentFragment = this;
    private OverlayManagerWrapper mOverlayService;
    private PackageManager mPackageManager;
    private Preference mSystemWideShape;
    private CustomSeekBarPreference mCornerRadius;
    private CustomSeekBarPreference mContentPadding;
    private SecureSettingSwitchPreference mRoundedFwvals;
    private ListPreference mSystemUiThemePref;
    private CustomSeekBarPreference mQsPanelAlpha;
    private ColorPickerPreference mQsPanelColor;
    private SystemSettingSwitchPreference mQsOreoStyle;
    private int mQsPanelAlphaValue;
    private boolean mChangeQsPanelAlpha = true;

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mSystemThemeBase) {
            String current = getTheme(BASE_THEME_CATEGORY);
            if (((String) newValue).equals(current))
                return true;
            mOverlayService.setEnabledExclusiveInCategory((String) newValue, UserHandle.myUserId());
            mSystemThemeBase.setSummary(getCurrentTheme(BASE_THEME_CATEGORY));
        } else if (preference == mSystemWideShape) {
            mSystemWideShape.setSummary(getCurrentTheme(SYSTEM_SHAPE_CATEGORY));
        } else if (preference == mCornerRadius) {
            Settings.Secure.putInt(getContext().getContentResolver(), Settings.Secure.SYSUI_ROUNDED_SIZE,
                    ((int) newValue) * 1);
        } else if (preference == mContentPadding) {
            Settings.Secure.putInt(getContext().getContentResolver(), Settings.Secure.SYSUI_ROUNDED_CONTENT_PADDING,
                    ((int) newValue) * 1);
        } else if (preference == mSystemUiThemePref) {
            int value = Integer.parseInt((String) newValue);
            Settings.Secure.putInt(getContext().getContentResolver(), Settings.Secure.THEME_MODE, value);
            mSystemUiThemePref.setSummary(mSystemUiThemePref.getEntries()[value]);
        } else if (preference == mQsPanelAlpha) {
            int qsTransparencyValue = (int) newValue;
            // Convert QS transparency value on scale of 0-100 to corresponding alpha values 255-100
            mQsPanelAlphaValue = (int) (255 - (qsTransparencyValue * 155 / 100));

            if (!mChangeQsPanelAlpha)
                return true;
            mChangeQsPanelAlpha = false;
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.QS_PANEL_BG_ALPHA, mQsPanelAlphaValue,
                    UserHandle.USER_CURRENT);
            mHandler.postDelayed(() -> {
                    Settings.System.putIntForUser(getContentResolver(),
                            Settings.System.QS_PANEL_BG_ALPHA, mQsPanelAlphaValue,
                            UserHandle.USER_CURRENT);
                    mChangeQsPanelAlpha = true;
                }, 1000);
        } else if (preference == mQsPanelColor) {
            int bgColor = (Integer) newValue;
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.QS_PANEL_BG_COLOR, bgColor,
                    UserHandle.USER_CURRENT);
        } else if (preference == mThemeColor) {
            int color = (Integer) newValue;
            String hexColor = String.format("%08X", (0xFFFFFFFF & color));
            SystemProperties.set(ACCENT_COLOR_PROP, hexColor);
            mOverlayService.reloadAndroidAssets(UserHandle.USER_CURRENT);
            mOverlayService.reloadAssets("com.android.settings", UserHandle.USER_CURRENT);
            mOverlayService.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
        } else if (preference == mRoundedFwvals) {
            restoreCorners();
        } else if (preference == mQsOreoStyle) {
            new RestartSystemUiTask(getContext()).execute();
        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.theme);
        // OMS and PMS setup
        mOverlayService = ServiceManager.getService(Context.OVERLAY_SERVICE) != null ? new OverlayManagerWrapper()
                : null;
        mPackageManager = getActivity().getPackageManager();
        mHandler = new Handler();
        setupBasePref();
        setupShapePref();
        setupCornerPrefs();
        setupStylePref();
        setupQsPrefs();
        setupAccentPref();
    }

    private void setupBasePref() {
        mSystemThemeBase = (ListPreference) findPreference(KEY_BASE_THEME);
        mSystemThemeBase.setSummary(getCurrentTheme(BASE_THEME_CATEGORY));

        String[] pkgs = getAvailableThemes(BASE_THEME_CATEGORY);
        CharSequence[] labels = new CharSequence[pkgs.length];
        for (int i = 0; i < pkgs.length; i++) {
            try {
                labels[i] = mPackageManager.getApplicationInfo(pkgs[i], 0).loadLabel(mPackageManager);
            } catch (PackageManager.NameNotFoundException e) {
                labels[i] = pkgs[i];
            }
        }

        mSystemThemeBase.setEntries(labels);
        mSystemThemeBase.setEntryValues(pkgs);
        mSystemThemeBase.setValue(getTheme(BASE_THEME_CATEGORY));
        mSystemThemeBase.setOnPreferenceChangeListener(this);
    }

    private void setupShapePref() {
        mSystemWideShape = (Preference) findPreference(KEY_SYSTEM_SHAPE);
        mSystemWideShape.setSummary(getCurrentTheme(SYSTEM_SHAPE_CATEGORY));
    }

    private void setupCornerPrefs() {
        Resources res = null;
        Context ctx = getContext();
        float density = Resources.getSystem().getDisplayMetrics().density;

        try {
            res = ctx.getPackageManager().getResourcesForApplication("com.android.systemui");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Rounded Corner Radius
        mCornerRadius = (CustomSeekBarPreference) findPreference(SYSUI_ROUNDED_SIZE);
        mCornerRadius.setOnPreferenceChangeListener(this);
        int resourceIdRadius = res.getIdentifier("com.android.systemui:dimen/rounded_corner_radius", null, null);
        int cornerRadius = Settings.Secure.getInt(ctx.getContentResolver(), Settings.Secure.SYSUI_ROUNDED_SIZE,
                (int) (res.getDimension(resourceIdRadius) / density));
        mCornerRadius.setValue(cornerRadius / 1);

        // Rounded Content Padding
        mContentPadding = (CustomSeekBarPreference) findPreference(SYSUI_ROUNDED_CONTENT_PADDING);
        mContentPadding.setOnPreferenceChangeListener(this);
        int resourceIdPadding = res.getIdentifier("com.android.systemui:dimen/rounded_corner_content_padding", null,
                null);
        int contentPadding = Settings.Secure.getInt(ctx.getContentResolver(),
                Settings.Secure.SYSUI_ROUNDED_CONTENT_PADDING,
                (int) (res.getDimension(resourceIdPadding) / density));
        mContentPadding.setValue(contentPadding / 1);

        // Rounded use Framework Values
        mRoundedFwvals = (SecureSettingSwitchPreference) findPreference(SYSUI_ROUNDED_FWVALS);
        mRoundedFwvals.setOnPreferenceChangeListener(this);
    }

    private void restoreCorners() {
        Resources res = null;
        float density = Resources.getSystem().getDisplayMetrics().density;

        try {
            res = getContext().getPackageManager().getResourcesForApplication("com.android.systemui");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        int resourceIdRadius = res.getIdentifier("com.android.systemui:dimen/rounded_corner_radius", null, null);
        int resourceIdPadding = res.getIdentifier("com.android.systemui:dimen/rounded_corner_content_padding", null,
                null);
        mCornerRadius.setValue((int) (res.getDimension(resourceIdRadius) / density));
        mContentPadding.setValue((int) (res.getDimension(resourceIdPadding) / density));
    }

    private void setupStylePref() {
        mSystemUiThemePref = (ListPreference) findPreference(KEY_SYSUI_THEME);
        int value = Settings.Secure.getInt(getContext().getContentResolver(), Settings.Secure.THEME_MODE, 0);
        int index = mSystemUiThemePref.findIndexOfValue(Integer.toString(value));
        mSystemUiThemePref.setValue(Integer.toString(value));
        mSystemUiThemePref.setSummary(mSystemUiThemePref.getEntries()[index]);
        mSystemUiThemePref.setOnPreferenceChangeListener(this);
    }

    private void setupQsPrefs() {
        mQsPanelAlpha = (CustomSeekBarPreference) findPreference(QS_PANEL_ALPHA);
        int qsPanelAlpha = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.QS_PANEL_BG_ALPHA, 255, UserHandle.USER_CURRENT);
        // Convert QS alpha values 100-255 to corresponding transparency value 100-0
        int qsTransparencyValue = (int) (100 - (qsPanelAlpha - 100) * 100 / 155);
        mQsPanelAlpha.setValue(qsTransparencyValue);
        mQsPanelAlpha.setOnPreferenceChangeListener(this);

        mQsPanelColor = (ColorPickerPreference) findPreference(QS_PANEL_COLOR);
        int QsColor = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.QS_PANEL_BG_COLOR, Color.WHITE, UserHandle.USER_CURRENT);
        mQsPanelColor.setNewPreviewColor(QsColor);
        mQsPanelColor.setOnPreferenceChangeListener(this);

        mQsOreoStyle = (SystemSettingSwitchPreference) findPreference(QS_PANEL_USE_OREO_STYLE);
        mQsOreoStyle.setOnPreferenceChangeListener(this);
    }

    private void setupAccentPref() {
        mThemeColor = (ColorPickerPreference) findPreference(ACCENT_COLOR);
        String colorVal = SystemProperties.get(ACCENT_COLOR_PROP, "-1");
        int color = "-1".equals(colorVal)
                ? Color.WHITE
                : Color.parseColor("#" + colorVal);
        mThemeColor.setNewPreviewColor(color);
        mThemeColor.setOnPreferenceChangeListener(this);
    }

    public void updateEnableState() {
        if (mSystemWideShape == null) {
            return;
        }
        mSystemWideShape.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ShapeDialog.show(mCurrentFragment, preference);
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateEnableState();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        @Override
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean enabled) {
            List<SearchIndexableResource> indexables = new ArrayList<>();
            SearchIndexableResource indexable = new SearchIndexableResource(context);
            indexable.xmlResId = R.xml.theme;
            indexables.add(indexable);
            return indexables;
        }

        @Override
        public List<String> getNonIndexableKeys(Context context) {
            List<String> keys = super.getNonIndexableKeys(context);
            return keys;
        }
    };

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SYBERIA;
    }

    // Theme/OMS handling methods
    private CharSequence getCurrentTheme(String category) {
        String currentPkg = getTheme(category);
        CharSequence label = null;
        try {
            label = mPackageManager.getApplicationInfo(currentPkg, 0).loadLabel(mPackageManager);
        } catch (PackageManager.NameNotFoundException e) {
            label = currentPkg;
        }
        return label;
    }

    private String[] getAvailableThemes(String category) {
        List<OverlayInfo> infos = mOverlayService.getOverlayInfosForTarget("android", UserHandle.myUserId());
        List<String> pkgs = new ArrayList<>(infos.size());
        for (int i = 0, size = infos.size(); i < size; i++) {
            if (isTheme(infos.get(i), category)) {
                pkgs.add(infos.get(i).packageName);
            }
        }
        return pkgs.toArray(new String[pkgs.size()]);
    }

    private String getTheme(String category) {
        List<OverlayInfo> infos = mOverlayService.getOverlayInfosForTarget("android", UserHandle.myUserId());
        for (int i = 0, size = infos.size(); i < size; i++) {
            if (infos.get(i).isEnabled() && isTheme(infos.get(i), category)) {
                return infos.get(i).packageName;
            }
        }
        return null;
    }

    private boolean isTheme(OverlayInfo oi, String category) {
        if (!category.equals(oi.category)) {
            return false;
        }
        try {
            PackageInfo pi = mPackageManager.getPackageInfo(oi.packageName, 0);
            return pi != null && !pi.isStaticOverlayPackage();
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private static class RestartSystemUiTask extends AsyncTask<Void, Void, Void> {
        private Context mContext;
        public RestartSystemUiTask(Context context) {
            super();
            mContext = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                 Thread.sleep(300); //0.3s
            } catch (InterruptedException ie) {}
            try {
                ActivityManager am =
                        (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
                IActivityManager ams = ActivityManager.getService();
                for (ActivityManager.RunningAppProcessInfo app: am.getRunningAppProcesses()) {
                    if ("com.android.systemui".equals(app.processName)) {
                        ams.killApplicationProcess(app.processName, app.uid);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
