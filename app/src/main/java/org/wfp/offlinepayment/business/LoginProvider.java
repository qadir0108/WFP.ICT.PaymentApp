package org.wfp.offlinepayment.business;

import android.content.Context;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.wfp.offlinepayment.exceptions.EmptyAuthTokenException;
import org.wfp.offlinepayment.exceptions.EmptyStringException;
import org.wfp.offlinepayment.exceptions.JSONNullableException;
import org.wfp.offlinepayment.exceptions.LoginException;
import org.wfp.offlinepayment.exceptions.NetworkStatePermissionException;
import org.wfp.offlinepayment.exceptions.NotConnectedException;
import org.wfp.offlinepayment.exceptions.UrlConnectionException;
import org.wfp.offlinepayment.model.BaseModel;
import org.wfp.offlinepayment.model.LoginModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class LoginProvider extends BaseProvider {

	public LoginModel LoginModel = new LoginModel();

	
	public LoginProvider(Context context) throws UrlConnectionException,
			JSONException, JSONNullableException, NotConnectedException,
			NetworkStatePermissionException {
		super(context);
	}
	
	public boolean CheckLoginStatus()
	{

		boolean savedLoginData = false;
		try {
			LoginModel savedLoginModel = Database.GetLoginData(context);
			LoginModel.setAuthToken(savedLoginModel.getAuthToken());			
			LoginModel.setUserId(savedLoginModel.getUserId());
			LoginModel.setUserName(savedLoginModel.getUserName());
			savedLoginData = true;
		} catch (Exception e) {	
			savedLoginData = false;
		} 
		
		return savedLoginData;
	}
	
	public String Login(LoginModel loginModel) throws 
	JSONException, 
	ClientProtocolException, 
	IOException, 
	EmptyStringException, 
	JSONNullableException, 
	LoginException
	{
		
		if(loginModel.getUserName().equals(EMPTY_STRING))
			throw new EmptyStringException();
		if(loginModel.getPassword().equals(EMPTY_STRING))
			throw new EmptyStringException();

		LoginModel = loginModel;

		HttpPost postRequest = new HttpPost(BaseModel.GetLoginServiceUrl(context));		
		
		postRequest.setHeader("Content-Type", "application/json");

		JSONStringer loginJson = new JSONStringer()
			.object()
				.key(org.wfp.offlinepayment.model.LoginModel.LoginModelEnum.Username.Value).value(loginModel.getUserName())
				.key(org.wfp.offlinepayment.model.LoginModel.LoginModelEnum.Password.Value).value(loginModel.getPassword())
			.endObject();
			
		StringEntity loginEntity = new StringEntity(loginJson.toString());
		postRequest.setEntity(loginEntity);
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpResponse httpResponse = httpClient.execute(postRequest);
		
		if(httpResponse.getStatusLine().getStatusCode() != 200)
			throw new LoginException();
		
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
			
			if(response.equals(EMPTY_STRING))
				throw new EmptyStringException();
			else
			{
				JSONObject object = GetJSONObject(response);

				boolean success = setBooleanValueFromJSON(BaseModel.JsonMessageHeaderEnum.IsSuccess.Value, object);
				
				if(success)	{		
					LoginModel.setAuthToken(setStringValueFromJSON(org.wfp.offlinepayment.model.LoginModel.LoginModelEnum.AuthToken.Value, object));
					LoginModel.setTime(ProviderUtility.GetMilis24Hours());
					Database.WriteLoginData(context, LoginModel);
				}
				else
					return setStringValueFromJSON(BaseModel.JsonMessageHeaderEnum.ErrorMessage.Value, object);
			}
		}
		else
			throw new NullPointerException();
	
//		LoginModel.setAuthToken("123");
//		LoginModel.setTime(ProviderUtility.GetMilis24Hours());	
//		Database.WriteLoginData(context, LoginModel);
		return EMPTY_STRING;
	}

	@Override
	public URL GenerateUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void SetContent() throws JSONException {
		// TODO Auto-generated method stub

	}

	@Override
	public void FetchContent() throws UrlConnectionException, JSONException,
			JSONNullableException, NotConnectedException,
			NetworkStatePermissionException {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends BaseModel> T DecodeContent(JSONObject object) {
		// TODO Auto-generated method stub
		return null;
	}

	public void LogOff() {
		Database.LogOff(context);
		
	}

	public void ActivateLoginData() throws EmptyAuthTokenException {
		Database.ActivateLoginData(context);
		
	}

}
