package com.sherlock.communitydeed;

public class DeedData {
    
    public long   KEY_ID;
    public String mTitle;
    public String mDesc;
    public double mDonation;    // This will need to be a list of User IDs with their donations
    public String mImgUri;
    public double mLat;
    public double mLon;
    
    public DeedData(long key, String title, String desc, double donation, String imgPath, double lat, double lon) {
        this.KEY_ID = key;
        this.mTitle = title;
        this.mDesc = desc;
        this.mDonation = donation;
        this.mImgUri = imgPath;
        this.mLat = lat;
        this.mLon = lon;
    }

}
