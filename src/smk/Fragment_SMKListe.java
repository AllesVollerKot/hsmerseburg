package smk;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockFragment;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hsmerseburg.Activity_MainActivity.OnListItemClickListener;
import com.example.hsmerseburg.R;

import datenUndHelfer.SQLiteHelper;
import datenUndHelfer.Sharedstrings;

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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

public class Fragment_SMKListe extends SherlockFragment {
	private Context context;
	private ArrayList<SMKAenderung> mSMKListe;
	private ProgressBar mLoadAnimation;
	private SharedPreferences mPrefs;
	private ArrayAdapter_SMKAdapter mAdapter;
	private OnListItemClickListener mCallback;
	private SQLiteHelper mSqlite;
	private String mTag;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnListItemClickListener) getActivity();
		} catch (ClassCastException e) {
			throw new ClassCastException(getActivity().toString() + " must implement OnListItemClickListener");
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
		mSMKListe = new ArrayList<SMKAenderung>();
		mLoadAnimation = (ProgressBar) getView().findViewById(R.id.loadingAnimation);
		mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		ListView lv = (ListView) getView().findViewById(R.id.list);
		lv.setFadingEdgeLength(0);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
				mCallback.itemClicked(mSMKListe.get(index).getId(), "smk", mSMKListe.get(index).getNewsLink());
			}
		});
		mAdapter = new ArrayAdapter_SMKAdapter(context, mSMKListe);
		lv.setAdapter(mAdapter);
		mSqlite = new SQLiteHelper(context);

		// Alle Events aus sqlite laden
		mAdapter.addAll(mSqlite.getAllSmkList());

		// neue Events laden und anzeigen
		smkLoad();

	}

	public ArrayList<SMKAenderung> jArrayToEventlist(JSONArray jArray) {
		ArrayList<SMKAenderung> liste = new ArrayList<SMKAenderung>();
		try {
			if (jArray != null) {
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject objekt = jArray.getJSONObject(i);
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

	public void writeEventlistInSqlite(ArrayList<SMKAenderung> liste) {
		// Alle neuen Items Eintragen in SQLITE
		for (SMKAenderung smkItem : liste) {
			mSqlite.addSMK(smkItem);
		}
	}

	public void smkLoad() {
		SQLiteHelper sqlite = new SQLiteHelper(context);
		int maxSmk = sqlite.getMaxAenderungID(SQLiteHelper.FACHBEREICHSMK);
		Log.i(mTag, "Max smk id " + maxSmk);
		mLoadAnimation.setVisibility(View.VISIBLE);
		new Volley();
		RequestQueue queue = Volley.newRequestQueue(context);

		JsonObjectRequest myReqs = new JsonObjectRequest(Method.GET, "http://sexyateam.de/home/datenabfrage.php?tabelle=smk&maxid=" + maxSmk, null,
				createMyReqSuccessListener(), createMyReqErrorListener());
		queue.add(myReqs);
	}

	private Response.Listener<JSONObject> createMyReqSuccessListener() {
		return new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				mLoadAnimation.setVisibility(View.INVISIBLE);
				JSONArray jar = null;
				try {
					if (response != null) {
						jar = response.getJSONArray("aenderung");
						ArrayList<SMKAenderung> liste = jArrayToEventlist(jar);
						writeEventlistInSqlite(liste);
						for (SMKAenderung item : liste) {
							mAdapter.add(item);
						}
						mPrefs.edit().putBoolean(context.getString(R.string.preference_firstSMKSearch), false).apply();
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
				mLoadAnimation.setVisibility(View.INVISIBLE);
				Log.i(mTag, "createMyReqErrorListener: " + error.getMessage());
			}
		};
	}
}
