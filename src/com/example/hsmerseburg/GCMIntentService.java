package com.example.hsmerseburg;

import java.util.concurrent.TimeUnit;

import com.google.android.gcm.GCMBaseIntentService;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class GCMIntentService extends GCMBaseIntentService {

	final static String SENDER_ID = "1002451505744";
	private String mTag = "Meldung";

	public GCMIntentService() {
		super(SENDER_ID);
	}

	public void triggerNotification(String title, String text, int id) {
		Intent intent = new Intent(this, Activity_MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, id, intent, 0);
		Notification nB = new NotificationCompat.Builder(this).setContentTitle(title).setContentText(text).setTicker("Notification!")
				.setWhen(System.currentTimeMillis()).setContentIntent(pendingIntent).setDefaults(Notification.DEFAULT_SOUND).setAutoCancel(true)
				.setSmallIcon(R.drawable.ic_launcher).build();
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nB.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
		notificationManager.notify(id, nB);
	}

	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onMessage(Context context, Intent arg1) {
		Log.i(mTag, "RECIEVED GCM");
		startService();
	}

	public void startService() {

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String updateinterval = prefs.getString(getResources().getString(R.string.preference_updateinterval), "15");

		Intent serviceIntent = new Intent(getApplicationContext(), Service_GetUpdatesService.class);
		Log.i(mTag, "Updateinterval: " + updateinterval);
		if (updateinterval.contentEquals("Nie")) {
			Log.i(mTag, "Starte Service einmalig");
			startService(serviceIntent);
		} else {
			Log.i(mTag, "Starte Service mehrmalig");
			PendingIntent servicePendingIntent = PendingIntent.getService(GCMIntentService.this, 0, serviceIntent, 0);
			AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			long interval = TimeUnit.MINUTES.toMillis(5);
			long firstStart = System.currentTimeMillis();
			am.setInexactRepeating(AlarmManager.RTC, firstStart, interval, servicePendingIntent);
		}
	}

	@Override
	protected void onRegistered(Context arg0, String arg1) {

		Log.i(TAG, "OnRegistered" + arg1);
		GCMRegister register = new GCMRegister();
		register.sendIDToDatabase(arg1);

	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		Log.i(TAG, "Device unregistered");

	}
}