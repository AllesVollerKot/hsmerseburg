package com.example.hsmerseburg;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

import datenUndHelfer.Sharedstrings;

public class GCMRegister {

	private String ID;
	Boolean test;
	private String mTag = "Meldung";

	
	
	public void registerForGCM(Context context) {
		GCMRegistrar.unregister(context);
		GCMRegistrar.register(context, "1002451505744");
		String regId = GCMRegistrar.getRegistrationId(context);
		sendIDToDatabase(regId);		
	}

	public void sendIDToDatabase(String id) {
		ID = id;
		sendPost send = new sendPost();
		send.execute();
	}

	public static Boolean gcmTest(String testID) {
		String antwort = null;
		boolean result = false;
		try {
			antwort = new CheckID().execute(testID).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (antwort.contains("id vorhanden")) {
			result = true;
		} else {
			if (antwort.contains("id nicht vorhanden")) {
				result = false;
			}
		}
		return result;
	}

	private static class CheckID extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String antwort = "leer";
			String id = params[0];
			try {
				URL url = new URL("http://sexyateam.de/home/datenabfrage.php?gcm_regid=" + id);
				URLConnection connection = url.openConnection();
				connection.connect();
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuilder sb = new StringBuilder();
				while ((inputLine = in.readLine()) != null) {
					sb.append(inputLine);
				}
				in.close();
				antwort = sb.toString();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return antwort;
		}
	}

	class sendPost extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			URL url = null;
			try {
				url = new URL("http://sexyateam.de/home/register.php");
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String urlParameters = "regId=" + ID;
			HttpURLConnection connection = null;
			try {
				// Create connection

				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

				connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
				connection.setRequestProperty("Content-Language", "de-de");

				connection.setUseCaches(false);
				connection.setDoInput(true);
				connection.setDoOutput(true);

				// Send request
				DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
				wr.writeBytes(urlParameters);
				wr.flush();
				wr.close();

				// Get Response
				InputStream is = connection.getInputStream();
				BufferedReader rd = new BufferedReader(new InputStreamReader(is));
				String line;
				StringBuffer response = new StringBuffer();
				while ((line = rd.readLine()) != null) {
					response.append(line);
					response.append('\r');
				}
				rd.close();
				
				Log.i(mTag, response.toString());

			} catch (Exception e) {

				e.printStackTrace();
				return null;

			} finally {

				if (connection != null) {
					connection.disconnect();
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Log.i(mTag, "senden sendIDToDatabase beendet");
			super.onPostExecute(result);
		}

	}
}
