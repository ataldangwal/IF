package com.openogy.classes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.openogy.www.anif.R;
import com.openogy.www.anif.SingleAdDisplayActivity;
import com.openogy.www.anif.SingleWishDisplayActivity;
import com.openogy.www.anif.User_Profile_Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class NotificationsAdapter extends ArrayAdapter<Ads> implements Serializable {
    private ArrayList<Notifications> notifications;
    private static LayoutInflater inflater = null;
    private Context context;
    private ViewHolder holder;
    Dialog dialogForDelete;
    View vi;
    Globals g = Globals.getInstance();

    public NotificationsAdapter(Activity activity, int textViewResourceId, ArrayList<Notifications> _allNotifications, Context context) {
        super(activity, textViewResourceId);

        try {
            this.context = context;
            this.notifications = _allNotifications;
            dialogForDelete = new Dialog(getContext());
            g.setNotificationData(_allNotifications);

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {

        }
    }

    public void addListItemToAdapter(ArrayList<Notifications> list) {
        //add list to current arraylist of data
        notifications.addAll(list);
        g.setNotificationData(notifications);

        //Notify UI
        this.notifyDataSetChanged();
    }


    public int getCount() {
        return notifications.size();
    }

    public Notifications getItem(Notifications position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView display_notificationText;
        public int position;
        public LinearLayout LinearLayoutSingleNotification;
        public ImageButton ImageButtonCall;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        vi = convertView;

        try {
            if (convertView == null) {

                vi = inflater.inflate(R.layout.layout_single_notifications, null);
                holder = new ViewHolder();

                holder.display_notificationText = vi.findViewById(R.id.tvNotificationtext);
                holder.LinearLayoutSingleNotification = vi.findViewById(R.id.LinearLayoutSingleNotification);
                holder.ImageButtonCall = vi.findViewById(R.id.ImageButtonCall);


                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }

            if(notifications.get(position).isDisplayPhoneNumber)
                holder.ImageButtonCall.setVisibility(View.VISIBLE);
            else if(!notifications.get(position).isDisplayPhoneNumber)
                holder.ImageButtonCall.setVisibility(View.GONE);

            String NotificationText = "";
            if(notifications.get(position).isCommentForAd)
            {
                NotificationText = notifications.get(position).UserName + " " + context.getString(R.string.notification_commentForAd);
                holder.ImageButtonCall.setVisibility(View.GONE);
            }


            if(notifications.get(position).isCommentForWish)
            {
                NotificationText = notifications.get(position).UserName + " " + context.getString(R.string.notification_commentForWish);
                holder.ImageButtonCall.setVisibility(View.GONE);
            }


            if(notifications.get(position).isDemand)
            {
                NotificationText = notifications.get(position).UserName + " " + context.getString(R.string.notification_demand);
                holder.ImageButtonCall.setVisibility(View.VISIBLE);
            }


            if(notifications.get(position).isFulfill)
            {
                holder.ImageButtonCall.setVisibility(View.VISIBLE);
                NotificationText = notifications.get(position).UserName + " " + context.getString(R.string.notification_fulfill);
            }


            if(notifications.get(position).isFirstBid)
                NotificationText = notifications.get(position).UserName + " " + context.getString(R.string.notification_first_bid);

            if(notifications.get(position).isHigherBid)
                NotificationText = notifications.get(position).UserName + " " + context.getString(R.string.notification_higher_bid);

            SpannableString notificationString = new SpannableString(NotificationText);
            ClickableSpan userNameSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {

                    //Mark notification as read
                    new MarkNotificationReadAsyncTask(notifications.get(position).NotificationId).execute();

                    Intent intent = new Intent(context,User_Profile_Activity.class);
                    intent.putExtra("Token", notifications.get(position).UserToken);
                    context.startActivity(intent);
                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                    ds.setColor(Color.BLACK);
                }
            };
            notificationString.setSpan(userNameSpan, 0,notifications.get(position).UserName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            notificationString.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), 0,notifications.get(position).UserName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            ClickableSpan adSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {

                    //Mark notification as read
                    new MarkNotificationReadAsyncTask(notifications.get(position).NotificationId).execute();

                    if(notifications.get(position).isCommentForAd || notifications.get(position).isDemand || notifications.get(position).isFirstBid || notifications.get(position).isHigherBid)
                    {
                        Intent adIntent = new Intent(context,SingleAdDisplayActivity.class);
                        adIntent.putExtra("AdId", notifications.get(position).AdOrWishId);
                        adIntent.putExtra("PhoneNumber", notifications.get(position).UserPhoneNumber);
                        adIntent.putExtra("Position", position);
                        adIntent.putExtra("Token",notifications.get(position).VendorToken);
                        context.startActivity(adIntent);
                    }
                    else if(notifications.get(position).isCommentForWish || notifications.get(position).isFulfill)
                    {
                        Intent wishIntent = new Intent(context,SingleWishDisplayActivity.class);
                        wishIntent.putExtra("WishId", notifications.get(position).AdOrWishId);
                        wishIntent.putExtra("PhoneNumber",notifications.get(position).UserPhoneNumber);
                        wishIntent.putExtra("Token",notifications.get(position).VendorToken);
                        context.startActivity(wishIntent);
                    }
                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                    ds.setColor(Color.BLACK);
                }
            };
            notificationString.setSpan(adSpan, NotificationText.indexOf("has") + 3,NotificationText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            notificationString.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), NotificationText.indexOf("your") + 4,NotificationText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.display_notificationText.setText(notificationString);
            holder.display_notificationText.setMovementMethod(LinkMovementMethod.getInstance());
            holder.display_notificationText.setHighlightColor(Color.TRANSPARENT);

            if(notifications.get(position).isRead)
                vi.setBackgroundColor(Color.parseColor("#FFFFFF"));
            else
                vi.setBackgroundColor(Color.parseColor("#FFFFFF"));

            holder.position = position;

            ImageButton callImageButton = vi.findViewById(R.id.ImageButtonCall);

            //Call Button Click Event
            callImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //Mark notification as read
                    new MarkNotificationReadAsyncTask(notifications.get(position).NotificationId).execute();

                    // Use format with "tel:" and phone number to create phoneNumber.
                    String phoneNumber = String.format("tel: %s",
                            notifications.get(position).UserPhoneNumber);
                    // Create the intent.
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                    // Set the data for the intent as the phone number.
                    dialIntent.setData(Uri.parse(phoneNumber));
                    // If package resolves to an app, send intent.
                    if (dialIntent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(dialIntent);
                    } else {
                        Log.e(TAG, "Can't resolve app for ACTION_DIAL Intent.");
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();

        }
        return vi;
    }

    public class MarkNotificationReadAsyncTask extends AsyncTask<Integer, Void, String> {

        private int notificationId;



        private MarkNotificationReadAsyncTask(int notificationId) {
            this.notificationId = notificationId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... params) {


            try {
                String urlAPI = CommonFunctions.API_URL + "Notifications/MarkNotificationRead";
                URL url = new URL(urlAPI);
                //Create URL connection
                HttpURLConnection conn = null;
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                //Create Json Object
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("NotificationId",notificationId);

                //Post json content
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();

                return conn.getResponseMessage();

            } catch (ProtocolException e) {
                return e.toString();
            } catch (IOException e) {
                return e.toString();
            } catch (JSONException e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }


}
