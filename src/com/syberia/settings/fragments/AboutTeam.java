/*
 * Copyright © 2018 Syberia Project
 * Date: 05.10.2018
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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.internal.logging.nano.MetricsProto;

import com.syberia.settings.fragments.common.AboutTeamAdapter;
import com.syberia.settings.fragments.common.AboutTeamAdapter.About;
import com.syberia.settings.fragments.common.AboutTeamAdapter.Dev;
import com.syberia.settings.fragments.common.AboutTeamAdapter.TeamHeader;
import com.syberia.settings.fragments.common.AboutTeamAdapter.Header;
import com.syberia.settings.fragments.common.AboutTeamAdapter.OnClickListener;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;
import java.io.InputStream;
import java.lang.Exception;

import android.widget.Toast;

public class AboutTeam extends SettingsPreferenceFragment {

    private List<AboutTeamAdapter.About> list = new ArrayList<>();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.about_team, null);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.about_team_title);
        initList();

        RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new AboutTeamAdapter(list, new AboutTeamAdapter.OnClickListener() {
            @Override
            public void OnClick(String url) {
                if (!url.isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
            }
        }));
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SYBERIA;
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("syberia.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            //Toast.makeText(getActivity(), ex.toString(), Toast.LENGTH_LONG).show();
            return null;
        }
        return json;
    }

    private void initList() {
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            String teamGithub = "";
            String teamTelegram = "";

            if(obj.has("team_github")) teamGithub = obj.getString("team_github");
            if(obj.has("team_telegram")) teamTelegram = obj.getString("team_telegram");

            list.add(new AboutTeamAdapter.TeamHeader(teamGithub,teamTelegram));

            if(obj.has("team")){
                list.add(new AboutTeamAdapter.Header("Team"));
                JSONArray team = obj.getJSONArray("team");
                for (int i = 0; i < team.length(); i++) {
                    JSONObject dev = team.getJSONObject(i);
                    list.add(new AboutTeamAdapter.Dev(
                            dev.getString("name"),
                            dev.getString("role"),
                            dev.getString("avatar"),
                            dev.getString("xda_link"),
                            "",
                            dev.getString("telegram_link")
                    ));
                }
            }
            if(obj.has("maintainers")){
                list.add(new AboutTeamAdapter.Header("Maintainers"));
                JSONArray maintainers = obj.getJSONArray("maintainers");
                for (int i = 0; i < maintainers.length(); i++) {
                    JSONObject maintainer = maintainers.getJSONObject(i);
                    list.add(new AboutTeamAdapter.Maintainer(
                                            maintainer.getString("device"),
                                            new AboutTeamAdapter.Dev(
                                                    maintainer.getString("name"),
                                                    "",
                                                    maintainer.getString("avatar"),
                                                    maintainer.getString("xda_link"),
                                                    maintainer.getString("github_link"),
                                                    ""
                                            )
                                    )
                            );
                }
            }
        } catch (Exception e) {
            //Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }
}