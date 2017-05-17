package org.wfp.offlinepayment.business;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
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
import org.wfp.offlinepayment.model.BeneficiaryModel;

import android.content.Context;
import android.net.ConnectivityManager;

public abstract class BaseProvider {

	public abstract URL GenerateUrl();

	public abstract void SetContent() throws JSONException,
			DatabaseInsertException, DatabaseInsertException;

	public abstract void FetchContent() throws UrlConnectionException,
			JSONException, JSONNullableException, NotConnectedException,
			NetworkStatePermissionException, UrlConnectionException, JSONNullableException, NotConnectedException, NetworkStatePermissionException;

	public abstract <T extends BaseModel> T DecodeContent(JSONObject object);

	public String getErrorMessage() {
		return ErrorMessage;
	}

	public void setErrorMessage(String msg) {
		this.ErrorMessage = msg;
	}	

	private String ErrorMessage = EMPTY_STRING;
	protected static final String EMPTY_STRING = "";
	protected static Context context;

	protected CookieStore cookieStore;

	public BaseProvider(Context context) {
		this.context = context;

	}

	public void InitializeProvider() throws UrlConnectionException,
			JSONException, JSONNullableException, NotConnectedException,
			NetworkStatePermissionException, DatabaseInsertException {
		FetchContent();
		SetContent();
	};

	protected synchronized BaseModel.JsonMessageHeader GetDataJsonMessageHeaderData(
			URL jsonUrl, String arrayName) throws UrlConnectionException,
			JSONNullableException, JSONException, NotConnectedException,
			NetworkStatePermissionException {
		BaseModel.JsonMessageHeader model = BaseModel
				.getJsonMessageHeaderInstance();

		JSONObject object = GetJSONObject(jsonUrl);

		model.setSuccess(setBooleanValueFromJSON(BaseModel.JsonMessageHeaderEnum.IsSuccess.Value, object));
		model.setAuthTokenValid(setBooleanValueFromJSON(BaseModel.JsonMessageHeaderEnum.IsTokenValid.Value,object));
		
		if (!model.isSuccess())
			model.setErrorMessage(setStringValueFromJSON(BaseModel.JsonMessageHeaderEnum.ErrorMessage.Value,object));
		else if(!model.isAuthTokenValid()){
			model.setErrorMessage(ProviderUtility.AUTH_TOKEN_INVALID);
		}
		else {
			model.setErrorMessage(EMPTY_STRING);
			
			try {
				model.setJsonArray(object.getJSONArray(arrayName));
			} catch (JSONException ex) {
				
			}
			
		}

		return model;
	}

	protected String setStringValueFromJSON(String property, JSONObject object) {
		String s;
		try {
			s = object.getString(property);
		} catch (JSONException ex) {
			return EMPTY_STRING;
		}
		return s;
	}

	protected int setIntValueFromJSON(String property, JSONObject object) {
		int i;
		try {
			i = object.getInt(property);
		} catch (JSONException ex) {
			i = 0;
		}
		return i;
	}

	protected double setDoubleValueFromJSON(String property, JSONObject object) {
		double i;
		try {
			i = object.getDouble(property);
		} catch (JSONException ex) {
			i = 0;
		}
		return i;
	}

	protected boolean setBooleanValueFromJSON(String property, JSONObject object) {
		boolean i;
		try {
			i = object.getBoolean(property);
		} catch (JSONException ex) {
			i = true; //only while authtokenvalid doesn not exist in all Message json objektima. Treba biti false;
		}
		return i;
	}

	protected String[] setStringArrayValuesFromJson(String property,
			JSONObject object) {
		String array[] = new String[5];

		try {
			JSONArray jsonArray = object.getJSONArray(property);

			int length;
			if (jsonArray.length() < 5)
				length = jsonArray.length();
			else
				length = 5;

			for (int i = 0; i < length; i++) {
				array[i] = jsonArray.getString(i);
			}
		} catch (JSONException ex) {
		}

		return array;
	}
	
	protected String StringToDate(String date, String dateFormat)
	{
		try {
			Date temp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(date);
			String returnV = new SimpleDateFormat(dateFormat).format(temp); 
			return returnV;
		} catch (ParseException e) {
			return EMPTY_STRING;
		}
		
		
	}

	protected JSONArray GetJSONArray(URL jsonUrl)
			throws UrlConnectionException, JSONException,
			JSONNullableException, NotConnectedException,
			NetworkStatePermissionException {
		String json = DownloadJSON(jsonUrl);

		JSONArray array = null;
		array = new JSONArray(json);

		if (array.equals(null))
			throw new JSONNullableException();

		return array;
	}

	protected JSONObject GetJSONObject(String json)
			throws JSONNullableException, JSONException {
		if (json.equals(EMPTY_STRING))
			throw new JSONNullableException();

		// if(json.startsWith("["))
		// json = json.replace("[", "");
		// if(json.endsWith("]"))
		// json = json.replace("]", "");

		JSONObject object = new JSONObject(json);

		if (object.equals(null))
			throw new JSONNullableException();

		return object;
	}

	protected JSONObject GetJSONObject(URL jsonUrl)
			throws UrlConnectionException, JSONNullableException,
			JSONException, NotConnectedException,
			NetworkStatePermissionException {
		String json = DownloadJSON(jsonUrl);
		return GetJSONObject(json);
	}

	private synchronized static String DownloadJSON(URL url)
			throws UrlConnectionException, NotConnectedException,
			NetworkStatePermissionException {

		IsConnected();

		String value = EMPTY_STRING;
		try {
			URLConnection conn = url.openConnection();
			value = InputStreamToString(conn.getInputStream());

		} catch (Exception e) {
			throw new UrlConnectionException();
		}

		return value;
	}

	protected static String InputStreamToString(InputStream is)
			throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		byte[] b = new byte[4096];
		int length;

		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		}

		String value = os.toString();

		os.close();
		is.close();

		return value;
	}

	protected URL GetURLForString(String url) {
		URL u = null;
		try {
			u = new URL(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return u;
	}

	public static void IsConnected() throws NetworkStatePermissionException,
			NotConnectedException {
		try {
			ConnectivityManager conman = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			if (conman == null || conman.getActiveNetworkInfo() == null
					|| !conman.getActiveNetworkInfo().isConnectedOrConnecting())
				throw new NotConnectedException();
		} catch (SecurityException ex) {
			throw new NetworkStatePermissionException();
		}
	}

	public static boolean IsWiFiConnection(Context context) {
		try {
			ConnectivityManager conman = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			if (conman == null || conman.getActiveNetworkInfo() == null)
				throw new NotConnectedException();
			else
				return conman.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;

		} catch (Exception ex) {
			return false;
		}
	}
//
//	protected boolean UploadImage(String url, String imagePath)
//			throws EmptyStringException {
//
//		DefaultHttpClient httpClient = new DefaultHttpClient();
//		httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(
//				httpClient.getParams(), httpClient.getConnectionManager()
//						.getSchemeRegistry()), httpClient.getParams());
//
//		HttpContext localContext = new BasicHttpContext();
//
//		HttpPost signaturePost = new HttpPost(url);
//
//		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//		if (imagePath.contains("file://"))
//			imagePath = imagePath.replace("file://", "");
//
//		nameValuePairs.add(new BasicNameValuePair("attach1", imagePath));
//
//		try {
//			MultipartEntity entity = new MultipartEntity();
//
//			for (int index = 0; index < nameValuePairs.size(); index++) {
//				if (nameValuePairs.get(index).getName()
//						.equalsIgnoreCase("attach1")) {
//					// If the key equals to "image", we use FileBody to transfer
//					// the data
//					entity.addPart(nameValuePairs.get(index).getName(),
//							new FileBody(new File(nameValuePairs.get(index)
//									.getValue())));
//				}
//			}
//
//			signaturePost.setEntity(entity);
//
//			HttpResponse signatureResponse = httpClient.execute(signaturePost,
//					localContext);
//
//			HttpEntity signatureResponseEntity = signatureResponse.getEntity();
//
//			if (signatureResponseEntity != null) {
//				BufferedReader br = new BufferedReader(new InputStreamReader(
//						signatureResponseEntity.getContent()));
//
//				String temp = "";
//				StringBuilder responseStringBuilder = new StringBuilder();
//
//				while ((temp = br.readLine()) != null) {
//					responseStringBuilder.append(temp);
//				}
//
//				String response = responseStringBuilder.toString();
//
//				if (response.equals(EMPTY_STRING))
//					throw new EmptyStringException();
//				// else
//				// {
//				// JSONObject object = GetJSONObject(response);
//				// String messageJson =
//				// setStringValueFromJSON(MessageEnum.Message.Value, object);
//				//
//				// boolean success =
//				// setBooleanValueFromJSON(JsonMessageHeaderEnum.IsSuccess.Value,
//				// GetJSONObject(messageJson));
//				//
//				// if(!success)
//				// return
//				// setStringValueFromJSON(JsonMessageHeaderEnum.ErrorMessage.Value,
//				// GetJSONObject(messageJson));
//				// }
//			}
//
//		} catch (IOException e) {
//			return false;
//		}
//
//		return true;
//	}


}
