package com.openogy.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.openogy.classes.AdapterWishes;
import com.openogy.classes.CommonFunctions;
import com.openogy.classes.Globals;
import com.openogy.classes.Wishes;
import com.openogy.www.anif.R;
import com.openogy.www.anif.Registration_Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

public class WishlistFragment extends Fragment {

    ListView listView;
    ImageView iv;
    GetWishForCityAsyncTask wishData;
    public View footer;
    public Handler mHandler;
    public boolean isLoading = false;
    public int offset = 0;
    public int counter = 0;
    public int responseCode;
    public AdapterWishes adbWishes;
    public String CategoryName = "";
    private boolean isMoreData = true;
    View view;
    TextView noWishes;
    SwipeRefreshLayout pullToRefresh;
    int refreshcounter = 0;
    Menu topIconMenu;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(getArguments() != null)
        {
            CategoryName = getArguments().getString("CategoryName");
        }

        if(CategoryName.isEmpty()){
            if(checkNetworkConnection())
            {
                String url = CommonFunctions.API_URL + "WishList/GetWishesForCity";
                wishData = (GetWishForCityAsyncTask) new GetWishForCityAsyncTask().execute(url);
            }
            else
                Toast.makeText(getContext(),"No Internet connection",Toast.LENGTH_SHORT).show();

        }
        else if(!CategoryName.isEmpty())
        {
            if(checkNetworkConnection())
            {
                String url = CommonFunctions.API_URL + "WishList/GetWishesForCityWithCategory";
                wishData = (GetWishForCityAsyncTask) new GetWishForCityAsyncTask().execute(url);
            }
            else
                Toast.makeText(getContext(),"No Internet connection",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        noWishes = getView().findViewById(R.id.tvNoWishList);
        noWishes.setVisibility(View.GONE);

        listView = getView().findViewById(R.id.WishListView);
        listView.setCacheColorHint(Color.TRANSPARENT);
        pullToRefresh = getView().findViewById(R.id.pullToRefresh);
        //Setting Icon to filled when redirects from post wish page
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.navigation);
        Menu bottomNavigationMenu =  bottomNavigationView.getMenu();

        //Changing icons of the bottomnavigationitems
        MenuItem item =   bottomNavigationMenu.findItem(R.id.navigation_wishlist);
        MenuItem adItem = bottomNavigationMenu.findItem(R.id.navigation_home);
        MenuItem postItem = bottomNavigationMenu.findItem(R.id.navigation_postad);
        MenuItem notificationItem = bottomNavigationMenu.findItem(R.id.navigation_notifications);
        MenuItem profileItem = bottomNavigationMenu.findItem(R.id.navigation_userprofile);
        item.setIcon(R.drawable.icons_wish_list_filled);
        adItem.setIcon(R.drawable.icons_market);
        postItem.setIcon(R.drawable.icons_add);
        notificationItem.setIcon(R.drawable.icons_notification);
        profileItem.setIcon(R.drawable.icons_profile);

        Globals g = Globals.getInstance();

        if (g.getFlagForBackPressed())
        {
            g.setFlagForBackPressed(false);
            return;
        };

        if(g.getFlagForNewNotification())
        {
            notificationItem.setIcon(R.drawable.iconss_notification_alarm);
        }
        else
            notificationItem.setIcon(R.drawable.icons_notification);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshcounter = 1;
                offset = 0;
                counter = 0;
                isLoading = false;
                //Resetting the categories and filter
                SetDefaultMenuValues();
                String url = CommonFunctions.API_URL + "WishList/GetWishesForCity";
                wishData = (GetWishForCityAsyncTask) new GetWishForCityAsyncTask().execute(url);
            }
        });

        if(getArguments() != null)
        {
            CategoryName = getArguments().getString("CategoryName");
        }



            //Add footer to listView
            footer = getLayoutInflater().inflate(R.layout.listview_footer,listView,false);

            int headerCount = listView.getHeaderViewsCount();

        if (headerCount == 0)
        {
            //Add header to listView
            View header = getLayoutInflater().inflate(R.layout.layout_premiumwishad, listView, false);
            iv = header.findViewById(R.id.PremiumWishAdImageView);
            int imageresource = getResources().getIdentifier("@drawable/ifbannerforwishlist", "drawable", getActivity().getPackageName());
            Drawable myDrawable =  getView().getResources().getDrawable(imageresource);
            Bitmap b = ((BitmapDrawable) myDrawable).getBitmap();
            iv.setImageBitmap(b);
            listView.addHeaderView(header, null, false);
        }



            mHandler = new MyHandler();




        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView,  int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if(absListView.getLastVisiblePosition() == totalItemCount - 1 && listView.getCount() >= 7 && isLoading == false)
                {
                    isLoading = true;
                    offset = offset + 7;
                    if(CategoryName.isEmpty()){
                        if(checkNetworkConnection())
                        {
                            String url = CommonFunctions.API_URL + "WishList/GetWishesForCity";
                            wishData = (GetWishForCityAsyncTask) new GetWishForCityAsyncTask().execute(url);
                        }
                        else
                            Toast.makeText(getContext(),"No Internet connection",Toast.LENGTH_SHORT).show();

                    }
                    else if(!CategoryName.isEmpty())
                    {
                        if(checkNetworkConnection())
                        {
                            String url = CommonFunctions.API_URL + "WishList/GetWishesForCityWithCategory";
                            wishData = (GetWishForCityAsyncTask) new GetWishForCityAsyncTask().execute(url);
                        }
                        else
                            Toast.makeText(getContext(),"No Internet connection",Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isConnected = false;
        if (networkInfo != null && (isConnected = networkInfo.isConnected())) {
            isConnected = true;


        } else {
            isConnected = false;
        }

        return isConnected;
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //Add loading view during search processing
                    listView.addFooterView(footer,null,false);
                    break;
                case 1:
                    //Update data adapter and UI
                    adbWishes.addListItemToAdapter((ArrayList<Wishes>)msg.obj);
                    //Renove Footer after update
                    listView.removeFooterView(footer);
                    isLoading = false;
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        topIconMenu = menu;
    }

    public class ThreadGetMoreData extends Thread {
        @Override
        public void run() {
            super.run();
            //mHandler.sendEmptyMessage(0);

            String wishList = null;
            try {

                wishList = wishData.get();

                if(wishList != null)
                {
                    ArrayList<Wishes> arrayList = null;
                    Gson gson = new Gson();
                    Type collectionType = new TypeToken<Collection<Wishes>>() {
                    }.getType();
                    Collection<Wishes> wishCollection = gson.fromJson(wishList, collectionType);
                    Wishes[] wishArray = wishCollection.toArray(new Wishes[wishCollection.size()]);

                    arrayList = new ArrayList<Wishes>(Arrays.asList(wishArray));

                    for(int i = 0; i < arrayList.size(); i ++)
                    {
                        if(arrayList.get(i).WishImage != null)
                        {
                            byte[] bytes = android.util.Base64.decode(arrayList.get(i).WishImage, 0);

                            Wishes a =  arrayList.get(i);
                            a.WishImageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        }

                    }

                    Thread.sleep(2000);

                    Message msg = mHandler.obtainMessage(1, arrayList);
                    mHandler.sendMessage(msg);
                }



            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
    }

    private void SetWishData() throws InterruptedException, ExecutionException {
        String wishList = wishData.get();
        ArrayList<Wishes> arrayList = null;
        if(wishList == null)
            Toast.makeText(getContext(),"No Wishes",Toast.LENGTH_SHORT).show();
        else {

            Gson gson = new Gson();
            Type collectionType = new TypeToken<Collection<Wishes>>() {
            }.getType();
            Collection<Wishes> wishesCollection = gson.fromJson(wishList, collectionType);
            Wishes[] wishArray = wishesCollection.toArray(new Wishes[wishesCollection.size()]);

            arrayList = new ArrayList<Wishes>(Arrays.asList(wishArray));

            for(int i = 0; i < arrayList.size(); i ++)
            {
                if (arrayList.get(i).WishImage!=null)
                {
                    byte[] bytes = android.util.Base64.decode(arrayList.get(i).WishImage, 0);

                    Wishes a =  arrayList.get(i);
                    a.WishImageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                }

            }

            adbWishes= new AdapterWishes(getActivity(), 0, arrayList, getContext());
            if(adbWishes == null)
            {
                Toast.makeText(getContext(),"No Wishes",Toast.LENGTH_SHORT).show();
            }
            else
            {
                //listView.setDivider(null);
                listView.setAdapter(adbWishes);
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view != null) {
            if (view.getParent() != null) {
                ((ViewGroup)view.getParent()).removeView(view);
            }
            return view;
        }

        view = inflater.inflate(R.layout.layout_wishlistfragment,container,false);
        return view;
    }

    public class GetWishForCityAsyncTask extends AsyncTask<String, Void, String> {

        private Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(counter == 0 && refreshcounter == 0)
            {

                View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_progressbar, null);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(view);
                dialog.show();
            }
            else if(refreshcounter == 0)
                listView.addFooterView(footer,null,false);

        }

        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                try {
                    return HttpPost(urls[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            } catch (IOException e) {
                return null;
            }
        }



    private  String HttpPost(String myUrl) throws IOException, JSONException {

        URL url = new URL(myUrl);

        // 1. create HttpURLConnection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        // 2. build JSON object
        JSONObject jsonObject = buidJsonObject();

        // 3. add JSON content to POST request body
        setPostRequestContent(conn, jsonObject);

        // 4. make POST request to the given URL
        conn.connect();

        InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder = new StringBuilder();
        String bufferedStrChunk = null;

        StringBuffer stringBuffer = new StringBuffer();
        String line;
        while ((line = bufferedReader.readLine()) != null)
        {
            stringBuilder.append(line);
        }

        responseCode = conn.getResponseCode();

        conn.disconnect();
        inputStreamReader.close();
        // 5. return response message
        return stringBuilder.toString();

    }

    private  JSONObject buidJsonObject() throws JSONException {

        Globals g = Globals.getInstance();
        String token = g.getData();
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("Token",token);
        jsonObject.accumulate("Offset",offset);
        if(!CategoryName.isEmpty())
            jsonObject.accumulate("Category",CategoryName);
        return jsonObject;
    }

    private  void setPostRequestContent(HttpURLConnection conn,
                                              JSONObject jsonObject) throws IOException {

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonObject.toString());
        Log.i(Registration_Activity.class.toString(), jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();
    }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(responseCode == 200)
            {
                if(counter == 0)
                {
                    try {
                        SetWishData();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    counter++;


                }
                else if(s != null)
                {
                    Thread thread = new ThreadGetMoreData();
                    thread.start();
                }

                else if(s == null)
                    listView.removeFooterView(footer);
            }

            else if(counter ==0)
                noWishes.setVisibility(View.VISIBLE);
            if(dialog.isShowing()){
                // Dismiss/hide the progress dialog
                dialog.dismiss();
            }
            pullToRefresh.setRefreshing(false);
            if(refreshcounter >= 1)
                refreshcounter = 0;
        }
    }

    private void SetDefaultMenuValues() {
        CategoryName = "";

        Globals g = Globals.getInstance();
        int categoryListCount = g.getCategorySubmenuCount();

        if(categoryListCount > 0)
        {
            for (int i = 0; i < categoryListCount; i++) {
                MenuItem menuItemCategory = topIconMenu.findItem(R.id.action_category);
                SubMenu subMenu = menuItemCategory.getSubMenu();
                MenuItem subMenuItem = subMenu.getItem(i);
                String subMenuTitle = subMenuItem.getTitle().toString();
                SpannableString s = new SpannableString(subMenuTitle);
                s.setSpan(new StyleSpan(Typeface.NORMAL), 0, s.length(), 0);
                subMenuItem.setTitle(s);
            }
        }

        for (int i = 0; i < 3; i++)
        {
            MenuItem menuItemCategory = topIconMenu.findItem(R.id.action_Filter);
            SubMenu subMenu = menuItemCategory.getSubMenu();
            MenuItem subMenuItem = subMenu.getItem(i);
            String subMenuTitle = subMenuItem.getTitle().toString();
            SpannableString s = new SpannableString(subMenuTitle);
            s.setSpan(new StyleSpan(Typeface.NORMAL), 0, s.length(), 0);
            subMenuItem.setTitle(s);
        }
    }
}
