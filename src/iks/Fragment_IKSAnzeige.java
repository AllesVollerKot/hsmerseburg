package iks;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.hsmerseburg.R;

import datenUndHelfer.MultiHelper;
import datenUndHelfer.SQLiteHelper;

import aenderungen.Aenderungsitem;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class Fragment_IKSAnzeige extends SherlockFragment {
	private Context context;
	static final String tag = "Meldung";
	private int iksID = 0;
	private String url = "";
	private ProgressBar bar;
	private WebView webView;
	SQLiteHelper sqlite;

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

	public void reload() {
		if (iksID != 0) {
			Aenderungsitem iks = sqlite.getIKS(iksID);
			
			StringBuilder sb = new StringBuilder();
			sb.append("<h3>").append(iks.getNewsTitle()).append("</h3>");
			sb.append("<h2>").append(iks.getNewsText()).append("</h2>");
			
			String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
			webView.loadDataWithBaseURL("http://www.hs-merseburg.de", header + sb.toString(), "text/html", "charset=UTF-8", "www.hs-merseburg.de/");
			bar.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		bar = (ProgressBar) getView().findViewById(R.id.webViewProgressBar);
		sqlite = new SQLiteHelper(context);
		webView = (WebView) getView().findViewById(R.id.newswebview);
		if (getArguments() != null) {
			iksID = getArguments().getInt("id", 0);
			url = getArguments().getString("url");
		}
		reload();		
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
			Intent sharingIntent = MultiHelper.sendMessage(url, "IKS Änderung");			
			startActivity(Intent.createChooser(sharingIntent, "Senden mit: "));
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
