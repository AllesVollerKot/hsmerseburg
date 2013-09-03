package datenUndHelfer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import event.EventItem;

import smk.SMKAenderung;

import aenderungen.Aenderungsitem;
import android.util.Log;

public class MySQLZugriff {
	private final String mTag = "Meldung";
	// constructor
	public MySQLZugriff() {
	}
	
	public ArrayList<Aenderungsitem> NewsParser(int id) {
		JSONObject input = getJSON("http://sexyateam.de/home/datenabfrage.php?tabelle=news&maxid=" + id);
		ArrayList<Aenderungsitem> liste = new ArrayList<Aenderungsitem>();
		JSONArray jar = null;
		try {
			if (input != null) {
				jar = input.getJSONArray("news");
				for (int i = 0; i < jar.length(); i++) {
					JSONObject objekt = jar.getJSONObject(i);
					Aenderungsitem item = new Aenderungsitem();
					item.setId(objekt.getInt("id"));
					item.setTitle(objekt.getString("titel"));
					item.setText(objekt.getString("newstext"));
					item.setLink(objekt.getString("link"));
					liste.add(item);
				}
			}
		} catch (JSONException e1) {
		}

		return liste;
	}

	public ArrayList<SMKAenderung> SMKParser(int id) {
		JSONObject input = getJSON("http://sexyateam.de/home/datenabfrage.php?tabelle=smk&maxid=" + id);
		ArrayList<SMKAenderung> liste = new ArrayList<SMKAenderung>();
		JSONArray jar = null;
		try {
			if (input != null) {
				jar = input.getJSONArray("aenderung");
				for (int i = 0; i < jar.length(); i++) {
					JSONObject objekt = jar.getJSONObject(i);
					SMKAenderung item = new SMKAenderung();
					item.setId(objekt.getInt("id"));
					item.setTitle(objekt.getString("titel"));
					item.setText(objekt.getString("aenderungtext"));
					item.setText2(objekt.getString("aenderungtext2"));
					item.setLink(objekt.getString("link"));
					liste.add(item);
				}
			}
		} catch (JSONException e1) {
		}

		return liste;
	}

	public ArrayList<EventItem> EventParser(int id) {
		Log.i(mTag, "EventParser");
		JSONObject input = getJSON("http://sexyateam.de/home/datenabfrage.php?tabelle=events&maxid=" + id);
		ArrayList<EventItem> liste = new ArrayList<EventItem>();
		JSONArray jar = null;
		try {
			if (input != null) {
				jar = input.getJSONArray("events");
				for (int i = 0; i < jar.length(); i++) {
					JSONObject objekt = jar.getJSONObject(i);
					EventItem item = new EventItem();
					item.setId(objekt.getInt("id"));
					item.setVeranstaltungTitel(objekt.getString("titel"));
					item.setVeranstaltungOrt(objekt.getString("ort"));
					item.setVeranstaltungLink(objekt.getString("link"));
					item.setVeranstaltungZeitBeginn(objekt.getString("beginnzeit"));
					item.setVeranstaltungZeitEnde(objekt.getString("endzeit"));
					item.setVeranstaltungDatumBeginn(objekt.getString("begindatum"));
					item.setVeranstaltungDatumEnde(objekt.getString("enddatum"));
					liste.add(item);
				}
			}
		} catch (JSONException e1) {
		}
		return liste;
	}

	public ArrayList<Aenderungsitem> IKSParser(int id) {
		Log.i(mTag, "AenderungParser");
		JSONObject input = null;
		input = getJSON("http://sexyateam.de/home/datenabfrage.php?tabelle=iks&maxid=" + id);
		ArrayList<Aenderungsitem> liste = new ArrayList<Aenderungsitem>();
		JSONArray jar = null;
		try {
			if (input != null) {
				jar = input.getJSONArray("aenderung");
				for (int i = 0; i < jar.length(); i++) {
					JSONObject objekt = jar.getJSONObject(i);
					Aenderungsitem item = new Aenderungsitem();
					item.setId(objekt.getInt("id"));
					item.setTitle(objekt.getString("titel"));
					item.setText(objekt.getString("aenderungtext"));
					item.setLink(objekt.getString("link"));
					liste.add(item);
				}
			}
		} catch (JSONException e1) {
		}
		return liste;
	}

	public ArrayList<Aenderungsitem> WiWiParser(int id) {
		Log.i(mTag, "AenderungParser");
		JSONObject input = null;
		input = getJSON("http://sexyateam.de/home/datenabfrage.php?tabelle=wiwi&maxid=" + id);
		ArrayList<Aenderungsitem> liste = new ArrayList<Aenderungsitem>();
		JSONArray jar = null;
		try {
			if (input != null) {
				jar = input.getJSONArray("aenderung");
				for (int i = 0; i < jar.length(); i++) {
					JSONObject objekt = jar.getJSONObject(i);
					Aenderungsitem item = new Aenderungsitem();
					item.setId(objekt.getInt("id"));
					item.setTitle(objekt.getString("titel"));
					item.setText(objekt.getString("aenderungtext"));
					item.setLink(objekt.getString("link"));
					liste.add(item);
				}
			}
		} catch (JSONException e1) {
		}
		return liste;
	}
	
	public JSONObject getJSON(String url) {
		InputStream is = null;
		JSONObject jObj = null;
		String json = "";
		// Making HTTP request
		try {
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
		}

		// return JSON String
		return jObj;

	}
}