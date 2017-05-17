package org.wfp.offlinepayment.activities;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;
import org.wfp.offlinepayment.R;
import org.wfp.offlinepayment.business.LoginProvider;
import org.wfp.offlinepayment.business.PreferenceUtility;
import org.wfp.offlinepayment.business.ProviderUtility;
import org.wfp.offlinepayment.exceptions.EmptyStringException;
import org.wfp.offlinepayment.exceptions.JSONNullableException;
import org.wfp.offlinepayment.exceptions.LoginException;
import org.wfp.offlinepayment.exceptions.NetworkStatePermissionException;
import org.wfp.offlinepayment.exceptions.NotConnectedException;
import org.wfp.offlinepayment.exceptions.UrlConnectionException;
import org.wfp.offlinepayment.model.LoginModel;
import org.wfp.offlinepayment.utils.ErrorLogUtility;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class Login extends Activity implements OnClickListener, OnEditorActionListener {

	EditText Username;
	EditText Password;

	Button LoginButton;
	Button btnRegister;

	TextView tvCompanyName;

	ProgressDialog progressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		//BugSenseHandler.initAndStartSession(this, "ffe3e7ce");
		//BugSenseHandler.flush(this);
		Log.d("LOGIN_ACTIVITY", "ON_CREATE METHOD");

		ErrorLogUtility errorUtility = ErrorLogUtility.getInstance();
		errorUtility.Init(getApplicationContext());
		errorUtility.CheckCrashErrorAndSendLog(getApplicationContext());

		//		int a = 0;
		//		a = 1/0;

		ActionBar actionBar = this.getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(false);

		Username = (EditText) findViewById(R.id.usernameEditText);
		Password = (EditText) findViewById(R.id.passwordEditText);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Login.this);
		Username.setText(preferences.getString("UserName", "agent1"));
		Password.setText(preferences.getString("Password", "P@kistan1"));

		Username.setOnEditorActionListener(this);
		Password.setOnEditorActionListener(this);

		LoginButton = (Button) findViewById(R.id.loginButton);
		LoginButton.setOnClickListener(this);

		PreferenceUtility.GetPreferences(this);
		tvCompanyName = (TextView) findViewById(R.id.tvCompanyName);
		tvCompanyName.setText(PreferenceUtility.CompanyName);

		try
		{
			ProviderUtility.LoginProvider = new LoginProvider(Login.this);
			if (ProviderUtility.LoginProvider.CheckLoginStatus())
			{
				Intent i = new Intent(this, MainActivity.class);
				startActivity(i);
				this.finish();
			}

		} catch (Exception e)
		{
		}

	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.loginButton:
			LoginModel loginModel = new LoginModel();
			loginModel.setUserName(Username.getText().toString());
			loginModel.setPassword(Password.getText().toString());
			new LoginAsync().execute(loginModel);
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{

		getMenuInflater().inflate(R.menu.mainmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		switch (item.getItemId())
		{
		case R.id.settings:

			Intent i = new Intent(getApplicationContext(), Preferences.class);
			startActivity(i);

			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	class LoginAsync extends AsyncTask<LoginModel, Void, Object>
	{
		protected void onPreExecute()
		{

			progressDialog = new ProgressDialog(Login.this);
			progressDialog.setTitle("Logging");
			progressDialog.setMessage("Logging into application.");
			progressDialog.show();

			super.onPreExecute();
		};

		String message = "";

		@Override
		protected Object doInBackground(LoginModel... params)
		{
			try
			{

				if (ProviderUtility.LoginProvider == null)
					ProviderUtility.LoginProvider = new LoginProvider(Login.this);

				LoginModel loginModel = params[0];
				message = ProviderUtility.LoginProvider.Login(loginModel);

				return message;

			} catch (UrlConnectionException e)
			{
				return e;
			} catch (JSONException e)
			{
				return e;
			} catch (JSONNullableException e)
			{
				return e;
			} catch (NotConnectedException e)
			{
				return e;
			} catch (NetworkStatePermissionException e)
			{
				return e;
			} catch (ClientProtocolException e)
			{
				return e;
			} catch (IOException e)
			{
				return e;
			} catch (LoginException e)
			{
				return e;
			} catch (EmptyStringException e)
			{
				return e;
			}

		}

		@Override
		protected void onPostExecute(Object result)
		{

			progressDialog.dismiss();

			if (result instanceof HttpHostConnectException)
				Toast.makeText(Login.this, "No connection", Toast.LENGTH_SHORT).show();
			else if (result instanceof Exception && !((Exception) result).getMessage().equals(""))
				Toast.makeText(Login.this, ((Exception) result).getMessage(), Toast.LENGTH_SHORT).show();
			else if (!message.equals(""))
			{
				ShowLoginMessage(message);
			}
			else
			{
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Login.this);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("UserName", Username.getText().toString());
				editor.putString("Password", Password.getText().toString());
				editor.commit();

				Intent i = new Intent(Login.this, MainActivity.class);
				startActivity(i);
				Login.this.finish();
			}

		}

	}

	private void ShowLoginMessage(String message)
	{
		AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
		builder.setTitle(message);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();

			}
		});
		dialog = builder.create();
		dialog.show();
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
	{
		//	new LoginAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
		return true;
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

}
