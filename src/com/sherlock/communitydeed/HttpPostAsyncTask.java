package com.sherlock.communitydeed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import android.os.AsyncTask;

public class HttpPostAsyncTask extends AsyncTask<String, Integer, Double>{

    private final String HTTP_SERVER = "http://battlehack.10.10.90.161.xip.io/api/login";
    
    @Override
    protected Double doInBackground(String... params) {
        // TODO Auto-generated method stub
        try {
            postData(params[0], params[1]);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Double result){
        //Toast.makeText(, "command sent", Toast.LENGTH_LONG).show();
    }
    protected void onProgressUpdate(Integer... progress){
        //pb.setProgress(progress[0]);
    }

    public void postData(String strKey, String strValue) throws JSONException {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://battlehack.10.10.90.161.xip.io/api/login");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(strKey, strValue));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            String result = EntityUtils.toString(response.getEntity());
            result = result.replaceAll("\n", "\\n");
            JSONObject myObject = new JSONObject(result);
            
            Log.i("HttpPost", myObject.get("user_id").toString());
            

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }

    private void Log(String string, Object object) {
        // TODO Auto-generated method stub
        
    }

}