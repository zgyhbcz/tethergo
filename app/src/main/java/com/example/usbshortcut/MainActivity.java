package com.example.usbshortcut;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Invisible launcher/shortcut entry point: opens the tethering settings page and
 * finishes immediately.
 *
 * The intent action decides which page:
 * "…OPEN_TETHER" opens 个人热点, anything else (launcher icon, "…OPEN_ADVANCED")
 * opens 更多热点设置.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String action = getIntent() != null ? getIntent().getAction() : null;
        boolean advanced = !TetherLauncher.ACTION_OPEN_TETHER.equals(action);
        if (!TetherLauncher.open(this, advanced)) {
            Toast.makeText(this, R.string.no_tether_settings, Toast.LENGTH_LONG).show();
        }
        finish();
    }
}
