package com.v2dawn.noactivegui.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import java.nio.charset.StandardCharsets;

import com.v2dawn.noactivegui.databinding.FragmentLogcatBinding;

public class LogcatFragment extends Fragment {

    private FragmentLogcatBinding binding;
    TextView logView;
    LinearLayout forBottom;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLogcatBinding.inflate(inflater, container, false);

        logView = binding.logView;
        forBottom = binding.forBottom;

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            byte[] response = msg.getData().getByteArray("response");

            if (response == null || response.length == 0) {
                return;
            }

            logView.setMovementMethod(ScrollingMovementMethod.getInstance());//流畅滑动
            logView.setText(new String(response, StandardCharsets.UTF_8));
            forBottom.requestFocus();//请求焦点，直接到日志底部
        }
    };

}