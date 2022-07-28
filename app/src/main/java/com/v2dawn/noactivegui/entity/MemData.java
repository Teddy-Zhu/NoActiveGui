package com.v2dawn.noactivegui.entity;

import android.os.FileObserver;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.v2dawn.noactivegui.utils.FreezerConfig;
import com.v2dawn.noactivegui.utils.Log;

import lombok.Data;

@Data
public class MemData {
    private Set<String> whiteApps = new HashSet<>();
    private Set<String> blackSystemApps = new HashSet<>();
    private Set<String> whiteProcessList = new HashSet<>();
    private Set<String> killProcessList = new HashSet<>();
    private final String[] files = {FreezerConfig.whiteAppConfig, FreezerConfig.whiteProcessConfig,
            FreezerConfig.killProcessConfig, FreezerConfig.blackSystemAppConfig};


    public MemData() {
    }

    public void initConfig(){
        FreezerConfig.checkAndInit();
        reload();
    }

    public void writeWhiteApps() {
        FreezerConfig.writeWhiteApps(whiteApps);
    }

    public void writeBlackSystemApps() {
        FreezerConfig.writeBlackSystemApps(blackSystemApps);
    }


    public void reload() {
        for (String file : files) {
            Log.d("Reload " + file);
            Set<String> newConfig = new HashSet<>(FreezerConfig.get(file));
            switch (file) {
                case FreezerConfig.whiteAppConfig:
                    setWhiteApps(newConfig);
                    break;
                case FreezerConfig.whiteProcessConfig:
                    setWhiteProcessList(newConfig);
                    break;
                case FreezerConfig.killProcessConfig:
                    setKillProcessList(newConfig);
                    break;
                case FreezerConfig.blackSystemAppConfig:
                    setBlackSystemApps(newConfig);
                    break;
            }
        }
    }
}
