package com.openogy.classes;

import org.json.JSONException;
import org.json.JSONObject;

public class AdWithBids {

    public String AdTitle;
    public String AdImageOne;
    public String AdImageTwo;
    public String AdImageThree;
    public String AdImageFour;
    public String AdImageFive;
    public String SellerName;
    public String SellerLocality;
    public String SellingPrice;
    public String SecondBidAmount;
    public String  ThirdBidAmount;
    public String FirstBidAmount;
    public String AdDescription;
    public String NoOfBids;
    public String Date;
    public boolean isDisplayPhoneNumber;


    public String getAdTitle() {
        return AdTitle;
    }

    public void setAdTitle(String adTitle) {
        AdTitle = adTitle;
    }

    public String getAdImageOne() {
        return AdImageOne;
    }

    public void setAdImageOne(String adImageOne) {
        AdImageOne = adImageOne;
    }

    public String getAdImageTwo() {
        return AdImageTwo;
    }

    public void setAdImageTwo(String adImageTwo) {
        AdImageTwo = adImageTwo;
    }

    public String getAdImageThree() {
        return AdImageThree;
    }

    public void setAdImageThree(String adImageThree) {
        AdImageThree = adImageThree;
    }

    public String getAdImageFour() {
        return AdImageFour;
    }

    public void setAdImageFour(String adImageFour) {
        AdImageFour = adImageFour;
    }

    public String getAdImageFive() {
        return AdImageFive;
    }

    public void setAdImageFive(String adImageFive) {
        AdImageFive = adImageFive;
    }

    public String getSellerName() {
        return SellerName;
    }

    public void setSellerName(String sellerName) {
        SellerName = sellerName;
    }

    public String getSellerLocality() {
        return SellerLocality;
    }

    public void setSellerLocality(String sellerLocality) {
        SellerLocality = sellerLocality;
    }

    public String getSellingPrice() {
        return SellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        SellingPrice = sellingPrice;
    }

    public String getBidAmount() {
        return FirstBidAmount;
    }

    public void setBidAmount(String bidAmount) {
        FirstBidAmount = bidAmount;
    }

    public String getAdDescription() {
        return AdDescription;
    }

    public void setAdDescription(String adDescription) {
        AdDescription = adDescription;
    }

    public static AdWithBids GetBidFromJson(JSONObject jsonObject) {
        AdWithBids bid = new AdWithBids();
        // Deserialize json into object fields
        try {
            bid.AdTitle = jsonObject.getString("AdTitle");
            bid.AdImageOne = jsonObject.getString("AdImageOne");
            bid.AdImageTwo = jsonObject.getString("AdImageTwo");
            bid.AdImageThree = jsonObject.getString("AdImageThree");
            bid.AdImageFour = jsonObject.getString("AdImageFour");
            bid.AdImageFive = jsonObject.getString("AdImageFive");
            bid.SellerName = jsonObject.getString("SellerName");
            bid.SellerLocality = jsonObject.getString("SellerLocality");
            bid.SellingPrice = jsonObject.getString("SellingPrice");
            bid.FirstBidAmount = jsonObject.getString("FirstBidAmount");
            bid.SecondBidAmount = jsonObject.getString("SecondBidAmount");
            bid.ThirdBidAmount = jsonObject.getString("ThirdBidAmount");
            bid.AdDescription = jsonObject.getString("AdDescription");
            bid.isDisplayPhoneNumber = jsonObject.getBoolean("isDisplayPhoneNumber");
            bid.NoOfBids = jsonObject.getString("NoOfBids");
            bid.Date = jsonObject.getString("Date");



        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return bid;
    }
}
