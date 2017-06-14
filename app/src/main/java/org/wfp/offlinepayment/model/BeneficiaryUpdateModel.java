package org.wfp.offlinepayment.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.wfp.offlinepayment.enums.BeneficiaryUpdateEnum;

import java.io.Serializable;
import java.util.Date;

public class BeneficiaryUpdateModel extends BaseModel implements Serializable {

	private String PaymentId;
	private String DatePaid;
    private String LatPaid;
    private String LngPaid;

    public String getPaymentId() {
        return PaymentId;
    }

    public void setPaymentId(String paymentId) {
        PaymentId = paymentId;
    }

    public String getDatePaid() {
        return DatePaid;
    }

    public void setDatePaid(String datePaid) {
        DatePaid = datePaid;
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put(BeneficiaryUpdateEnum.PaymentId.Value, PaymentId);
            obj.put(BeneficiaryUpdateEnum.DatePaid.Value, DatePaid);
            obj.put(BeneficiaryUpdateEnum.LatPaid.Value, LatPaid);
            obj.put(BeneficiaryUpdateEnum.LngPaid.Value, LngPaid);

        } catch (JSONException e) {
        }
        return obj;
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
}
