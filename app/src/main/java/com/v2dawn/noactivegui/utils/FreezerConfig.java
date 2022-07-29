package com.v2dawn.noactivegui.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class FreezerConfig {
    public final static String ConfigDir = "/data/system/NoActive";
    public final static String whiteAppConfig = "whiteApp.conf";
    public final static String blackSystemAppConfig = "blackSystemApp.conf";
    public final static String whiteProcessConfig = "whiteProcess.conf";
    public final static String killProcessConfig = "killProcess.conf";
    public final static String disableOOM = "disable.oom";
    public final static String kill19 = "kill.19";
    public final static String kill20 = "kill.20";
    public final static String freezerV2 = "freezer.v2";
    public final static String colorOs = "color.os";
    public final static String debug = "debug";

    public static boolean isConfigOn(String configName) {
        return SuTool.existFile(ConfigDir + "/" + configName);
    }

    public static int getKillSignal() {
        if (isConfigOn(kill19)) {
            return 19;
        }
        if (isConfigOn(kill20)) {
            return 20;
        }
        return -1;
    }


    public static boolean isUseKill() {
        return isConfigOn(kill19) || isConfigOn(kill20);
    }

    public static boolean isColorOs() {
        return isConfigOn(colorOs);
    }


    public static void checkAndInit() {

        String whiteApp = ConfigDir + "/" + whiteAppConfig;
        String whiteProcess = ConfigDir + "/" + whiteProcessConfig;
        String killProcess = ConfigDir + "/" + killProcessConfig;
        String blackSystemApp = ConfigDir + "/" + blackSystemAppConfig;
        if (!SuTool.existDir(ConfigDir)) {
            boolean mkdir = SuTool.mkdir(ConfigDir);
            if (!mkdir) return;
            Log.i("init config dir");
        }
        if (!SuTool.existFile(whiteApp)) {
            SuTool.createFile(whiteApp);
            Log.i("init white app conf");
        }

        if (!SuTool.existFile(whiteProcess)) {
            SuTool.createFile(whiteProcess);
            Log.i("init white process conf");
        }
        if (!SuTool.existFile(killProcess)) {
            SuTool.createFile(killProcess);
            Log.i("init kill process conf");
        }

        if (!SuTool.existFile(blackSystemApp)) {
            SuTool.createFile(blackSystemApp);
            Log.i("init black system app conf");
        }

    }

    public static void createFile(File file) {
        try {
            boolean newFile = file.createNewFile();
            if (!newFile) {
                throw new IOException();
            }
        } catch (IOException e) {
            Log.i(file.getName() + " file create filed");
        }
    }


    public static void writeWhiteApps(Set<String> whiteApps) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String whiteApp : whiteApps) {
            stringBuilder.append(whiteApp).append(System.lineSeparator());
        }
        SuTool.writeFile(ConfigDir + "/" + whiteAppConfig, stringBuilder.toString());
    }

    public static void writeBlackSystemApps(Set<String> blackSystemApps) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String blackSystemApp : blackSystemApps) {
            stringBuilder.append(blackSystemApp).append(System.lineSeparator());
        }
        SuTool.writeFile(ConfigDir + "/" + blackSystemAppConfig, stringBuilder.toString());
    }


    private static void writeFile(String fileName, String content) {
        File file = new File(ConfigDir, fileName);
        if (!file.exists()) {
            createFile(file);
        }
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(content);
            bufferedWriter.close();
        } catch (FileNotFoundException fileNotFoundException) {
            Log.i(fileName + " file not found");
        } catch (IOException e) {
            Log.i(fileName + " file write filed");
        }


    }

    public static Set<String> get(String name) {
        return SuTool.readConfigFile(ConfigDir + "/" + name);
    }


    public static NoActiveConfig loadConfig() {

        NoActiveConfig noActiveConfig = new NoActiveConfig();
        noActiveConfig.setKill19(isConfigOn(kill19));
        noActiveConfig.setKill20(isConfigOn(kill20));

        noActiveConfig.setDisableOOM(isConfigOn(disableOOM));
        noActiveConfig.setForceFreezerV2(isConfigOn(freezerV2));
        noActiveConfig.setColorOS(isConfigOn(colorOs));
        noActiveConfig.setDebug(isConfigOn(debug));
        return noActiveConfig;
    }


    public static boolean openDebug() {
        return requireFile(debug);
    }

    public static boolean closeDebug() {
        return requireNotExistFile(debug);
    }

    public static boolean forceFreezerV2() {
        return requireFile(freezerV2);
    }

    public static boolean disableFreezerV2() {
        return requireNotExistFile(freezerV2);
    }

    public static boolean openOOM() {

        return requireNotExistFile(disableOOM);
    }

    public static boolean closeOOM() {
        return requireFile(disableOOM);
    }

    public static boolean setColorOs() {

        return requireFile(colorOs);
    }

    public static boolean unSetColorOs() {
        return requireNotExistFile(colorOs);
    }

    public static boolean useKill19() {
        return requireNotExistFile(kill20) && requireFile(kill19);
    }

    public static boolean useKill20() {
        return requireNotExistFile(kill19) && requireFile(kill20);
    }

    public static boolean unUseKill() {
        return requireNotExistFile(kill19) && requireNotExistFile(kill20);
    }

    public static boolean requireFile(String name) {
        return isConfigOn(name) || SuTool.createFile(ConfigDir + "/" + name);
    }

    public static boolean requireNotExistFile(String name) {
        return !isConfigOn(name) || SuTool.removeFile(ConfigDir + "/" + name);
    }


}
