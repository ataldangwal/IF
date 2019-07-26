package com.openogy.classes;

import java.util.ArrayList;

public class Globals{
    private static Globals instance;

    // Global variable
    private String data;
    private ArrayList<Ads> adsArrayList;
    private boolean isNewBid = false;
    private boolean isBackPressed = false;
    private boolean isNewNotification;
    private ArrayList<Notifications> notifications;
    private int categorySubmenuCount = 0;

    // Restrict the constructor from being instantiated
    private Globals(){}

    public void setData(String d){
        this.data=d;
    }
    public String getData(){
        return this.data;
    }

    public void setCategorySubmenuCount(int d){
        this.categorySubmenuCount=d;
    }
    public int getCategorySubmenuCount(){
        return this.categorySubmenuCount;
    }

    public void setFlagForNewBid(boolean value) {this.isNewBid = value;}
    public boolean getFlagForNewBid() {return this.isNewBid;}


    public void setFlagForNewNotification(boolean value) {this.isNewNotification = value;}
    public boolean getFlagForNewNotification() {return this.isNewNotification;}

    public void setFlagForBackPressed(boolean value) {this.isBackPressed = value;}
    public boolean getFlagForBackPressed() {return this.isBackPressed;}

    public void setArrayData(ArrayList<Ads> d){
        this.adsArrayList=d;
    }
    public ArrayList<Ads> getArrayData(){
        return this.adsArrayList;
    }

    public void setNotificationData(ArrayList<Notifications> d){
        this.notifications=d;
    }
    public ArrayList<Notifications> getNotificationData(){
        return this.notifications;
    }

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }
}
