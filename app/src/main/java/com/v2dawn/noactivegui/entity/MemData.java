package com.v2dawn.noactivegui.entity;

import android.os.FileObserver;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.v2dawn.noactivegui.utils.ConfigFileObserver;
import com.v2dawn.noactivegui.utils.FreezerConfig;
import com.v2dawn.noactivegui.utils.Log;

import lombok.Data;

@Data
public class MemData {
    private Set<String> whiteApps = new HashSet<>();
    private Set<String> blackSystemApps = new HashSet<>();
    private Set<String> whiteProcessList = new HashSet<>();
    private Set<String> killProcessList = new HashSet<>();
    private Set<String> appBackgroundSet = Collections.synchronizedSet(new LinkedHashSet<>());
    private final FileObserver fileObserver;


    public MemData() {
        fileObserver = new ConfigFileObserver(this);
    }


    public void writeWhiteApps() {
        FreezerConfig.writeWhiteApps(whiteApps);
    }

    public void writeBlackSystemApps() {
        FreezerConfig.writeBlackSystemApps(blackSystemApps);
    }

    public int getBackgroundIndex(String packageName) {
        int total = appBackgroundSet.size();
        for (String pkg : appBackgroundSet) {
            if (packageName.equals(pkg)) {
                return total;
            } else {
                total -= 1;
            }
        }
        return total;
    }

}
