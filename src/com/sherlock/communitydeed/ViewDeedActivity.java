package com.sherlock.communitydeed;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalService;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ViewDeedActivity extends Activity {

    // PayPal settings
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "AdtQtBBAQeqsgu_ZLRQ5h250D-u7xJ7_uRK4PCCm-SpDAa73ZLWol61vy82J";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    
    private static PayPalConfiguration config = new PayPalConfiguration()
        .environment(CONFIG_ENVIRONMENT)
        .clientId(CONFIG_CLIENT_ID)
        // The following are only used in PayPalFuturePaymentActivity.
        .merchantName("GoodDeed")
        .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
        .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));
    // ===============
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_deed);
        
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
        
        Intent intentData = getIntent();
        EditText editTitle = (EditText)findViewById(R.id.deed_view_title_value);
        editTitle.setText(intentData.getStringExtra(DeedData.TITLE));
        EditText editDesc = (EditText)findViewById(R.id.deed_view_desc_value);
        editDesc.setText(intentData.getStringExtra(DeedData.DESC));
        EditText editDonation = (EditText)findViewById(R.id.deed_view_donation_value);
        editDonation.setText("$" + String.valueOf(intentData.getDoubleExtra(DeedData.DONATION, 0.0f)));

        // TIME button 
        Button btnDonate = (Button) findViewById(R.id.deed_view_donate);
        btnDonate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                
                // Invoke PayPal API to authenticate future payment
                Intent intent = new Intent(ViewDeedActivity.this, PayPalFuturePaymentActivity.class);
                startActivityForResult(intent, REQUEST_CODE_FUTURE_PAYMENT);
                
                // Send Deed details to server
                setResult(Activity.RESULT_OK, null);
                finish();
            }
        });
        
        // MONEY button 
        Button btnDo = (Button) findViewById(R.id.deed_view_do);
        btnDo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        

    }
    
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        
        stopService(new Intent(this, PayPalService.class));
        
        super.onDestroy();
    }
    
}
