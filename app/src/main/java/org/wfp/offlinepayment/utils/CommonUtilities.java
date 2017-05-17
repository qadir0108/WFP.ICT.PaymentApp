/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wfp.offlinepayment.utils;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import org.wfp.offlinepayment.R;

/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public final class CommonUtilities {

	/**
	 * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
	 */
	static final String SERVER_URL = "http://10.0.2.2:8080/gcm-demo";

	/**
	 * Google API project id registered to use GCM.
	 */
	//991887209696
	public static final String SENDER_ID = "797360053386";

	public static String VEHICAL_CODE = "ABC123";

	/**
	 * Tag used on log messages.
	 */
	public static final String TAG = "GCMDemo";

	/**
	 * Intent used to display a message in the screen.
	 */
	public static final String ACTION_DISPLAY_GCM_MESSAGE = "DISPLAY_GCM_MESSAGE";

	public static final String ACTION_DISPLAY_POD_MESSAGE = "DISPLAY_POD_MESSAGE";

	public static final String ACTION_POD_MESSAGE_M = "POD_MESSAGE_M";
	public static final String ACTION_POD_MESSAGE_C = "POD_MESSAGE_C";
	public static final String ACTION_POD_MESSAGE_CD = "POD_MESSAGE_CD";
	
	public static final String ACTION_UPDATE_CONSIGNMENTS = "UPDATE_CONSIGNMENTS";

	/**
	 * Intent's extra that contains the message to be displayed.
	 */
	public static final String EXTRA_MESSAGE = "message";
	
	public static final String FROM_NOTIFICATION = "from_notification";

	public static final String EXTRA_NOTIFICATION_ID = "notificationId";

	/**
	 * Notifies UI to display a message.
	 * <p>
	 * This method is defined in the common helper because it's used both by the
	 * UI and the background service.
	 * 
	 * @param context
	 *          application's context.
	 * @param message
	 *          message to be displayed.
	 */
	public static void displayGCMMessage(Context context, String message)
	{
		Intent intent = new Intent(ACTION_DISPLAY_GCM_MESSAGE);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}

	public static void displayPODMessage(Context context, String message)
	{
		Intent intent = new Intent(ACTION_DISPLAY_POD_MESSAGE);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}

	public static void broadCastPODMessage(Context context, String type, String Id, int notificationId)
	{
		Intent intent = null;
		if (type.equals("M"))
			intent = new Intent(ACTION_POD_MESSAGE_M);
		else if (type.equals("C"))
			intent = new Intent(ACTION_POD_MESSAGE_C);
		else if (type.equals("CD"))
			intent = new Intent(ACTION_POD_MESSAGE_CD);

		intent.putExtra(EXTRA_MESSAGE, Id);
		intent.putExtra(EXTRA_NOTIFICATION_ID, notificationId);
		context.sendBroadcast(intent);
	}

	public static void showNotification(Context context, Intent notificationIntent, int notificationId, int icon, String title, String message)
	{

		NotificationManager notificationManager = null;
		Builder mBuilder = null;

		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		mBuilder = new Builder(context);
		mBuilder.setAutoCancel(true);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

		mBuilder.setSmallIcon(icon);
		mBuilder.setContentTitle(title);
		mBuilder.setContentText(message);
		mBuilder.setContentIntent(pendingIntent);

		notificationManager.notify(notificationId, mBuilder.build());

	}

	public static void playSound(Context context)
	{

		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Ringtone r = RingtoneManager.getRingtone(context, notification);
		r.play();

	}
	public static void playSoundRinger(Context context)
	{

		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		Ringtone r = RingtoneManager.getRingtone(context, notification);
		r.play();

	}
	
	public static void fadeOutView(final Context context, final View v)
	{
		final Animation fadeOutAnimation = AnimationUtils.loadAnimation(
				context, R.anim.fadeout);
		fadeOutAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0)
			{
			}

			@Override
			public void onAnimationRepeat(Animation arg0)
			{
			}

			@Override
			public void onAnimationEnd(Animation arg0)
			{
				v.setVisibility(View.GONE);

			}
		});
		// hintImageView.setAnimation(animation);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run()
			{
				((Activity) context).runOnUiThread(new Runnable() {
					@Override
					public void run()
					{
						v.startAnimation(fadeOutAnimation);
					}
				});
			}
		}, 5000);
	}
}
