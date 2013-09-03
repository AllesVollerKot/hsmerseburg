package com.example.hsmerseburg;

import iks.Fragment_IKSAnzeige;
import iks.Fragment_IKSListe;
import java.util.concurrent.TimeUnit;

import smk.Fragment_SMKAnzeige;
import smk.Fragment_SMKListe;
import wiwi.Fragment_WiWiAnzeige;
import wiwi.Fragment_WiWiListe;
import news.Fragment_NewsAnzeige;
import news.Fragment_NewsListe;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gcm.GCMRegistrar;

import datenUndHelfer.MultiInterface;
import datenUndHelfer.TextView_TextFitTextView;
import event.Fragment_EventAnzeige;
import event.Fragment_EventListe;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.AlarmManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ActionProvider;
import android.view.SubMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

public class Activity_MainActivity extends SherlockFragmentActivity implements MultiInterface {
	final Context mContext = this;
	private String mTag;
	private SharedPreferences mPrefs;
	private OnSharedPreferenceChangeListener mPrefListener;
	private FrameLayout mFrameLayout;
	private ListView mDrawerList;
	private DrawerLayout mDrawerLayout;
	private String[] mdrawer_titles;
	private ActionBarDrawerToggle mDrawerToggle;
	private MenuItem mReloadIcon;
	private String mActionbarColor = "";
	private String mActionbarTitle = "";
	private boolean mfirstStart = true;

	public interface OnListItemClickListener {
		public void itemClicked(int position, String fragment, String url);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTag = getResources().getString(R.string.log_tag);
		// Hauptlayout laden
		mFrameLayout = new FrameLayout(mContext);
		setContentView(mFrameLayout);
		getLayoutInflater().inflate(R.layout.activity_mainactivity_layout, mFrameLayout);

		// actionbar einrichten
		ActionBar mActionBar = getSupportActionBar();
		mActionBar.setDisplayUseLogoEnabled(false);
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setHomeButtonEnabled(true);
	
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		mPrefs.registerOnSharedPreferenceChangeListener(mPrefListener);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mdrawer_titles = getResources().getStringArray(com.example.hsmerseburg.R.array.drawer_items);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mdrawer_titles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

			public void onDrawerClosed(View view) {
				mReloadIcon.setVisible(true);
			}

			public void onDrawerOpened(View drawerView) {
				mReloadIcon.setVisible(false);
				mfirstStart = mPrefs.getBoolean(getResources().getString(R.string.preference_firstStart), true);
				if (mfirstStart) {
					mFrameLayout.removeViewAt(1);
					SharedPreferences.Editor editor = mPrefs.edit();
					editor.putBoolean(getResources().getString(R.string.preference_firstStart), false);
					editor.commit();
				}

			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerToggle.setDrawerIndicatorEnabled(true);

		// nur beim öffnen der app
		if (savedInstanceState == null) {
			selectDrawerItem(0);
			mDrawerList.setItemChecked(0, true);
			startService();
			firstStart();

		}
		// bei jedem mal oncreate
		if (savedInstanceState != null) {
			if (savedInstanceState.getString("color") != null) {
				changeActionbarColor(savedInstanceState.getString("color"));
			}
			if (savedInstanceState.getString("title") != null) {
				changeActionbarTitle(savedInstanceState.getString("title"));
			}
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void firstStart() {
		mfirstStart = mPrefs.getBoolean(getResources().getString(R.string.preference_firstStart), true);
		if (mfirstStart) {
			getLayoutInflater().inflate(R.layout.activity_mainactivity_overlay, mFrameLayout);
			TextView_TextFitTextView overlaytTextView = (TextView_TextFitTextView) findViewById(R.id.text1);
			overlaytTextView.setFitTextToBox(true);
			Typeface font = Typeface.createFromAsset(getAssets(), "fonts/rayando.ttf");
			overlaytTextView.setTypeface(font);
			Button overlayButton = (Button) findViewById(R.id.overlay_button);
			overlayButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mFrameLayout.removeViewAt(1);
					SharedPreferences.Editor editor = mPrefs.edit();
					editor.putBoolean(getResources().getString(R.string.preference_firstStart), false);
					editor.commit();
				}
			});
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("color", mActionbarColor);
		outState.putString("title", mActionbarTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menumain, menu);
		mReloadIcon = menu.findItem(R.id.optionen_load);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(getMenuItem(item))) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.optionen_optionen:
			startActivity(new Intent(mContext, PreferenceActivity_Preferences.class));
			break;
		case R.id.optionen_about:			
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private android.view.MenuItem getMenuItem(final MenuItem item) {
		return new android.view.MenuItem() {
			@Override
			public int getItemId() {
				return item.getItemId();
			}

			public boolean isEnabled() {
				return true;
			}

			@Override
			public boolean collapseActionView() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean expandActionView() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public ActionProvider getActionProvider() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public View getActionView() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public char getAlphabeticShortcut() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getGroupId() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Drawable getIcon() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Intent getIntent() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ContextMenuInfo getMenuInfo() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public char getNumericShortcut() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getOrder() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public SubMenu getSubMenu() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CharSequence getTitle() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CharSequence getTitleCondensed() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean hasSubMenu() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isActionViewExpanded() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isCheckable() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isChecked() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isVisible() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public android.view.MenuItem setActionProvider(ActionProvider actionProvider) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setActionView(View view) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setActionView(int resId) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setAlphabeticShortcut(char alphaChar) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setCheckable(boolean checkable) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setChecked(boolean checked) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setEnabled(boolean enabled) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setIcon(Drawable icon) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setIcon(int iconRes) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setIntent(Intent intent) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setNumericShortcut(char numericChar) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setShortcut(char numericChar, char alphaChar) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setShowAsAction(int actionEnum) {
				// TODO Auto-generated method stub

			}

			@Override
			public android.view.MenuItem setShowAsActionFlags(int actionEnum) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setTitle(CharSequence title) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setTitle(int title) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setTitleCondensed(CharSequence title) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setVisible(boolean visible) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	public void startService() {
		if (pushTest() == false) {
			String UpdateInterval = mPrefs.getString(getResources().getString(R.string.preference_updateinterval), "Nie");
			Intent serviceIntent = new Intent(this, Service_GetUpdatesService.class);
			PendingIntent servicePendingIntent = PendingIntent.getService(getApplicationContext(), 0, serviceIntent, 0);
			AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			if (UpdateInterval.contains("Nie")) {
				am.cancel(servicePendingIntent);
				Toast.makeText(getBaseContext(), "Service gestoppt", Toast.LENGTH_LONG).show();
			} else {
				long interval = TimeUnit.MINUTES.toMillis(Long.parseLong(UpdateInterval));
				long firstStart = System.currentTimeMillis();
				am.setInexactRepeating(AlarmManager.RTC, firstStart, interval, servicePendingIntent);
			}
		}
	}

	public void stopService() {
		Intent serviceIntent = new Intent(getApplicationContext(), Service_GetUpdatesService.class);
		PendingIntent servicePendingIntent = PendingIntent.getService(Activity_MainActivity.this, 0, serviceIntent, 0);
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.cancel(servicePendingIntent);
	}

	public boolean pushTest() {
		boolean pushOk = false;
		try {
			// Checken OB DAS GERÄT GCM UNTERSTÜTZT
			GCMRegistrar.checkDevice(this);
			pushOk = true;
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

	public void selectDrawerItem(int i) {
		switch (i) {
		case 0:
			changeActionbarColor(getResources().getString(R.color.news));
			changeActionbarTitle("News");
			FragmentTransaction transactionnews = getSupportFragmentManager().beginTransaction();
			Fragment_NewsListe liste = new Fragment_NewsListe();
			transactionnews.replace(R.id.main, liste, getResources().getString(R.string.fragment_newslist));
			transactionnews.commit();
			break;
		case 1:
			changeActionbarColor(getResources().getString(R.color.news));
			changeActionbarTitle("Events");
			FragmentTransaction transactionevents = getSupportFragmentManager().beginTransaction();
			Fragment_EventListe eventListe = new Fragment_EventListe();
			transactionevents.replace(R.id.main, eventListe, "eventtag");
			transactionevents.commit();
			break;
		case 2:
			changeActionbarColor(getResources().getString(R.color.wiwi));
			changeActionbarTitle("WiWi");
			FragmentTransaction transactionwiwi = getSupportFragmentManager().beginTransaction();
			Fragment_WiWiListe wiwiListe = new Fragment_WiWiListe();
			transactionwiwi.replace(R.id.main, wiwiListe, "wiwitag");
			transactionwiwi.commit();
			break;
		case 3:
			changeActionbarColor(getResources().getString(R.color.smk));
			changeActionbarTitle("SMK");
			FragmentTransaction transactionsmk = getSupportFragmentManager().beginTransaction();
			Fragment_SMKListe smkListe = new Fragment_SMKListe();
			transactionsmk.replace(R.id.main, smkListe, "smktag");
			transactionsmk.commit();
			break;
		case 4:
			changeActionbarColor(getResources().getString(R.color.iks));
			changeActionbarTitle("IKS");
			FragmentTransaction transactioniks = getSupportFragmentManager().beginTransaction();
			Fragment_IKSListe iksListe = new Fragment_IKSListe();
			transactioniks.replace(R.id.main, iksListe, "ikstag");
			transactioniks.commit();
			break;
		}
		mDrawerList.setItemChecked(i, true);
		getSupportActionBar().setTitle(mdrawer_titles[i]);

		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void itemClicked(int position, String fragment, String url) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		Bundle extras = new Bundle();
		if (fragment.contains("news")) {
			Fragment_NewsAnzeige anzeige = new Fragment_NewsAnzeige();
			extras.putInt("id", position);
			extras.putString("url", url);
			anzeige.setArguments(extras);
			transaction.replace(R.id.main, anzeige, getResources().getString(R.string.fragment_newsanzeige));
			transaction.addToBackStack(null);
			transaction.commit();
		}
		if (fragment.contains("events")) {
			Fragment_EventAnzeige anzeige = new Fragment_EventAnzeige();
			extras.putInt("id", position);
			extras.putString("url", url);
			anzeige.setArguments(extras);
			transaction.replace(R.id.main, anzeige, getResources().getString(R.string.fragment_eventanzeige));
			transaction.addToBackStack(null);
			transaction.commit();
		}
		if (fragment.contains("wiwi")) {
			Fragment_WiWiAnzeige anzeige = new Fragment_WiWiAnzeige();
			extras.putInt("id", position);
			extras.putString("url", url);
			anzeige.setArguments(extras);
			transaction.replace(R.id.main, anzeige, getResources().getString(R.string.fragment_wiwianzeige));
			transaction.addToBackStack(null);
			transaction.commit();
		}
		if (fragment.contains("smk")) {
			Fragment_SMKAnzeige anzeige = new Fragment_SMKAnzeige();
			extras.putInt("id", position);
			extras.putString("url", url);
			anzeige.setArguments(extras);
			transaction.replace(R.id.main, anzeige, getResources().getString(R.string.fragment_smkanzeige));
			transaction.addToBackStack(null);
			transaction.commit();
		}
		if (fragment.contains("iks")) {
			Fragment_IKSAnzeige anzeige = new Fragment_IKSAnzeige();
			extras.putInt("id", position);
			extras.putString("url", url);
			anzeige.setArguments(extras);
			transaction.replace(R.id.main, anzeige, getResources().getString(R.string.fragment_iksanzeige));
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	public void changeActionbarColor(String farbe) {
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(farbe)));
		mActionbarColor = farbe;
	}

	public void changeActionbarTitle(String title) {
		getSupportActionBar().setTitle(title);
		mActionbarTitle = title;
	}

	class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position, long id) {
			selectDrawerItem(position);
		}
	}

}
