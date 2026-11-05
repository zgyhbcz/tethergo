package com.example.usbshortcut;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

/**
 * Shared entry points for the launcher icon, the long-press shortcuts and the
 * Quick Settings tile.
 *
 * Tethering UI differs per OEM/Android version, so each target is a list tried in
 * order of specificity, falling back to the general wireless/settings screens.
 * vivo / iQOO (OriginOS) exposes a "更多热点设置" page (holding the USB tethering
 * switch) that is a child of the "个人热点" page; opening the parent first keeps it
 * in the back stack, so Back returns there instead of the home screen.
 */
final class TetherLauncher {

    static final String ACTION_OPEN_ADVANCED = "com.example.usbshortcut.OPEN_ADVANCED";
    static final String ACTION_OPEN_TETHER = "com.example.usbshortcut.OPEN_TETHER";

    private TetherLauncher() {
    }

    /** "更多热点设置": the page with the USB tethering switch on OriginOS. */
    private static Intent[] advancedTargets() {
        return new Intent[] {
                new Intent("android.settings.VIVO_TETHER_OTHER_SETTINGS"),
                component("com.android.settings",
                        "com.android.settings.Settings$VivoTetherOtherShareMethodsActivity")
        };
    }

    /** The tethering page itself ("个人热点" on OriginOS), plus AOSP fallbacks. */
    private static Intent[] tetherTargets() {
        return new Intent[] {
                new Intent("android.settings.VIVO_TETHER_SETTINGS"),
                component("com.android.settings",
                        "com.android.settings.Settings$VivoTetherSettingsActivity"),
                // Android 13+ public action (literal: constant may be missing from the SDK).
                new Intent("android.settings.TETHER_SETTINGS"),
                component("com.android.settings",
                        "com.android.settings.Settings$TetherSettingsActivity"),
                component("com.android.settings", "com.android.settings.TetherSettings"),
                component("com.android.settings", "com.android.settings.wifi.TetherSettings"),
                new Intent(Settings.ACTION_WIRELESS_SETTINGS),
                new Intent(Settings.ACTION_SETTINGS)
        };
    }

    /**
     * Opens the requested page. When {@code advanced} is true the parent page is
     * opened first (best effort) so that Back leads to it.
     */
    static boolean open(Context context, boolean advanced) {
        if (!advanced) {
            return start(context, tetherTargets());
        }
        boolean parentOpened = start(context, tetherTargets());
        if (start(context, advancedTargets())) {
            return true;
        }
        return parentOpened;
    }

    /** First target that some installed app can handle, or null. Used by the tile. */
    static Intent resolve(Context context, boolean advanced) {
        for (Intent intent : advanced ? advancedTargets() : tetherTargets()) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (context.getPackageManager().resolveActivity(intent, 0) != null) {
                return intent;
            }
        }
        return null;
    }

    private static boolean start(Context context, Intent[] targets) {
        for (Intent intent : targets) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(intent);
                return true;
            } catch (Exception ignored) {
                // ActivityNotFoundException / SecurityException (not exported): try next.
            }
        }
        return false;
    }

    private static Intent component(String pkg, String cls) {
        return new Intent().setComponent(new ComponentName(pkg, cls));
    }
}
