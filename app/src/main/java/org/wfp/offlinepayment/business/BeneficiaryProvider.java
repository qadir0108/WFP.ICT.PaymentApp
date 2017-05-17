package org.wfp.offlinepayment.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
import org.wfp.offlinepayment.exceptions.DatabaseUpdateException;
import org.wfp.offlinepayment.exceptions.JSONNullableException;
import org.wfp.offlinepayment.exceptions.NetworkStatePermissionException;
import org.wfp.offlinepayment.exceptions.NotConnectedException;
import org.wfp.offlinepayment.exceptions.UrlConnectionException;
import org.wfp.offlinepayment.model.BaseModel;
import org.wfp.offlinepayment.model.BeneficiaryModel;

import android.content.Context;
import android.support.annotation.NonNull;

public class BeneficiaryProvider extends BaseProvider {

	Context context;
	String VehicleCode;

	public ArrayList<BeneficiaryModel> beneficiaryModels = new ArrayList<BeneficiaryModel>();
	public String RunSheetID = EMPTY_STRING;

	JSONArray array = new JSONArray();

	public int numberOfImportedConsigments = 0;
	public int numberOfImportedItems = 0;

	public BeneficiaryProvider(Context context) throws UrlConnectionException, JSONException, JSONNullableException, NotConnectedException, NetworkStatePermissionException {
		super(context);

		this.context = context;
		//this.VehicleCode = ProviderUtility.LoginProvider.LoginModel.getVehicleCode();
		//Database = new Database(context);

	}

	public void loadBeneficiaryData() throws UrlConnectionException, JSONException, JSONNullableException, NotConnectedException, NetworkStatePermissionException, DatabaseInsertException
	{
		InitializeProvider();
	}

	@Override
	public URL GenerateUrl()
	{
		String url = BaseModel.GetPaymentsUrl(context);
		url = url.replace("authtokenvalue", ProviderUtility.LoginProvider.LoginModel.getAuthToken());
//				.replace("vehiclecode", ProviderUtility.LoginProvider.LoginModel.getVehicleCode());

		return GetURLForString(url);

	}

	public void reloadeneficiaryData() throws UrlConnectionException, JSONException, JSONNullableException, NotConnectedException, NetworkStatePermissionException, DatabaseInsertException
	{
		beneficiaryModels.clear();
		InitializeProvider();
	}

	@Override
	public void SetContent() throws JSONException, DatabaseInsertException
	{
		ArrayList<BeneficiaryModel> beneficiaryModels = new ArrayList<BeneficiaryModel>();

		for (int i = 0; i < array.length(); i++)
		{
			JSONObject consignmentJsonObject = array.getJSONObject(i);
			BeneficiaryModel model = DecodeContent(consignmentJsonObject);
			beneficiaryModels.add(model);
		}

		if(beneficiaryModels.size() > 0) {
			int importresult = Database.writeBeneficiaries(context, beneficiaryModels);
			if (importresult == -1)
				throw new DatabaseInsertException();
			else
				numberOfImportedConsigments = importresult;
		}

	}

	public ArrayList<BeneficiaryModel> getPayments()
	{
		return Database.getAll(context);
	}

	public ArrayList<BeneficiaryModel> getNotSynced()
	{
		return Database.getNotSynced(context);
	}

	public BeneficiaryModel getPaymentById(String PaymentId)
	{
		return Database.getByPaymentId(context, PaymentId);
	}

	public void savePayment(String paymentId, int status) throws DatabaseUpdateException
	{
		if (Database.savePayment(context, paymentId, status) != 1)
			throw new DatabaseUpdateException();
	}

	@Override
	public void FetchContent() throws UrlConnectionException, JSONException,
			JSONNullableException, NotConnectedException,
			NetworkStatePermissionException
	{

		BaseModel.JsonMessageHeader object = GetDataJsonMessageHeaderData(GenerateUrl(), "Result");

		array = object.getJsonArray();
		setErrorMessage(object.getErrorMessage());

	}

	@SuppressWarnings("unchecked")
	@Override
	public BeneficiaryModel DecodeContent(JSONObject object)
	{
		BeneficiaryModel model = new BeneficiaryModel();

		model.setId(setStringValueFromJSON(Database.COLUMN_ID, object));
		model.setPaymentId(setStringValueFromJSON(Database.COLUMN_PAYMENT_ID, object));
		model.setPaymentCycle(setStringValueFromJSON(Database.COLUMN_PAYMENT_CYCLE, object));
		model.setDistrict(setStringValueFromJSON(Database.COLUMN_DISTRICT, object));
		model.setTehsil(setStringValueFromJSON(Database.COLUMN_TEHSIL, object));
		model.setUc(setStringValueFromJSON(Database.COLUMN_UC, object));
		model.setVillage(setStringValueFromJSON(Database.COLUMN_VILLAGE, object));
		model.setAddress(setStringValueFromJSON(Database.COLUMN_ADDRESS, object));
		model.setSchool(setStringValueFromJSON(Database.COLUMN_SCHOOL, object));
		model.setBeneficiaryCNIC(setStringValueFromJSON(Database.COLUMN_BENEFICIARY_CNIC, object));
		model.setBeneficiaryName(setStringValueFromJSON(Database.COLUMN_BENEFICIARY_NAME, object));
		model.setFatherName(setStringValueFromJSON(Database.COLUMN_FATHER_NAME, object));
		model.setAmount(setIntValueFromJSON(Database.COLUMN_AMONUT, object));
		model.setStatus(setIntValueFromJSON(Database.COLUMN_STATUS, object));
		model.setDateDownloaded(new java.util.Date());

		return model;
	}

	public boolean uploadPaymentsData()
			throws JSONException, ClientProtocolException, IOException,
			JSONNullableException, NotConnectedException, NetworkStatePermissionException, DatabaseUpdateException {

		IsConnected();

		List<BeneficiaryModel> data = ProviderUtility.BeneficiaryProvider.getPayments();
		List<String> notSynced = new ArrayList<String>();
		for (BeneficiaryModel model : data) {
			if(model.isPaid() && !model.isSynced())
			notSynced.add(model.getPaymentId());
		}
		if(notSynced.size() == 0) throw new IOException("All synced");

		JSONArray jsArray = new JSONArray(notSynced);

		String url = BaseModel.GetUpdatePaymentsUrl(context);
		url = url.replace("authtokenvalue", ProviderUtility.LoginProvider.LoginModel.getAuthToken());
		HttpPost postRequest = new HttpPost(url);
		postRequest.setHeader("Content-Type", "application/json");
		JSONStringer completedJson = new JSONStringer();
		String authToken = ProviderUtility.LoginProvider.LoginModel.getAuthToken();
		completedJson.object();
		//completedJson.key(BaseModel.JsonMessageEnum.AuthToken.Value).value(authToken);
		completedJson.key(BaseModel.JsonMessageEnum.Data.Value).value(jsArray);
		completedJson.endObject();

		StringEntity loginEntity = new StringEntity(completedJson.toString());
		postRequest.setEntity(loginEntity);

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpResponse httpResponse = httpClient.execute(postRequest);
		HttpEntity responseEntity = httpResponse.getEntity();

		if (responseEntity != null) {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					responseEntity.getContent()));

			String temp = "";
			StringBuilder responseStringBuilder = new StringBuilder();

			while ((temp = br.readLine()) != null) {
				responseStringBuilder.append(temp);
			}

			String response = responseStringBuilder.toString();

			boolean success = setBooleanValueFromJSON(BaseModel.JsonMessageHeaderEnum.IsSuccess.Value,GetJSONObject(response));

			if (!success) {
				setErrorMessage(setStringValueFromJSON(BaseModel.JsonMessageHeaderEnum.ErrorMessage.Value,GetJSONObject(response)));
				return false;
			} else
			{
				for (String paymentId: notSynced) {
					Database.setPaymentSynced(context, paymentId);
				}

			}
		}

		return true;
	}
}
