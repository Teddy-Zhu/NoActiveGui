package com.v2dawn.noactivegui.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;


import com.google.android.material.snackbar.Snackbar;
import com.v2dawn.noactivegui.R;
import com.v2dawn.noactivegui.databinding.FragmentHomeBinding;
import com.v2dawn.noactivegui.utils.AppUtils;


public class HomeFragment extends Fragment implements View.OnClickListener {

    private FragmentHomeBinding binding;
    TextView moduleInfo, moduleState;
    LinearLayout stateLayout, coolApkLink, coolApkLink2;
    boolean moduleIsRunning = false;
    String moduleName;
    String moduleVersion;
    ConstraintLayout constraintLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        constraintLayout = binding.constraintLayoutHome;

        moduleInfo = binding.infoText;
        moduleState = binding.stateText;
        stateLayout = binding.stateLayout;
        coolApkLink = binding.coolapkLink;
        coolApkLink2 = binding.coolapkLink2;

        updateStatus();

        stateLayout.setOnClickListener(this);


        coolApkLink.setOnClickListener(this);
        coolApkLink2.setOnClickListener(this);


        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                menuInflater.inflate(R.menu.home_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.about) {
                    aboutDialog();
                }
                return false;
            }
        }, this.getViewLifecycleOwner());


        return binding.getRoot();
    }

    public void aboutDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.about);
        dialog.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateStatus() {
        boolean isActive = AppUtils.isModuleActive();

        if (!isActive) {
            stateLayout.setBackgroundResource(R.color.warn_red);
            moduleInfo.setText(R.string.freezeit_offline);
            moduleState.setText(R.string.freezeit_offline_tips);
            return;
        }

        moduleIsRunning = true;
        moduleName = getString(R.string.app_name);
        moduleVersion = getVersionName(HomeFragment.this.getContext());

        stateLayout.setBackgroundResource(R.color.normal_green);
        moduleInfo.setText(R.string.freezeit_online);
        moduleState.setText(moduleVersion);

    }

    public static int getVersionCode(Context context) {
        PackageManager manager = context.getPackageManager();
        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }

    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }


    private void statusNotification() {

        boolean isActive = AppUtils.isModuleActive();
        if (!isActive) {
            Toast.makeText(getContext(), "检测尚未实现", Toast.LENGTH_SHORT).show();

//            Toast.makeText(getContext(), getString(R.string.freezeit_offline), Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(moduleName + " " + moduleVersion)
                .setIcon(R.drawable.ic_changelog_24dp)
                .setCancelable(true)
                .setMessage("模块已激活");

        AlertDialog dlg = builder.create();
        dlg.show();

    }

    ;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.stateLayout) {
            statusNotification();
        } else if (id == R.id.coolapk_link) {
            try {
                Intent intent = new Intent();
                intent.setClassName("com.coolapk.market", "com.coolapk.market.view.AppLinkActivity");
                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse("coolmarket://u/3851777"));
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.coolapk_link))));
            }
        } else if (id == R.id.coolapk_link_2) {
            try {
                Intent intent = new Intent();
                intent.setClassName("com.coolapk.market", "com.coolapk.market.view.AppLinkActivity");
                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse("coolmarket://u/1212220"));
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.coolapk_link_2))));
            }
        }
    }

    public void donateDialog(int ID) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(ID);
        dialog.show();
    }

}