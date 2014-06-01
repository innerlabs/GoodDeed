package com.sherlock.communitydeed;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.os.Bundle;
import android.app.Activity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;

import com.facebook.*;
import com.facebook.model.*;

import com.sherlock.communitydeed.HttpPostAsyncTask;
import com.sherlock.communitydeed.CreateDeedActivity;

public class MainActivity extends Activity {
    
    public static final String TAG = "GoodDeed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // start Facebook Login
        Session.openActiveSession(this, true, new Session.StatusCallback() {

            // callback when session changes state
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (session.isOpened()) {

                    // make request to the /me API
                    Request.newMeRequest(session, new Request.GraphUserCallback() {

                        // callback after Graph API response with user object
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            Log.i(TAG, "Completed newMeRequest");
                            if (user != null) {
                                TextView welcome = (TextView) findViewById(R.id.welcome);
                                welcome.setText("Hello " + user.getName() + "!");
                            }
                        }
                    }).executeAsync();
                } else {
                    Log.i(TAG, "session.isOpened() == false");
                }
            }
        });
        
        Log.i(TAG, Session.getActiveSession().getAccessToken());
        new HttpPostAsyncTask().execute("fb_access_token", Session.getActiveSession().getAccessToken());
        
        deedCreateActivity();
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }
    
    @Override
    public void onResume() {
        super.onResume();
    }
    
    public void printKeyHash() {
     // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.sherlock.communitydeed", 
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
    
    public void deedCreateActivity() {
        startActivity(new Intent(this, CreateDeedActivity.class));
    }

}
