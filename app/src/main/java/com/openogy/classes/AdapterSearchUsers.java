package com.openogy.classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.openogy.www.anif.R;
import com.openogy.www.anif.User_Profile_Activity;

import java.io.Serializable;
import java.util.ArrayList;

public class AdapterSearchUsers extends ArrayAdapter<Users> implements Serializable {
    private Activity activity;
    private ArrayList<Users> Users;
    private static LayoutInflater inflater = null;
    private Context context;
    private ViewHolder holder;

    public AdapterSearchUsers(Activity activity, int textViewResourceId, ArrayList<Users> _allUsers, Context context) {
        super(activity, textViewResourceId, _allUsers);

        try {
            this.context = context;
            this.activity = activity;
            this.Users = _allUsers;

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {

        }
    }

    public void addListItemToAdapter(ArrayList<Users> list) {
        //add list to current arraylist of data
        Users.addAll(list);

        //Notify UI
        this.notifyDataSetChanged();
    }

    public int getCount() {
        return Users.size();
    }

    public Users getItem(Users position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView display_name;
        public ImageView display_profileImage;
        public ProgressBar ImageProgressbar;
        public int position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        try {
            if (convertView == null) {

                vi = inflater.inflate(R.layout.layout_search_user, null);
                holder = new ViewHolder();

                holder.display_name = vi.findViewById(R.id.tvUserName);
                holder.display_profileImage = vi.findViewById(R.id.ProfileImageView);
                holder.position = position;

                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }

            holder.display_name.setText(Users.get(position).FullName);
            holder.display_profileImage.setImageBitmap(Users.get(position).UserImageBitmap);
            holder.position = position;

            //Go to profile if name is clicked
            holder.display_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, User_Profile_Activity.class);
                    intent.putExtra("Token", Users.get(position).login_token);
                    context.startActivity(intent);
                }
            });

            holder.display_profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, User_Profile_Activity.class);
                    intent.putExtra("Token", Users.get(position).login_token);
                    context.startActivity(intent);
                }
            });




        } catch (Exception e) {
            e.printStackTrace();

        }
        return vi;
    }
}
