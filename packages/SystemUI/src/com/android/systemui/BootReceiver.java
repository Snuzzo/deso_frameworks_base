/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import android.widget.TextView;

/**
 * Performs a number of miscellaneous, non-system-critical actions
 * after the system has finished booting.
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "SystemUIBootReceiver";
    private static String mFirstBootNotify = SystemProperties.get("firstbootnotify");

    @Override
    public void onReceive(final Context context, Intent intent) {
        try {
            // Start the load average overlay, if activated
            ContentResolver res = context.getContentResolver();
            if (Settings.Global.getInt(res, Settings.Global.SHOW_PROCESSES, 0) != 0) {
                Intent loadavg = new Intent(context, com.android.systemui.LoadAverageService.class);
                context.startService(loadavg);
            }
        } catch (Exception e) {
            Log.e(TAG, "Can't start load average service", e);
        }
        if (mFirstBootNotify.equals("true")){
			FirstBootNotify(context);
		}
	}
    
    public void FirstBootNotify(Context context) {
		Intent newintent = new Intent();
		newintent.setClassName("com.android.settings", "com.android.settings.orbix.ChangeLog");
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, newintent, PendingIntent.FLAG_ONE_SHOT);
		Notification.Builder mBuilder = new Notification.Builder(context)
					.setSmallIcon(R.drawable.first_boot_notify)
                    .setAutoCancel(true)
                    .setContentTitle("Welcome to DesolationRom")
                    .setContentText("Tap to view changelog.")
					.setContentIntent(contentIntent)
					.setStyle(new Notification.InboxStyle()
					.setBigContentTitle("Welcome to DesolationRom")
					.addLine("Build status: "+SystemProperties.get("rom.buildtype"))
					.addLine("Build date: "+SystemProperties.get("ro.build.date"))
					.addLine("Device: "+SystemProperties.get("ro.product.device"))
					.addLine("Tap to view changelog."));
		NotificationManager mNotificationManager =
			(NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(1, mBuilder.build());
	}
}
