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

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.content.res.Resources;
import android.os.PowerManager;
import android.os.ServiceManager;
import android.text.TextUtils;

import java.io.FileDescriptor;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.internal.logging.nano.MetricsProto;

import com.syberia.settings.preference.CustomSeekBarPreference;
import com.syberia.settings.preference.ActionFragment;

import com.android.internal.util.hwkeys.ActionConstants;
import com.android.internal.util.hwkeys.ActionUtils;

public class ButtonsSettings extends ActionFragment implements OnPreferenceChangeListener{

    private static final String TORCH_POWER_BUTTON_GESTURE = "torch_power_button_gesture";

    private static final String KEY_BUTTON_MANUAL_BRIGHTNESS_NEW = "button_manual_brightness_new";
    private static final String KEY_BUTTON_TIMEOUT = "button_timeout";
    private static final String KEY_BUTON_BACKLIGHT_OPTIONS = "button_backlight_options_category";
    private static final String KEY_VOLUME_KEY_CURSOR_CONTROL = "volume_key_cursor_control";

    private static final String FINGERPRINT_CUSTOM_ICON = "custom_fingerprint_icon";
    private static final int GET_CUSTOM_FP_ICON = 69;

    private static final String CATEGORY_HWKEY = "hardware_keys";
    private static final String CATEGORY_HOME = "home_key";
    private static final String CATEGORY_MENU = "menu_key";
    private static final String CATEGORY_BACK = "back_key";
    private static final String CATEGORY_ASSIST = "assist_key";
    private static final String CATEGORY_APPSWITCH = "app_switch_key";
    private static final String HWKEY_DISABLE = "hardware_keys_disable";

    private ListPreference mTorchPowerButton;
    private ListPreference mVolumeKeyCursorControl;
    private SwitchPreference mHwKeyDisable;

    private CustomSeekBarPreference mButtonTimoutBar;
    private CustomSeekBarPreference mManualButtonBrightness;
    private PreferenceCategory mButtonBackLightCategory;

    private Preference mFilePicker;

    // Masks for checking presence of hardware keys.
    // Must match values in frameworks/base/core/res/res/values/config.xml
    // Masks for checking presence of hardware keys.
    // Must match values in frameworks/base/core/res/res/values/config.xml
    public static final int KEY_MASK_HOME = 0x01;
    public static final int KEY_MASK_BACK = 0x02;
    public static final int KEY_MASK_MENU = 0x04;
    public static final int KEY_MASK_ASSIST = 0x08;
    public static final int KEY_MASK_APP_SWITCH = 0x10;
    public static final int KEY_MASK_CAMERA = 0x20;
    public static final int KEY_MASK_VOLUME = 0x40;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.buttons_settings);
        final Resources res = getResources();
        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();

        mFilePicker = (Preference) findPreference(FINGERPRINT_CUSTOM_ICON);

        boolean isFODDevice = getResources().getBoolean(com.android.internal.R.bool.config_needCustomFODView);
        if (!isFODDevice){
            removePreference(FINGERPRINT_CUSTOM_ICON);
        } else {
            final String customIconURI = Settings.System.getString(resolver,
                Settings.System.OMNI_CUSTOM_FP_ICON);

            if (!TextUtils.isEmpty(customIconURI)) {
                setPickerIcon(customIconURI);
                mFilePicker.setSummary(customIconURI);
            }

            mFilePicker.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/png");

                    startActivityForResult(intent, GET_CUSTOM_FP_ICON);

                    return true;
                }
            });
        }

	// Cursor volume keys
        int cursorControlAction = Settings.System.getInt(resolver,
                Settings.System.VOLUME_KEY_CURSOR_CONTROL, 0);
        mVolumeKeyCursorControl = initActionList(KEY_VOLUME_KEY_CURSOR_CONTROL,
                cursorControlAction);

	mManualButtonBrightness = (CustomSeekBarPreference) findPreference(
                KEY_BUTTON_MANUAL_BRIGHTNESS_NEW);
        final int customButtonBrightness = getResources().getInteger(
                com.android.internal.R.integer.config_button_brightness_default);
        final int currentBrightness = Settings.System.getInt(resolver,
                Settings.System.CUSTOM_BUTTON_BRIGHTNESS, customButtonBrightness);
        PowerManager pm = (PowerManager)getActivity().getSystemService(Context.POWER_SERVICE);
        mManualButtonBrightness.setMax(pm.getMaximumScreenBrightnessSetting());
        mManualButtonBrightness.setValue(currentBrightness);
        mManualButtonBrightness.setOnPreferenceChangeListener(this);
         mButtonTimoutBar = (CustomSeekBarPreference) findPreference(KEY_BUTTON_TIMEOUT);
        int currentTimeout = Settings.System.getInt(resolver,
                Settings.System.BUTTON_BACKLIGHT_TIMEOUT, 0);
        mButtonTimoutBar.setValue(currentTimeout);
        mButtonTimoutBar.setOnPreferenceChangeListener(this);
         final boolean enableBacklightOptions = getResources().getBoolean(
                com.android.internal.R.bool.config_button_brightness_support);
         mButtonBackLightCategory = (PreferenceCategory) findPreference(KEY_BUTON_BACKLIGHT_OPTIONS);
        if (!enableBacklightOptions) {
            prefScreen.removePreference(mButtonBackLightCategory);
        }

        final boolean needsNavbar = ActionUtils.hasNavbarByDefault(getActivity());
        final PreferenceCategory hwkeyCat = (PreferenceCategory) prefScreen.findPreference(CATEGORY_HWKEY);
        int keysDisabled = 0;
        if (!needsNavbar) {
            mHwKeyDisable = (SwitchPreference) hwkeyCat.findPreference(HWKEY_DISABLE);
            keysDisabled = Settings.Secure.getIntForUser(getContentResolver(), Settings.Secure.HARDWARE_KEYS_DISABLE, 0, UserHandle.USER_CURRENT);
            mHwKeyDisable.setChecked(keysDisabled != 0);
            mHwKeyDisable.setOnPreferenceChangeListener(this);
	}
	mTorchPowerButton = (ListPreference) findPreference(TORCH_POWER_BUTTON_GESTURE);
        int mTorchPowerButtonValue = Settings.System.getInt(resolver,
                    Settings.System.TORCH_POWER_BUTTON_GESTURE, 0);
        mTorchPowerButton.setValue(Integer.toString(mTorchPowerButtonValue));
        mTorchPowerButton.setSummary(mTorchPowerButton.getEntry());
        mTorchPowerButton.setOnPreferenceChangeListener(this);
	
        // bits for hardware keys present on device
        final int deviceKeys = getResources().getInteger(
        com.android.internal.R.integer.config_deviceHardwareKeys);
        // read bits for present hardware keys
        final boolean hasHomeKey = (deviceKeys & KEY_MASK_HOME) != 0;
        final boolean hasBackKey = (deviceKeys & KEY_MASK_BACK) != 0;
        final boolean hasMenuKey = (deviceKeys & KEY_MASK_MENU) != 0;
        final boolean hasAssistKey = (deviceKeys & KEY_MASK_ASSIST) != 0;
        final boolean hasAppSwitchKey = (deviceKeys & KEY_MASK_APP_SWITCH) != 0;
        // load categories and init/remove preferences based on device
        // configuration
        final PreferenceCategory backCategory =
        (PreferenceCategory) prefScreen.findPreference(CATEGORY_BACK);
        final PreferenceCategory homeCategory =
        (PreferenceCategory) prefScreen.findPreference(CATEGORY_HOME);
        final PreferenceCategory menuCategory =
        (PreferenceCategory) prefScreen.findPreference(CATEGORY_MENU);
        final PreferenceCategory assistCategory =
        (PreferenceCategory) prefScreen.findPreference(CATEGORY_ASSIST);
        final PreferenceCategory appSwitchCategory =
        (PreferenceCategory) prefScreen.findPreference(CATEGORY_APPSWITCH);
        // back key
        if (!hasBackKey) {
            prefScreen.removePreference(backCategory);
        }

        // home key
        if (!hasHomeKey) {
            prefScreen.removePreference(homeCategory);
        }
        // App switch key (recents)
        if (!hasAppSwitchKey) {
            prefScreen.removePreference(appSwitchCategory);
        }
        // menu key
        if (!hasMenuKey) {
            prefScreen.removePreference(menuCategory);
        }
        // search/assist key
        if (!hasAssistKey) {
            prefScreen.removePreference(assistCategory);
        }
        // let super know we can load ActionPreferences
        onPreferenceScreenLoaded(ActionConstants.getDefaults(ActionConstants.HWKEYS));
        // load preferences first
        setActionPreferencesEnabled(keysDisabled == 0);

    }

    private ListPreference initActionList(String key, int value) {
        ListPreference list = (ListPreference) getPreferenceScreen().findPreference(key);
        list.setValue(Integer.toString(value));
        list.setSummary(list.getEntry());
        list.setOnPreferenceChangeListener(this);
        return list;
    }

    private void handleActionListChange(ListPreference pref, Object newValue, String setting) {
        String value = (String) newValue;
        int index = pref.findIndexOfValue(value);
         pref.setSummary(pref.getEntries()[index]);
        Settings.System.putInt(getContentResolver(), setting, Integer.valueOf(value));
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SYBERIA;
    }

    @Override
    protected boolean usesExtendedActionsList() {
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
        Intent resultData) {
        if (requestCode == GET_CUSTOM_FP_ICON && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                mFilePicker.setSummary(uri.toString());
                setPickerIcon(uri.toString());
                Settings.System.putString(getContentResolver(), Settings.System.OMNI_CUSTOM_FP_ICON,
                    uri.toString());
            }
        } else if (requestCode == GET_CUSTOM_FP_ICON && resultCode == Activity.RESULT_CANCELED) {
            mFilePicker.setSummary("");
            mFilePicker.setIcon(new ColorDrawable(Color.TRANSPARENT));
            Settings.System.putString(getContentResolver(), Settings.System.OMNI_CUSTOM_FP_ICON, "");
        }
    }

    private void setPickerIcon(String uri) {
        try {
                ParcelFileDescriptor parcelFileDescriptor =
                    getContext().getContentResolver().openFileDescriptor(Uri.parse(uri), "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                parcelFileDescriptor.close();
                Drawable d = new BitmapDrawable(getResources(), image);
                mFilePicker.setIcon(d);
            }
            catch (Exception e) {}
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
		ContentResolver resolver = getActivity().getContentResolver();
	if (preference == mHwKeyDisable) {
            boolean value = (Boolean) newValue;
            Settings.Secure.putInt(getActivity().getContentResolver(), Settings.Secure.HARDWARE_KEYS_DISABLE,
                    value ? 1 : 0);
            setActionPreferencesEnabled(!value);
            return true;
        } else  if (preference == mTorchPowerButton) {
            int mTorchPowerButtonValue = Integer.valueOf((String) newValue);
            int index = mTorchPowerButton.findIndexOfValue((String) newValue);
            mTorchPowerButton.setSummary(
                    mTorchPowerButton.getEntries()[index]);
            Settings.System.putInt(getActivity().getContentResolver(), Settings.System.TORCH_POWER_BUTTON_GESTURE,
                    mTorchPowerButtonValue);
            if (mTorchPowerButtonValue == 1) {
                //if doubletap for torch is enabled, switch off double tap for camera
                Settings.Secure.putInt(getActivity().getContentResolver(), Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED,1);
	    }
        } else if (preference == mButtonTimoutBar) {
            int buttonTimeout = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.BUTTON_BACKLIGHT_TIMEOUT, buttonTimeout);
        } else if (preference == mManualButtonBrightness) {
            int buttonBrightness = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.CUSTOM_BUTTON_BRIGHTNESS, buttonBrightness);
        } else if (preference == mVolumeKeyCursorControl) {
            handleActionListChange(mVolumeKeyCursorControl, newValue,
                    Settings.System.VOLUME_KEY_CURSOR_CONTROL);
        } else {
	    return false; 
	}
        return true;
    }
}