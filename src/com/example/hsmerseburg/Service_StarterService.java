package com.example.hsmerseburg;

import java.util.concurrent.TimeUnit;

import com.google.android.gcm.GCMRegistrar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class Service_StarterService extends Service {
	final String mTag ="Meldung";

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if (pushTest() == false) {
			String Auswahl = "60";
			Intent serviceIntent = new Intent(getApplicationContext(), Service_GetUpdatesService.class);
			PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, serviceIntent, 0);
			AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			if (Auswahl.contains("Nie")) {
				am.cancel(servicePendingIntent);
			} else {
				long interval = TimeUnit.MINUTES.toMillis(Long.parseLong(Auswahl));
				long firstStart = System.currentTimeMillis();
				am.setInexactRepeating(AlarmManager.RTC, firstStart, interval, servicePendingIntent);
			}
		}
	}

	public boolean pushTest() {
		boolean pushOk = false;
		try {
			// Checken OB DAS GERÄT GCM UNTERSTÜTZT
			GCMRegistrar.checkDevice(this);
			pushOk = true;
			Log.i(mTag, "Push möglich");
		} catch (Exception e) {
			// GCM NICHT UNTERSTÜTZT
			Log.i(mTag, e.toString());
			Log.i(mTag, "Push nicht möglich");
		}
		final String GCMRegID = GCMRegistrar.getRegistrationId(this);

		// WENN PUSH MÖGLICH IST
		if (pushOk) {
			GCMRegister register = new GCMRegister();
			// WENN NOCH KEINE ID VORHANDEN IST
			if (GCMRegID.equals("")) {
				Log.i(mTag, "Noch keine ID vorhanden");
				register.registerForGCM(this);
			}
			// ID SCHON VORHNADEN
			else {
				// CHECK OB AUCH AUF SERVER GESPEICHERT
				if (GCMRegister.gcmTest(GCMRegID)) {
					Log.i(mTag, "Device Registered on GCM and Database");
				} else {
					// ID NICHT IN DATENBANK ALSO NEU SENDEN
					register.sendIDToDatabase(GCMRegID);
					Log.i(mTag, "Device Registered on GCM but not on Database. Register Again.");
				}
			}
			return true;
		}
		return false;
	}

}
