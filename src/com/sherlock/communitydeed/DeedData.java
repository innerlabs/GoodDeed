package com.sherlock.communitydeed;

public class DeedData {
    
    public static final String TITLE = "deed_title";
    public static final String DESC = "deed_desc";
    public static final String DONATION = "deed_donation";
    public static final String IMGURI = "deed_imguri";
    public static final String LATITUDE = "deed_latitude";
    public static final String LONGITUDE = "deed_longitude";
    
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
