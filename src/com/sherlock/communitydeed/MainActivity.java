package com.sherlock.communitydeed;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
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
    
    ArrayList<DeedData> DeedDataList;
    private DeedDataAdapter deedAdapter;

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
                                //TextView welcome = (TextView) findViewById(R.id.welcome);
                                //welcome.setText("Hello " + user.getName() + "!");
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

        // Create a Good Deed 
        Button btnCreate = (Button) findViewById(R.id.deed_main_create);
        btnCreate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deedCreateActivity();
            }
        });
        
        ListView deedList = (ListView) findViewById(R.id.deed_list);
        DeedDataList = new ArrayList<DeedData>();
        createDummyDeedList();
        deedAdapter = new DeedDataAdapter(this, R.layout.deed_item_view, DeedDataList);
        deedList.setAdapter(deedAdapter);
        
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
    
    private void createDummyDeedList() {
        DeedData deed = new DeedData(1, "Clear garbage in McCleary Park", 
                "Our kids play there often but lately there's been more garbage because of increased park events. Let's keep it clean!", 
                5.00, "/img/IMG_2001.jpg", 56.3235, -79.5821);
        DeedDataList.add(deed);
        
        deed = new DeedData(2, "Shovel snow off walking path through Battle Park", 
                "In the spring/summer, lots of people cut through this park to save time getting to TTC but in winter the path is blocked by snow.", 
                10.00, "/img/IMG_4954.jpg", 100.7321, 38.2578);
        DeedDataList.add(deed);
        
        deed = new DeedData(3, "Rake the gravel in dog park", 
                "Some reckless adults have been racing their Big Wheels in the dog park, causing all the gravel to clump at the sides.", 
                7.00, "/img/IMG_0009.jpg", 90.6543, 15.0052);
        DeedDataList.add(deed);
    }

}
