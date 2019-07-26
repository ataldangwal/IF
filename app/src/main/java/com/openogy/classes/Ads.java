package com.openogy.classes;

import android.content.Context;
import android.graphics.Bitmap;


public class Ads {

    public int AdId ;
    public String AdTitle;
    public String VendorPhoneNumber;
    public String AdCategory;
    public String AdLocality;
    public String AdCity;
    public String UserToken;
    public String AdImageOne;
    public Bitmap AdImageBitmap;
    public String AdDescription;
    public String AdSellingPrice;
    public String CreatedOn;
    public boolean IsActive;
    public String SoldDate;
    public boolean IsReported;
    public String VendorName;
    public String AdHighestBid;
    public boolean IsSold;
    public String DeletedDate;
    public boolean IsDeleted;
    public boolean IsEligibleForRepost;
    public String NoOfDemands;
    public boolean isDemanded;
    public String AdDivision;
    public String UserBadge;
    public boolean isDisplayPhoneNumber;


    public String isUserBadge() {
        return UserBadge;
    }

    public void setUserBadge(String userBadge) {
        UserBadge = userBadge;
    }

    public String getAdDivision() {
        return AdDivision;
    }

    public void setAdDivision(String adDivision) {
        AdDivision = adDivision;
    }

    public boolean isDemanded() {
        return isDemanded;
    }

    public void setDemanded(boolean demanded) {
        isDemanded = demanded;
    }

    public String getNoOfDemands() {

        return NoOfDemands;
    }

    public void setNoOfDemands(String noOfDemands) {
        this.NoOfDemands = noOfDemands;
    }

    public Bitmap getAdImageBitmap() {
        return AdImageBitmap;
    }

    public void setAdImageBitmap(Bitmap adImageBitmap) {
        AdImageBitmap = adImageBitmap;
    }

    public boolean isSold() {
        return IsSold;
    }

    public void setSold(boolean sold) {
        IsSold = sold;
    }

    public String getDeletedDate() {
        return DeletedDate;
    }

    public void setDeletedDate(String deletedDate) {
        DeletedDate = deletedDate;
    }

    public boolean isDeleted() {
        return IsDeleted;
    }

    public void setDeleted(boolean deleted) {
        IsDeleted = deleted;
    }

    public boolean isEligibleForRepost() {
        return IsEligibleForRepost;
    }

    public void setEligibleForRepost(boolean eligibleForRepost) {
        IsEligibleForRepost = eligibleForRepost;
    }

    public android.content.Context getContext() {
        return Context;
    }

    public void setContext(android.content.Context context) {
        Context = context;
    }

    public String getAdHighestBid() {
        return AdHighestBid;
    }

    public void setAdHighestBid(String adHighestBid) {
        AdHighestBid = adHighestBid;
    }

    public String getVendorName() {
        return VendorName;
    }

    public void setVendorName(String vendorName) {
        this.VendorName = vendorName;
    }

    public String getVendorPhoneNumber() {
        return VendorPhoneNumber;
    }

    public void setVendorPhoneNumber(String VendorPhoneNumber) {
        this.VendorPhoneNumber = VendorPhoneNumber;
    }


    public int getAdId() {
        return AdId;
    }

    public void setAdId(int adId) {
        AdId = adId;
    }

    public String getAdTitle() {
        return AdTitle;
    }

    public void setAdTitle(String adTitle) {
        AdTitle = adTitle;
    }

    public String getAdCategory() {
        return AdCategory;
    }

    public void setAdCategory(String adCategory) {
        AdCategory = adCategory;
    }

    public String getAdLocality() {
        return AdLocality;
    }

    public void setAdLocality(String adLocality) {
        AdLocality = adLocality;
    }

    public String getAdCity() {
        return AdCity;
    }

    public void setAdCity(String adCity) {
        AdCity = adCity;
    }

    public String getUserToken() {
        return UserToken;
    }

    public void setUserToken(String userToken) {
        UserToken = userToken;
    }

    public String getAdImageOne() {
        return AdImageOne;
    }

    public void setAdImageOne(String adImageOne) {
        AdImageOne = adImageOne;
    }

    public String getAdDescription() {
        return AdDescription;
    }

    public void setAdDescription(String adDescription) {
        AdDescription = adDescription;
    }

    public String getAdSellingPrice() {
        return AdSellingPrice;
    }

    public void setAdSellingPrice(String adSellingPrice) {
        AdSellingPrice = adSellingPrice;
    }

    public String getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(String createdOn) {
        CreatedOn = createdOn;
    }

    public boolean isActive() {
        return IsActive;
    }

    public void setActive(boolean active) {
        IsActive = active;
    }

    public String getSoldDate() {
        return SoldDate;
    }

    public void setSoldDate(String soldDate) {
        SoldDate = soldDate;
    }

    public boolean isReported() {
        return IsReported;
    }

    public void setReported(boolean reported) {
        IsReported = reported;
    }

    public Context Context;
}
