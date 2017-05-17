package org.wfp.offlinepayment.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.wfp.offlinepayment.exceptions.DatabaseInsertException;
import org.wfp.offlinepayment.exceptions.JSONNullableException;
import org.wfp.offlinepayment.exceptions.NetworkStatePermissionException;
import org.wfp.offlinepayment.exceptions.NotConnectedException;
import org.wfp.offlinepayment.exceptions.UrlConnectionException;
import org.wfp.offlinepayment.model.BaseModel;
import org.wfp.offlinepayment.model.LogModel;

import android.content.Context;

public class ErrorLogProvider extends BaseProvider {

	Context context;
	
	JSONArray array = new JSONArray();
	
	public ErrorLogProvider(Context context) throws UrlConnectionException, JSONException, JSONNullableException, NotConnectedException, NetworkStatePermissionException {
		super(context);
		
		this.context = context;
		
	}
	
	public boolean SendLog(String log) throws UrlConnectionException, JSONException, JSONNullableException, NotConnectedException, NetworkStatePermissionException, DatabaseInsertException, ClientProtocolException, IOException{
		
		try
		{
		PreferenceUtility.GetPreferences(context);
		String UserName = "";

		if(ProviderUtility.LoginProvider != null)
			if(ProviderUtility.LoginProvider.LoginModel !=  null)
			{
				UserName = ProviderUtility.LoginProvider.LoginModel.getUserName();
			}
		
		HttpPost postRequest = new HttpPost(BaseModel.GetSendLogUrl(context));
		
		postRequest.setHeader("Content-Type", "application/json");

		JSONStringer jsonData = new JSONStringer();
		
		jsonData.object();
		jsonData.key(LogModel.LogModelEnum.CompanyCode.Value).value(PreferenceUtility.CompanyCode);
		jsonData.key(LogModel.LogModelEnum.CompanyName.Value).value(PreferenceUtility.CompanyName);
		jsonData.key(LogModel.LogModelEnum.UserName.Value).value(UserName);
		jsonData.key(LogModel.LogModelEnum.Log.Value).value(log);
		jsonData.endObject();
			
		StringEntity loginEntity = new StringEntity(jsonData.toString());
		postRequest.setEntity(loginEntity);
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpResponse httpResponse = httpClient.execute(postRequest);
		HttpEntity responseEntity = httpResponse.getEntity();
		
		if(responseEntity != null)
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(responseEntity.getContent()));
			
			String temp = "";			
			StringBuilder responseStringBuilder = new StringBuilder();
			
			while((temp = br.readLine()) != null)
			{
				responseStringBuilder.append(temp);
			}
			
			String response = responseStringBuilder.toString();
			
			boolean success = setBooleanValueFromJSON(BaseModel.JsonMessageHeaderEnum.IsSuccess.Value, GetJSONObject(response));
			
			if(success)
				return true;
			else
				return false;
			
		}
		}catch( Exception ex)
		{
			return false;
		}
		
		return false;
		
	}
	
	@Override
	public URL GenerateUrl() {
		String url = BaseModel.GetSendLogUrl(context);
		return GetURLForString(url);

	}

	@Override
	public <T extends BaseModel> T DecodeContent(JSONObject object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void SetContent() throws JSONException, DatabaseInsertException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void FetchContent() throws UrlConnectionException, JSONException,
			JSONNullableException, NotConnectedException,
			NetworkStatePermissionException {
		// TODO Auto-generated method stub
		
	}
	
}
