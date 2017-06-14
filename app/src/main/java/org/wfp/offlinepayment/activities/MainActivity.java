package org.wfp.offlinepayment.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.wfp.offlinepayment.R;
import org.wfp.offlinepayment.business.BeneficiaryProvider;
import org.wfp.offlinepayment.business.LoginProvider;
import org.wfp.offlinepayment.business.ProviderUtility;
import org.wfp.offlinepayment.exceptions.DatabaseInsertException;
import org.wfp.offlinepayment.exceptions.DatabaseUpdateException;
import org.wfp.offlinepayment.exceptions.JSONNullableException;
import org.wfp.offlinepayment.exceptions.NetworkStatePermissionException;
import org.wfp.offlinepayment.exceptions.NotConnectedException;
import org.wfp.offlinepayment.exceptions.UrlConnectionException;
import org.wfp.offlinepayment.iterfaces.ProgressUpdateListener;
import org.wfp.offlinepayment.model.BaseModel;
import org.wfp.offlinepayment.model.BeneficiaryModel;
import org.wfp.offlinepayment.model.ProgressUpdateModel;
import org.wfp.offlinepayment.utils.CommonUtilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, ProgressUpdateListener {

    LinearLayout layoutProgress;
    TextView txtTitle = null;
    TextView txtText = null;
    ProgressBar progressBar = null;

    Button btnPay;
    Button btnSync;
    Button btnLogOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            ProviderUtility.LoginProvider = new LoginProvider(MainActivity.this);
            ProviderUtility.LoginProvider.CheckLoginStatus();
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

        layoutProgress = (LinearLayout) findViewById(R.id.layoutProgress);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtText = (TextView) findViewById(R.id.txtText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnPay = (Button) findViewById(R.id.btnPay);
        btnSync = (Button) findViewById(R.id.btnSync);
        btnLogOff = (Button) findViewById(R.id.btnLogoff);
        //listView = (ListView) findViewById(R.id.listView1);

        btnPay.setOnClickListener(this);
        btnSync.setOnClickListener(this);
        btnLogOff.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);

        switch (item.getItemId())
        {
            case R.id.settings:

                Intent i = new Intent(getApplicationContext(), Preferences.class);
                startActivity(i);

                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.btnPay:
                Intent myIntent = new Intent(MainActivity.this, ScanActivity.class);
                myIntent.putExtra("key", "value"); //Optional parameters
                MainActivity.this.startActivity(myIntent);
                break;
            case R.id.btnSync:
                new FetchAndStoreBeneficiaries().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                break;
            case R.id.btnLogoff:

                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure to logout?")
                        .setContentText("Your session information will be cleared.")
                        .setConfirmText("Yes,Logout")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                ProviderUtility.LoginProvider.LogOff();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run()
                                    {
                                        MainActivity.this.finish();
                                    }
                                }, 2000);

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

                break;

            default:
                break;
        }

    }

    @Override
    public void OnProgressUpdateListener(final ProgressUpdateModel model)
    {

        runOnUiThread(new Runnable() {

            @Override
            public void run()
            {
                if (model != null)
                {

                    int stepPercentage = model.getStep() / model.getItemsCount() * 100;

                    progressBar.setMax(model.getItemsCount());
                    progressBar.setProgress(model.getStep());
                    txtTitle.setText(model.getTaskName());
                    txtText.setText(model.getTitle() + " - " + model.getMessage());

                    if (stepPercentage == 100)
                    {
                        try
                        {

//                            if (ProviderUtility.SignatureProvider != null)
//                                ProviderUtility.SignatureProvider.UnRegisterProgressUpdateListener();
                            Thread.sleep(2000);

                        } catch (InterruptedException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }

                }
            }
        });

    }


    class FetchAndStoreBeneficiaries extends AsyncTask<Void, Void, String>
    {

        boolean loaded = false;

        @Override
        protected void onPreExecute()
        {
            layoutProgress.setVisibility(View.VISIBLE);

            txtTitle.setText("Loading");
            txtText.setText("Beneficiaries to be paid.");
            progressBar.setIndeterminate(false);
            progressBar.setMax(5);
            progressBar.setProgress(1);

            progressBar.getProgressDrawable().clearColorFilter();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params)
        {
            try
            {
                updateProgressBar(2, 1, "Loading", "Fetching beneficiaries from server...");
                ProviderUtility.BeneficiaryProvider = new BeneficiaryProvider(MainActivity.this);
                ProviderUtility.BeneficiaryProvider.loadBeneficiaryData();
                if (!ProviderUtility.BeneficiaryProvider.getErrorMessage().equals(""))
                    return ProviderUtility.BeneficiaryProvider.getErrorMessage();
                updateProgressBar(2, 2, "Completed", "Data loading completed.");

            } catch (UrlConnectionException e)
            {
                return "Urlexc";
            } catch (JSONException e)
            {
                return "JSONexc";
            } catch (JSONNullableException e)
            {
                return "JSONNULLexc";
            } catch (NotConnectedException e)
            {
                return "NotConnected";
            } catch (NetworkStatePermissionException e)
            {
                return "Networkexc";
            } catch (DatabaseInsertException e)
            {
                return "DatabaseInsertexc";
            }

            return null;
        }

        @Override
        protected void onPostExecute(String message)
        {

            loaded = true;

            progressBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            CommonUtilities.fadeOutView(MainActivity.this, layoutProgress);

            super.onPostExecute(message);

            new UploadPayments().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
        }
    }

    class UploadPayments extends AsyncTask<Void, Void, String>
    {

        boolean loaded = false;

        @Override
        protected void onPreExecute()
        {
            layoutProgress.setVisibility(View.VISIBLE);

            txtTitle.setText("Uploading");
            txtText.setText("Beneficiaries payment data.");
            progressBar.setIndeterminate(false);
            progressBar.setMax(5);
            progressBar.setProgress(1);

            progressBar.getProgressDrawable().clearColorFilter();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params)
        {
            try
            {
                updateProgressBar(2, 1, "Uploading", "Uploading beneficiaries data to server...");
                ProviderUtility.BeneficiaryProvider = new BeneficiaryProvider(MainActivity.this);
                ProviderUtility.BeneficiaryProvider.uploadPaymentsData();
                if (!ProviderUtility.BeneficiaryProvider.getErrorMessage().equals(""))
                    return ProviderUtility.BeneficiaryProvider.getErrorMessage();
                updateProgressBar(2, 2, "Completed", "Data uploading completed.");

            } catch (UrlConnectionException e)
            {
                return "Urlexc";
            } catch (JSONException e)
            {
                return "JSONexc";
            } catch (JSONNullableException e)
            {
                return "JSONNULLexc";
            } catch (NotConnectedException e)
            {
                return "NotConnected";
            } catch (NetworkStatePermissionException e)
            {
                return "Networkexc";
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DatabaseUpdateException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String message)
        {

            if(message != null) {
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error!")
                        .setContentText("There is error in Uploading! " + message)
                        .show();
            } else
            {
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("WFP Offline Payment!")
                        .setContentText("Data has been synched with sever!")
                        .show();
            }

            loaded = true;

            progressBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            CommonUtilities.fadeOutView(MainActivity.this, layoutProgress);

            super.onPostExecute(message);

        }
    }

    private void updateProgressBar(final int count, final int step, final String title, final String message)
    {
        final String task = "Reloading Beneficiaries";
        runOnUiThread(new Runnable() {

            @Override
            public void run()
            {

                int stepPercentage = step / count * 100;

                progressBar.setMax(count);
                progressBar.setProgress(step);
                txtTitle.setText(task);
                txtText.setText(title + " - " + message);

                if (stepPercentage == 100)
                {
//                    if (TaskQueue.getQueue().peek() == EnumTask.SyncConsignments)
//                    {
//                        // Remove Sync Task
//                        TaskQueue.getQueue().remove();
//                        StartScreen.this.finish();
//                    }

                }

            }
        });

    }

}
