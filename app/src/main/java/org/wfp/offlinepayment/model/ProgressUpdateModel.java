package org.wfp.offlinepayment.model;
import android.app.ProgressDialog;

public class ProgressUpdateModel {

	private ProgressDialog Dialog;
	private String TaskName;
	private int NotificationId;
	private int ItemsCount;
	private int Step;
	private String Message;
	private String Title;
	
	public int getItemsCount() {
		return ItemsCount;
	}
	public void setItemsCount(int itemsCount) {
		ItemsCount = itemsCount;
	}
	public int getStep() {
		return Step;
	}
	public void setStep(int step) {
		Step = step;
	}
	public ProgressDialog getDialog() {
		return Dialog;
	}
	public void setDialog(ProgressDialog dialog) {
		Dialog = dialog;
	}
	public String getMessage() {
		return Message;
	}
	public void setMessage(String message) {
		Message = message;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getTaskName() {
		return TaskName;
	}
	public void setTaskName(String taskName) {
		TaskName = taskName;
	}
	public int getNotificationId() {
		return NotificationId;
	}
	public void setNotificationId(int notificationId) {
		NotificationId = notificationId;
	}
	
}
