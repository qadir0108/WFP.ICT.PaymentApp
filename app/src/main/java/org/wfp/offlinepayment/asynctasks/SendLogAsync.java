package org.wfp.offlinepayment.asynctasks;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.wfp.offlinepayment.business.ErrorLogProvider;
import org.wfp.offlinepayment.exceptions.DatabaseInsertException;
import org.wfp.offlinepayment.exceptions.JSONNullableException;
import org.wfp.offlinepayment.exceptions.NetworkStatePermissionException;
import org.wfp.offlinepayment.exceptions.NotConnectedException;
import org.wfp.offlinepayment.exceptions.UrlConnectionException;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class SendLogAsync extends AsyncTask<String, Void, Object>
{
	private Context context;

	public SendLogAsync(Context c)
	{
		context = c;
	}

	@Override
	protected Object doInBackground(String... params)
	{
		String log = (String)params[0];
		try
		{
			ErrorLogProvider dlp = new ErrorLogProvider(context);
			dlp.SendLog(log);
		} catch (UrlConnectionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONNullableException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NetworkStatePermissionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatabaseInsertException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Object result)
	{
		if (result instanceof Exception)
		{
			Toast.makeText(context, result.getClass().getName(), Toast.LENGTH_LONG).show();
		}

		super.onPostExecute(result);
	}

}