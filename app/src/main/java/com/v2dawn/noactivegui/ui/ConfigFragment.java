package com.v2dawn.noactivegui.ui;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.jakewharton.rxbinding4.appcompat.RxActionMenuView;
import com.jakewharton.rxbinding4.appcompat.RxSearchView;
import com.jakewharton.rxbinding4.appcompat.SearchViewQueryTextEvent;
import com.jakewharton.rxbinding4.swiperefreshlayout.RxSwipeRefreshLayout;
import com.v2dawn.noactivegui.R;
import com.v2dawn.noactivegui.databinding.FragmentConfigBinding;
import com.v2dawn.noactivegui.ui.support.ViewData;
import com.v2dawn.noactivegui.utils.AppListChangeListener;
import com.v2dawn.noactivegui.utils.ViewDataCacheBuilder;

import cn.hutool.core.collection.CollUtil;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.Unit;

public class ConfigFragment extends Fragment implements AppListChangeListener, ViewDataCacheBuilder {
    final String TAG = "ConfigFragment";
    private FragmentConfigBinding binding;
    AppListRecyclerAdapter recycleAdapter;
    List<ApplicationInfo> applicationInfoList = new ArrayList<>();
    SearchView searchView;
    MenuItem showSystemItem;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    PackageManager pm;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentConfigBinding.inflate(inflater, container, false);

        recyclerView = binding.recyclerviewApp;
        swipeRefreshLayout = binding.swipeRefreshLayout;

        RxSwipeRefreshLayout.refreshes(swipeRefreshLayout)
                .throttleFirst(2000L,TimeUnit.MILLISECONDS)
                .subscribe(unit -> refreshAppList(showSystemItem.isChecked()));

        pm = requireContext().getPackageManager();

        requireActivity().addMenuProvider(new MenuProvider() {

            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                menuInflater.inflate(R.menu.config_menu, menu);

                MenuItem searchItem = menu.findItem(R.id.search_view);
                searchView = (SearchView) searchItem.getActionView();
                if (searchView != null) {
                    bindSearchViewEvents();
                } else {
                    Log.e(TAG, "onCreateMenu: searchView == null");
                }
                showSystemItem = menu.findItem(R.id.show_system);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.show_system) {
                    boolean checked = !menuItem.isChecked();
                    menuItem.setChecked(checked);
                    refreshAppList(checked);
                    return false;
                }
                return false;
            }
        }, this.getViewLifecycleOwner());

        refreshAppList(false);

        return binding.getRoot();
    }

    private void searchViewTextChange(CharSequence query){
        if (recycleAdapter != null) {
            recycleAdapter.getFilter().filter(query);
        }
    }
    private void bindSearchViewEvents() {
        RxSearchView.queryTextChangeEvents(searchView)
                .throttleLast(1,TimeUnit.SECONDS)
                .subscribe(searchViewQueryTextEvent -> searchViewTextChange(searchViewQueryTextEvent.getQueryText()));
    }

    @SuppressLint("NewApi")
    private List<ViewData> buildCache() {

        List<ViewData> cache = new ArrayList<>();

        for (ApplicationInfo appInfo : this.applicationInfoList) {
            String label = getOriginLabel(appInfo);

            ViewData viewData = new ViewData();
            viewData.setLabel(label);
            viewData.setApplicationInfo(appInfo);

            build(viewData);

            cache.add(viewData);
        }
        cache.sort(Comparator.comparing(ViewData::getPriority));
        return cache;
    }

    public boolean isXposedModule(ApplicationInfo applicationInfo) {
        if (applicationInfo == null || applicationInfo.metaData == null) {
            return false;
        }

        return applicationInfo.metaData.containsKey("xposedminversion");
    }

    public boolean isSystem(ApplicationInfo applicationInfo) {
        if (applicationInfo == null) {
            return true;
        }
        return (applicationInfo.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0;
    }

    public boolean isImportantSystemApp(ApplicationInfo applicationInfo) {
        if (applicationInfo == null) {
            return true;
        }
        return applicationInfo.uid < 10000;
    }

    @SuppressLint("CheckResult")
    private void refreshAppList(Boolean showSystem) {
//        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setRefreshing(true);

        Observable.create((ObservableOnSubscribe< List<ViewData>>) emitter -> {
            refreshAppListApi(showSystem);
            List<ViewData> data = buildCache();

            emitter.onNext(data);
            emitter.onComplete();
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> refreshListView(data));

    }

    private void refreshAppListApi(Boolean showSystem) {
        applicationInfoList.clear();

        List<ApplicationInfo> allApplicationInfoList = pm.getInstalledApplications(PackageManager.GET_META_DATA | PackageManager.MATCH_UNINSTALLED_PACKAGES);

        for (ApplicationInfo appInfo : allApplicationInfoList) {
            if (isSystem(appInfo) && !showSystem) {
                continue;
            }
            applicationInfoList.add(appInfo);
        }
    }

    @SuppressLint("CheckResult")
    private void refreshListView(List<ViewData> data) {

        recycleAdapter = new AppListRecyclerAdapter(requireContext(), MainActivity.memData.getWhiteApps(),
                MainActivity.memData.getBlackSystemApps(),
                data, CollUtil.newArrayList(data),
                ConfigFragment.this, ConfigFragment.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setAdapter(recycleAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        swipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void notifyAppsChanged(int position, ViewData viewData) {
        recycleAdapter.notifyItemChanged(position, 0);

        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            if (viewData.getIsSystem()) {
                MainActivity.memData.writeBlackSystemApps();
            } else {
                MainActivity.memData.writeWhiteApps();
            }
            emitter.onNext(true);
            emitter.onComplete();
        }).throttleLast(2000L, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

    }

    @Override
    public void build(ViewData viewData) {

        ApplicationInfo appInfo = viewData.getApplicationInfo();
        String label = viewData.getLabel();

        viewData.setIsSystem(isSystem(appInfo));
        viewData.setIsImportantSystemApp(isImportantSystemApp(appInfo));
        viewData.setIsXposedModule(isXposedModule(appInfo));
        viewData.setName(label);
        Drawable icon = appInfo.loadIcon(pm);
        viewData.setPackageName(appInfo.packageName);
        _build(viewData, label, icon, appInfo, MainActivity.memData.getBlackSystemApps(), MainActivity.memData.getWhiteApps());
    }

    @Override
    public void rebuild(ViewData viewData) {
        _build(viewData,
                getOriginLabel(viewData.getApplicationInfo()),
                viewData.getApplicationInfo().loadIcon(pm),
                viewData.getApplicationInfo(),
                MainActivity.memData.getBlackSystemApps(), MainActivity.memData.getWhiteApps());
    }

    private void _build(ViewData viewData,
                        String label,
                        Drawable icon,
                        ApplicationInfo appInfo,
                        Set<String> blackSystemApps,
                        Set<String> whiteListConf) {

        Boolean gray = false;

        StringBuilder prefix = new StringBuilder();

        Integer priority = 20;

        if (viewData.getIsSystem()) {
//            prefix.append("[系统]");
//            if (viewData.getIsXposedModule()) {
//                prefix.append("[XP]");
//            }
            if (viewData.getIsImportantSystemApp()) {
                priority += 5;
            }
            if (blackSystemApps.contains(appInfo.packageName)) {
                priority -= 2;
//                prefix.append("[黑名单]");
                gray = true;
            } else {
                priority += 5;
            }
        } else {
//            if (viewData.getIsXposedModule()) {
//                prefix.append("[XP]");
//            }
            if (whiteListConf.contains(appInfo.packageName)) {
                priority -= 5;
                viewData.setLabel(label);
                viewData.setIcon(icon);
            } else {
                gray = true;
//                prefix.append("[黑名单]");
            }
        }
        prefix.append(" ");
        viewData.setPriority(priority);
        viewData.setEnable(!gray);
        viewData.setLabel(prefix + label);
        viewData.setIcon(gray ? Utils.convertToGrayscale(icon) : icon);

    }

    private String getOriginLabel(ApplicationInfo applicationInfo) {
        String label = pm.getApplicationLabel(applicationInfo).toString();
        if (label.endsWith("Application") || label.endsWith(".xml") || label.endsWith("false"))
            label = applicationInfo.packageName;
        return label;
    }
}