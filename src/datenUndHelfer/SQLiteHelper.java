package datenUndHelfer;

import java.util.ArrayList;
import java.util.Collections;

import event.EventItem;

import smk.SMKAenderung;

import aenderungen.Aenderungsitem;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

	private final String mtag = "Meldung";
	
	public static final String FACHBEREICHWIWI = "WW";
	public static final String FACHBEREICHIKS = "IKS";
	public static final String FACHBEREICHSMK = "SMK";

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "newsDatabase";

	private static final String TABLE_NEWS = "news";
	private static final String TABLE_WW = "ww";
	private static final String TABLE_IKS = "iks";
	private static final String TABLE_SMK = "smk";
	private static final String TABLE_EVENTS = "events";

	private static final String NEWS_KEY_ID = "id";
	private static final String NEWS_KEY_TITEL = "titel";
	private static final String NEWS_KEY_TEXT = "newstext";
	private static final String NEWS_KEY_LINK = "link";
	private static final String NEWS_KEY_HTML = "html";

	private static final String WW_KEY_ID = "id";
	private static final String WW_KEY_TITEL = "titel";
	private static final String WW_KEY_TEXT = "wwtext";
	private static final String WW_KEY_LINK = "link";
	private static final String WW_KEY_HTML = "html";

	private static final String IKS_KEY_ID = "id";
	private static final String IKS_KEY_TITEL = "titel";
	private static final String IKS_KEY_TEXT = "ikstext";
	private static final String IKS_KEY_LINK = "link";

	private static final String SMK_KEY_ID = "id";
	private static final String SMK_KEY_TITEL = "titel";
	private static final String SMK_KEY_TEXT = "smktext";
	private static final String SMK_KEY_TEXT2 = "smktext2";
	private static final String SMK_KEY_LINK = "link";
	private static final String SMK_KEY_HTML = "html";

	private static final String EVENT_KEY_ID = "id";
	private static final String EVENT_KEY_TITEL = "titel";
	private static final String EVENT_KEY_ORT = "ikstext";
	private static final String EVENT_KEY_LINK = "link";
	private static final String EVENT_KEY_BEGINNZEIT = "beginnzeit";
	private static final String EVENT_KEY_ENDZEIT = "endzeit";
	private static final String EVENT_KEY_BEGINDATUM = "begindatum";
	private static final String EVENT_KEY_ENDDATUM = "enddatum";
	private static final String EVENT_KEY_HTML = "html";

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(mtag, "sqlite oncreate");

		// TABELLE NEWS
		String CREATE_NEWS_TABLE = "CREATE TABLE " + TABLE_NEWS + "(" + NEWS_KEY_ID + " INTEGER PRIMARY KEY," + NEWS_KEY_TITEL + " TEXT,"
				+ NEWS_KEY_TEXT + " TEXT," + NEWS_KEY_LINK + " TEXT," + NEWS_KEY_HTML + " TEXT" + ")";
		db.execSQL(CREATE_NEWS_TABLE);

		// TABELLE WIWI
		String CREATE_WW_TABLE = "CREATE TABLE " + TABLE_WW + "(" + WW_KEY_ID + " INTEGER PRIMARY KEY," + WW_KEY_TITEL + " TEXT," + WW_KEY_TEXT
				+ " TEXT," + WW_KEY_LINK + " TEXT," + WW_KEY_HTML + " TEXT" + ")";
		db.execSQL(CREATE_WW_TABLE);

		// TABELLE IKS
		String CREATE_IKS_TABLE = "CREATE TABLE " + TABLE_IKS + "(" + IKS_KEY_ID + " INTEGER PRIMARY KEY," + IKS_KEY_TITEL + " TEXT," + IKS_KEY_TEXT
				+ " TEXT," + IKS_KEY_LINK + " TEXT" + ")";
		db.execSQL(CREATE_IKS_TABLE);

		// TABELLE SMK
		String CREATE_SMK_TABLE = "CREATE TABLE " + TABLE_SMK + "" + "(" + SMK_KEY_ID + " INTEGER PRIMARY KEY," + SMK_KEY_TITEL + " TEXT,"
				+ SMK_KEY_TEXT + " TEXT," + SMK_KEY_TEXT2 + " TEXT," + SMK_KEY_LINK + " TEXT," + SMK_KEY_HTML + " TEXT" + ")";
		db.execSQL(CREATE_SMK_TABLE);

		// TABELLE EVENTS
		String CREATE_EVENT_TABLE = "CREATE TABLE " + TABLE_EVENTS + "(" + EVENT_KEY_ID + " INTEGER PRIMARY KEY," + EVENT_KEY_TITEL + " TEXT,"
				+ EVENT_KEY_ORT + " TEXT," + EVENT_KEY_BEGINNZEIT + " TEXT," + EVENT_KEY_ENDZEIT + " TEXT," + EVENT_KEY_BEGINDATUM + " TEXT,"
				+ EVENT_KEY_ENDDATUM + " TEXT," + EVENT_KEY_LINK + " TEXT," + EVENT_KEY_HTML + " TEXT" + ")";
		db.execSQL(CREATE_EVENT_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WW);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_IKS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SMK);
		onCreate(db);
		Log.i(mtag, "sqlite onUpgrade");
	}

	public void addNews(Aenderungsitem news) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(NEWS_KEY_ID, news.getId());
		values.put(NEWS_KEY_TITEL, news.getNewsTitle());
		values.put(NEWS_KEY_TEXT, news.getNewsText());
		values.put(NEWS_KEY_LINK, news.getNewsLink());
		try {
			db.insertOrThrow(TABLE_NEWS, null, values);
			Log.i(mtag, "Added to News: " + values.getAsString(NEWS_KEY_TITEL));
		} catch (SQLException e) {
		}

		// db.close();
	}

	public Aenderungsitem getNews(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NEWS, new String[] { NEWS_KEY_ID, NEWS_KEY_TITEL, NEWS_KEY_TEXT, NEWS_KEY_LINK }, NEWS_KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		Aenderungsitem news = new Aenderungsitem(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3));
		return news;
	}

	public String getNewsHTML(int id) {
		String html = "";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NEWS, new String[] { NEWS_KEY_ID, NEWS_KEY_HTML }, NEWS_KEY_ID + "=?", new String[] { String.valueOf(id) },
				null, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			html = cursor.getString(1);
		}
		return html;
	}

	public int setNewsHTML(int id, String html) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(NEWS_KEY_HTML, html);
		return db.update(TABLE_NEWS, values, NEWS_KEY_ID + " = ?", new String[] { String.valueOf(id) });
	}

	public void addEvent(EventItem event) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(EVENT_KEY_ID, event.getId());
		values.put(EVENT_KEY_TITEL, event.getVeranstaltungTitel());
		values.put(EVENT_KEY_ORT, event.getVeranstaltungOrt());
		values.put(EVENT_KEY_LINK, event.getVeranstaltungLink());
		values.put(EVENT_KEY_BEGINNZEIT, event.getVeranstaltungZeitBeginn());
		values.put(EVENT_KEY_ENDZEIT, event.getVeranstaltungZeitEnde());
		values.put(EVENT_KEY_BEGINDATUM, event.getVeranstaltungDatumBeginn());
		values.put(EVENT_KEY_ENDDATUM, event.getVeranstaltungDatumEnde());
		try {
			db.insertOrThrow(TABLE_EVENTS, null, values);
			Log.i(mtag, "Added to Events: " + values.getAsString(EVENT_KEY_TITEL));
		} catch (SQLException e) {
		}

		// db.close();
	}

	public EventItem getEvent(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_EVENTS, new String[] { EVENT_KEY_ID, EVENT_KEY_TITEL, EVENT_KEY_ORT, EVENT_KEY_LINK, EVENT_KEY_BEGINNZEIT,
				EVENT_KEY_ENDZEIT, EVENT_KEY_BEGINDATUM, EVENT_KEY_ENDDATUM }, EVENT_KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null,
				null);
		if (cursor != null)
			cursor.moveToFirst();
		EventItem event = new EventItem(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3),
				cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7));
		return event;
	}

	public int setEventHTML(int id, String html) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(EVENT_KEY_HTML, html);
		return db.update(TABLE_EVENTS, values, EVENT_KEY_ID + " = ?", new String[] { String.valueOf(id) });
	}

	public void addWiWi(Aenderungsitem news) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(WW_KEY_ID, news.getId());
		values.put(WW_KEY_TITEL, news.getNewsTitle());
		values.put(WW_KEY_TEXT, news.getNewsText());
		values.put(WW_KEY_LINK, news.getNewsLink());
		try {
			db.insertOrThrow(TABLE_WW, null, values);
			Log.i(mtag, "Added to WW: " + values.getAsString(WW_KEY_TITEL));
		} catch (SQLException e) {
		}
		// db.close();
	}

	public int setWiWiHTML(int id, String html) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(WW_KEY_HTML, html);
		return db.update(TABLE_WW, values, WW_KEY_ID + " = ?", new String[] { String.valueOf(id) });
	}

	public String getWiWiHTML(int id) {
		String html = "";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_WW, new String[] { WW_KEY_ID, WW_KEY_HTML }, WW_KEY_ID + "=?", new String[] { String.valueOf(id) }, null,
				null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			html = cursor.getString(1);
		}
		return html;
	}

	public void addIKS(Aenderungsitem news) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(IKS_KEY_ID, news.getId());
		values.put(IKS_KEY_TITEL, news.getNewsTitle());
		values.put(IKS_KEY_TEXT, news.getNewsText());
		values.put(IKS_KEY_LINK, news.getNewsLink());
		try {
			db.insertOrThrow(TABLE_IKS, null, values);
			Log.i(mtag, "Added to IKS: " + values.getAsString(IKS_KEY_TITEL));
		} catch (SQLException e) {
		}
		// db.close();
	}

	public Aenderungsitem getIKS(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_IKS, new String[] { IKS_KEY_ID, IKS_KEY_TITEL, IKS_KEY_TEXT, IKS_KEY_LINK }, IKS_KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		Aenderungsitem iks = new Aenderungsitem(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3));
		return iks;
	}

	public void addSMK(SMKAenderung smk) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(SMK_KEY_ID, smk.getId());
		values.put(SMK_KEY_TITEL, smk.getNewsTitle());
		values.put(SMK_KEY_TEXT, smk.getNewsText());
		values.put(SMK_KEY_TEXT2, smk.getText2());
		values.put(SMK_KEY_LINK, smk.getNewsLink());
		try {
			db.insertOrThrow(TABLE_SMK, null, values);
			Log.i(mtag, "Added to SMK: " + values.getAsString(SMK_KEY_TITEL));
		} catch (SQLException e) {
		}

		// db.close();
	}

	public SMKAenderung getSMK(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_SMK, new String[] { SMK_KEY_ID, SMK_KEY_TITEL, SMK_KEY_TEXT, SMK_KEY_TEXT2, SMK_KEY_LINK }, SMK_KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		SMKAenderung news = new SMKAenderung(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3),
				cursor.getString(4));
		return news;
	}

	public String getSMKHTML(int id) {
		String html = "";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_SMK, new String[] { SMK_KEY_ID, SMK_KEY_HTML }, SMK_KEY_ID + "=?", new String[] { String.valueOf(id) }, null,
				null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			html = cursor.getString(1);
		}
		return html;
	}

	public int setSMKHTML(int id, String html) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(SMK_KEY_HTML, html);
		return db.update(TABLE_SMK, values, SMK_KEY_ID + " = ?", new String[] { String.valueOf(id) });
	}

	public String getEventHTML(int id) {
		String html = "";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_EVENTS, new String[] { EVENT_KEY_ID, EVENT_KEY_HTML }, EVENT_KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			html = cursor.getString(1);
		}
		return html;
	}

	public int getIKSAenderungCount() {
		SQLiteDatabase db = this.getReadableDatabase();
		String countQuery = "";
		countQuery = "SELECT  * FROM " + TABLE_IKS;
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();
		return cursor.getCount();
	}

	public Cursor getAllNews() {
		SQLiteDatabase db = this.getWritableDatabase();
		String selectQuery = "SELECT  * FROM " + TABLE_NEWS;
		Cursor cursor = db.rawQuery(selectQuery, null);
		return cursor;
	}

	public ArrayList<Aenderungsitem> getAllNewsList() {
		Cursor cursor = getAllNews();
		ArrayList<Aenderungsitem> liste = new ArrayList<Aenderungsitem>();
		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				do {
					Aenderungsitem item = new Aenderungsitem();
					item.setId(Integer.parseInt(cursor.getString(0)));
					item.setTitle(cursor.getString(1));
					item.setText(cursor.getString(2));
					item.setLink(cursor.getString(3));
					liste.add(item);
				} while (cursor.moveToNext());
				Collections.reverse(liste);
			}
		}
		return liste;
	}

	public Cursor getAllEvents() {
		SQLiteDatabase db = this.getWritableDatabase();
		String selectQuery = "SELECT  * FROM " + TABLE_EVENTS;
		Cursor cursor = db.rawQuery(selectQuery, null);
		return cursor;
	}

	public ArrayList<EventItem> getAllEventList() {
		Cursor cursor = getAllEvents();
		ArrayList<EventItem> liste = new ArrayList<EventItem>();
		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				do {
					EventItem item = new EventItem();
					item.setId(Integer.parseInt(cursor.getString(0)));
					item.setVeranstaltungTitel(cursor.getString(1));
					item.setVeranstaltungOrt(cursor.getString(2));
					item.setVeranstaltungZeitBeginn(cursor.getString(3));
					item.setVeranstaltungZeitEnde(cursor.getString(4));
					item.setVeranstaltungDatumBeginn(cursor.getString(5));
					item.setVeranstaltungDatumEnde(cursor.getString(6));
					item.setVeranstaltungLink(cursor.getString(7));
					liste.add(item);
				} while (cursor.moveToNext());
				Collections.reverse(liste);
			}
		}
		return liste;
	}

	public Cursor getAllWIWI() {
		SQLiteDatabase db = this.getWritableDatabase();
		String selectQuery = "SELECT  * FROM " + TABLE_WW;
		Cursor cursor = db.rawQuery(selectQuery, null);
		return cursor;
	}

	public ArrayList<Aenderungsitem> getAllWIWIList() {
		Cursor cursor = getAllWIWI();
		ArrayList<Aenderungsitem> liste = new ArrayList<Aenderungsitem>();
		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				do {
					Aenderungsitem item = new Aenderungsitem();
					item.setId(Integer.parseInt(cursor.getString(0)));
					item.setTitle(cursor.getString(1));
					item.setText(cursor.getString(2));
					item.setLink(cursor.getString(3));
					liste.add(item);
				} while (cursor.moveToNext());
				Collections.reverse(liste);
			}
		}
		return liste;
	}

	public Cursor getAllSMK() {
		SQLiteDatabase db = this.getWritableDatabase();
		String selectQuery = "SELECT  * FROM " + TABLE_SMK;
		Cursor cursor = db.rawQuery(selectQuery, null);
		return cursor;
	}

	public ArrayList<SMKAenderung> getAllSmkList() {
		Cursor cursor = getAllSMK();
		ArrayList<SMKAenderung> liste = new ArrayList<SMKAenderung>();
		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				do {
					SMKAenderung item = new SMKAenderung();
					item.setId(Integer.parseInt(cursor.getString(0)));
					item.setTitle(cursor.getString(1));
					item.setText(cursor.getString(2));
					item.setText2(cursor.getString(3));
					item.setLink(cursor.getString(4));
					liste.add(item);
				} while (cursor.moveToNext());
				Collections.reverse(liste);
			}
		}
		return liste;
	}

	public Cursor getAllIKS() {
		SQLiteDatabase db = this.getWritableDatabase();
		String selectQuery = "SELECT  * FROM " + TABLE_IKS;
		Cursor cursor = db.rawQuery(selectQuery, null);
		return cursor;
	}

	public ArrayList<Aenderungsitem> getAllIKSList() {
		Cursor cursor = getAllIKS();
		ArrayList<Aenderungsitem> liste = new ArrayList<Aenderungsitem>();
		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				do {
					Aenderungsitem item = new Aenderungsitem();
					item.setId(Integer.parseInt(cursor.getString(0)));
					item.setTitle(cursor.getString(1));
					item.setText(cursor.getString(2));
					item.setLink(cursor.getString(3));
					liste.add(item);
				} while (cursor.moveToNext());
				Collections.reverse(liste);
			}
		}
		return liste;
	}

	public int getWiWiCount() {
		SQLiteDatabase db = this.getReadableDatabase();
		String countQuery = "";
		countQuery = "SELECT  * FROM " + TABLE_WW;
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();
		return cursor.getCount();
	}

	public int getSMKAenderungCount() {
		SQLiteDatabase db = this.getReadableDatabase();
		String countQuery = "";
		countQuery = "SELECT  * FROM " + TABLE_SMK;
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();
		return cursor.getCount();
	}

	public int getNewsCount() {
		SQLiteDatabase db = this.getReadableDatabase();
		String countQuery = "SELECT  * FROM " + TABLE_NEWS;
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount();
		cursor.close();
		return count;
	}

	public int getEventCount() {
		SQLiteDatabase db = this.getReadableDatabase();
		String countQuery = "SELECT  * FROM " + TABLE_EVENTS;
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();
		return cursor.getCount();
	}

	public int getMaxNewsID() {
		SQLiteDatabase db = this.getReadableDatabase();
		String countQuery = "SELECT MAX(id) FROM " + TABLE_NEWS;
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = 0;
		if (cursor.moveToFirst()) {
			do {
				count = cursor.getInt(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return count;
	}

	public int getMaxAenderungID(String fachbereich) {
		SQLiteDatabase db = this.getReadableDatabase();
		String countQuery = "SELECT MAX(id) FROM " + TABLE_WW;
		if (fachbereich.contentEquals("WW")) {
			countQuery = "SELECT MAX(id) FROM " + TABLE_WW;
		}
		if (fachbereich.contentEquals("IKS")) {
			countQuery = "SELECT MAX(id) FROM " + TABLE_IKS;
		}
		if (fachbereich.contentEquals("SMK")) {
			countQuery = "SELECT MAX(id) FROM " + TABLE_SMK;
		}
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = 0;
		if (cursor.moveToFirst()) {
			do {
				count = cursor.getInt(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return count;
	}

	public int getMaxEventID() {
		SQLiteDatabase db = this.getReadableDatabase();
		String countQuery = "SELECT MAX(id) FROM " + TABLE_EVENTS;
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = 0;
		if (cursor.moveToFirst()) {
			do {
				count = cursor.getInt(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return count;
	}

	public void deleteAll() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("delete from " + TABLE_NEWS);
		db.execSQL("delete from " + TABLE_WW);
		db.execSQL("delete from " + TABLE_IKS);
		db.execSQL("delete from " + TABLE_EVENTS);
		db.execSQL("delete from " + TABLE_SMK);
	}
}
