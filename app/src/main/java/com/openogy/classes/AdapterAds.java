package com.openogy.classes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.openogy.www.anif.R;
import com.openogy.www.anif.SingleAdDisplayActivity;
import com.openogy.www.anif.User_Profile_Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class AdapterAds extends ArrayAdapter<Ads> implements Serializable {
    private Activity activity;
    private ArrayList<Ads> Ads;
    private static LayoutInflater inflater = null;
    private Context context;
    private Dialog dialogForMakingBid;
    private Dialog dialogForReport;
    private String highestBid;
    private ViewHolder holder;

    public AdapterAds(Activity activity, int textViewResourceId, ArrayList<Ads> _allAds, Context context) {
        super(activity, textViewResourceId, _allAds);

        try {
            this.context = context;
            this.activity = activity;
            this.Ads = _allAds;
            dialogForMakingBid = new Dialog(context);
            dialogForReport = new Dialog(context);
            highestBid = "";

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Globals g = Globals.getInstance();

        } catch (Exception e) {

        }
    }

    public void addListItemToAdapter(ArrayList<Ads> list) {
        //add list to current arraylist of data
        Ads.addAll(list);

        //Notify UI
        this.notifyDataSetChanged();
    }

    public void updateResults() {

        //Triggers the list update
        this.notifyDataSetChanged();
    }

    public void UpdateListItemToAdapter(String highestBid, int position) {
        //add list to current arraylist of data
        Ads ad = Ads.get(position);
        ad.setAdHighestBid(highestBid);

        //Notify UI
        this.notifyDataSetChanged();
    }

    public int getCount() {
        return Ads.size();
    }

    public Ads getItem(Ads position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView display_name;
        public TextView display_title;
        public TextView display_sellingPrice;
        public TextView display_highestBid;
        public TextView display_locality;
        public ImageView display_adImage;
        public ProgressBar ImageProgressbar;
        public int position;
        public Button btnDemand;
        public TextView display_noOfDemands;
        public TextView txtCall, txtBid, txtReport;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        try {
            if (convertView == null) {

                vi = inflater.inflate(R.layout.layout_singlead, null);
                holder = new ViewHolder();

                holder.display_name = vi.findViewById(R.id.tvVendorName);
                holder.display_locality = vi.findViewById(R.id.tvLocality);
                holder.display_title = vi.findViewById(R.id.tvAdTitle);
                holder.display_sellingPrice = vi.findViewById(R.id.tvSellingPrice);
                holder.display_highestBid = vi.findViewById(R.id.tvHighestBid);
                holder.display_adImage = vi.findViewById(R.id.IvAdImage);
                holder.ImageProgressbar = vi.findViewById(R.id.ImageProgressbar);
                holder.btnDemand = vi.findViewById(R.id.btnDemand);
                holder.display_noOfDemands = vi.findViewById(R.id.tvNoOfDemands);
                holder.txtBid = vi.findViewById(R.id.txtBid);
                holder.txtCall = vi.findViewById(R.id.txtCall);
                holder.txtReport = vi.findViewById(R.id.txtReport);

                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }

            if((Ads.get(position).UserBadge).equals("0"))
                holder.display_name.setTextColor(Color.parseColor("#000000"));
            else if((Ads.get(position).UserBadge).equals("1"))
                holder.display_name.setTextColor(Color.parseColor("#6C541E"));
            else if((Ads.get(position).UserBadge).equals("2"))
                holder.display_name.setTextColor(Color.parseColor("#808080"));
            else if((Ads.get(position).UserBadge).equals("3"))
                holder.display_name.setTextColor(Color.parseColor("#DAA520"));
            else if((Ads.get(position).UserBadge).equals("4"))
                holder.display_name.setTextColor(Color.parseColor("#2E75F9"));



            holder.display_name.setText(Ads.get(position).VendorName);
            holder.display_locality.setText(Ads.get(position).AdLocality);
            holder.display_title.setText(Ads.get(position).AdTitle);

            if((Ads.get(position).AdSellingPrice).equals("0"))
                holder.display_sellingPrice.setText(R.string.txt_no_selling_price);
            else
                holder.display_sellingPrice.setText(getFormatedAmount(Integer.parseInt(Ads.get(position).AdSellingPrice)));
            holder.display_highestBid.setText((Ads.get(position).AdHighestBid).equals("0") ? "No bid" : getFormatedAmount(Integer.parseInt(Ads.get(position).AdHighestBid)));
            holder.display_noOfDemands.setText((Ads.get(position).NoOfDemands).equals("0") ? " " : Ads.get(position).NoOfDemands);
            //byte[] bytes = android.util.Base64.decode((Ads.get(position).AdImageOne), 0);
            holder.ImageProgressbar.setVisibility(View.GONE);
            holder.display_adImage.setImageBitmap(Ads.get(position).AdImageBitmap);
            holder.position = position;
            holder.btnDemand.setBackgroundResource(R.drawable.roundbuttonempty);
            holder.btnDemand.setTextColor(Color.parseColor("#00BAED"));

            if(Ads.get(position).isDemanded)
            {
               // holder.btnDemand.setBackgroundColor(Color.parseColor("#DC143C"));
                holder.btnDemand.setBackgroundResource(R.drawable.roundbuttonfilled);
                holder.btnDemand.setTextColor(Color.parseColor("#FFFFFF"));
                holder.display_noOfDemands.setTextColor(Color.parseColor("#00BAED"));
            }

            else
            {
                holder.display_noOfDemands.setTextColor(Color.parseColor("#00BAED"));
            }

            //new GetImageTask(bytes, position, holder).execute(holder);

            ImageButton callImageButton = vi.findViewById(R.id.ImageButtonCall);
            ImageButton bidImageButton = vi.findViewById(R.id.ImageButtonBid);
            ImageButton reportImageButton = vi.findViewById(R.id.ImageButtonReport);



            Globals g = Globals.getInstance();

            if(Ads.get(position).UserToken.equals(g.getData()))
            {
                callImageButton.setVisibility(View.GONE);
                bidImageButton.setVisibility(View.GONE);
                reportImageButton.setVisibility(View.GONE);
                holder.txtBid.setVisibility(View.GONE);
                holder.txtCall.setVisibility(View.GONE);
                holder.txtReport.setVisibility(View.GONE);
            }
            else if(Ads.get(position).isDisplayPhoneNumber)
            {
                callImageButton.setVisibility(View.VISIBLE);
                bidImageButton.setVisibility(View.VISIBLE);
                reportImageButton.setVisibility(View.VISIBLE);
                holder.txtBid.setVisibility(View.VISIBLE);
                holder.txtCall.setVisibility(View.VISIBLE);
                holder.txtReport.setVisibility(View.VISIBLE);
            }
            else if(!Ads.get(position).isDisplayPhoneNumber)
            {
                callImageButton.setVisibility(View.GONE);
                bidImageButton.setVisibility(View.VISIBLE);
                reportImageButton.setVisibility(View.VISIBLE);
                holder.txtBid.setVisibility(View.VISIBLE);
                holder.txtCall.setVisibility(View.GONE);
                holder.txtReport.setVisibility(View.VISIBLE);

            }

            //Go to profile if name is clicked
            holder.display_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,User_Profile_Activity.class);
                    intent.putExtra("Token", Ads.get(position).UserToken);
                    context.startActivity(intent);
                }
            });

            holder.display_adImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   Intent intent = new Intent(context,SingleAdDisplayActivity.class);
                    intent.putExtra("AdId", Ads.get(position).AdId);
                    intent.putExtra("PhoneNumber", Ads.get(position).VendorPhoneNumber);
                    intent.putExtra("Position", position);
                    intent.putExtra("Token",Ads.get(position).UserToken);
                    Globals g = Globals.getInstance();
                    g.setArrayData(Ads);
                    context.startActivity(intent);
                }
            });

            holder.btnDemand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AddOrRemoveAdDemandTask(position,holder).execute();
                }
            });


            //Call Button Click Event
            callImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Use format with "tel:" and phone number to create phoneNumber.
                    String phoneNumber = String.format("tel: %s",
                            Ads.get(position).VendorPhoneNumber);
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

            //Make Bid Button Click Event
            bidImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialogForMakingBid.setContentView(R.layout.bid_popup);
                    // dialog.setCancelable(false);
                    final EditText editText = dialogForMakingBid.findViewById(R.id.editTextBidAmount);
                    Button btnSubmit = dialogForMakingBid.findViewById(R.id.btnSubmit);
                    final TextView txtResult = dialogForMakingBid.findViewById(R.id.txtResult);
                    final CheckBox displayPhoneNumbercheckbox = dialogForMakingBid.findViewById(R.id.displayPhoneNumbercheckbox);
                    txtResult.setText("");
                    btnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (editText.getText().toString().trim().isEmpty())
                                txtResult.setText(R.string.error_bid);
                            else {
                                new MakeBidTask(editText.getText().toString().trim(), position, (displayPhoneNumbercheckbox.isChecked())).execute();
                            }
                        }
                    });
                    Button btnCancel = dialogForMakingBid.findViewById(R.id.btnCancel);
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogForMakingBid.dismiss();
                        }
                    });

                    dialogForMakingBid.show();
                }
            });


            //Report Button Click Event
            reportImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialogForReport.setContentView(R.layout.report_popup);
                    //dialog.setCancelable(false);
                    final EditText editText = dialogForReport.findViewById(R.id.editTextReason);
                    Button btnSubmit = dialogForReport.findViewById(R.id.btnSubmit);
                    final TextView txtResult = dialogForReport.findViewById(R.id.txtResult);
                    txtResult.setText("");
                    btnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (editText.getText().toString().trim().isEmpty())
                                txtResult.setText(R.string.error_report);
                            else {
                                new SubmitReportTask(editText.getText().toString().trim()).execute(position);

                            }
                        }
                    });
                    Button btnCancel = dialogForReport.findViewById(R.id.btnCancel);
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogForReport.dismiss();
                        }
                    });

                    dialogForReport.show();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();

        }
        return vi;
    }

                         /*  May be used later   */

    /*public class GetImageTask extends AsyncTask<ViewHolder, Void, Bitmap> {
        private final byte[] adImage;
        private final int position;

        private ViewHolder v;

        private GetImageTask(byte[] adImage, int position, ViewHolder v) {
            this.adImage = adImage;
            this.position = position;
            this.v = v;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            v.ImageProgressbar.setVisibility(View.VISIBLE);
            v.display_adImage.setVisibility(View.GONE);
        }

        @Override
        protected Bitmap doInBackground(ViewHolder... params) {
            v = params[0];
            Bitmap bitmap = BitmapFactory.decodeByteArray(adImage, 0, adImage.length);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (v.position == position) {
                // If this item hasn't been recycled already, hide the
                // progress and set and show the image
                v.ImageProgressbar.setVisibility(View.GONE);
                v.display_adImage.setVisibility(View.VISIBLE);
                v.display_adImage.setImageBitmap(result);
            }
        }
    }*/

    public class SubmitReportTask extends AsyncTask<Integer, Void, String> {

        private final String reportReason;
        private int position;

        private Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black);

        private SubmitReportTask(String reportReason) {
            this.reportReason = reportReason;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_progressbar, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(view);
            dialog.show();
        }

        @Override
        protected String doInBackground(Integer... params) {

            position = params[0];
            try {
                String urlAPI = CommonFunctions.API_URL + "Ad/SubmitReportForAds";
                URL url = new URL(urlAPI);
                //Create URL connection
                HttpURLConnection conn = null;
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                //Create Json Object
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("ReportReason", reportReason);
                jsonObject.accumulate("AdId", Ads.get(position).AdId);
                Globals g = Globals.getInstance();
                String token = g.getData();
                jsonObject.accumulate("Token", token);

                //Post json content
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonObject.toString());
                Log.i("Report Reason", jsonObject.toString());
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

            if (s.equalsIgnoreCase("ok"))
                Toast.makeText(context, "Report submitted", Toast.LENGTH_SHORT).show();

            if (dialog.isShowing()) {
                // Dismiss/hide the progress dialog
                dialog.dismiss();
            }

            if(dialogForReport.isShowing())
            {
                dialogForReport.dismiss();
            }
        }
    }

    public class MakeBidTask extends AsyncTask<Integer, Void, String> {

        private final String bidAmount;
        private final int position;
        private final boolean isDisplayPhoneNumber;

        private Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black);

        private MakeBidTask(String bidAmount, int position, boolean isDisplayPhoneNumber) {
            this.bidAmount = bidAmount;
            this.position = position;
            this.isDisplayPhoneNumber = isDisplayPhoneNumber;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_progressbar, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(view);
            dialog.show();
        }

        @Override
        protected String doInBackground(Integer... params) {

            try {
                String urlAPI = CommonFunctions.API_URL + "Ad/MakeBidForAds";
                URL url = new URL(urlAPI);
                //Create URL connection
                HttpURLConnection conn = null;
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                //Create Json Object
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("BidAmount", bidAmount);
                jsonObject.accumulate("AdId", Ads.get(position).AdId);
                jsonObject.accumulate("isDisplayPhoneNumber",isDisplayPhoneNumber);
                Globals g = Globals.getInstance();
                String token = g.getData();
                jsonObject.accumulate("Token", token);

                //Post json content
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonObject.toString());
                Log.i("Bid Amount", jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();

                InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(line);
                }

                highestBid = stringBuilder.toString();
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

            if (s.equalsIgnoreCase("ok"))
            {
                if (!highestBid.isEmpty())
                {
                    Ads ads =  Ads.get(position);
                    ads.setAdHighestBid(highestBid.trim());
                    notifyDataSetChanged();

                }
                Toast.makeText(context, "Bid Placed", Toast.LENGTH_SHORT).show();
            }

            else
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();

            if (dialog.isShowing()) {
                // Dismiss/hide the progress dialog
                dialog.dismiss();
            }

            if(dialogForMakingBid.isShowing())
            {
                dialogForMakingBid.dismiss();
            }
        }
    }

    public class AddOrRemoveAdDemandTask extends AsyncTask<Integer, Void, String> {

        private final int position;
        private ViewHolder holder;

        //private Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black);

        private AddOrRemoveAdDemandTask(int position, ViewHolder holder) {
            this.position = position;
            this.holder = holder;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_progressbar_wishpost, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(view);
            dialog.show();*/
        }

        @Override
        protected String doInBackground(Integer... params) {

            try {
                String urlAPI = CommonFunctions.API_URL + "Ad/AddOrRemoveAdDemand";
                URL url = new URL(urlAPI);
                //Create URL connection
                HttpURLConnection conn = null;
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                //Create Json Object
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("AdId", Ads.get(position).AdId);
                Globals g = Globals.getInstance();
                String token = g.getData();
                jsonObject.accumulate("Token", token);

                //Post json content
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();

                InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(line);
                }

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

            if (s.equalsIgnoreCase("ok"))
            {
                Ads ads =  Ads.get(position);
                int noOfDemands = Integer.parseInt(ads.getNoOfDemands()) ;
                String newNoOfDemand = String.valueOf(noOfDemands + 1);
                ads.setNoOfDemands(newNoOfDemand);
                ads.setDemanded(true);
                notifyDataSetChanged();
            }

            else if(s.equalsIgnoreCase("Moved Permanently"))
            {
                Ads ads =  Ads.get(position);
                int noOfDemands = Integer.parseInt(ads.getNoOfDemands());
                String newNoOfDemand = String.valueOf(noOfDemands - 1);
                ads.setNoOfDemands(newNoOfDemand);
                ads.setDemanded(false);
                notifyDataSetChanged();
            }
            else
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();


            /*if (dialog.isShowing()) {
                // Dismiss/hide the progress dialog
                dialog.dismiss();
            }*/
        }
    }

    private String getFormatedAmount(int amount){
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(amount);
    }


    }
