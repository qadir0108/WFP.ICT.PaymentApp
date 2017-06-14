package org.wfp.offlinepayment.model;

import java.io.Serializable;
import java.util.Date;

public class BeneficiaryModel extends BaseModel implements Serializable {

	private String Id;
	private String PaymentId;
	private String PaymentCycle;
	private String District;
	private String Tehsil;
	private String SchoolId;
	private String SchoolName;
	private String StudentId;
	private String StudentName;
	private String DateOfBirth;
	private String StudentClass;
	private String BeneficiaryCNIC;
	private String BeneficiaryName;
	private int Amount;
	private int Status;

	private Date DateDownloaded;
	private boolean Paid;
	private Date DatePaid;
	private String LatPaid;
	private String LngPaid;

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

	public String getSchoolId() {
		return SchoolId;
	}

	public void setSchoolId(String schoolId) {
		SchoolId = schoolId;
	}

	public String getSchoolName() {
		return SchoolName;
	}

	public void setSchoolName(String schoolName) {
		SchoolName = schoolName;
	}

	public String getStudentId() {
		return StudentId;
	}

	public void setStudentId(String studentId) {
		StudentId = studentId;
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

    public String getLatPaid() {
        return LatPaid;
    }

    public void setLatPaid(String latPaid) {
        LatPaid = latPaid;
    }

    public String getLngPaid() {
        return LngPaid;
    }

    public void setLngPaid(String lngPaid) {
        LngPaid = lngPaid;
    }

    public String getStudentName() {
        return StudentName;
    }

    public void setStudentName(String studentName) {
        StudentName = studentName;
    }

    public String getDateOfBirth() {
        return DateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        DateOfBirth = dateOfBirth;
    }

    public String getStudentClass() {
        return StudentClass;
    }

    public void setStudentClass(String studentClass) {
        StudentClass = studentClass;
    }
}
