package com.openogy.classes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.openogy.www.anif.R;
import com.openogy.www.anif.SingleWishDisplayActivity;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserProfileWishesAdapter extends ArrayAdapter<Ads> implements Serializable {
    private Activity activity;
    private ArrayList<Wishes> Wishes;
    private static LayoutInflater inflater = null;
    private Context context;
    private ViewHolder holder;
    Dialog dialogForSold;
    Dialog dialogForDelete;
    Dialog dialogForRepost;

    public UserProfileWishesAdapter(Activity activity, int textViewResourceId, ArrayList<Wishes> _allWishes, Context context) {
        super(activity, textViewResourceId);

        try {
            this.context = context;
            this.activity = activity;
            this.Wishes = _allWishes;
            dialogForDelete = new Dialog(getContext());

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {

        }
    }

    public void addListItemToAdapter(ArrayList<Wishes> list) {
        //add list to current arraylist of data
        Wishes.addAll(list);

        //Notify UI
        this.notifyDataSetChanged();
    }

    public void updateResults() {

        //Triggers the list update
        this.notifyDataSetChanged();
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
        public TextView display_description;
        public TextView display_locality;
        public ImageView display_adImage;
        public TextView display_Date;
        public int position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        try {
            if (convertView == null) {

                vi = inflater.inflate(R.layout.user_profile_wishlist_layout, null);
                holder = new ViewHolder();

                holder.display_locality = vi.findViewById(R.id.tvLocality);
                holder.display_adImage = vi.findViewById(R.id.IvAdImage);
                holder.display_Date = vi.findViewById(R.id.tvDateTime);
                holder.display_description = vi.findViewById(R.id.tvDescription);

                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }

            holder.display_locality.setText(Wishes.get(position).WishLocality);
            holder.display_description.setText(Wishes.get(position).WishDescription);

            if(Wishes.get(position).WishImageBitmap!= null)
            {
                holder.display_adImage.setVisibility(View.VISIBLE);
                holder.display_adImage.setImageBitmap(Wishes.get(position).WishImageBitmap);
            }

            else
                holder.display_adImage.setVisibility(View.GONE);


            //Get date from list
            String date = Wishes.get(position).CreatedOn;

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
                    Intent intent = new Intent(context,SingleWishDisplayActivity.class);
                    intent.putExtra("WishId", Wishes.get(position).WishId);
                    intent.putExtra("PhoneNumber",Wishes.get(position).UserPhoneNumber);
                    intent.putExtra("Token",Wishes.get(position).UserToken);
                    context.startActivity(intent);
                }
            });

            Button btnDelete = vi.findViewById(R.id.btnDelete);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogForDelete.setContentView(R.layout.areyousure_delete_wish_popup);
                    Button btnYes = dialogForDelete.findViewById(R.id.btnYes);
                    Button btnNo = dialogForDelete.findViewById(R.id.btnNo);
                    dialogForDelete.show();

                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new MarkWishAsDeletedAsyncTask(position).execute();
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


    public class MarkWishAsDeletedAsyncTask extends AsyncTask<Integer, Void, String> {

        private final int position;

        private Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black);

        private MarkWishAsDeletedAsyncTask(int position) {
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_progressbar, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(view);
            dialog.show();
        }

        @Override
        protected String doInBackground(Integer... params) {

            try {
                String urlAPI = CommonFunctions.API_URL + "WishList/MarkWishAsDeleted";
                URL url = new URL(urlAPI);
                //Create URL connection
                HttpURLConnection conn = null;
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                //Create Json Object
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("WishId", Wishes.get(position).WishId);

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
                Wishes.remove(position);
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
}
