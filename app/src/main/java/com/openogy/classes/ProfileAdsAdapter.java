package com.openogy.classes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.openogy.www.anif.R;
import com.openogy.www.anif.SingleAdDisplayActivity;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ProfileAdsAdapter extends ArrayAdapter<Ads> implements Serializable {
    private Activity activity;
    private ArrayList<Ads> Ads;
    private static LayoutInflater inflater = null;
    private Context context;
    private Dialog dialogForMakingBid;
    private Dialog dialogForReport;
    private String highestBid;
    private ViewHolder holder;

    public ProfileAdsAdapter(Activity activity, int textViewResourceId, ArrayList<Ads> _allAds, Context context) {
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

                vi = inflater.inflate(R.layout.profile_ads_layout, null);
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
            /*else
                holder.display_tvNoOfDemands.setVisibility(View.GONE);*/
            if((Ads.get(position).AdSellingPrice).equals("0"))
                holder.display_sellingPrice.setText(context.getText(R.string.txt_no_selling_price));
            else
                holder.display_sellingPrice.setText(getFormatedAmount(Integer.parseInt(Ads.get(position).AdSellingPrice)));
            holder.display_highestBid.setText((Ads.get(position).AdHighestBid).equals("0") ? "No bid" : getFormatedAmount(Integer.parseInt(Ads.get(position).AdHighestBid)));
            //byte[] bytes = android.util.Base64.decode((Ads.get(position).AdImageOne), 0);
            holder.display_adImage.setImageBitmap(Ads.get(position).AdImageBitmap);

            //Get date from list
            String date = Ads.get(position).CreatedOn;

            //Specify the get date format
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date updateLast = format.parse(date);

            //Convert into desired format
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
            String dateTime = dateFormat.format(updateLast);

           /* DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            Date updateLast = format.parse(date);
            String newDate = format.format(updateLast);*/


            holder.display_Date.setText(dateTime);
            holder.position = position;
            //new GetImageTask(bytes, position, holder).execute(holder);

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

        } catch (Exception e) {
            e.printStackTrace();

        }
        return vi;
    }

    private String getFormatedAmount(int amount){
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(amount);
    }


}
