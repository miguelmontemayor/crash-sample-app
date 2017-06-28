package com.miguelmontemayor.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.accounts.Account;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.view.View;


import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.v1.BindingException;
import com.clover.sdk.v1.ClientException;
import com.clover.sdk.v1.ServiceException;
import com.clover.sdk.v1.merchant.Merchant;
import com.clover.sdk.v1.merchant.MerchantConnector;


import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity{

    private Account mAccount;
    private MerchantConnector merchantConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Retrieve the Clover account
        if (mAccount == null) {
            mAccount = CloverAccount.getAccount(this);

            if (mAccount == null) {
                return;
            }
        }

        // Connect InventoryConnector
        connect();

        // Log merchant info
        new LogMerchantAsyncTask().execute();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    private void connect() {
        disconnect();
        if (mAccount != null) {
            merchantConnector = new MerchantConnector(this, mAccount, null);
            merchantConnector.connect();
        }
    }

    private void disconnect() {
        if (merchantConnector != null) {
            merchantConnector.disconnect();
            merchantConnector = null;
        }
    }

    /*
    This method logs merchant information to the Crashlytics reporting, which
    will allow the developer to see merchant details in the crash reports.
     */
    private class LogMerchantAsyncTask extends AsyncTask<Void, Void, Merchant> {

        @Override
        protected final Merchant doInBackground(Void... params) {
            try {
                //Get the merchant
                return merchantConnector.getMerchant();

            } catch (RemoteException | ClientException | ServiceException | BindingException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected final void onPostExecute(Merchant merchant) {
            if(merchant!=null) {
                Crashlytics.setUserIdentifier(merchant.getId());
                Crashlytics.setUserName(merchant.getName());
            }
        }
    }

    /*
    This method forces a crash for testing. Make sure to disable this
    on any production applications!
     */
    public void forceCrash(View view) {
        throw new RuntimeException("This is a forced crash");
    }

}
