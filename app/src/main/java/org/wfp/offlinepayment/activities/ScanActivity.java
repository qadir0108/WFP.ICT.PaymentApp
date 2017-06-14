package org.wfp.offlinepayment.activities;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.wfp.offlinepayment.R;
import org.wfp.offlinepayment.business.BeneficiaryProvider;
import org.wfp.offlinepayment.business.ProviderUtility;
import org.wfp.offlinepayment.exceptions.DatabaseUpdateException;
import org.wfp.offlinepayment.exceptions.JSONNullableException;
import org.wfp.offlinepayment.exceptions.NetworkStatePermissionException;
import org.wfp.offlinepayment.exceptions.NotConnectedException;
import org.wfp.offlinepayment.exceptions.UrlConnectionException;
import org.wfp.offlinepayment.model.BaseModel;
import org.wfp.offlinepayment.model.BeneficiaryModel;
import org.wfp.offlinepayment.services.GPSTracker;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ScanActivity extends AppCompatActivity {

    BeneficiaryModel model;
    GPSTracker tracker;
    boolean isLocationGot;
    private Button btnScan;
    private Button btnPay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan);
        tracker = new GPSTracker(ScanActivity.this);

        btnScan = (Button) findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(ScanActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan Payment Token");
                integrator.initiateScan();
            }
        });

        btnPay = (Button) findViewById(R.id.btnPay);
        btnPay.setEnabled(false);
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // sweet CONFIRM PAY

                new SweetAlertDialog(ScanActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Beneficiary will be marked as PAID!")
                        .setConfirmText("Yes,PAY")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                try {

                                    ProviderUtility.BeneficiaryProvider = new BeneficiaryProvider(ScanActivity.this);
                                    ProviderUtility.BeneficiaryProvider.savePayment(model.getPaymentId(), BaseModel.PaymentStatusEnum.Encashed.ordinal(), tracker.getLatitude(), tracker.getLongitude());
                                } catch (UrlConnectionException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (JSONNullableException e) {
                                    e.printStackTrace();
                                } catch (NotConnectedException e) {
                                    e.printStackTrace();
                                } catch (NetworkStatePermissionException e) {
                                    e.printStackTrace();
                                }
                                catch (DatabaseUpdateException e) {
                                    e.printStackTrace();
                                }

                                sDialog
                                        .setTitleText("Paid!")
                                        .setContentText("Beneficiary has been paid!")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(null)
                                        .showCancelButton(false)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        })
                        .setCancelText("No, cancel!")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .show();

            }
        });


        if (tracker.canGetLocation()) {
            isLocationGot = true;
//            new SweetAlertDialog(ScanActivity.this, SweetAlertDialog.SUCCESS_TYPE)
//                    .setTitleText("Done!")
//                    .setContentText(tracker.getLatitude() + " " +tracker.getLongitude())
//                    .show();
        } else {
            isLocationGot = false;
            tracker.showSettingsAlert();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {

            try {
                String paymentId = scanResult.getContents();//"05000012571000";

                if(paymentId != null) {

                    ProviderUtility.BeneficiaryProvider = new BeneficiaryProvider(ScanActivity.this);
                    model = ProviderUtility.BeneficiaryProvider.getPaymentById(paymentId);
                    if(model == null)
                    {
                        // sweet Not Found
                        btnPay.setEnabled(false);
                        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error!")
                                .setContentText("Beneficiary NOT FOUND!")
                                .show();
                    } else {

                        if(model.isPaid())
                        {
                            // sweet ALREADY PAID
                            btnPay.setEnabled(false);
                            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error!")
                                    .setContentText("Beneficiary ALREADY PAID!")
                                    .show();
                        }
                        else {
                            // sweet FOUND -> PAY
                            btnPay.setEnabled(true);
                            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("WFP Offline Payment!")
                                    .setContentText("Beneficiary FOUND!")
                                    .show();

                            ((TextView) findViewById(R.id.lblSchoolName)).setText(model.getSchoolName());
                            ((TextView) findViewById(R.id.lblSchoolId)).setText(model.getSchoolId());
                            ((TextView) findViewById(R.id.lblDistrict)).setText(model.getDistrict());
                            ((TextView) findViewById(R.id.lblTehsil)).setText(model.getTehsil());
                            ((TextView) findViewById(R.id.lblStudentName)).setText(model.getStudentName());
                            ((TextView) findViewById(R.id.lblStudentId)).setText(model.getStudentId());
                            ((TextView) findViewById(R.id.lblStudentClass)).setText(model.getStudentClass());
                            ((TextView) findViewById(R.id.lblStudentDoB)).setText(model.getDateOfBirth());
                            ((TextView) findViewById(R.id.lblBeneficiaryName)).setText(model.getBeneficiaryName());
                            ((TextView) findViewById(R.id.lblBeneficiaryCNIC)).setText(model.getBeneficiaryCNIC());
                            ((TextView) findViewById(R.id.lblPaymentCycle)).setText(model.getPaymentCycle());
                            ((TextView) findViewById(R.id.lblAmount)).setText(model.getAmount() + "");
                        }
                    }
                }

            } catch (UrlConnectionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (JSONNullableException e) {
                e.printStackTrace();
            } catch (NotConnectedException e) {
                e.printStackTrace();
            } catch (NetworkStatePermissionException e) {
                e.printStackTrace();
            }

        }
        // else continue with any other code you need in the method

    }

}
