package org.wfp.offlinepayment.enums;

/**
 * Created by Administrator on 7/6/2017.
 */

public enum BeneficiaryEnum {

    Id("Id"),
    PaymentId("PaymentId"),
    PaymentCycle("PaymentCycle"),
    District("District"),
    Tehsil("Tehsil"),
    SchoolId("SchoolId"),
    SchoolName("SchoolName"),
    StudentId("StudentId"),
    StudentName("StudentName"),
    DateOfBirth("DateOfBirth"),
    StudentClass("StudentClass"),
    BeneficiaryCNIC("BeneficiaryCNIC"),
    BeneficiaryName("BeneficiaryName"),
    Amount("Amount"),
    Status("Status"),
    DateDownloaded("DateDownloaded"),
    IsPaid("IsPaid"),
    DatePaid("DatePaid"),
    LatPaid("LatPaid"),
    LngPaid("LngPaid"),
    IsSynced("IsSynced"),
    DateSynced("DateSynced")
    ;
    public String Value;

    private BeneficiaryEnum(String v){
        Value = v;
    }

}