package com.openogy.classes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.openogy.www.anif.R;
import com.openogy.www.anif.User_Profile_Activity;

import java.util.ArrayList;

public class AdapterAdComments extends ArrayAdapter<Ads> {
    private int adCount;
    private Activity activity;
    private ArrayList<AdComments> AdComments;
    private static LayoutInflater inflater = null;
    private static final int TYPE_ITEM1 = 0;
    private static final int TYPE_ITEM2 = 1;
    private Context context;
    private Dialog dialogForMakingBid;
    private Dialog dialogForReport;
    private String highestBid;
    private ViewHolder holder;


    public AdapterAdComments(Activity activity, int textViewResourceId, ArrayList<AdComments> _allAdComments, Context context) {
        super(activity, textViewResourceId);

        try {
            this.context = context;
            this.activity = activity;
            this.AdComments = _allAdComments;
            dialogForMakingBid = new Dialog(context);

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {

        }
    }

    public void addListItemToAdapter(ArrayList<AdComments> list) {
        //add list to current arraylist of data
        AdComments.addAll(list);

        //Notify UI
        this.notifyDataSetChanged();
    }

    public int getCount() {
        return AdComments.size();
    }

    public AdComments getItem(AdComments position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView display_name;
        public TextView display_comment;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        try {
            if (convertView == null) {

                vi = inflater.inflate(R.layout.comments, null);
                holder = new ViewHolder();

                holder.display_name = vi.findViewById(R.id.tvCommentTitle);
                holder.display_comment = vi.findViewById(R.id.tvCommentText);

                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }

            holder.display_name.setText(AdComments.get(position).UserName);
            holder.display_comment.setText(AdComments.get(position).Comment);

            holder.display_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,User_Profile_Activity.class);
                    intent.putExtra("Token", AdComments.get(position).UserToken);
                    context.startActivity(intent);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();

        }
        return vi;
        }


    }


