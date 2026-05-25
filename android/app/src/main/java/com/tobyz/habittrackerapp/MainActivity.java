package com.tobyz.habittrackerapp;
 
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
 
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
 
import com.getcapacitor.BridgeActivity;
 
public class MainActivity extends BridgeActivity {
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
 
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(params);
        }
 
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
 
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            int bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            bridge.getWebView().evaluateJavascript(
                    "document.documentElement.style.setProperty('--ion-safe-area-bottom', Math.max(0, ('" + bottomInset + "' / window.devicePixelRatio) - 12) + 'px')",
                    null
            );
            return insets;
        });
    }
}