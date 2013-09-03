package wiwi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hsmerseburg.R;

import datenUndHelfer.MultiHelper;
import datenUndHelfer.SQLiteHelper;
import datenUndHelfer.Sharedstrings;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class Fragment_WiWiAnzeige extends SherlockFragment {
	private Context context;
	static final String tag = "Meldung";
	private int wiwiId = 0;
	private String url = "";
	private ProgressBar bar;
	private SQLiteHelper sqlite;
	private WebView webView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
		context = getSherlockActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_webview_layout, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bar = (ProgressBar) getView().findViewById(R.id.webViewProgressBar);
		webView = (WebView) getView().findViewById(R.id.newswebview);
		sqlite = new SQLiteHelper(context);
		if (getArguments() != null) {
			wiwiId = getArguments().getInt("id", 0);
			url = getArguments().getString("url");
		}

		if (wiwiId != 0) {
			String htmlSqlite = sqlite.getWiWiHTML(wiwiId);
			if (htmlSqlite == null) {
				wiwiLoad(wiwiId);
			} else {
				webView.loadDataWithBaseURL("http://www.hs-merseburg.de", htmlSqlite, "text/html", "charset=UTF-8", "www.hs-merseburg.de/");
				bar.setVisibility(View.INVISIBLE);
			}
		}
	}

	public void setWebViewContent(String html) {
		webView.loadDataWithBaseURL("http://www.hs-merseburg.de", html, "text/html", "charset=UTF-8", "www.hs-merseburg.de/");
		bar.setVisibility(View.INVISIBLE);
		sqlite.setWiWiHTML(wiwiId, html);
		webView.invalidate();
	}

	public void wiwiLoad(int id) {
		bar.setVisibility(View.VISIBLE);
		new Volley();
		RequestQueue queue = Volley.newRequestQueue(context);
		JsonObjectRequest myReqs = new JsonObjectRequest(Method.GET, "http://sexyateam.de/home/datenabfrage.php?html=wiwi&id=" + id, null,
				createMyReqSuccessListener(), createMyReqErrorListener());
		queue.add(myReqs);
	}

	private Response.Listener<JSONObject> createMyReqSuccessListener() {
		return new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				String html = "";
				JSONArray jar = null;
				try {
					if (response != null) {
						jar = response.getJSONArray("wiwihtml");
						JSONObject objekt = jar.getJSONObject(0);
						html = objekt.getString("html");
						String header = getResources().getString(R.string.webview_header);
						setWebViewContent(header + html);
					}
				} catch (JSONException e1) {
				}
			}
		};
	}

	private Response.ErrorListener createMyReqErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.i(tag, error.getMessage());
				String errorText = getResources().getString(R.string.webview_error);
				webView.loadData(errorText, "text/html", "charset=UTF-8");
			}
		};
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.findItem(R.id.optionen_send_as_message).setVisible(true);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.optionen_send_as_message:
			Intent sharingIntent = MultiHelper.sendMessage(url, "WiWi News");
			startActivity(Intent.createChooser(sharingIntent, "Senden mit: "));
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
