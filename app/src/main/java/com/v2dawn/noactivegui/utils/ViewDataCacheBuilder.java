package com.v2dawn.noactivegui.utils;

import com.v2dawn.noactivegui.ui.support.ViewData;

public interface ViewDataCacheBuilder {

    void build(ViewData viewData);

    void rebuild(ViewData viewData);
}
