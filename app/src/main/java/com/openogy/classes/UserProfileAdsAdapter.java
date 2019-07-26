package com.openogy.classes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.openogy.www.anif.R;
import com.openogy.www.anif.SingleAdDisplayActivity;

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
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UserProfileAdsAdapter extends ArrayAdapter<Ads> implements Serializable {
    private Activity activity;
    private ArrayList<Ads> Ads;
    private static LayoutInflater inflater = null;
    private Context context;
    private Dialog dialogForMakingBid;
    private Dialog dialogForReport;
    private String highestBid;
    private ViewHolder holder;
    Dialog dialogForSold;
    Dialog dialogForDelete;
    Dialog dialogForRepost;

    public UserProfileAdsAdapter(Activity activity, int textViewResourceId, ArrayList<Ads> _allAds, Context context) {
        super(activity, textViewResourceId, _allAds);

        try {
            this.context = context;
            this.activity = activity;
            this.Ads = _allAds;
            dialogForMakingBid = new Dialog(context);
            dialogForReport = new Dialog(context);
            highestBid = "";
            dialogForSold = new Dialog(getContext());
            dialogForDelete = new Dialog(getContext());
            dialogForRepost = new Dialog(getContext());

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
        public TextView display_title;
        public TextView display_sellingPrice;
        public TextView display_highestBid;
        public TextView display_locality;
        public ImageView display_adImage;
        public TextView display_Date;
        public int position;
        public TextView display_tvNoOfDemands;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        try {
            if (convertView == null) {

                vi = inflater.inflate(R.layout.user_profile_ads_layout, null);
                holder = new ViewHolder();

                holder.display_locality = vi.findViewById(R.id.tvLocality);
                holder.display_title = vi.findViewById(R.id.tvAdTitle);
                holder.display_sellingPrice = vi.findViewById(R.id.tvSellingPrice);
                holder.display_highestBid = vi.findViewById(R.id.tvHighestBid);
                holder.display_adImage = vi.findViewById(R.id.IvAdImage);
                holder.display_Date = vi.findViewById(R.id.tvDateTime);
                holder.display_tvNoOfDemands = vi.findViewById(R.id.tvNoOfDemands);


                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }

            holder.display_locality.setText(Ads.get(position).AdLocality);
            holder.display_title.setText(Ads.get(position).AdTitle);
            if(!(Ads.get(position).NoOfDemands).equals("0"))
                holder.display_tvNoOfDemands.setText(context.getText(R.string.txt_number_of_demands) + " : "  +    Ads.get(position).NoOfDemands);

            if((Ads.get(position).AdSellingPrice).equals("0"))
                holder.display_sellingPrice.setText(context.getText(R.string.txt_no_selling_price));
            else
                holder.display_sellingPrice.setText(getFormatedAmount(Integer.parseInt(Ads.get(position).AdSellingPrice)));
            holder.display_highestBid.setText((Ads.get(position).AdHighestBid).equals("0") ? "No bid" : getFormatedAmount(Integer.parseInt(Ads.get(position).AdHighestBid)));
            holder.display_adImage.setImageBitmap(Ads.get(position).AdImageBitmap);


            //Get date from list
            String date = Ads.get(position).CreatedOn;

            //Specify the get date format
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date updateLast = format.parse(date);

            //Convert into desired format
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
            String dateTime = dateFormat.format(updateLast);


            holder.display_Date.setText(dateTime);
            holder.position = position;

            holder.display_adImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SingleAdDisplayActivity.class);
                    intent.putExtra("AdId", Ads.get(position).AdId);
                    intent.putExtra("PhoneNumber", Ads.get(position).VendorPhoneNumber);
                    intent.putExtra("Position", position);
                    intent.putExtra("Token",Ads.get(position).UserToken);
                    Globals g = Globals.getInstance();
                    g.setArrayData(Ads);
                    context.startActivity(intent);
                }
            });

            Button btnRepost = vi.findViewById(R.id.btnRepost);

            if (Ads.get(position).IsEligibleForRepost == false) {
                btnRepost.setVisibility(View.INVISIBLE);


            } else {
                btnRepost.setVisibility(View.VISIBLE);
            }


            Button btnSold = vi.findViewById(R.id.btnSold);
            Button btnDelete = vi.findViewById(R.id.btnDelete);

            btnRepost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogForRepost.setContentView(R.layout.areyousure_repost_popup);
                    Button btnYes = dialogForRepost.findViewById(R.id.btnYes);
                    Button btnNo = dialogForRepost.findViewById(R.id.btnNo);
                    dialogForRepost.show();

                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new RepostSelectedAdAsyncTask(position).execute();
                        }
                    });

                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogForRepost.dismiss();
                        }
                    });
                }
            });

            btnSold.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialogForSold.setContentView(R.layout.areyousure_sold_popup);
                    Button btnYes = dialogForSold.findViewById(R.id.btnYes);
                    Button btnNo = dialogForSold.findViewById(R.id.btnNo);
                    final EditText SellingPriceEditText  = dialogForSold.findViewById(R.id.SellingPriceEditText);
                    dialogForSold.show();

                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(SellingPriceEditText.getText().toString().trim().equals(""))
                                Toast.makeText(context, "Selling price required", Toast.LENGTH_SHORT).show();
                            else
                                new MarkAdAsSoldAsyncTask(position, Integer.parseInt(SellingPriceEditText.getText().toString().trim())).execute();
                        }
                    });

                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogForSold.dismiss();
                        }
                    });
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogForDelete.setContentView(R.layout.areyousure_delete_popup);
                    Button btnYes = dialogForDelete.findViewById(R.id.btnYes);
                    Button btnNo = dialogForDelete.findViewById(R.id.btnNo);
                    dialogForDelete.show();

                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new MarkAdAsDeletedAsyncTask(position).execute();
                        }
                    });

                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogForDelete.dismiss();
                        }
                    });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();

        }
        return vi;
    }

    private String getFormatedAmount(int amount) {
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(amount);
    }

    public class MarkAdAsSoldAsyncTask extends AsyncTask<Integer, Void, String> {

        private final int position;
        private final int soldPrice;

        private Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black);

        private MarkAdAsSoldAsyncTask(int position, int soldPrice) {
            this.position = position;
            this.soldPrice = soldPrice;
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
                String urlAPI = CommonFunctions.API_URL + "Ad/MarkSoldSelectedAd";
                URL url = new URL(urlAPI);
                //Create URL connection
                HttpURLConnection conn = null;
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                //Create Json Object
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("AdId", Ads.get(position).AdId);
                jsonObject.accumulate("SoldPrice",soldPrice);
                Globals g = Globals.getInstance();
                jsonObject.accumulate("Token",g.getData());

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

            if (s.equalsIgnoreCase("ok")) {
                    remove(Ads.get(position));
                    notifyDataSetChanged();
            } else
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();

            if (dialog.isShowing()) {
                // Dismiss/hide the progress dialog
                dialog.dismiss();
            }

            if (dialogForSold.isShowing()) {
                dialogForSold.dismiss();
            }
        }
    }

    public class MarkAdAsDeletedAsyncTask extends AsyncTask<Integer, Void, String> {

        private final int position;

        private Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black);

        private MarkAdAsDeletedAsyncTask(int position) {
            this.position = position;
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
                String urlAPI = CommonFunctions.API_URL + "Ad/DeleteSelectedAd";
                URL url = new URL(urlAPI);
                //Create URL connection
                HttpURLConnection conn = null;
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                //Create Json Object
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("AdId", Ads.get(position).AdId);

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

            if (s.equalsIgnoreCase("ok")) {
                remove(Ads.get(position));
                notifyDataSetChanged();
            } else
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();

            if (dialog.isShowing()) {
                // Dismiss/hide the progress dialog
                dialog.dismiss();
            }

            if (dialogForDelete.isShowing()) {
                dialogForDelete.dismiss();
            }
        }
    }

    public class RepostSelectedAdAsyncTask extends AsyncTask<Integer, Void, String> {

        private final int position;

        private Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black);

        private RepostSelectedAdAsyncTask(int position) {
            this.position = position;
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
                String urlAPI = CommonFunctions.API_URL + "Ad/RepostSelectedAd";
                URL url = new URL(urlAPI);
                //Create URL connection
                HttpURLConnection conn = null;
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                //Create Json Object
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("AdId", Ads.get(position).AdId);

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

            if (s.equalsIgnoreCase("ok")) {
                remove(Ads.get(position));
                notifyDataSetChanged();

                Toast.makeText(activity, "Refresh to see the ad", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();

            if (dialog.isShowing()) {
                // Dismiss/hide the progress dialog
                dialog.dismiss();
            }

            if (dialogForRepost.isShowing()) {
                dialogForRepost.dismiss();
            }
        }
    }


}
