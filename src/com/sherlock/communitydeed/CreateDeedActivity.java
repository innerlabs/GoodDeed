package com.sherlock.communitydeed;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;
import android.provider.MediaStore;

import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;


public class CreateDeedActivity extends Activity implements LocationListener {

    public static final String TAG = "CreateDeed";
    
    // PayPal settings
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "AdtQtBBAQeqsgu_ZLRQ5h250D-u7xJ7_uRK4PCCm-SpDAa73ZLWol61vy82J";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    // ===============
    
    private LocationManager mLocMgr;
    private Location mLocBestReading;      // Current best location estimate

    static final int CAMERA_PIC_REQUEST = 1;
    
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int MEDIA_TYPE_AUDIO = 3;
    
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("GoodDeed")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_deed);
        
        // Acquire a reference to the system Location Manager
        mLocMgr = (LocationManager) this.getSystemService(getApplicationContext().LOCATION_SERVICE);
        
        // Register the listener with the Location Manager to receive location updates
        mLocMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
        
        // CONFIRM button 
        Button btnConfirm = (Button) findViewById(R.id.create_confirm);
        btnConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                
                // Invoke PayPal API to authenticate future payment
                paypalFuturePmtConsent();
                
                // Send Deed details to server
                setResult(Activity.RESULT_OK, null);
                finish();
            }
        });
        
        // CANCEL button 
        Button btnCancel = (Button) findViewById(R.id.create_cancel);
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // Attach media button 
        Button btnMedia = (Button) findViewById(R.id.create_deed_media);
        btnMedia.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCameraIntent();
            }
        });
    }
    
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        mLocMgr.removeUpdates(this);
        super.onDestroy();
    }

    public void paypalFuturePmtConsent() {
        Intent intent = new Intent(CreateDeedActivity.this, PayPalFuturePaymentActivity.class);
        startActivityForResult(intent, REQUEST_CODE_FUTURE_PAYMENT);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            // Should never get here
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("PayPal", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("PayPal", authorization_code);
                        
                        String corr_id = PayPalConfiguration.getApplicationCorrelationId(this);
                        Log.i("PayPal", "Correlation ID: " + corr_id);

                        Toast.makeText(
                                getApplicationContext(),
                                "Future donation code received from PayPal", Toast.LENGTH_LONG)
                                .show();
                        
                        sendDataToServer(authorization_code, corr_id);

                    } catch (JSONException e) {
                        Log.e("PayPal", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("PayPal", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("PayPal",
                      "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        } else if (requestCode == CAMERA_PIC_REQUEST) {
            if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                Uri dataUri = data.getData();

                // User had pick an image.
                Cursor cursor = getContentResolver().query(dataUri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
                cursor.moveToFirst();

                // Link to the image
                String imageFilePath = cursor.getString(0);
                cursor.close();

                Log.i(TAG, "Media: " + imageFilePath);
            }
        }
    }
    
    private void sendDataToServer(String auth, String corrId) {

        /**
         * TODO: Send the authorization response to your server, where it can
         * exchange the authorization code for OAuth access and refresh tokens.
         * 
         * Your server must then store these tokens, so that your server code
         * can execute payments for this user in the future.
         * 
         * A more complete example that includes the required app-server to
         * PayPal-server integration is available from
         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
         */
        Log.i("CreateDeed", auth + "/" + corrId);

    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub

        if (null == mLocBestReading || location.getAccuracy() < mLocBestReading.getAccuracy()) {

            // Update best estimate
            mLocBestReading = location;
            Log.i(TAG, "Lat: " + String.valueOf(location.getLatitude()) + "/Lon: " + String.valueOf(location.getLongitude()));

        }
        
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
        
    }
    
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
    
    private static File getOutputMediaFile(int type) {
        Log.d(TAG, "getOutputMediaFile() type:" + type);
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        // For future implementation: store videos in a separate directory
        File mediaStorageDir = new File(
                Environment.getExternalStorageDirectory(),
                "GoodDeed");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else if (type == MEDIA_TYPE_AUDIO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "AUD_" + timeStamp + ".3gp");
        } else {
            Log.e(TAG, "typ of media file not supported: type was:" + type);
            return null;
        }

        return mediaFile;
    }
     
    private void launchCameraIntent() {

        /*
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File camFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, camFile);
        startActivityForResult(intent, CAMERA_PIC_REQUEST);
        */
        
        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String pickTitle = "Select Photo"; // Or get from strings.xml
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, 
                new Intent[] { takePhotoIntent } );

        startActivityForResult(chooserIntent, CAMERA_PIC_REQUEST);
        
    }
}
