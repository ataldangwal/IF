package com.openogy.classes;

import android.graphics.Bitmap;

public class Wishes {

    public int WishId;
    public String WishDescription;
    public String WishCategory;
    public String WishLocality;
    public String WishCity;
    public String WishImage;
    public Bitmap WishImageBitmap;
    public String UserName;
    public String UserToken;
    public String CreatedOn;
    public boolean IsActive;
    public boolean IsReported;
    public String UserPhoneNumber;
    public String NoOfFulfill;
    public String WishDivision;
    public boolean isFullfill;
    public String UserBadge;
    public boolean isDisplayPhoneNumber;

    public String getWishDivision() {
        return WishDivision;
    }

    public void setWishDivision(String wishDivision) {
        WishDivision = wishDivision;
    }

    public Bitmap getWishImageBitmap() {
        return WishImageBitmap;
    }

    public void setWishImageBitmap(Bitmap wishImageBitmap) {
        WishImageBitmap = wishImageBitmap;
    }

    public String getNoOfFulfill() {
        return NoOfFulfill;
    }

    public void setNoOfFulfill(String noOfFulfill) {
        NoOfFulfill = noOfFulfill;
    }

    public boolean isFullFill() {
        return isFullfill;
    }

    public void setFullFill(boolean fullFill) {
        isFullfill = fullFill;
    }



    public String getUserPhoneNumber() {
        return UserPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        UserPhoneNumber = userPhoneNumber;
    }

    public int getWishId() {
        return WishId;
    }

    public void setWishId(int wishId) {
        WishId = wishId;
    }

    public String getWishDescription() {
        return WishDescription;
    }

    public void setWishDescription(String wishDescription) {
        WishDescription = wishDescription;
    }

    public String getWishCategory() {
        return WishCategory;
    }

    public void setWishCategory(String wishCategory) {
        WishCategory = wishCategory;
    }

    public String getWishLocality() {
        return WishLocality;
    }

    public void setWishLocality(String wishLocality) {
        WishLocality = wishLocality;
    }

    public String getWishCity() {
        return WishCity;
    }

    public void setWishCity(String wishCity) {
        WishCity = wishCity;
    }

    public String getWishImage() {
        return WishImage;
    }

    public void setWishImage(String wishImage) {
        WishImage = wishImage;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserToken() {
        return UserToken;
    }

    public void setUserToken(String userToken) {
        UserToken = userToken;
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

    public boolean isReported() {
        return IsReported;
    }

    public void setReported(boolean reported) {
        IsReported = reported;
    }

    public String getUserBadge() {
        return UserBadge;
    }

    public void setUserBadge(String userBadge) {
        UserBadge = userBadge;
    }

    public boolean isDisplayPhoneNumber() {
        return isDisplayPhoneNumber;
    }

    public void setDisplayPhoneNumber(boolean displayPhoneNumber) {
        isDisplayPhoneNumber = displayPhoneNumber;
    }
}
