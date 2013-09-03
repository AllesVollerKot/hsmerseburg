package iks;

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
import datenUndHelfer.Sharedstrings;
import aenderungen.Aenderungsitem;
import aenderungen.ArrayAdapter_AendungerungsAdapter;
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

public class Fragment_IKSListe extends SherlockFragment {
	private Context context;
	private ArrayList<Aenderungsitem> mIKSListe;
	private ProgressBar mLoadAnimation;
	private SharedPreferences mPrefs;
	private ArrayAdapter_AendungerungsAdapter mAdapter;
	private OnListItemClickListener mCallback;
	private SQLiteHelper mSqlite;
	private String mTag ;

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
		mIKSListe = new ArrayList<Aenderungsitem>();
		mLoadAnimation = (ProgressBar) getView().findViewById(R.id.loadingAnimation);
		mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		ListView lv = (ListView) getView().findViewById(R.id.list);
		lv.setFadingEdgeLength(0);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
				mCallback.itemClicked(mIKSListe.get(index).getId(), "iks", mIKSListe.get(index).getNewsLink());
			}
		});
		mAdapter = new ArrayAdapter_AendungerungsAdapter(context, mIKSListe);
		lv.setAdapter(mAdapter);
		mSqlite = new SQLiteHelper(context);		
		mAdapter.addAll(mSqlite.getAllIKSList());
		// neue IKSnews laden und anzeigen
		iksLoad();

	}

	public ArrayList<Aenderungsitem> jArrayToIKSlist(JSONArray jArray) {
		ArrayList<Aenderungsitem> liste = new ArrayList<Aenderungsitem>();
		try {
			if (jArray != null) {
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject objekt = jArray.getJSONObject(i);
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

	public void writeIKSlistInSqlite(ArrayList<Aenderungsitem> liste) {

		// Alle neuen Items Eintragen in SQLITE
		for (Aenderungsitem iksItem : liste) {
			mSqlite.addIKS(iksItem);
		}
	}

	public void iksLoad() {
		SQLiteHelper sqlite = new SQLiteHelper(context);
		int maxIKS = sqlite.getMaxAenderungID(SQLiteHelper.FACHBEREICHIKS);
		Log.i(mTag, "Max iks id " + maxIKS);
		mLoadAnimation.setVisibility(View.VISIBLE);
		new Volley();
		RequestQueue queue = Volley.newRequestQueue(context);
		JsonObjectRequest myReqs = new JsonObjectRequest(Method.GET, "http://sexyateam.de/home/datenabfrage.php?tabelle=iks&maxid=" + maxIKS, null,
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
						ArrayList<Aenderungsitem> liste = jArrayToIKSlist(jar);
						writeIKSlistInSqlite(liste);
						for (Aenderungsitem item : liste) {
							mAdapter.add(item);
						}
						mPrefs.edit().putBoolean(context.getString(R.string.preference_firstIKSSearch), false).apply();
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
