package com.sherlock.communitydeed;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DeedDataAdapter extends ArrayAdapter<DeedData> {

    private int mResource;
    
    public DeedDataAdapter(Context context, int resource, List<DeedData> objects) {
        super(context, resource, objects);
        // TODO Auto-generated constructor stub
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout deedView = null;
        try {
            DeedData item = getItem(position);

            long KEY_ID = item.KEY_ID;
            String title = item.mTitle;
            String donation = String.valueOf(Math.round(item.mDonation));
            
            if (convertView == null) {
                deedView = new LinearLayout(getContext());
                String inflater = Context.LAYOUT_INFLATER_SERVICE;
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
                vi.inflate(mResource, deedView, true);
            } else {
                deedView = (LinearLayout) convertView;
            }

            TextView KEY_IDTV = (TextView) deedView.findViewById(R.id.deed_row_KEY_ID);
            TextView titleTV = (TextView) deedView.findViewById(R.id.deed_row_title);
            TextView donationTV = (TextView) deedView.findViewById(R.id.deed_row_donation);
            
            KEY_IDTV.setText("" + title);
            titleTV.setText("Sponsorship: $" + donation);
            donationTV.setText("Image:" + item.mImgUri);
            Log.i("DeedDataAdapter", title);
            
        } catch (Exception e) {
            Toast.makeText(getContext(),
                    "exception in ArrayAdpter: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
        return deedView;
    }

}
