package org.wfp.offlinepayment.business;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;

import android.os.Environment;
import android.util.Log;

public class ProviderUtility {

	public static LoginProvider LoginProvider = null;
	public static BeneficiaryProvider BeneficiaryProvider = null;

	public static final int CUSTOMER_ACTIVITY_REQUEST_CODE = 1000;
	public static final int CONSIGNEMNT_STATUS_MANAGEMENT_REQUEST_CODE = 2000;
	public static final int IMAGE_CAPTURE = 3000;
	public static final int GALLERY_ACTIVITY_REQUEST_CODE = 4000;
	public static final int MESSAGE_REPLY_ACTIVITY_REQUEST_CODE = 5000;
	public static final int NEW_MESSAGE_ACTIVITY_REQUEST_CODE = 6000;
	public static final int NOTIFICATION_REQUESTCODE = 7000;
	public static final int NOTIFICATION_ID = 8000;
	public static final int GPS_SETTINGS_ACTIVITY = 9000;
	public static final int CONSIGNEMNT_STATUS_MANAGEMENT_REQUEST_CODE_MULTI = 10000;
	public static final int CUSTOMER_ACTIVITY_REQUEST_CODE_MULTI = 11000;
	public static final int GALLERY_ACTIVITY_REQUEST_CODE_MULTI = 12000;
	public static final int FORWARD_TO_CONS_LIST = 13000;
	
	public static final String CLIENT_TF = "TF";
	public static final String CLIENT_MCL = "MCL";
	public static final String CLIENT_CFM = "CFM";
	public static final String CLIENT_RAPID = "RAPID";
	
	public static final String JOB_TYPE_NORMAL_DELIVERY = "ND";
	public static final String JOB_TYPE_NORMAL_DELIVERY_STR = "Normal Delivery";
	public static final String JOB_TYPE_CONTAINER_DELIVERY = "CD";
	public static final String JOB_TYPE_CONTAINER_DELIVERY_STR = "Container Delivery";
	
	public static final String SERVICE_TYPE_IMPORT = "IMP";
	public static final String SERVICE_TYPE_IMPORT_STR = "Import";
	public static final String SERVICE_TYPE_EXPORT = "EXP";
	public static final String SERVICE_TYPE_EXPORT_STR = "Export";
	
	public static final String CONSIGNMENT_INTENT_KEY = "CONSIGNMENT_ID";
	public static final String MESSAGE_INTENT_KEY = "MESSAGE_KEY";
	public static final String CONSIGNMENT_IMAGE = "IMAGE";
	public static final String ITEM_INTENT_KEY = "ITEM_ID";
	public static final String CUSTOMER_SIGN_CAPTURE_USERNAME = "CUSTOMER_SIGN_USERNAME";
	public static final String CUSTOMER_SIGN_CAPTURE_FILENAME = "CUSTOMER_SIGN_FILENAME";
	public static final String CUSTOMER_TRIP_STATUS = "CUSTOMER_TRIP_STATUS";
	public static final String CUSTOMER_POD_STATUS = "CUSTOMER_POD_STATUS";
	public static final String CUSTOMER_CONSIGNMENTID = "CONSIGMENTID";
	public static final String CONSIGNMENT_ID_LIST_KEY = "CONSIGNMENT_ID_LIST_KEY";
	public static final String CUSTOMER_SIGN_LIST = "CONSIGNMENT_SIGN_LIST";
	public static final String CONSIGNMENT_INTENT_KEY_MULTI = "CONSIGNMENT_ID";
	
	public static final String AUTH_TOKEN_INVALID = "$#authtokeninvalid$#";
	
	
	public static final String ImagesRootFolder = Environment.getExternalStorageDirectory() + "/Androidpod/ConsignmentImages/";
	public static final String DELIVERY_KIND = "DELIVERY_KIND";

	public static final SimpleDateFormat DateFormatStr = new SimpleDateFormat("dd MMM, yyyy, hh:mm a");
	public static final String ARRIVAL_TIME = "Arrival_Time";

	private String getTodaysDate() { 
		
		final Calendar c = Calendar.getInstance();
		int todaysDate = (c.get(Calendar.YEAR) * 10000) + ((c.get(Calendar.MONTH) + 1) * 100) +	(c.get(Calendar.DAY_OF_MONTH));
		Log.w("DATE:",String.valueOf(todaysDate));
		return(String.valueOf(todaysDate));
	
	}
	
	private String getCurrentTime() {
	
		final Calendar c = Calendar.getInstance();
		int currentTime = (c.get(Calendar.HOUR_OF_DAY) * 10000) + (c.get(Calendar.MINUTE) * 100) +	(c.get(Calendar.SECOND));
		Log.w("TIME:",String.valueOf(currentTime));
		return(String.valueOf(currentTime));
	
	}
	
	public static String GetGMTTime()
	{
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
		return sdf.format(new Date());
	}
	
	public static String GetMilisTimeToString()
	{
		return String.valueOf(GetMilisTimeToLong());
	}
	
	public static long GetMilisTimeToLong()
	{
		return new Date().getTime();
	}
	
	public static long GetMilis24Hours()
	{
		return GetMilisTimeToLong() + 86400000; //to "now" time add 24h in miliseconds.
		//return GetMilisTimeToLong() + 60000;
	}
	
	public static long GetMilisOneMinute()
	{
		return GetMilisTimeToLong() + 60000; //to "now" time add 24h in miliseconds.
		//return GetMilisTimeToLong() + 60000;
	}
}

