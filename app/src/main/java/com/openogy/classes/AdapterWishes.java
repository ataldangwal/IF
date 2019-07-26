package com.openogy.classes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.openogy.www.anif.R;
import com.openogy.www.anif.SingleWishDisplayActivity;
import com.openogy.www.anif.User_Profile_Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class AdapterWishes extends ArrayAdapter<Wishes> {
    private Activity activity;
    private ArrayList<Wishes> Wishes;
    private static LayoutInflater inflater = null;
    private Dialog dialogForReport;
    private Context context;

    public AdapterWishes(Activity activity, int textViewResourceId, ArrayList<Wishes> _allWishes, Context context) {
        super(activity, textViewResourceId, _allWishes);
        try {
            this.context = context;
            this.activity = activity;
            this.Wishes = _allWishes;
            dialogForReport = new Dialog(context);

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {

        }
    }

    public void addListItemToAdapter(ArrayList<Wishes> list) {
        //add list to current arraylist of data
        Wishes.addAll(list);
    }

    public int getCount() {
        return Wishes.size();
    }

    public Wishes getItem(Wishes position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView display_name;
        public TextView display_description;
        public TextView display_locality;
        public ImageView display_adImage;
        public int wishId;
        public int position;
        public ProgressBar ImageProgressbar;
        public Button btnFulfill;
        public TextView display_NoOfFulfills, tvCall, tvReport;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {

                    vi = inflater.inflate(R.layout.layout_singlewishlist, null);
                    holder = new ViewHolder();

                    holder.display_name =vi.findViewById(R.id.tvName);
                    holder.display_locality = vi.findViewById(R.id.tvLocality);
                    holder.display_description =vi.findViewById(R.id.Description);
                    holder.display_adImage = vi.findViewById(R.id.WishListImageView);
                    holder.display_NoOfFulfills = vi.findViewById(R.id.tvNoOfFulfills);
                    holder.btnFulfill = vi.findViewById(R.id.btnFulfill);
                    holder.wishId = 0;
                    holder.position = position;
                    holder.tvCall = vi.findViewById(R.id.tvCall);
                    holder.tvReport = vi.findViewById(R.id.tvReport);
                    vi.setTag(holder);

            } else {
                holder = (ViewHolder) vi.getTag();
            }

            if((Wishes.get(position).UserBadge).equals("0"))
                holder.display_name.setTextColor(Color.parseColor("#000000"));
            else if((Wishes.get(position).UserBadge).equals("1"))
                holder.display_name.setTextColor(Color.parseColor("#6C541E"));
            else if((Wishes.get(position).UserBadge).equals("2"))
                holder.display_name.setTextColor(Color.parseColor("#808080"));
            else if((Wishes.get(position).UserBadge).equals("3"))
                holder.display_name.setTextColor(Color.parseColor("#DAA520"));
            else if((Wishes.get(position).UserBadge).equals("4"))
                holder.display_name.setTextColor(Color.parseColor("#2E75F9"));

                holder.display_name.setText(Wishes.get(position).UserName);
            holder.display_locality.setText(Wishes.get(position).WishLocality);
                holder.display_description.setText(Wishes.get(position).WishDescription);
                holder.display_NoOfFulfills.setText((Wishes.get(position).NoOfFulfill).equals("0")? " ":Wishes.get(position).NoOfFulfill );

            if (Wishes.get(position).WishImageBitmap != null)
            {
                holder.display_adImage.setVisibility(View.VISIBLE);
                holder.display_adImage.setImageBitmap(Wishes.get(position).WishImageBitmap != null?Wishes.get(position).WishImageBitmap : null);
            }
            else
                holder.display_adImage.setVisibility(View.GONE);

                    //new GetImageTask(bytes, position).execute(holder);holder.display_adImage.setVisibility(View.GONE)

            holder.btnFulfill.setBackgroundResource(R.drawable.roundbuttonempty);
            holder.btnFulfill.setTextColor(Color.parseColor("#00BAED"));

            if(Wishes.get(position).isFullfill)
            {
                holder.btnFulfill.setBackgroundResource(R.drawable.roundbuttonfilled);
                holder.btnFulfill.setTextColor(Color.parseColor("#FFFFFF"));
                holder.display_NoOfFulfills.setTextColor(Color.parseColor("#00BAED"));
            }

            else
            {
                holder.display_NoOfFulfills.setTextColor(Color.parseColor("#00BAED"));
            }
                holder.wishId = Wishes.get(position).WishId;

            ImageButton callImageButton = vi.findViewById(R.id.ImageButtonCall);
            ImageButton reportImageButton = vi.findViewById(R.id.ImageButtonReport);

            Globals g = Globals.getInstance();

            if(Wishes.get(position).UserToken.equals(g.getData()))
            {
                callImageButton.setVisibility(View.GONE);
                reportImageButton.setVisibility(View.GONE);
                holder.tvReport.setVisibility(View.GONE);
                holder.tvCall.setVisibility(View.GONE);
            }
            else if(Wishes.get(position).isDisplayPhoneNumber)
            {
                callImageButton.setVisibility(View.VISIBLE);
                reportImageButton.setVisibility(View.VISIBLE);
                holder.tvReport.setVisibility(View.VISIBLE);
                holder.tvCall.setVisibility(View.VISIBLE);
            }
            else if(!Wishes.get(position).isDisplayPhoneNumber)
            {
                callImageButton.setVisibility(View.GONE);
                reportImageButton.setVisibility(View.VISIBLE);
                holder.tvReport.setVisibility(View.VISIBLE);
                holder.tvCall.setVisibility(View.GONE);

            }

            //Go to profile if name is clicked
            holder.display_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,User_Profile_Activity.class);
                    intent.putExtra("Token", Wishes.get(position).UserToken);
                    context.startActivity(intent);
                }
            });

            holder.display_adImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,SingleWishDisplayActivity.class);
                    intent.putExtra("WishId", Wishes.get(position).WishId);
                    intent.putExtra("PhoneNumber",Wishes.get(position).UserPhoneNumber);
                    intent.putExtra("Token",Wishes.get(position).UserToken);
                    context.startActivity(intent);
                }
            });
            holder.display_description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,SingleWishDisplayActivity.class);
                    intent.putExtra("WishId", Wishes.get(position).WishId);
                    intent.putExtra("PhoneNumber",Wishes.get(position).UserPhoneNumber);
                    intent.putExtra("Token",Wishes.get(position).UserToken);
                    context.startActivity(intent);
                }
            });

            holder.btnFulfill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                new AddOrRemoveWishFulfillTask(position,holder).execute();
                }
            });

            //Call Button Click Event
            callImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Use format with "tel:" and phone number to create phoneNumber.
                    String phoneNumber = String.format("tel: %s",
                            Wishes.get(position).UserPhoneNumber);
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
                                new AdapterWishes.SubmitReportTask(editText.getText().toString().trim()).execute(position);

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

    public class GetImageTask extends AsyncTask<ViewHolder, Void, Bitmap> {
        private final byte[] adImage;
        private final int position;

        private ViewHolder v;

        private GetImageTask(byte[] adImage, int position) {
            this.adImage = adImage;
            this.position = position;
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
    }

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
                String urlAPI = CommonFunctions.API_URL + "WishList/SubmitReportForWish";
                URL url = new URL(urlAPI);
                //Create URL connection
                HttpURLConnection conn = null;
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                //Create Json Object
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("ReportReason", reportReason);
                jsonObject.accumulate("WishId", Wishes.get(position).WishId);
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

    public class AddOrRemoveWishFulfillTask extends AsyncTask<Integer, Void, String> {

        private final int position;
        private ViewHolder holder;

        //private Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black);

        private AddOrRemoveWishFulfillTask(int position, ViewHolder holder) {
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
                String urlAPI = CommonFunctions.API_URL + "WishList/AddOrRemoveAdFullfill";
                URL url = new URL(urlAPI);
                //Create URL connection
                HttpURLConnection conn = null;
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                //Create Json Object
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("WishId", Wishes.get(position).WishId);
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
                Wishes ads =  Wishes.get(position);
                int noOfDemands = Integer.parseInt(ads.getNoOfFulfill()) ;
                String newNoOfDemand = String.valueOf(noOfDemands + 1);
                ads.setNoOfFulfill(newNoOfDemand);
                ads.setFullFill(true);
                notifyDataSetChanged();
            }

            else if(s.equalsIgnoreCase("Moved Permanently"))
            {
                Wishes ads =  Wishes.get(position);
                int noOfDemands = Integer.parseInt(ads.getNoOfFulfill());
                String newNoOfDemand = String.valueOf(noOfDemands - 1);
                ads.setNoOfFulfill(newNoOfDemand);
                ads.setFullFill(false);
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
}