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
        list.add(new AboutTeamAdapter.Maintainer(
                        "Xiaomi Mi 5s (capricorn)",
                        new AboutTeamAdapter.Dev(
                                "mesziman",
                                "",
                                "https://avatars1.githubusercontent.com/u/4874388?s=400&v=4",
                                "https://forum.xda-developers.com/mi-5s/development/rom-syberia-rom-1-0-t3835690",
                                "https://github.com/mesziman",
                                ""
                        )
                )
        );
        list.add(new AboutTeamAdapter.Maintainer(
                        "Xiaomi Redmi Note 3 (kenzo)",
                        new AboutTeamAdapter.Dev(
                                "Amitava_123",
                                "",
                                "https://avatars0.githubusercontent.com/u/26063103?s=400&v=4",
                                "https://forum.xda-developers.com/redmi-note-3/development/rom-syberia-project-t3837399",
                                "https://github.com/Amitava123",
                                ""
                        )
                )
        );
        list.add(new AboutTeamAdapter.Maintainer(
                        "Xiaomi Redmi Note 4 (mido)",
                        new AboutTeamAdapter.Dev(
                                "NATO666613",
                                "",
                                "https://avatars3.githubusercontent.com/u/7190697?s=400&v=4",
                                "https://forum.xda-developers.com/redmi-note-4/xiaomi-redmi-note-4-snapdragon-roms-kernels-recoveries--other-development/rom-syberia-project-t3846212",
                                "https://github.com/kondors1995",
                                ""
                        )
                )
        );
        list.add(new AboutTeamAdapter.Maintainer(
                        "Xiaomi Mi Mix 2 (chiron)",
                        new AboutTeamAdapter.Dev(
                                "AleD219",
                                "",
                                "https://avatars0.githubusercontent.com/u/40178945?s=400&v=4",
                                "https://forum.xda-developers.com/mi-mix-2/development/rom-syberia-project-t3847007",
                                "https://github.com/AleD219",
                                ""
                        )
                )
        );
        list.add(new AboutTeamAdapter.Maintainer(
                        "Asus ZenFone Max Pro M1 (X00TD)",
                        new AboutTeamAdapter.Dev(
                                "thetoymaker",
                                "",
                                "https://avatars2.githubusercontent.com/u/37396330?s=400&v=4",
                                "https://forum.xda-developers.com/asus-zenfone-max-pro-m1/development/rom-syberia-project-t3848146",
                                "https://github.com/Toymakerftw",
                                ""
                        )
                )
        );
	list.add(new AboutTeamAdapter.Maintainer(
                        "Xiaomi Redmi Note 5 (whyred)",
                        new AboutTeamAdapter.Dev(
                                "federicobenedetti",
                                "",
                                "https://avatars0.githubusercontent.com/u/26311786?s=400&v=4",
                                "https://forum.xda-developers.com/redmi-note-5-pro/development/rom-syberia-project-t3863434",
                                "https://github.com/federicobenedetti",
                                ""
                        )
                )
        );
	list.add(new AboutTeamAdapter.Maintainer(
                        "Xiaomi Redmi 3s (land)",
                        new AboutTeamAdapter.Dev(
                                "Weritos666",
                                "",
                                "https://avatars2.githubusercontent.com/u/3286052?s=400&v=4",
                                "https://forum.xda-developers.com/xiaomi-redmi-3s/development/9-0-syberia-os-9-0-t3837103",
                                "https://github.com/weritos666",
                                ""
                        )
                )
        );
	list.add(new AboutTeamAdapter.Maintainer(
                        "Moto G5 Plus (potter)",
                        new AboutTeamAdapter.Dev(
                                "Ashwin4RC",
                                "",
                                "https://avatars0.githubusercontent.com/u/40515149?s=400&v=4",
                                "https://forum.xda-developers.com/member.php?u=8866469",
                                "https://github.com/ashwin4rc",
                                ""
                        )
                )
        );
	list.add(new AboutTeamAdapter.Maintainer(
                        "Xiaomi Poco F1 (beryllium)",
                        new AboutTeamAdapter.Dev(
                                "fir3walk",
                                "",
                                "https://avatars0.githubusercontent.com/u/18166102?s=400&v=4",
                                "https://forum.xda-developers.com/poco-f1/development/rom-syberia-project-t3862622",
                                "https://github.com/fir3walk",
                                ""
                        )
                )
        );
	list.add(new AboutTeamAdapter.Maintainer(
                        "Xiaomi Mi 5s+ (natrium)",
                        new AboutTeamAdapter.Dev(
                                "cfdddd",
                                "",
                                "https://avatars0.githubusercontent.com/u/31411742?s=400&v=4",
                                "https://forum.xda-developers.com/mi-5s-plus/development/rom-syberia-project-t3869145",
                                "https://github.com/00day0",
                                ""
                        )
                )
        );

    }
}