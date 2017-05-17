package org.wfp.offlinepayment.model;

import org.json.JSONArray;
import org.wfp.offlinepayment.business.PreferenceUtility;
import org.wfp.offlinepayment.exceptions.ValueValidationException;

import android.content.Context;

public class BaseModel {
	
	public JsonMessageHeader Instance;

	public static JsonMessageHeader getJsonMessageHeaderInstance()
	{
		return new JsonMessageHeader();
	}
	
	public static class JsonMessageHeader extends BaseModel {

		private boolean Success;
		private String ErrorMessage;
		private boolean AuthTokenValid;
		private JSONArray JsonArray = new JSONArray();
		public boolean isSuccess() {
			return Success;
		}
		public void setSuccess(boolean success) {
			Success = success;
		}
		public String getErrorMessage() {
			return ErrorMessage;
		}
		public void setErrorMessage(String errorMessage) {
			ErrorMessage = errorMessage;
		}
		public JSONArray getJsonArray() {
			return JsonArray;
		}
		public void setJsonArray(JSONArray jsonArray) {
			JsonArray = jsonArray;
		}
		public boolean isAuthTokenValid() {
			return AuthTokenValid;
		}
		public void setAuthTokenValid(boolean authTokenValid) {
			AuthTokenValid = authTokenValid;
		}
	}


	public static String GetLoginServiceUrl(Context context){
		return PreferenceUtility.GetPreferences(context).Hostname + ":" + GetPort(context) + "/api/payments/login";
	}

	public static String GetPaymentsUrl(Context context){
		return PreferenceUtility.GetPreferences(context).Hostname+ ":" + GetPort(context) + "/api/payments?AuthToken=authtokenvalue";
	}

	public static String GetUpdatePaymentsUrl(Context context){
		return PreferenceUtility.GetPreferences(context).Hostname+ ":" + GetPort(context) + "/api/payments/update?AuthToken=authtokenvalue";
	}

	public static String GetSendLogUrl(Context context){
		//return TransfocusServer + "/ws/json/syncreply/SendErrorLog";
		return PreferenceUtility.GetPreferences(context).Hostname + ":" + GetPort(context) + "/ws/json/syncreply/SendErrorLog";
	}

	public static String GetSignaturesUrl(Context context){
		return PreferenceUtility.GetPreferences(context).Hostname+ ":" + GetPort(context) + "/WS/json/reply/SendDriverSignature";
	}
	public static String GetItemUrl(Context context){
		return PreferenceUtility.GetPreferences(context).Hostname+ ":" + GetPort(context) + "/ws/json/syncreply/GetRunSheetItems?AuthToken=authtokenvalue&ConsignmentID=consignmentidvalue";
	}
	public static String GetGCMRegisterUrl(Context context){
		return PreferenceUtility.GetPreferences(context).Hostname+ ":" + GetPort(context) + "/ws/json/reply/GCMRegister";
	}
	public static String GetTripUrl(Context context){
		return PreferenceUtility.GetPreferences(context).Hostname+ ":" + GetPort(context);
	}
	public static String GetPodUrl(Context context){
		return PreferenceUtility.GetPreferences(context).Hostname+ ":" + GetPort(context);
	}
	public static String GetMessageFeedUrl(Context context){
		return PreferenceUtility.GetPreferences(context).Hostname+ ":" + GetPort(context) + "/ws/json/syncreply/GetMessages?AuthToken=authtokenvalue&Folder=messagefolder";
	}
	public static String GetMessageCommitUrl(Context context){
		return PreferenceUtility.GetPreferences(context).Hostname+ ":" + GetPort(context) + "/ws/json/syncreply/SendMessage";
	}
	public static String GetRecipientUrl(Context context){
		return PreferenceUtility.GetPreferences(context).Hostname+ ":" + GetPort(context) + "/ws/json/syncreply/GetUsers?AuthToken=authtokenvalue";
	}
	public static String GetDataSynchornizationUrl(Context context){
		return PreferenceUtility.GetPreferences(context).Hostname+ ":" + GetPort(context) + "/ws/json/syncreply/UpdateConsignment";
	}
	public static String GetRunsheetAckUrl(Context context){
		return PreferenceUtility.GetPreferences(context).Hostname+ ":" + GetPort(context) + "/ws/json/syncreply/UpdateRunsheetAck";
	}
	public static String GetMessageAckUrl(Context context){
		return PreferenceUtility.GetPreferences(context).Hostname+ ":" + GetPort(context) + "/ws/json/syncreply/UpdateMessageAck";
	}
	public static String GetGpsSynchornizationUrl(Context context){
		return PreferenceUtility.GetPreferences(context).Hostname+ ":" + GetPort(context);
	}
	//
	public static String GetGpxUrl(Context context){
		return PreferenceUtility.GetPreferences(context).Hostname+ ":" + GetPort(context)  + "/WS/json/syncreply/GetRunSheetRoute?AuthToken=authtokenvalue&RunSheetID=runsheetidvalue";
	}
	
	public static String GetSignatureUploadServiceUrl(Context context){
		return PreferenceUtility.GetPreferences(context).Hostname+ ":" + GetPort(context) + "/FileHandler/SendDriverSignature.aspx?ID=idimagevalue&SignatureID=signatureidvalue&AuthToken=authtokenvalue";
		//return PreferenceUtility.GetInstance(context).Hostname+ ":" + GetPort(context) + "/UploadTest.aspx";
	}
	
	public static String GetConsignmentSignatureUploadServiceUrl(Context context){
		return PreferenceUtility.GetPreferences(context).Hostname+ ":" + GetPort(context) + "/FileHandler/SendConsignmentSignature.aspx?AuthToken=authtokenvalue&ConsignmentID=consignmentidvalue";
	}
	
	public static String GetConsignmentPhotosServiceUrl(Context context){
		return PreferenceUtility.GetPreferences(context).Hostname+ ":" + GetPort(context) + "/FileHandler/SendConsignmentPhotos.aspx?AuthToken=authtokenvalue&ConsignmentID=consignmentidvalue&ID=imageidvalue";
	}
	
	public static String FinalizeConsignmentSynchonizationUrl(Context context){
		return PreferenceUtility.GetPreferences(context).Hostname+ ":" + GetPort(context) + "/ws/json/syncreply/UpdateRunSheet";
	}
	//
	
	private static int GetPort(Context context){
		try {
			return PreferenceUtility.GetPreferences(context).GetPort();
		} catch (ValueValidationException e) {
			
			e.printStackTrace();
		}
		
		return 80;
	}

	public enum JsonMessageHeaderEnum{
		
		IsSuccess("IsSucess"),
		IsTokenValid("IsTokenValid"),
		ErrorMessage("ErrorMessage");

		public String Value;
		
		private JsonMessageHeaderEnum(String v){
			Value = v;
		}
		
	}

	public enum JsonMessageEnum{

		AuthToken("AuthToken"),
		Data("Data");
		public String Value;

		private JsonMessageEnum(String v){
			Value = v;
		}

		}

	public enum PaymentStatusEnum{
		Loaded,
		Generated,
		Encashed
	}
}
