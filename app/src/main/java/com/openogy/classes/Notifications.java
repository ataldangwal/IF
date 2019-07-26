package com.openogy.classes;

public class Notifications {

    public int NotificationId;
    public int AdOrWishId;
    public boolean isFirstBid;
    public boolean isHigherBid;
    public boolean isDemand;
    public boolean isFulfill;
    public boolean isCommentForAd;
    public boolean isCommentForWish;
    public String UserToken;
    public String UserName;
    public String UserPhoneNumber;
    public String NotificationDate;
    public boolean isRead;
    public String VendorName;
    public String VendorToken;
    public boolean isDisplayPhoneNumber;

    public String getVendorName() {
        return VendorName;
    }

    public void setVendorName(String vendorName) {
        VendorName = vendorName;
    }

    public String getVendorToken() {
        return VendorToken;
    }

    public void setVendorToken(String vendorToken) {
        VendorToken = vendorToken;
    }

    public int getNotificationId() {
        return NotificationId;
    }

    public void setNotificationId(int notificationId) {
        NotificationId = notificationId;
    }

    public int getAdOrWishId() {
        return AdOrWishId;
    }

    public void setAdOrWishId(int adOrWishId) {
        AdOrWishId = adOrWishId;
    }

    public boolean isFirstBid() {
        return isFirstBid;
    }

    public void setFirstBid(boolean firstBid) {
        isFirstBid = firstBid;
    }

    public boolean isHigherBid() {
        return isHigherBid;
    }

    public void setHigherBid(boolean higherBid) {
        isHigherBid = higherBid;
    }

    public boolean isDemand() {
        return isDemand;
    }

    public void setDemand(boolean demand) {
        isDemand = demand;
    }

    public boolean isFulfill() {
        return isFulfill;
    }

    public void setFulfill(boolean fulfill) {
        isFulfill = fulfill;
    }

    public boolean isCommentForAd() {
        return isCommentForAd;
    }

    public void setCommentForAd(boolean commentForAd) {
        isCommentForAd = commentForAd;
    }

    public boolean isCommentForWish() {
        return isCommentForWish;
    }

    public void setCommentForWish(boolean commentForWish) {
        isCommentForWish = commentForWish;
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

    public String getUserPhoneNumber() {
        return UserPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        UserPhoneNumber = userPhoneNumber;
    }

    public String getNotificationDate() {
        return NotificationDate;
    }

    public void setNotificationDate(String notificationDate) {
        NotificationDate = notificationDate;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
