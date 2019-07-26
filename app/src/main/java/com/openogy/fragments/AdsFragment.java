package com.openogy.fragments;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.openogy.classes.AdapterAds;
import com.openogy.classes.Ads;
import com.openogy.classes.CommonFunctions;
import com.openogy.classes.Globals;
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

public class AdsFragment extends Fragment {

    private ListView listview;
    private ImageView iv;
    private GetAdsForCityAsyncTask adData;
    public View footer;
    public Handler mHandler;
    public boolean isLoading = false;
    public AdapterAds adbAds;
    public int offset = 0;
    public int counter = 0;
    public int responseCode;
    public String CategoryName = "";
    public String Filter = "";
    View view;
    TextView tvNoAds;
    SwipeRefreshLayout pullToRefresh;
    int refreshcounter = 0;
    Menu topIconMenu;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);

        if(getArguments() != null)
        {
            CategoryName = getArguments().getString("CategoryName");
            Filter = getArguments().getString("Filter");
        }

        if(CategoryName.isEmpty() && Filter.isEmpty()){
            String url = CommonFunctions.API_URL + "Ad/GetAdsForCity";
            //adData = (GetAdsForCityAsyncTask) new GetAdsForCityAsyncTask().execute(url);
            new GetAdsForCityAsyncTask().execute(url);
        }

        else if(!CategoryName.isEmpty() && Filter.isEmpty())
        {
            String url = CommonFunctions.API_URL + "Ad/GetAdsForCityWithCategory";
            adData = (GetAdsForCityAsyncTask) new GetAdsForCityAsyncTask().execute(url);
        }

        else if(CategoryName.isEmpty() && !Filter.isEmpty())
        {
            String url = CommonFunctions.API_URL + "Ad/GetAdsForCityWithFilter";
            adData = (GetAdsForCityAsyncTask) new GetAdsForCityAsyncTask().execute(url);
        }

        else if(!CategoryName.isEmpty() && !Filter.isEmpty())
        {
            String url = CommonFunctions.API_URL + "Ad/GetAdsForCityWithCategoryAndFilter";
            adData = (GetAdsForCityAsyncTask) new GetAdsForCityAsyncTask().execute(url);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setHasOptionsMenu(true);

        if(getArguments() != null)
        {
            CategoryName = getArguments().getString("CategoryName");
            Filter = getArguments().getString("Filter");
        }

        tvNoAds = getView().findViewById(R.id.tvNoAds);
        tvNoAds.setVisibility(View.GONE);

        listview = getView().findViewById(R.id.AdOrCommentListView);
        pullToRefresh = getView().findViewById(R.id.pullToRefresh);
        //Setting Icon to filled when redirects from post Ad page
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.navigation);
        Menu bottomNavigationMenu =  bottomNavigationView.getMenu();

        //Changing icons of the bottomnavigationitems
        MenuItem item =   bottomNavigationMenu.findItem(R.id.navigation_home);
        MenuItem wishItem = bottomNavigationMenu.findItem(R.id.navigation_wishlist);
        MenuItem postItem = bottomNavigationMenu.findItem(R.id.navigation_postad);
        MenuItem notificationItem = bottomNavigationMenu.findItem(R.id.navigation_notifications);
        MenuItem profileItem = bottomNavigationMenu.findItem(R.id.navigation_userprofile);
        item.setIcon(R.drawable.icons_market_filled);
        wishItem.setIcon(R.drawable.icons_wish_list);
        postItem.setIcon(R.drawable.icons_add);
       // notificationItem.setIcon(R.drawable.icons_notification);
        profileItem.setIcon(R.drawable.icons_profile);

        Globals g = Globals.getInstance();

        if (g.getFlagForBackPressed())
        {
            notificationItem.setIcon(R.drawable.icons_notification);
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
                offset = 0;
                counter = 0;
                refreshcounter = 1;
               isLoading = false;
                //Resetting the categories and filter
                //SetDefaultMenuValues();
                String url = CommonFunctions.API_URL + "Ad/GetAdsForCity";
                new GetAdsForCityAsyncTask().execute(url);
            }
        });

        pullToRefresh.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(@NonNull SwipeRefreshLayout parent, @Nullable View child) {
                pullToRefresh.setRefreshing(false);
                int position = listview.getFirstVisiblePosition();
                if(position == 0)
                    return false;
                else
                    return true;
            }


        });




        //Add header to listview
        int headers = listview.getHeaderViewsCount();
        if(headers == 0)
        {
            View header = getLayoutInflater().inflate(R.layout.layout_premiumad, listview, false);
            footer = getLayoutInflater().inflate(R.layout.listview_footer,listview,false);
            iv = header.findViewById(R.id.PremiumAdImageView);
            int imageresource = getResources().getIdentifier("@drawable/ifbanner", "drawable", getActivity().getPackageName());
            Drawable myDrawable =  getView().getResources().getDrawable(imageresource);
            Bitmap b = ((BitmapDrawable) myDrawable).getBitmap();
            iv.setImageBitmap(b);
            listview.addHeaderView(header, null, false);
        }

        mHandler = new MyHandler();



        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Ads adControl = (Ads)adapterView.getItemAtPosition(i);
            }
        });


        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
               @Override
               public void onScrollStateChanged(AbsListView absListView, int i) {

               }

               @Override
               public void onScroll(AbsListView absListView,  int firstVisibleItem,
                                    int visibleItemCount, int totalItemCount) {
                if(absListView.getLastVisiblePosition() == totalItemCount - 1 && listview.getCount() >= 7 && isLoading == false)
                {
                    isLoading = true;
                    offset = offset + 7;


                    if(CategoryName.isEmpty() && Filter.isEmpty()){
                        String url = CommonFunctions.API_URL + "Ad/GetAdsForCity";
                        adData = (GetAdsForCityAsyncTask) new GetAdsForCityAsyncTask().execute(url);
                    }

                    else if(!CategoryName.isEmpty() && Filter.isEmpty())
                    {
                        String url = CommonFunctions.API_URL + "Ad/GetAdsForCityWithCategory";
                        adData = (GetAdsForCityAsyncTask) new GetAdsForCityAsyncTask().execute(url);
                    }

                    else if(CategoryName.isEmpty() && !Filter.isEmpty())
                    {
                        String url = CommonFunctions.API_URL + "Ad/GetAdsForCityWithFilter";
                        adData = (GetAdsForCityAsyncTask) new GetAdsForCityAsyncTask().execute(url);
                    }

                    else if(!CategoryName.isEmpty() && !Filter.isEmpty())
                    {
                        String url = CommonFunctions.API_URL + "Ad/GetAdsForCityWithCategoryAndFilter";
                        adData = (GetAdsForCityAsyncTask) new GetAdsForCityAsyncTask().execute(url);
                    }
                }
               }
           });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        topIconMenu = menu;
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //Add loading view during search processing
                    listview.addFooterView(footer,null,false);
                    break;
                case 1:
                    //Update data adapter and UI
                    adbAds.addListItemToAdapter((ArrayList<Ads>)msg.obj);
                    //Renove Footer after update
                    listview.removeFooterView(footer);
                    isLoading = false;
                    break;
                default:
                    break;
            }
        }
    }

    public class ThreadGetMoreData extends Thread {
        @Override
        public void run() {
            super.run();
           // mHandler.sendEmptyMessage(0);

            String adsList = null;
            try {

                adsList = adData.get();

                if(adsList != null)
                {
                    ArrayList<Ads> arrayList = null;
                    Gson gson = new Gson();
                    Type collectionType = new TypeToken<Collection<Ads>>() {
                    }.getType();
                    Collection<Ads> adsCollection = gson.fromJson(adsList, collectionType);
                    Ads[] adsArray = adsCollection.toArray(new Ads[adsCollection.size()]);

                    arrayList = new ArrayList<Ads>(Arrays.asList(adsArray));

                    for(int i = 0; i < arrayList.size(); i ++)
                    {
                        byte[] bytes = android.util.Base64.decode(arrayList.get(i).AdImageOne, 0);

                        Ads a =  arrayList.get(i);
                        a.AdImageBitmap  = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
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

    private void LoadingData(String adData) throws InterruptedException, ExecutionException {
        String adsList = adData;
        ArrayList<Ads> arrayList = null;
        if(adsList == null)
        {
            Toast.makeText(getContext(),"No Ads",Toast.LENGTH_SHORT).show();
            listview.setAdapter(null);
            tvNoAds.setVisibility(View.VISIBLE);
        }


        else {

            Gson gson = new Gson();
            Type collectionType = new TypeToken<Collection<Ads>>() {
            }.getType();
            Collection<Ads> adsCollection = gson.fromJson(adsList, collectionType);
            Ads[] adsArray = adsCollection.toArray(new Ads[adsCollection.size()]);

            arrayList = new ArrayList<Ads>(Arrays.asList(adsArray));

            for(int i = 0; i < arrayList.size(); i ++)
            {
                byte[] bytes = android.util.Base64.decode(arrayList.get(i).AdImageOne, 0);

                 Ads a =  arrayList.get(i);
                a.AdImageBitmap  = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }

            //then populate myListItems
        adbAds= new AdapterAds (getActivity(), 0, arrayList, getContext());
        if(adbAds == null)
        {
            Toast.makeText(getContext(),"No Ads",Toast.LENGTH_SHORT).show();
        }
        else
        {
            //listview.setDivider(null);
            listview.setAdapter(adbAds);
        }

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Globals g = Globals.getInstance();
        Boolean isNewBid = g.getFlagForNewBid();
        if(isNewBid)
        {
            adbAds.updateResults();
            g.setFlagForNewBid(false);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        if (view != null) {
            if ((ViewGroup)view.getParent() != null) {
                ((ViewGroup)view.getParent()).removeView(view);
            }
            return view;
        }

        view = inflater.inflate(R.layout.layout_adsfragment,container,false);
        return view;


    }

    public class GetAdsForCityAsyncTask extends AsyncTask<String, Void, String> {

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
                listview.addFooterView(footer,null,false);

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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(responseCode == 200)
            {
                if(counter == 0)
                {
                    try {
                        LoadingData(s);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    counter++;
                    // If task execution completed

                }
                else if(s != null)
                {
                    Thread thread = new ThreadGetMoreData();
                    thread.start();
                    //listview.removeFooterView(footer);
                }
                else if(s == null)
                    listview.removeFooterView(footer);
            }
            else if(counter == 0)
                tvNoAds.setVisibility(View.VISIBLE);

            if(dialog.isShowing()){
                // Dismiss/hide the progress dialog
                dialog.dismiss();
            }
            pullToRefresh.setRefreshing(false);
            if(refreshcounter >= 1)
                refreshcounter = 0;
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
        bufferedReader.close();
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
            jsonObject.accumulate("Category",CategoryName.trim());
        if(!Filter.isEmpty())
            jsonObject.accumulate("Filter",Filter.trim());



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

    private void SetDefaultMenuValues() {
        CategoryName = "";
        Filter = "";

        Globals g = Globals.getInstance();
        int categoryListCount = g.getCategorySubmenuCount();

            if(categoryListCount > 0)
            {
                for (int i = 0; i < categoryListCount; i++) {
                    MenuItem menuItemCategory = topIconMenu.findItem(R.id.action_category);
                    SubMenu subMenu = menuItemCategory.getSubMenu();
                    MenuItem subMenuItem = subMenu.getItem(i);
                    String subMenuTitle = subMenuItem.getTitle().toString().trim();
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
            String subMenuTitle = subMenuItem.getTitle().toString().trim();
            SpannableString s = new SpannableString(subMenuTitle);
            s.setSpan(new StyleSpan(Typeface.NORMAL), 0, s.length(), 0);
            subMenuItem.setTitle(s);
        }
    }

}
