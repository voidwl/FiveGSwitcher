package com.ysy.switcherfiveg

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.blankj.utilcode.util.ToastUtils

class SwitcherTileService : TileService() {

    companion object {
        private const val TAG = "SwitcherTileService"
    }

    private val m5GSupport by lazy { FiveGUtils.isFiveGCapable() }
    private var mActiveIcon: Icon? = null
    private var mInActiveIcon: Icon? = null

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Log.v(TAG, "attachBaseContext")
        mActiveIcon = mActiveIcon ?: Icon.createWithResource(this, R.drawable.ic_5g_white_24dp)
        mInActiveIcon = mInActiveIcon ?: Icon.createWithResource(this, R.drawable.ic_5g_white_24dp)
            .setTint(0x80FFFFFF.toInt())
    }

    override fun onStartListening() {
        super.onStartListening()
        Log.v(TAG, "onStartListening")
        if (m5GSupport) {
            updateTile(FiveGUtils.isUserFiveGEnabled())
        } else {
            qsTile?.apply {
                state = Tile.STATE_UNAVAILABLE
                updateTile()
            }
        }
    }

    override fun onClick() {
        super.onClick()
        Log.v(TAG, "onClick ${qsTile?.state}")
        if (!m5GSupport) {
            ToastUtils.showLong(R.string.settings_main_title_not_support)
            return
        }
        if (!FSApp.isSettingsInitDone()) {
            ToastUtils.showLong(R.string.toast_settings_not_init)
            startActivityAndCollapse(Intent(FSApp.getContext(), MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            })
            return
        }
        toggle()
    }

    private fun toggle() {
        val newEnabled = !FiveGUtils.isUserFiveGEnabled()
        FiveGUtils.setUserFiveGEnabled(newEnabled)
        updateTile(newEnabled)
        LocalBroadcastManager.getInstance(this)
            .sendBroadcast(Intent(MoreBottomSheetFragment.SettingsFragment.TAG).apply {
                putExtra(MoreBottomSheetFragment.SettingsFragment.EXTRA_KEY_ENABLE_5G, newEnabled)
            })
    }

    private fun updateTile(active: Boolean) {
        qsTile?.apply {
            icon = if (active) mActiveIcon else mInActiveIcon
            label = FSApp.getLabel() ?: getString(R.string.five_g_tile_label)
            state = if (active) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            updateTile()
        }
    }


    override fun onStopListening() {
        super.onStopListening()
        Log.v(TAG, "onStopListening")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "onDestroy")
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.v(TAG, "onBind")
        return super.onBind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.v(TAG, "onUnbind")
        return super.onUnbind(intent)
    }

    override fun onTileAdded() {
        super.onTileAdded()
        Log.v(TAG, "onTileAdded")
    }

    override fun onTileRemoved() {
        super.onTileRemoved()
        Log.v(TAG, "onTileRemoved")
    }
}
