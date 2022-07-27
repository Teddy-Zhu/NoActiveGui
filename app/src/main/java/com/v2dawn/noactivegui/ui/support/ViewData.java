package com.v2dawn.noactivegui.ui.support;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

import lombok.Data;

@Data
public class ViewData {

    String name;
    String label;
    String packageName;
    Drawable icon;

    Boolean enable;
    Integer priority;
    Boolean isXposedModule;
    Boolean isSystem;
    Boolean isImportantSystemApp;
    ApplicationInfo applicationInfo;
}
