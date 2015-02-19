/*
 * Copyright (C) 2015 The DesolationROM Project
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
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.SystemProperties;
import android.util.Log;

/**
 * Performs a number of miscellaneous, non-system-critical actions
 * after the system has finished booting.
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "SystemUIBootReceiver";
    private static String mFirstBootNotify = SystemProperties.get("firstbootnotify");

    @Override
    public void onReceive(final Context context, Intent intent) {
	ContentResolver res = context.getContentResolver();
        try {
            // Start the load average overlay, if activated
            if (Settings.Global.getInt(res, Settings.Global.SHOW_PROCESSES, 0) != 0) {
                Intent loadavg = new Intent(context, com.android.systemui.LoadAverageService.class);
                context.startService(loadavg);
            }
        } catch (Exception e) {
            Log.e(TAG, "Can't start load average service", e);
        }
        if (mFirstBootNotify.equals("true")) {
	    FirstBootNotify(context);
	} else {
	    WelcomeBackNotify(context);
	}
    }
    
    public void FirstBootNotify(Context context) {
        Notification.Builder mBuilder = new Notification.Builder(context)
	        .setSmallIcon(R.drawable.first_boot_notify)
                .setAutoCancel(true)
                .setContentTitle("Welcome to DesolationROM")
                .setContentText("")
		.setStyle(new Notification.InboxStyle()
		.setBigContentTitle("Welcome to DesolationROM")
		.addLine("Build status: "+SystemProperties.get("rom.buildtype"))
		.addLine("Build date: "+SystemProperties.get("ro.build.date"))
		.addLine("Device: "+SystemProperties.get("ro.product.device")));
	NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(1, mBuilder.build());
    }
	
    public void WelcomeBackNotify(Context context) {
        Notification.Builder mBuilder = new Notification.Builder(context)
	        .setSmallIcon(R.drawable.first_boot_notify)
                .setAutoCancel(true)
                .setContentTitle("Welcome back to DesolationROM")
                .setContentText("Build status: "+SystemProperties.get("rom.buildtype")+SystemProperties.get("ro.product.device")+" build!");
	NotificationManager mNotificationManager =
	        (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(1, mBuilder.build());
    }
}
