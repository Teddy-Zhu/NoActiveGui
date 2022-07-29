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
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;


import com.v2dawn.noactivegui.R;
import com.v2dawn.noactivegui.databinding.FragmentHomeBinding;
import com.v2dawn.noactivegui.utils.AppUtils;
import com.v2dawn.noactivegui.utils.FreezerConfig;
import com.v2dawn.noactivegui.utils.LsposedModuleUtils;
import com.v2dawn.noactivegui.utils.NoActiveConfig;


public class HomeFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private FragmentHomeBinding binding;
    TextView moduleInfo, moduleState;
    LinearLayout stateLayout, coolApkLink, coolApkLink2;
    boolean moduleIsRunning = false;
    String moduleName;
    String moduleVersion;
    ConstraintLayout constraintLayout;

    SwitchCompat useKill, useKill19, useKill20, disableOOM, forceFreezerV2, enableColorOS, debug;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        constraintLayout = binding.constraintLayoutHome;

        moduleInfo = binding.infoText;
        moduleState = binding.stateText;
        stateLayout = binding.stateLayout;
        coolApkLink = binding.coolapkLink;
        coolApkLink2 = binding.coolapkLink2;

        useKill = binding.useKill;
        useKill19 = binding.useKill19;
        useKill20 = binding.useKill20;
        disableOOM = binding.disableOom;
        forceFreezerV2 = binding.forceFreezerv2;
        enableColorOS = binding.enableColorosOom;
        debug = binding.enableDebug;

        updateConfigStatus();
        updateStatus();

        bindEvents();

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

    private void bindEvents() {
        useKill19.setOnCheckedChangeListener(this);
        useKill20.setOnCheckedChangeListener(this);
        disableOOM.setOnCheckedChangeListener(this);
        forceFreezerV2.setOnCheckedChangeListener(this);
        enableColorOS.setOnCheckedChangeListener(this);
    }

    private void updateStatus() {
        boolean isActive = AppUtils.isModuleActive(requireContext(), NO_ACTIVE_MODULE);

        if (!isActive) {
            stateLayout.setBackgroundResource(R.color.warn_red);
            moduleInfo.setText(R.string.freezeit_offline);
            moduleState.setText(R.string.freezeit_offline_tips);
            return;
        }

        moduleIsRunning = true;
        moduleName = getString(R.string.xp_module_name);
        moduleVersion = AppUtils.findPkgVersionCode(requireContext().getPackageManager(), NO_ACTIVE_MODULE);

        stateLayout.setBackgroundResource(R.color.normal_green);
        moduleInfo.setText(R.string.freezeit_online);
        moduleState.setText(moduleVersion);

    }

    private void updateConfigStatus() {
        NoActiveConfig noActiveConfig = FreezerConfig.loadConfig();

        useKill19.setChecked(noActiveConfig.isKill19());
        useKill20.setChecked(noActiveConfig.isKill20());
        updateUseKillStatus();
        disableOOM.setChecked(noActiveConfig.isDisableOOM());
        forceFreezerV2.setChecked(noActiveConfig.isForceFreezerV2());
        enableColorOS.setChecked(noActiveConfig.isColorOS());
        debug.setChecked(noActiveConfig.isDebug());
    }

    private void updateUseKillStatus() {
        useKill.setChecked(useKill19.isChecked() || useKill20.isChecked());
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

    public static final String NO_ACTIVE_MODULE = "cn.myflv.android.noactive";

    private void statusNotification() {

        boolean isActive = AppUtils.isModuleActive(requireContext(), NO_ACTIVE_MODULE);
        if (!isActive) {
            Toast.makeText(getContext(), getString(R.string.freezeit_offline), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.use_kill_19:
                if (isChecked) {
                    FreezerConfig.useKill19();
                    useKill20.setChecked(false);
                } else {
                    FreezerConfig.unUseKill();
                }
                updateUseKillStatus();

                break;
            case R.id.use_kill_20:
                if (isChecked) {
                    FreezerConfig.useKill20();
                    useKill19.setChecked(false);
                } else {
                    FreezerConfig.unUseKill();
                }
                updateUseKillStatus();
                break;
            case R.id.disable_oom:
                if (isChecked) {
                    FreezerConfig.closeOOM();
                } else {
                    FreezerConfig.openOOM();
                }
                enableColorOS.setEnabled(!isChecked);
                break;
            case R.id.force_freezerv2:
                if (isChecked) {
                    FreezerConfig.forceFreezerV2();
                } else {
                    FreezerConfig.disableFreezerV2();
                }
                break;
            case R.id.enable_coloros_oom:
                if (isChecked) {
                    FreezerConfig.setColorOs();
                } else {
                    FreezerConfig.unSetColorOs();
                }
                break;
            case R.id.enable_debug:
                if (isChecked) {
                    FreezerConfig.openDebug();
                } else {
                    FreezerConfig.closeDebug();
                }
            default:
                break;
        }
    }
}