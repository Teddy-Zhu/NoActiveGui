package com.v2dawn.noactivegui.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

public class AppUtils {
    private static final List<ApplicationInfo> applicationInfoList = new ArrayList<>();

    public static boolean isModuleActive(Context context, String moduleName) {
        return LsposedModuleUtils.detect(context, moduleName);
    }

    public static PackageInfo findPkgInfo(PackageManager pm, String pkgName) {
        try {
            PackageInfo packageInfo = pm.getPackageInfo(pkgName, 0);
            return packageInfo;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String findPkgVersionCode(PackageManager pm, String pkgName) {
        try {
            PackageInfo packageInfo = pm.getPackageInfo(pkgName, 0);
            return packageInfo.versionName + "(" + packageInfo.versionCode + ")";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean isXposedModule(ApplicationInfo applicationInfo) {
        if (applicationInfo == null || applicationInfo.metaData == null) {
            return false;
        }

        return applicationInfo.metaData.containsKey("xposedminversion");
    }


    public static boolean isImportantSystemApp(ApplicationInfo applicationInfo) {
        if (applicationInfo == null) {
            return true;
        }
        return applicationInfo.uid < 10000;
    }

    public static boolean isSystem(ApplicationInfo applicationInfo) {
        if (applicationInfo == null) {
            return true;
        }
        return (applicationInfo.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0;
    }

    public static void loadApplicationInfos(PackageManager pm, Boolean forceRefresh) {
        if (!forceRefresh && applicationInfoList.size() > 0) {
            return;
        }
        applicationInfoList.clear();

        List<ApplicationInfo> allApplicationInfoList = pm.getInstalledApplications(PackageManager.GET_META_DATA | PackageManager.MATCH_UNINSTALLED_PACKAGES);

        for (ApplicationInfo appInfo : allApplicationInfoList) {
            applicationInfoList.add(appInfo);
        }
    }

    public static List<ApplicationInfo> getApps() {
        return applicationInfoList;
    }

}
