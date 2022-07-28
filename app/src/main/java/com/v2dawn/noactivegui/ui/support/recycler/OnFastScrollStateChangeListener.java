package com.v2dawn.noactivegui.ui.support.recycler;

/**
 * https://github.com/timusus/RecyclerView-FastScroll
 */
public interface OnFastScrollStateChangeListener {
    /**
     * Called when fast scrolling begins
     */
    void onFastScrollStart();

    /**
     * Called when fast scrolling ends
     */
    void onFastScrollStop();
}
