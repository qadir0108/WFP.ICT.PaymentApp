package org.wfp.offlinepayment.business;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.wfp.offlinepayment.exceptions.ValueValidationException;

public class PreferenceUtility {

	public static String Hostname;
	private static String Port;
	public static String CompanyCode;
	public static String CompanyName;
	private static String GpsFrequency;

	public static PreferenceUtility GetPreferences(Context context){

		PreferenceUtility intance = new PreferenceUtility();

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

		Hostname = preferences.getString("hostname", "http://10.0.2.2");
		Port = preferences.getString("port", "8050");
		CompanyCode = preferences.getString("company_code", "WFP");
		CompanyName = preferences.getString("company_name", "WFP Offline Payment");
		GpsFrequency = preferences.getString("gps_frequency", "2");

		return intance;
	}
	
	
	public int GetGpsFrequency() throws ValueValidationException {
		
		int value;
		try{
			value = Integer.parseInt(GpsFrequency);
		}
		catch(Exception ex)
		{
			throw new ValueValidationException();
		}
		
		return value;
	}
	
	public int GetPort() throws ValueValidationException{
		
		int value;
		try{
			value = Integer.parseInt(Port);
		}
		catch(Exception ex)
		{
			throw new ValueValidationException();
		}
		
		return value;
	}
}
