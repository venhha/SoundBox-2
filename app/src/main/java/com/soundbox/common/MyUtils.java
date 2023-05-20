package com.soundbox.common;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class MyUtils {
    private static ProgressBarUtil mProgressBarUtil;

    public static ProgressBarUtil getProgressBarUtil() {
        if (mProgressBarUtil == null) {
            return new ProgressBarUtil();
        }
        return mProgressBarUtil;
    }

    public static class ProgressBarUtil {
        ProgressBar progressBar;
        Button button;

        public ProgressBarUtil() {
        }

        public ProgressBarUtil(ProgressBar progressBar, Button button) {
            this.progressBar = progressBar;
            this.button = button;
        }

        public void show() {
            progressBar.setVisibility(View.VISIBLE);
            if (button != null) {
                button.setVisibility(View.GONE);
            }
        }

        public void hide() {
            progressBar.setVisibility(View.GONE);
            if (button != null) {
                button.setVisibility(View.VISIBLE);
            }
        }

        public ProgressBarUtil set(ProgressBar progressBar, Button button) {
            this.progressBar = progressBar;
            this.button = button;
            return this;
        }
    }
}
