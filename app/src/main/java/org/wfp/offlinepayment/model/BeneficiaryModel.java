package org.wfp.offlinepayment.model;

import android.text.method.DateTimeKeyListener;

import org.wfp.offlinepayment.business.ProviderUtility;

import java.io.Serializable;
import java.util.Date;

public class BeneficiaryModel extends BaseModel implements Serializable {

	private String Id;
	private String PaymentId;
	private String PaymentCycle;
	private String District;
	private String Tehsil;
	private String Uc;
	private String Village;
	private String Address;
	private String School;
	private String BeneficiaryCNIC;
	private String BeneficiaryName;
	private String FatherName;
	private int Amount;
	private int Status;

	private Date DateDownloaded;
	private boolean Paid;
	private Date DatePaid;

	private boolean Synced;
	private Date DateSynced;

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getPaymentCycle() {
		return PaymentCycle;
	}

	public void setPaymentCycle(String paymentCycle) {
		PaymentCycle = paymentCycle;
	}

	public String getDistrict() {
		return District;
	}

	public void setDistrict(String district) {
		District = district;
	}

	public String getTehsil() {
		return Tehsil;
	}

	public void setTehsil(String tehsil) {
		Tehsil = tehsil;
	}

	public String getUc() {
		return Uc;
	}

	public void setUc(String uc) {
		Uc = uc;
	}

	public String getVillage() {
		return Village;
	}

	public void setVillage(String village) {
		Village = village;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public String getSchool() {
		return School;
	}

	public void setSchool(String school) {
		School = school;
	}

	public String getBeneficiaryCNIC() {
		return BeneficiaryCNIC;
	}

	public void setBeneficiaryCNIC(String beneficiaryCNIC) {
		BeneficiaryCNIC = beneficiaryCNIC;
	}

	public String getBeneficiaryName() {
		return BeneficiaryName;
	}

	public void setBeneficiaryName(String beneficiaryName) {
		BeneficiaryName = beneficiaryName;
	}

	public String getFatherName() {
		return FatherName;
	}

	public void setFatherName(String fatherName) {
		FatherName = fatherName;
	}

	public int getAmount() {
		return Amount;
	}

	public void setAmount(int amount) {
		Amount = amount;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public boolean isSynced() {
		return Synced;
	}

	public void setSynced(boolean synced) {
		Synced = synced;
	}

	public Date getDateDownloaded() {
		return DateDownloaded;
	}

	public void setDateDownloaded(Date dateDownloaded) {
		DateDownloaded = dateDownloaded;
	}

	public String getPaymentId() {
		return PaymentId;
	}

	public void setPaymentId(String paymentId) {
		PaymentId = paymentId;
	}

	public boolean isPaid() {
		return Paid;
	}

	public void setPaid(boolean paid) {
		Paid = paid;
	}

	public Date getDatePaid() {
		return DatePaid;
	}

	public void setDatePaid(Date datePaid) {
		DatePaid = datePaid;
	}

	public Date getDateSynced() {
		return DateSynced;
	}

	public void setDateSynced(Date dateSynced) {
		DateSynced = dateSynced;
	}
}
