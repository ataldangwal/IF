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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserProfileBidsAdapter extends ArrayAdapter<Ads> implements Serializable {
    private Activity activity;
    private ArrayList<UserAdsWithHisHighestBid> Bids;
    private static LayoutInflater inflater = null;
    private Context context;
    private ViewHolder holder;
    Dialog dialogForSold;
    Dialog dialogForDelete;
    Dialog dialogForRepost;

    public UserProfileBidsAdapter(Activity activity, int textViewResourceId, ArrayList<UserAdsWithHisHighestBid> _allWishes, Context context) {
        super(activity, textViewResourceId);

        try {
            this.context = context;
            this.activity = activity;
            this.Bids = _allWishes;
            dialogForDelete = new Dialog(getContext());

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {

        }
    }

    public void addListItemToAdapter(ArrayList<UserAdsWithHisHighestBid> list) {
        //add list to current arraylist of data
        Bids.addAll(list);

        //Notify UI
        this.notifyDataSetChanged();
    }

    public void updateResults() {

        //Triggers the list update
        this.notifyDataSetChanged();
    }


    public int getCount() {
        return Bids.size();
    }

    public UserAdsWithHisHighestBid getItem(UserAdsWithHisHighestBid position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView display_title;
        public TextView display_locality;
        public ImageView display_adImage;
        public TextView display_Date;
        public TextView display_highestBid;
        public TextView display_sellingPrice;
        public TextView display_userHighestBid;
        public TextView display_userBidDate;
        public int position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        try {
            if (convertView == null) {

                vi = inflater.inflate(R.layout.user_profile_bids_layout, null);
                holder = new ViewHolder();

                holder.display_title = vi.findViewById(R.id.tvAdTitle);
                holder.display_locality = vi.findViewById(R.id.tvLocality);
                holder.display_adImage = vi.findViewById(R.id.IvAdImage);
                holder.display_Date = vi.findViewById(R.id.tvDateTime);
                holder.display_highestBid = vi.findViewById(R.id.tvHighestBid);
                holder.display_sellingPrice = vi.findViewById(R.id.tvSellingPrice);
                holder.display_userHighestBid = vi.findViewById(R.id.tvUserHighestBid);
                holder.display_userBidDate = vi.findViewById(R.id.tvUserHighestBidDate);

                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }

            holder.display_title.setText(Bids.get(position).AdTitle);
            holder.display_locality.setText(Bids.get(position).AdLocality);
            holder.display_highestBid.setText(Bids.get(position).AdHighestBid);

            if(Bids.get(position).AdSellingPrice.equals("0"))
                holder.display_sellingPrice.setText(context.getText(R.string.txt_no_selling_price));
            else
                holder.display_sellingPrice.setText(Bids.get(position).AdSellingPrice);
            holder.display_userHighestBid.setText(Bids.get(position).BidAmount);

            holder.display_adImage.setImageBitmap(Bids.get(position).AdImageBitmap);

            //Get date from list
            String adDate = Bids.get(position).CreatedOn;
            String dateForBid = Bids.get(position).BidDate;


            //Specify the get date format
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date initialAdDate = format.parse(adDate);
            Date initialBidDate = format.parse(dateForBid);


            //Convert into desired format
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
            String finalAdDate = dateFormat.format(initialAdDate);
            String finalBidDate = dateFormat.format(initialBidDate);



            holder.display_Date.setText(finalAdDate);
            holder.display_userBidDate.setText(finalBidDate);

            holder.position = position;


            holder.display_adImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SingleAdDisplayActivity.class);
                    intent.putExtra("AdId", Bids.get(position).AdId);
                    intent.putExtra("PhoneNumber", Bids.get(position).VendorPhoneNumber);
                    intent.putExtra("Position", position);
                    intent.putExtra("Token",Bids.get(position).UserToken);
                   // Globals g = Globals.getInstance();
                    //g.setArrayData(Bids);
                    context.startActivity(intent);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();

        }
        return vi;
    }


}
