package org.wfp.offlinepayment.model;

public class LoginMessageHeader extends BaseModel {

	private boolean Success;
	private String ErrorMessage;
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
	

	

}
