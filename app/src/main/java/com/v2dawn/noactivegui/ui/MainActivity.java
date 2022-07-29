package com.v2dawn.noactivegui.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.v2dawn.noactivegui.R;
import com.v2dawn.noactivegui.databinding.ActivityMainBinding;
import com.v2dawn.noactivegui.entity.MemData;
import com.v2dawn.noactivegui.utils.LsposedModuleUtils;
import com.v2dawn.noactivegui.utils.SuTool;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.robv.android.xposed.XposedHelpers;

public class MainActivity extends AppCompatActivity {

    public static final MemData memData = new MemData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

        memData.initConfig();
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_config, R.id.navigation_logcat)
                .build();

        // FragmentContainerView
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

    }


    @Override
    protected void onDestroy() {
        SuTool.releaseSharedProcess();
        super.onDestroy();
    }
}