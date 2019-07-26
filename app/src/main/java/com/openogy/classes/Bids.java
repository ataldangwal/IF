package com.openogy.classes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Bids {

    public int BidId;
    public int AdId;
    public int BidAmount;
    public String BidDate;
    public boolean IsHighestBid;
    public boolean IsActive;
    public String UserToken;
    public String UserName;

    public int getBidId() {
        return BidId;
    }

    public void setBidId(int bidId) {
        BidId = bidId;
    }

    public int getAdId() {
        return AdId;
    }

    public void setAdId(int adId) {
        AdId = adId;
    }

    public int getBidAmount() {
        return BidAmount;
    }

    public void setBidAmount(int bidAmount) {
        BidAmount = bidAmount;
    }

    public String getBidDate() {
        return BidDate;
    }

    public void setBidDate(String bidDate) {
        BidDate = bidDate;
    }

    public boolean isHighestBid() {
        return IsHighestBid;
    }

    public void setHighestBid(boolean highestBid) {
        IsHighestBid = highestBid;
    }

    public boolean isActive() {
        return IsActive;
    }

    public void setActive(boolean active) {
        IsActive = active;
    }

    public String getUserToken() {
        return UserToken;
    }

    public void setUserToken(String userToken) {
        UserToken = userToken;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public static Bids GetBidFromJson(JSONObject jsonObject) {
        Bids bid = new Bids();
        // Deserialize json into object fields
        try {


            bid.BidId = jsonObject.getInt("BidId");
            bid.AdId = jsonObject.getInt("AdId");
            bid.BidAmount = jsonObject.getInt("BidAmount");
            bid.BidDate = jsonObject.getString("BidDate");
            bid.IsHighestBid = jsonObject.getBoolean("IsHighestBid");
            bid.IsActive = jsonObject.getBoolean("IsActive");
            bid.UserToken = jsonObject.getString("UserToken");
            bid.UserName = jsonObject.getString("UserName");




        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return bid;
    }

    public static List<Bids> GetBidListFromJson(JSONObject jsonObject) {
        List<Bids> bidList = null;
        Bids bid = new Bids();
        // Deserialize json into object fields
        try {
            ArrayList<Bids> arrayList = null;
            Gson gson = new Gson();
            Type collectionType = new TypeToken<Collection<Bids>>() {
            }.getType();
            bidList = gson.fromJson(jsonObject.toString(), collectionType);

            bid.BidAmount = jsonObject.getInt("BidAmount");

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return bidList;
    }
}
