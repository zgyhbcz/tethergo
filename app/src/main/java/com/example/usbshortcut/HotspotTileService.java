package com.example.usbshortcut;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

/**
 * Quick Settings tile: one tap from the notification shade straight to the
 * tethering page (更多热点设置 on OriginOS).
 *
 * The tile cannot switch tethering on/off by itself: that API
 * (ConnectivityManager/TetheringManager.startTethering) is guarded by the
 * signature-level TETHER_PRIVILEGED permission, so a normal app is not allowed
 * to toggle it. The tile therefore behaves as a one-tap entry point.
 */
public class HotspotTileService extends TileService {

    @Override
    public void onStartListening() {
        super.onStartListening();
        Tile tile = getQsTile();
        if (tile == null) {
            return;
        }
        tile.setLabel(getString(R.string.tile_label));
        tile.setState(Tile.STATE_INACTIVE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.setIcon(Icon.createWithResource(this, R.drawable.ic_tile_hotspot));
        }
        tile.updateTile();
    }

    @Override
    public void onClick() {
        super.onClick();
        Intent intent = TetherLauncher.resolve(this, true);
        if (intent == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 34) {
            // Android 14+ requires collapsing the shade through a PendingIntent.
            PendingIntent pending = PendingIntent.getActivity(
                    this, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            startActivityAndCollapse(pending);
        } else {
            startActivityAndCollapse(intent);
        }
    }
}
