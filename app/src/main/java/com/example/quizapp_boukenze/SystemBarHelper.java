package com.example.quizapp_boukenze;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public final class SystemBarHelper {
    private SystemBarHelper() {
    }

    public static void apply(View view, int horizontalDp, int topDp, int bottomDp) {
        int baseLeft = dp(view, horizontalDp);
        int baseTop = dp(view, topDp);
        int baseRight = dp(view, horizontalDp);
        int baseBottom = dp(view, bottomDp);

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    baseLeft + bars.left,
                    baseTop + bars.top,
                    baseRight + bars.right,
                    baseBottom + bars.bottom
            );
            return windowInsets;
        });

        ViewCompat.requestApplyInsets(view);
    }

    private static int dp(View view, int value) {
        return Math.round(value * view.getResources().getDisplayMetrics().density);
    }
}
