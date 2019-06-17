/* Copyright (C) 2018 The Potato Open Sauce Project
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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.os.Bundle;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.content.res.Resources;
import android.support.annotation.VisibleForTesting;
import android.support.v7.preference.Preference;
import android.util.PathParser;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.wrapper.OverlayManagerWrapper;
import com.android.settings.wrapper.OverlayManagerWrapper.OverlayInfo;

import java.util.ArrayList;
import java.util.List;

public class ShapeDialog extends InstrumentedDialogFragment implements OnClickListener {

    private static final String TAG_SYSTEM_SHAPE = "system_shape";

    private Context mContext;
    private static Preference mSystemShapePref;
    private OverlayManagerWrapper mOverlayService;
    private PackageManager mPackageManager;
    private View mView;
    private AlertDialog dialog;
    private ImageView mShapeViewer;
    private AlertDialog.Builder mBuilder;
    private int mCurrentSelected;
    private List<String> mList = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();

        mOverlayService = ServiceManager.getService(Context.OVERLAY_SERVICE) != null ? new OverlayManagerWrapper()
                : null;
        mPackageManager = mContext.getPackageManager();

        mView = LayoutInflater.from(mContext).inflate(R.layout.shape_dialog, null);
        mShapeViewer = mView.findViewById(R.id.shape);

        mBuilder = new AlertDialog.Builder(mContext);

        if (mView != null) {
            initView();
        }

        mBuilder.setView(mView).setCancelable(false);
        mBuilder.setPositiveButton(R.string.okay, this).setNegativeButton(R.string.cancel, this);
        mBuilder.setTitle(R.string.system_shape);

        dialog = mBuilder.create();
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    private void initView() {
        List<String> labels = new ArrayList<String>();

        for (String pkgName : getAvailableThemes()) {
            mList.add(pkgName);
            labels.add(getAppLabel(pkgName).toString());
        }

        String[] listArray = mList.toArray(new String[mList.size()]);
        String[] labelsArray = labels.toArray(new String[labels.size()]);

        mCurrentSelected = mList.indexOf(getCurrentTheme("android.shape"));
 
        mBuilder.setSingleChoiceItems(labelsArray, mList.indexOf(getCurrentTheme("android.shape")), new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String selectedShape = (String) listArray[i];
                mCurrentSelected = i;

                setImageViewPath(getPathFromPackage(selectedShape));
            }
        });

        String shapePath = mContext.getResources().getString(
                mContext.getResources().getSystem().getIdentifier("config_icon_mask", "string", "android"));

        setImageViewPath(shapePath);
    }

    private void setImageViewPath(String shapePath) {
        int shapeViewerSize = mContext.getResources().getDimensionPixelSize(R.dimen.shape_viewer_size);

        RelativeLayout.LayoutParams shapeParams = new RelativeLayout.LayoutParams(shapeViewerSize, shapeViewerSize);
        shapeParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        ShapeDrawable shapeDrawable = new ShapeDrawable(new PathShape(new Path(
                PathParser.createPathFromPathData(shapePath)), 100.0f, 100.0f));

        shapeDrawable.getPaint().setColor(Color.WHITE);
        shapeDrawable.setIntrinsicWidth(shapeViewerSize);
        shapeDrawable.setIntrinsicHeight(shapeViewerSize);

        mShapeViewer.setImageDrawable(shapeDrawable);
        mShapeViewer.setLayoutParams(shapeParams);
    }

    private boolean isTheme(OverlayInfo oi) {
        if (!"android.shape".equals(oi.category)) {
            return false;
        }
        try {
            PackageInfo pi = mPackageManager.getPackageInfo(oi.packageName, 0);
                return pi != null && !pi.isStaticOverlayPackage();
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @VisibleForTesting
    String[] getAvailableThemes() {
        List<OverlayInfo> infos = mOverlayService.getOverlayInfosForTarget("android", UserHandle.myUserId());
        List<String> pkgs = new ArrayList<>(infos.size());
        for (int i = 0, size = infos.size(); i < size; i++) {
            if (isTheme(infos.get(i))) {
                pkgs.add(infos.get(i).packageName);
            }
        }
        return pkgs.toArray(new String[pkgs.size()]);
    }

    // Theme/OMS handling methods
    private CharSequence getCurrentTheme(String category) {
        String currentPkg = getTheme(category);
        CharSequence label = currentPkg;
        return label;
    }

    private CharSequence getAppLabel(String pkg) {
        CharSequence label = null;
        try {
            label = mPackageManager.getApplicationInfo(pkg, 0).loadLabel(mPackageManager);
        } catch (PackageManager.NameNotFoundException e) {
            label = pkg;
        }
        return label;
    }

    private String getTheme(String category) {
        List<OverlayInfo> infos = mOverlayService.getOverlayInfosForTarget("android", UserHandle.myUserId());
        for (int i = 0, size = infos.size(); i < size; i++) {
            if (infos.get(i).isEnabled() && isTheme(infos.get(i))) {
                return infos.get(i).packageName;
            }
        }
        return null;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        ContentResolver resolver = mContext.getContentResolver();

        if (which == DialogInterface.BUTTON_POSITIVE) {
            String[] listArray = mList.toArray(new String[mList.size()]);
            mOverlayService.setEnabledExclusiveInCategory(listArray[mCurrentSelected], UserHandle.myUserId());
        }
    }

    public static void show(Fragment parent, Preference preference) {
        if (!parent.isAdded())
            return;
        mSystemShapePref = preference;
        final ShapeDialog dialog = new ShapeDialog();
        dialog.setTargetFragment(parent, 0);
        dialog.show(parent.getFragmentManager(), TAG_SYSTEM_SHAPE);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SYBERIA;
    }

    private String getPathFromPackage(String pkg) {
        String pathResName = "config_icon_mask";
        Resources res = null;
        try {
            res = mContext.getPackageManager().getResourcesForApplication(pkg);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int resId = res.getIdentifier(pkg + ":string/" + pathResName, null, null);
        return res.getString(resId);
    }
}
