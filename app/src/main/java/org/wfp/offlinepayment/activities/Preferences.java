package org.wfp.offlinepayment.activities;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import org.wfp.offlinepayment.R;
import org.wfp.offlinepayment.business.PreferenceUtility;

import static org.wfp.offlinepayment.utils.CommonUtilities.EXTRA_MESSAGE;
import static org.wfp.offlinepayment.utils.CommonUtilities.VEHICAL_CODE;

public class Preferences extends PreferenceActivity implements OnSharedPreferenceChangeListener, OnPreferenceClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);

		VEHICAL_CODE = getIntent().getStringExtra("VehicleCode");

		addPreferencesFromResource(R.xml.settings);

//		ActionBar actionBar = this.getActionBar();
//		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//		actionBar.setDisplayHomeAsUpEnabled(true);
//		actionBar.setDisplayShowTitleEnabled(false);

//		registerReceiver(mHandleGCMMessageReceiver, new IntentFilter(ACTION_DISPLAY_GCM_MESSAGE));
//		registerReceiver(mHandlePODMessageReceiver, new IntentFilter(ACTION_DISPLAY_POD_MESSAGE));
//
//		btnRegisterGCMServer = (Preference) getPreferenceManager().findPreference("btnRegisterGCMServer");
//			btnRegisterGCMServer.setOnPreferenceClickListener(this);
//		if (GCMRegistrar.isRegisteredOnServer(this))
//		{
//			btnRegisterGCMServer.setSummary("Already Registered with GCM Server");
//		}
//		btnRegisterPODServer = (Preference) getPreferenceManager().findPreference("btnRegisterPODServer");
//			btnRegisterPODServer.setOnPreferenceClickListener(this);

		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case android.R.id.home:
			Preferences.this.finish();
			break;
		}
		return true;

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key)
	{
		PreferenceUtility.GetPreferences(Preferences.this);

	}

	@Override
	public boolean onPreferenceClick(Preference preference)
	{
		return true;
	}

	@Override
	protected void onDestroy()
	{
//		unregisterReceiver(mHandleGCMMessageReceiver);
//		unregisterReceiver(mHandlePODMessageReceiver);

		super.onDestroy();
	}

}
