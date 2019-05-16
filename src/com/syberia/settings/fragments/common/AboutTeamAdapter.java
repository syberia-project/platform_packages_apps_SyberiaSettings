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

package com.syberia.settings.fragments.common;

import android.graphics.Bitmap;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.settings.R;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.ArrayList;
import java.util.List;

public class AboutTeamAdapter extends RecyclerView.Adapter<AboutTeamAdapter.Holder> {
    private static final int TYPE_TEAM_HEADER = R.layout.team_header;
    private static final int TYPE_TEAM_DEV = R.layout.team_card;
    private static final int TYPE_HEADER = R.layout.maintainers_header;
    private static final int TYPE_MAINTAINER = R.layout.maintainer_card;

    private List<About> list = new ArrayList<>();

    private OnClickListener listener;

    public AboutTeamAdapter(List<About> list, OnClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(viewType, viewGroup, false);
        switch (viewType) {
            case TYPE_TEAM_HEADER:
                return new TeamHeaderViewHolder(view, listener);
            case TYPE_TEAM_DEV:
                return new DevViewHolder(view, listener);
            case TYPE_HEADER:
                return new HeaderHolder(view, listener);
            default:
                return new MaintainerHolder(view, listener);
        }
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {
        viewHolder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        About item = list.get(position);
        if(item instanceof TeamHeader){
            return TYPE_TEAM_HEADER;
        } else if(item instanceof Dev){
            return TYPE_TEAM_DEV;
        } else if(item instanceof Header){
            return TYPE_HEADER;
        } else {
            return TYPE_MAINTAINER;
        }
    }

    public abstract static class About { }

    public static class Header extends About {
        public String title;

        public Header(String title) {
            this.title = title;
        }
    }

    public static class TeamHeader extends About {
        public String teamGithub;
        public String teamTelegram;

        public TeamHeader(String gihub, String telegram) {
            teamGithub = gihub;
            teamTelegram = telegram;
        }
    }

    public static class Dev extends About {
        public String devAvatarUrl;
        public String devName;
        public String devRole;
        public String devXDA;
        public String devGithub;
        public String devTelegram;

        public Dev(String devName, String devRole, String devAvatarUrl, String devXDA, String devGithub, String devTelegram) {
            this.devName = devName;
            this.devRole = devRole;
            this.devAvatarUrl = devAvatarUrl;
            this.devXDA = devXDA;
            this.devGithub = devGithub;
            this.devTelegram = devTelegram;
        }
    }

    public static class Maintainer extends About {
        public Dev dev;
        public String device;

        public Maintainer(String device, Dev dev) {
            this.dev = dev;
            this.device = device;
        }
    }

    static abstract class Holder extends RecyclerView.ViewHolder {

        public Holder(View itemView, OnClickListener listener) {
            super(itemView);
        }

        abstract void bind(About about);
    }

    static class TeamHeaderViewHolder extends Holder {
        private ImageButton teamGithub;
        private ImageButton teamTelegram;

        private TeamHeader team;

        public TeamHeaderViewHolder(View itemView, final OnClickListener listener) {
            super(itemView, listener);
            teamGithub = itemView.findViewById(R.id.teamGithub);
            teamTelegram = itemView.findViewById(R.id.teamTelegram);

            teamGithub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnClick(team.teamGithub);
                }
            });
            teamTelegram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnClick(team.teamTelegram);
                }
            });
        }

        @Override
        void bind(final About about) {
            team = (TeamHeader) about;
        }
            
    }

    static class HeaderHolder extends Holder {
        private TextView tvTitle;
        private Header header;

        public HeaderHolder(View itemView, OnClickListener listener) {
            super(itemView, listener);
            tvTitle = itemView.findViewById(R.id.textView);
        }

        @Override
        void bind(About about) {
            header = (Header) about;
            tvTitle.setText(header.title);
        }
    }

    static class MaintainerHolder extends Holder {
        private ImageButton ivXDA;
        private ImageButton ivDevGithub;
        private ImageView ivDevAvatar;
        private TextView tvDevName;
        private TextView tvDevice;
        private Maintainer maintainer;

        public MaintainerHolder(View itemView, final OnClickListener listener) {
            super(itemView, listener);
            ivXDA = itemView.findViewById(R.id.ivXDA);
            ivDevGithub = itemView.findViewById(R.id.ivDevGithub);
            ivDevAvatar = itemView.findViewById(R.id.ivDevAvatar);
            tvDevName = itemView.findViewById(R.id.tvDevName);
            tvDevice = itemView.findViewById(R.id.tvDevice);
            ivXDA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnClick(maintainer.dev.devXDA);
                }
            });
            ivDevGithub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnClick(maintainer.dev.devGithub);
                }
            });
        }

        @Override
        void bind(About about) {
            maintainer = (Maintainer) about;
            tvDevName.setText(maintainer.dev.devName);
            tvDevice.setText(maintainer.device);
            Glide.with(itemView.getContext()).load(maintainer.dev.devAvatarUrl).asBitmap().centerCrop().into(new BitmapImageViewTarget(ivDevAvatar){
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(itemView.getContext().getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    ivDevAvatar.setImageDrawable(circularBitmapDrawable);
                }
            });
        }
    }

    static class DevViewHolder extends Holder {
        private ImageButton ivXDA;
        private ImageButton ivTelegram;
        private ImageView ivDevAvatar;
        private TextView tvDevName;
        private TextView tvRole;
        private Dev dev;

        public DevViewHolder(View itemView, final OnClickListener listener) {
            super(itemView, listener);
            ivXDA = itemView.findViewById(R.id.ivXDA);
            ivTelegram = itemView.findViewById(R.id.ivTelegram);
            ivDevAvatar = itemView.findViewById(R.id.ivDevAvatar);
            tvDevName = itemView.findViewById(R.id.tvDevName);
            tvRole = itemView.findViewById(R.id.tvRole);
            ivXDA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnClick(dev.devXDA);
                }
            });
            ivTelegram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnClick(dev.devTelegram);
                }
            });
        }

        @Override
        void bind(About about) {
            dev = (Dev) about;
            tvDevName.setText(dev.devName);
            tvRole.setText(dev.devRole);
            Glide.with(itemView.getContext()).load(dev.devAvatarUrl).asBitmap().centerCrop().into(new BitmapImageViewTarget(ivDevAvatar){
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(itemView.getContext().getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    ivDevAvatar.setImageDrawable(circularBitmapDrawable);
                }
            });
            if(dev.devTelegram.isEmpty()){
                ivTelegram.setVisibility(View.INVISIBLE);
            } else {
                ivTelegram.setVisibility(View.VISIBLE);
            }
        }
    }

    public interface OnClickListener {
        void OnClick(String url);
    }
}
