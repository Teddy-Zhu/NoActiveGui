package com.v2dawn.noactivegui.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;


import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import com.v2dawn.noactivegui.R;
import com.v2dawn.noactivegui.databinding.FragmentLogcatBinding;
import com.v2dawn.noactivegui.utils.SuTool;

import cn.hutool.core.collection.CollUtil;

public class LogcatFragment extends Fragment {

    private FragmentLogcatBinding binding;
    TextView logView;
    LinearLayout forBottom;
    String logPath;

    public static final String LSP_LOG_PATH = "/data/adb/lspd/log";
    public static final String LSP_LOG_NAME_KEYWORD = "modules_";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLogcatBinding.inflate(inflater, container, false);

        logView = binding.logView;
        forBottom = binding.forBottom;


        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                menuInflater.inflate(R.menu.logcat_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.refresh_log) {
                    updateLog();
                }
                return false;
            }
        }, this.getViewLifecycleOwner());
        logPath = LSP_LOG_PATH + "/" + SuTool.findLatestFile(LSP_LOG_PATH, LSP_LOG_NAME_KEYWORD);
        updateLog();

        return binding.getRoot();
    }


    private void updateLog() {
        List<String> logs = SuTool.readOriginFileFilter(logPath, "NoActive ->");
        logView.setMovementMethod(ScrollingMovementMethod.getInstance());//流畅滑动
        logView.setText(CollUtil.join(logs, System.lineSeparator()));
        forBottom.requestFocus();//请求焦点，直接到日志底部
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}