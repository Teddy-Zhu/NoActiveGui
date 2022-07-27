package com.v2dawn.noactivegui.ui;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.v2dawn.noactivegui.R;
import com.v2dawn.noactivegui.ui.support.CornerLabelView;
import com.v2dawn.noactivegui.ui.support.ViewData;
import com.v2dawn.noactivegui.utils.AppListChangeListener;
import com.v2dawn.noactivegui.utils.ViewDataCacheBuilder;

public class AppListRecyclerAdapter extends RecyclerView.Adapter<AppListRecyclerAdapter.MyViewHolder> {

    private final Set<String> whiteListConf;

    private final Set<String> blackSystemApps;

    AppListChangeListener callback;
    Context context;

    List<ViewData> cacheData;

    List<ViewData> originCacheData;

    ViewDataCacheBuilder cacheBuilder;

    public AppListRecyclerAdapter(Context context,
                                  Set<String> whiteListConf,
                                  Set<String> blackSystemApps,
                                  List<ViewData> cacheData,
                                  List<ViewData> originCacheData,
                                  AppListChangeListener callback,
                                  ViewDataCacheBuilder viewDataCacheBuilder) {
        this.whiteListConf = whiteListConf;
        this.blackSystemApps = blackSystemApps;
        this.context = context;
        this.callback = callback;
        this.cacheData = cacheData;
        this.originCacheData = originCacheData;
        this.cacheBuilder = viewDataCacheBuilder;
    }


    private void reBuildState(int index) {
        ViewData viewData = this.cacheData.get(index);

        if (cacheBuilder != null) {
            cacheBuilder.rebuild(viewData);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n", "ResourceAsColor", "NewApi"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ViewData viewData = cacheData.get(position);

        holder.package_name.setText(viewData.getPackageName());
        holder.app_label.setText(viewData.getLabel());
        holder.app_icon.setImageDrawable(viewData.getIcon());

        if (viewData.getIsSystem()) {
            holder.sysApp.setText1("SYS");
            holder.sysApp.setFillColorResourceRemoveColorFilter(viewData.getIsImportantSystemApp() ? R.color.tr_black : R.color.tr_red);
            holder.sysApp.setVisibility(View.VISIBLE);
        } else {
            if (viewData.getIsXposedModule()) {
                holder.sysApp.setVisibility(View.VISIBLE);
                if (viewData.getEnable()) {
                    holder.sysApp.setFillColorResourceRemoveColorFilter(R.color.tr_blue);
                } else {
                    holder.sysApp.setFillColorWithColorFilter(R.color.tr_blue);
                }
                holder.sysApp.setText1("XP");

            } else {
                holder.sysApp.setVisibility(View.GONE);
            }


        }
        holder.itemBackground.setOnLongClickListener(v -> {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText(viewData.getName(), viewData.getPackageName());
            cm.setPrimaryClip(mClipData);
            Toast.makeText(context, context.getString(R.string.copy_pkg_name), Toast.LENGTH_SHORT).show();
            return true;
        });
        holder.itemBackground.setOnClickListener(v -> {

            if (viewData.getIsImportantSystemApp()) {
                Toast.makeText(context, context.getString(R.string.not_support_i_sysapp), Toast.LENGTH_SHORT).show();
                return;
            }
            if (viewData.getIsSystem()) {
                if (blackSystemApps.contains(viewData.getPackageName())) {
                    blackSystemApps.remove(viewData.getPackageName());
                    Toast.makeText(context, String.format(context.getString(R.string.remove_sys_black_apps), viewData.getName()), Toast.LENGTH_SHORT).show();
                } else {
                    blackSystemApps.add(viewData.getPackageName());
                    Toast.makeText(context, String.format(context.getString(R.string.add_sys_black_apps), viewData.getName()), Toast.LENGTH_SHORT).show();
                }

            } else {
                if (whiteListConf.contains(viewData.getPackageName())) {
                    Toast.makeText(context, String.format(context.getString(R.string.remove_white_apps), viewData.getName()), Toast.LENGTH_SHORT).show();
                    whiteListConf.remove(viewData.getPackageName());
                } else {
                    Toast.makeText(context, String.format(context.getString(R.string.add_white_apps), viewData.getName()), Toast.LENGTH_SHORT).show();
                    whiteListConf.add(viewData.getPackageName());
                }
            }
            reBuildState(position);

            callback.notifyAppsChanged(position, viewData);
        });

    }

    @Override
    public int getItemCount() {
        return cacheData.size();
    }

    private Filter nameFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();

            if (charSequence == null || charSequence.length() == 0) {
                results.values = originCacheData;
                return results;
            }
            String keyword = charSequence.toString().toLowerCase();
            List<ViewData> newData = new ArrayList<>();
            for (ViewData originCacheDatum : originCacheData) {
                if (originCacheDatum.getPackageName().toLowerCase().contains(keyword) || originCacheDatum.getName().toLowerCase().contains(keyword)) {
                    newData.add(originCacheDatum);
                }
            }
            results.values = newData;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            if (filterResults.values instanceof ArrayList) {
                cacheData.clear();
                cacheData.addAll((Collection<? extends ViewData>) filterResults.values);
                notifyDataSetChanged();
            }
        }
    };

    public Filter getFilter() {
        return nameFilter;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout itemBackground;
        ImageView app_icon;
        TextView app_label, package_name;

        CardView cardView;

        CornerLabelView sysApp;

        public MyViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.app_item_cardview);
            sysApp = view.findViewById(R.id.sys_app);
            itemBackground = view.findViewById(R.id.app_item_background);
            app_icon = view.findViewById(R.id.app_icon);
            app_label = view.findViewById(R.id.app_label);
            package_name = view.findViewById(R.id.package_name);
        }
    }
}
