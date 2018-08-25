/*
* Copyright © 2018 Syberia Project
* Date: 26.08.2018
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

package com.syberia.settings.deviceinfo;

import android.content.Context;
import android.os.SystemProperties;
import android.support.v7.preference.Preference;
import android.text.TextUtils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settings.core.PreferenceControllerMixin;

public class SyberiaVersionPreferenceController extends AbstractPreferenceController implements	PreferenceControllerMixin  {

	private static final String KEY_MOD_SYBERIA_VERSION = "syberia_version";
	private static final String PROPERTY_SYBERIA_VERSION = "ro.syberia.version";

	public SyberiaVersionPreferenceController(Context context) {
		super(context);
	}
	
	@Override
	public boolean isAvailable() {
		return !TextUtils.isEmpty(SystemProperties.get(PROPERTY_SYBERIA_VERSION));
	}
	
	@Override
	public void updateState(Preference preference) {
		super.updateState(preference);
		preference.setSummary(SystemProperties.get(PROPERTY_SYBERIA_VERSION));
	}
	
	@Override
	public String getPreferenceKey() {
		return KEY_MOD_SYBERIA_VERSION;
	}
}