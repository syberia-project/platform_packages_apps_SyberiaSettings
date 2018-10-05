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

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.internal.logging.nano.MetricsProto;

import com.syberia.settings.fragments.common.AboutTeamAdapter;
import com.syberia.settings.fragments.common.AboutTeamAdapter.About;
import com.syberia.settings.fragments.common.AboutTeamAdapter.Dev;
import com.syberia.settings.fragments.common.AboutTeamAdapter.Team;
import com.syberia.settings.fragments.common.AboutTeamAdapter.Header;
import com.syberia.settings.fragments.common.AboutTeamAdapter.OnClickListener;

import java.util.ArrayList;
import java.util.List;

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

    private void initList() {
        List<AboutTeamAdapter.Dev> team = new ArrayList<>();
        team.add(new AboutTeamAdapter.Dev(
                "DennySPb",
                "Lead ROM Developer",
                "https://syberiaos.com/img/avatars/DennySPb.jpg",
                "https://forum.xda-developers.com/member.php?u=3639510",
                "",
                "https://t.me/DennySPb"
        ));
        team.add(new AboutTeamAdapter.Dev(
                "blinoff82",
                "ROM Developer",
                "https://syberiaos.com/img/avatars/blinoff82.jpg",
                "https://forum.xda-developers.com/member.php?u=4937139",
                "",
                "https://t.me/blinoff82"
        ));
        team.add(new AboutTeamAdapter.Dev(
                "alexxxdev",
                "ROM Developer",
                "https://syberiaos.com/img/avatars/alexxxdev.jpg",
                "https://forum.xda-developers.com/member.php?u=3074355",
                "",
                "https://t.me/alexxxdev"
        ));
        team.add(new AboutTeamAdapter.Dev(
                "Lane Shukhov",
                "Designer, Site developer",
                "https://syberiaos.com/img/avatars/rlshukhov.jpg",
                "",
                "",
                "https://t.me/rlshukhov"
        ));
        list.add(new AboutTeamAdapter.Team(
                        "http://github.com/syberia-project",
                        "https://t.me/joinchat/AHkowkcfcWdzd5FpKZ3Hng",
                        team

                )
        );
        list.add(new AboutTeamAdapter.Header());
        list.add(new AboutTeamAdapter.Maintainer(
                        "Xiaomi Mi 5 (gemini)",
                        new AboutTeamAdapter.Dev(
                                "DennySPb",
                                "",
                                "https://syberiaos.com/img/avatars/DennySPb.jpg",
                                "https://forum.xda-developers.com/mi-5/development/rom-syberia-project-t3833868",
                                "https://github.com/DennySPB",
                                ""
                        )
                )
        );
        list.add(new AboutTeamAdapter.Maintainer(
                        "Xiaomi Mi 5 (gemini)",
                        new AboutTeamAdapter.Dev(
                                "blinoff82",
                                "",
                                "https://syberiaos.com/img/avatars/blinoff82.jpg",
                                "https://forum.xda-developers.com/mi-5/development/rom-syberia-project-t3833868",
                                "https://github.com/blinoff82",
                                ""
                        )
                )
        );
    }
}