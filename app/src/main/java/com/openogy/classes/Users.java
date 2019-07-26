package com.openogy.classes;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

public class Users{

    public int Userid;
    public String login_token;
    public String FullName;
    public String Email;
    public String Password;
    public String City;
    public String PhoneNumber;
    public String Rating;
    public String NumberOfBids;
    public String NumberOfSales ;
    public boolean IsActive;
    public String IsUpdated;
    public String CreatedOn;
    public String LastLogin;
    public boolean IsLoggedIn;
    public String UserProfileImage;
    public Bitmap UserImageBitmap;
    public String UserBadge;
    public String FeedbackRating;

    public Bitmap getUserImageBitmap() {
        return UserImageBitmap;
    }

    public void setUserImageBitmap(Bitmap userImageBitmap) {
        UserImageBitmap = userImageBitmap;
    }

    public String getUserBadge() {
        return UserBadge;
    }

    public void setUserBadge(String userBadge) {
        UserBadge = userBadge;
    }

    public int getUserid() {
        return Userid;
    }

    public void setUserid(int userid) {
        Userid = userid;
    }

    public String getLogin_token() {
        return login_token;
    }

    public void setLogin_token(String login_token) {
        this.login_token = login_token;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public String getNumberOfBids() {
        return NumberOfBids;
    }

    public void setNumberOfBids(String numberOfBids) {
        NumberOfBids = numberOfBids;
    }

    public String getNumberOfSales() {
        return NumberOfSales;
    }

    public void setNumberOfSales(String numberOfSales) {
        NumberOfSales = numberOfSales;
    }

    public boolean isActive() {
        return IsActive;
    }

    public void setActive(boolean active) {
        IsActive = active;
    }

    public String getIsUpdated() {
        return IsUpdated;
    }

    public void setIsUpdated(String isUpdated) {
        IsUpdated = isUpdated;
    }

    public String getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(String createdOn) {
        CreatedOn = createdOn;
    }

    public String getLastLogin() {
        return LastLogin;
    }

    public void setLastLogin(String lastLogin) {
        LastLogin = lastLogin;
    }

    public boolean isLoggedIn() {
        return IsLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        IsLoggedIn = loggedIn;
    }

    public String getUserProfileImage() {
        return UserProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        UserProfileImage = userProfileImage;
    }

    public static Users GetUserFromJson(JSONObject jsonObject) {
        Users user = new Users();
        // Deserialize json into object fields
        try {

            user.login_token = jsonObject.getString("login_token");
            user.FullName = jsonObject.getString("FullName");
            user.Email  = jsonObject.getString("Email");

            user.City = jsonObject.getString("City");
            user.PhoneNumber = jsonObject.getString("PhoneNumber");
            user.Rating = jsonObject.getString("Rating");
            user.NumberOfBids = jsonObject.getString("NumberOfBids");

            user.NumberOfSales = jsonObject.getString("NumberOfSales");
            user.UserProfileImage = jsonObject.getString("UserProfileImage");
            user.UserBadge = jsonObject.getString("UserBadge");
            user.FeedbackRating = jsonObject.getString("FeedbackRating");


        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return user;
    }


}
