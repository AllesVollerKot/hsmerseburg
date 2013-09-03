package com.example.hsmerseburg;

import java.util.ArrayList;

import smk.SMKAenderung;

import datenUndHelfer.SQLiteHelper;
import datenUndHelfer.MySQLZugriff;
import event.EventItem;
import aenderungen.Aenderungsitem;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class CopyOfService_GetUpdatesService extends Service {
	static final String tag = "Meldung";

	final Context context = this;

	final int startId_News = 0;
	final int startId_Events = 1;
	final int startId_WiWi = 2;
	final int startId_SMK = 3;
	final int startId_IKS = 4;

	final int NOTIFICATION_ID_ALL = 1340;
	final int NOTIFICATION_ID_NEWS = 1341;
	final int NOTIFICATION_ID_EVENTS = 1342;
	final int NOTIFICATION_ID_WIWI = 1343;
	final int NOTIFICATION_ID_IKS = 1344;
	final int NOTIFICATION_ID_SMK = 1345;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(tag, "Service gestartet");
		GetUpdatesTask task = new GetUpdatesTask();
		task.execute();
		return START_STICKY;
	}

	public void triggerNotification(String title, String text, int id, int icon, int startFragment) {
		Intent i = new Intent(context, Activity_MainActivity.class);
		PendingIntent pend = PendingIntent.getActivity(context, 0, i, 0);

		Notification myNotification = new NotificationCompat.Builder(context).setContentTitle(title).setContentText(text).setTicker(title)
				.setWhen(System.currentTimeMillis()).setContentIntent(pend).setDefaults(Notification.DEFAULT_SOUND).setAutoCancel(true)
				.setSmallIcon(icon).build();

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		myNotification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(id, myNotification);

	}

	private class GetUpdatesTask extends AsyncTask<Void, Void, Void> {
		ArrayList<Aenderungsitem> NewsListe;
		ArrayList<Aenderungsitem> IKSListe;
		ArrayList<Aenderungsitem> WiWiListe;
		ArrayList<SMKAenderung> SMKListe;
		ArrayList<EventItem> EventListe;
		SQLiteHelper data = new SQLiteHelper(getApplicationContext());

		@Override
		protected Void doInBackground(Void... arg0) {
			MySQLZugriff jdon = new MySQLZugriff();
			NewsListe = jdon.NewsParser(data.getMaxNewsID());
			EventListe = jdon.EventParser(data.getMaxEventID());
			WiWiListe = jdon.WiWiParser(data.getMaxAenderungID(SQLiteHelper.FACHBEREICHWIWI));
			IKSListe = jdon.IKSParser(data.getMaxAenderungID(SQLiteHelper.FACHBEREICHIKS));
			SMKListe = jdon.SMKParser(data.getMaxAenderungID(SQLiteHelper.FACHBEREICHSMK));
			return null;
		}

		private void newsNotification(int count) {
			if (count == 1) {
				triggerNotification(NewsListe.get(NewsListe.size() - 1).getNewsTitle(), "neue News vorhanden", NOTIFICATION_ID_NEWS,
						R.drawable.icon_home, startId_News);
			} else {
				triggerNotification("Home News", "mehrere News vorhanden", NOTIFICATION_ID_NEWS, R.drawable.icon_home, startId_News);
			}
		}

		private void eventNotification(int count) {
			if (count == 1) {
				triggerNotification(EventListe.get(EventListe.size() - 1).getVeranstaltungTitel(), "neue Veranstaltung vorhanden",
						NOTIFICATION_ID_EVENTS, R.drawable.icon_home, startId_Events);
			} else {
				triggerNotification("Events", "mehrere Events vorhanden", NOTIFICATION_ID_EVENTS, R.drawable.icon_home, startId_Events);
			}
		}

		private void wiwiNotification(int count) {
			if (count == 1) {
				triggerNotification(WiWiListe.get(WiWiListe.size() - 1).getNewsTitle(), "neue WiWi Änderung vorhanden", NOTIFICATION_ID_WIWI,
						R.drawable.icon_wiwi, startId_WiWi);
			} else {
				triggerNotification("WiWi Änderungen", "mehrere WiWi Änderungen vorhanden", NOTIFICATION_ID_WIWI, R.drawable.icon_wiwi, startId_WiWi);
			}
		}

		private void iksNotification(int count) {
			if (count == 1) {
				triggerNotification(IKSListe.get(IKSListe.size() - 1).getNewsTitle(), "neue IKS Änderung vorhanden", NOTIFICATION_ID_IKS,
						R.drawable.icon_iks, startId_IKS);
			} else {
				triggerNotification("IKS Änderungen", "mehrere IKS Änderungen vorhanden", NOTIFICATION_ID_IKS, R.drawable.icon_iks, startId_IKS);
			}
		}

		private void smkNotification(int count) {
			if (count == 1) {
				triggerNotification(SMKListe.get(SMKListe.size() - 1).getNewsTitle(), "neue SMK Änderung vorhanden", NOTIFICATION_ID_SMK,
						R.drawable.icon_smk, startId_SMK);
			} else {
				triggerNotification("SMK Änderungen", "mehrere SMK Änderungen vorhanden", NOTIFICATION_ID_SMK, R.drawable.icon_smk, startId_SMK);
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			boolean newsAnzeigen = prefs.getBoolean(getApplicationContext().getResources().getString(R.string.preference_showNews), true);
			boolean eventanzeigen = prefs.getBoolean(getApplicationContext().getResources().getString(R.string.preference_showEvents), true);
			boolean wwanzeigen = prefs.getBoolean(getApplicationContext().getResources().getString(R.string.preference_showWiWi), true);
			boolean iksanzeigen = prefs.getBoolean(getApplicationContext().getResources().getString(R.string.preference_showIKS), true);
			boolean smkanzeigen = prefs.getBoolean(getApplicationContext().getResources().getString(R.string.preference_showSMK), true);

			boolean firstNewsScan = prefs.getBoolean(getApplicationContext().getString(R.string.preference_firstNewsSearch), true);
			boolean firstEventScan = prefs.getBoolean(getApplicationContext().getString(R.string.preference_firstEventSearch), true);
			boolean firstWiWiScan = prefs.getBoolean(getApplicationContext().getString(R.string.preference_firstWIWISearch), true);
			boolean firstIksScan = prefs.getBoolean(getApplicationContext().getString(R.string.preference_firstIKSSearch), true);
			boolean firstSmkScan = prefs.getBoolean(getApplicationContext().getString(R.string.preference_firstSMKSearch), true);

			int counter;
			// NEWS EINTRAGEN
			if (NewsListe.size() > 0) {
				counter = 0;
				for (Aenderungsitem item : NewsListe) {
					data.addNews(item);
					counter += 1;
				}
				if (newsAnzeigen&&firstNewsScan == false) {
					newsNotification(counter);
				}
				prefs.edit().putBoolean(getApplication().getString(R.string.preference_firstNewsSearch), false).apply();
			} else {
			}
			// EVENTS EINTRAGEN
			if (EventListe.size() > 0) {
				counter = 0;
				for (EventItem item : EventListe) {
					data.addEvent(item);
					counter += 1;
				}
				if (eventanzeigen&&firstEventScan == false) {
					eventNotification(counter);
				}
				prefs.edit().putBoolean(getApplication().getString(R.string.preference_firstEventSearch), false).apply();
			} else {
			}
			// WiWi EINTRAGEN
			if (WiWiListe.size() > 0) {
				counter = 0;
				for (Aenderungsitem item : WiWiListe) {
					data.addWiWi(item);
					counter += 1;
				}
				if (wwanzeigen&&firstWiWiScan==false) {
					wiwiNotification(counter);
				}
				prefs.edit().putBoolean(getApplication().getString(R.string.preference_firstWIWISearch), false).apply();
			} else {
			}
			// IKS EINTRAGEN
			if (IKSListe.size() > 0) {
				counter = 0;
				for (Aenderungsitem item : IKSListe) {
					data.addIKS(item);
					counter += 1;
				}
				if (iksanzeigen&&firstIksScan==false) {
					iksNotification(counter);
				}
				prefs.edit().putBoolean(getApplication().getString(R.string.preference_firstIKSSearch), false).apply();
			} else {
			}
			// SMK EINTRAGEN
			if (SMKListe.size() > 0) {
				counter = 0;
				for (SMKAenderung item : SMKListe) {
					data.addSMK(item);
					counter += 1;
				}
				if (smkanzeigen&&firstSmkScan==false) {
					smkNotification(counter);
				}
				prefs.edit().putBoolean(getApplication().getString(R.string.preference_firstSMKSearch), false).apply();
			} else {
			}
			Log.i(tag, "Service beendet");
			stopSelf();
			super.onPostExecute(result);
		}
	}
}
