package org.wfp.offlinepayment.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wfp.offlinepayment.db.Database;
import org.wfp.offlinepayment.enums.BeneficiaryEnum;
import org.wfp.offlinepayment.exceptions.DatabaseInsertException;
import org.wfp.offlinepayment.exceptions.DatabaseUpdateException;
import org.wfp.offlinepayment.exceptions.JSONNullableException;
import org.wfp.offlinepayment.exceptions.NetworkStatePermissionException;
import org.wfp.offlinepayment.exceptions.NotConnectedException;
import org.wfp.offlinepayment.exceptions.UrlConnectionException;
import org.wfp.offlinepayment.model.BaseModel;
import org.wfp.offlinepayment.model.BeneficiaryModel;
import org.wfp.offlinepayment.model.BeneficiaryUpdateModel;

import android.content.Context;
import android.location.Location;

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

	public void savePayment(String paymentId, int status, double lat, double lng) throws DatabaseUpdateException
	{
		if (Database.savePayment(context, paymentId, status, String.valueOf(lat), String.valueOf(lng)) != 1)
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

		model.setId(setStringValueFromJSON(BeneficiaryEnum.Id.Value, object));
		model.setPaymentId(setStringValueFromJSON(BeneficiaryEnum.PaymentId.Value, object));
		model.setPaymentCycle(setStringValueFromJSON(BeneficiaryEnum.PaymentCycle.Value, object));
		model.setDistrict(setStringValueFromJSON(BeneficiaryEnum.District.Value, object));
		model.setTehsil(setStringValueFromJSON(BeneficiaryEnum.Tehsil.Value, object));
		model.setSchoolId(setStringValueFromJSON(BeneficiaryEnum.SchoolId.Value, object));
		model.setSchoolName(setStringValueFromJSON(BeneficiaryEnum.SchoolName.Value, object));
		model.setStudentId(setStringValueFromJSON(BeneficiaryEnum.StudentId.Value, object));
		model.setStudentName(setStringValueFromJSON(BeneficiaryEnum.StudentName.Value, object));
		model.setDateOfBirth(setStringValueFromJSON(BeneficiaryEnum.DateOfBirth.Value, object));
		model.setStudentClass(setStringValueFromJSON(BeneficiaryEnum.StudentClass.Value, object));
		model.setBeneficiaryCNIC(setStringValueFromJSON(BeneficiaryEnum.BeneficiaryCNIC.Value, object));
		model.setBeneficiaryName(setStringValueFromJSON(BeneficiaryEnum.BeneficiaryName.Value, object));
		model.setAmount(setIntValueFromJSON(BeneficiaryEnum.Amount.Value, object));
		model.setStatus(setIntValueFromJSON(BeneficiaryEnum.Status.Value, object));
		model.setDateDownloaded(new java.util.Date());

		return model;
	}

	public boolean uploadPaymentsData()
			throws JSONException, ClientProtocolException, IOException,
			JSONNullableException, NotConnectedException, NetworkStatePermissionException, DatabaseUpdateException {

		IsConnected();

		List<BeneficiaryModel> data = ProviderUtility.BeneficiaryProvider.getPayments();
		List<BeneficiaryUpdateModel> notSynced = new ArrayList<BeneficiaryUpdateModel>();
		for (BeneficiaryModel model : data) {
			//if(model.isPaid() && !model.isSynced())
            if(model.isPaid() )
            {
                BeneficiaryUpdateModel update = new BeneficiaryUpdateModel();
                update.setPaymentId(model.getPaymentId());
                update.setDatePaid(DateUtility.formatDateTimeToSend(model.getDatePaid()));
                update.setLatPaid(model.getLatPaid());
                update.setLngPaid(model.getLngPaid());
                notSynced.add(update);
            }
		}

		JSONArray jsArray = new JSONArray();
        for (BeneficiaryUpdateModel model : notSynced) {
            jsArray.put(model.getJSONObject());
        }

//        JSONStringer object = new JSONStringer()
//                .object()
//                .key(BeneficiaryUpdateEnum.PaymentId.Value).value(model.getPaymentId())
//                .key(BeneficiaryUpdateEnum.DatePaid.Value).value(model.getDatePaid())
//                .endObject();
//        JSONStringer object = new JSONStringer()
//                .object()
//                .key(BeneficiaryUpdateEnum.Data.Value).value(jsArray)
//                .endObject();

		String url = BaseModel.GetUpdatePaymentsUrl(context);
		url = url.replace("authtokenvalue", ProviderUtility.LoginProvider.LoginModel.getAuthToken());
		HttpPost postRequest = new HttpPost(url);
		postRequest.setHeader("Content-Type", "application/json");

		StringEntity loginEntity = new StringEntity(jsArray.toString());
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
				for (BeneficiaryUpdateModel payment: notSynced) {
					Database.setPaymentSynced(context, payment.getPaymentId());
				}

			}
		}

		return true;
	}
}
