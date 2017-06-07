package org.wfp.offlinepayment.enums;

/**
 * Created by Administrator on 7/6/2017.
 */

public enum BeneficiaryUpdateEnum{

    Data("Data"),
    PaymentId("PaymentId"),
    DatePaid("DatePaid");
    public String Value;

    private BeneficiaryUpdateEnum(String v){
        Value = v;
    }

}