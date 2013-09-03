package event;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockFragment;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hsmerseburg.Activity_MainActivity.OnListItemClickListener;
import com.example.hsmerseburg.R;
import datenUndHelfer.SQLiteHelper;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

public class Fragment_EventListe extends SherlockFragment {
	private Context context;
	private ArrayList<EventItem> mEventListe;
	private ProgressBar mLoadingAnimation;
	private SharedPreferences mPrefs;
	private ArrayAdapter_EventAdapter mAdapter;
	private OnListItemClickListener mCallback;
	private SQLiteHelper mSqlite;
	private String mTag;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnListItemClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnListItemClickListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getSherlockActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_list_layout, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mTag = getResources().getString(R.string.log_tag);
		mEventListe = new ArrayList<EventItem>();
		mLoadingAnimation = (ProgressBar) getView().findViewById(R.id.loadingAnimation);
		mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		ListView lv = (ListView) getView().findViewById(R.id.list);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
				mCallback.itemClicked(mEventListe.get(index).getId(), "events", mEventListe.get(index).getVeranstaltungLink());
			}
		});
		mAdapter = new ArrayAdapter_EventAdapter(context, mEventListe);
		lv.setAdapter(mAdapter);
		mSqlite = new SQLiteHelper(context);
		mAdapter.addAll(mSqlite.getAllEventList());
		eventLoad();
	}

	public ArrayList<EventItem> jArrayToEventlist(JSONArray jArray) {
		ArrayList<EventItem> liste = new ArrayList<EventItem>();
		try {
			if (jArray != null) {
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject objekt = jArray.getJSONObject(i);
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

	public void writeEventlistInSqlite(ArrayList<EventItem> liste) {

		// Alle neuen Items Eintragen in SQLITE
		for (EventItem eventItem : liste) {
			mSqlite.addEvent(eventItem);
		}
	}

	public void eventLoad() {
		SQLiteHelper sqlite = new SQLiteHelper(context);
		int maxEvents = sqlite.getMaxEventID();
		mLoadingAnimation.setVisibility(View.VISIBLE);
		new Volley();
		RequestQueue queue = Volley.newRequestQueue(context);
		JsonObjectRequest myReqs = new JsonObjectRequest(Method.GET, "http://sexyateam.de/home/datenabfrage.php?tabelle=events&maxid=" + maxEvents,
				null, createMyReqSuccessListener(), createMyReqErrorListener());
		queue.add(myReqs);

	}

	private Response.Listener<JSONObject> createMyReqSuccessListener() {
		return new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				mLoadingAnimation.setVisibility(View.INVISIBLE);
				JSONArray jar = null;
				try {
					if (response != null) {
						jar = response.getJSONArray("events");
						ArrayList<EventItem> liste = jArrayToEventlist(jar);
						writeEventlistInSqlite(liste);
						for (EventItem item : liste) {
							mAdapter.add(item);
						}
						mPrefs.edit().putBoolean(context.getString(R.string.preference_firstEventSearch), false).apply();
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
				mLoadingAnimation.setVisibility(View.INVISIBLE);
				Log.i(mTag, "createMyReqErrorListener: " + error.getMessage());
			}
		};
	}
}
