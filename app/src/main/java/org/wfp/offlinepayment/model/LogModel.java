package org.wfp.offlinepayment.model;

public class LogModel extends BaseModel {
	
	public enum LogModelEnum
	{
		CompanyCode("CompanyCode"),
		CompanyName("CompanyName"),
		UserName("UserName"),
		Log("Log"),
		;
		
		public String Value;
		
		private LogModelEnum(String v){
			Value = v;
		}
	}
	
	
}
