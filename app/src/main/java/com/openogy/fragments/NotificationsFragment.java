package com.openogy.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.openogy.classes.CommonFunctions;
import com.openogy.classes.Globals;
import com.openogy.classes.Notifications;
import com.openogy.classes.NotificationsAdapter;
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
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

public class NotificationsFragment extends Fragment {

    View view;
    ListView NotificationsListView;
    private int offset = 0;
    private int responseCode;
    private int counter = 0;
    public View footer;
    public Handler mHandler;
    public boolean isLoading = false;
    public NotificationsAdapter adbNotifications;
    private GetNotificationsForUserAsyncTask notificationsData;
    private TextView noNotifications;
    SwipeRefreshLayout pullToRefresh;
    private int notificationRefreshCounter = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view != null) {
            if (view.getParent() != null) {
                ((ViewGroup)view.getParent()).removeView(view);
            }
            return view;
        }

        view = inflater.inflate(R.layout.layout_notificationsfragment,container,false);
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Setting Icon to filled when redirects from post wish page
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.navigation);
        Menu bottomNavigationMenu =  bottomNavigationView.getMenu();

        //Changing icons of the bottomnavigationitems
        MenuItem item =   bottomNavigationMenu.findItem(R.id.navigation_notifications);
        MenuItem adItem = bottomNavigationMenu.findItem(R.id.navigation_home);
        MenuItem postItem = bottomNavigationMenu.findItem(R.id.navigation_postad);
        MenuItem wishItem = bottomNavigationMenu.findItem(R.id.navigation_wishlist);
        MenuItem profileItem = bottomNavigationMenu.findItem(R.id.navigation_userprofile);
        item.setIcon(R.drawable.icons_notification_filled);
        adItem.setIcon(R.drawable.icons_market);
        postItem.setIcon(R.drawable.icons_add);
        wishItem.setIcon(R.drawable.icons_wish_list);
        profileItem.setIcon(R.drawable.icons_profile);

        Globals g = Globals.getInstance();
        if (g.getFlagForBackPressed())
        {
            g.setFlagForBackPressed(false);
            return;
        };

        noNotifications = getView().findViewById(R.id.tvNoNotifications);

        NotificationsListView = getView().findViewById(R.id.NotificationsListView);
        pullToRefresh = getView().findViewById(R.id.pullToRefresh);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                offset = 0;
                counter = 0;
                isLoading = false;
                notificationRefreshCounter = 1;
                String url = CommonFunctions.API_URL + "Notifications/GetNotificationsForUser";
                notificationsData = (GetNotificationsForUserAsyncTask) new GetNotificationsForUserAsyncTask().execute(url);
            }
        });

        footer = getLayoutInflater().inflate(R.layout.listview_footer, NotificationsListView,false);


        String urlForGettingNotifications = CommonFunctions.API_URL + "Notifications/GetNotificationsForUser";
        notificationsData = (GetNotificationsForUserAsyncTask) new GetNotificationsForUserAsyncTask().execute(urlForGettingNotifications);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new MarkNotificationOldAsyncTask().execute();
            }
        }, 3000);

        mHandler = new MyHandler();

        NotificationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });


        NotificationsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView,  int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if(absListView.getLastVisiblePosition() == totalItemCount - 1 && NotificationsListView.getCount() >= 10 && isLoading == false)
                {
                    isLoading = true;
                    offset = offset + 10;

                    String url = CommonFunctions.API_URL + "Notifications/GetNotificationsForUser";
                    notificationsData = (GetNotificationsForUserAsyncTask) new GetNotificationsForUserAsyncTask().execute(url);
                }
            }
        });
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //Add loading view during search processing
                    NotificationsListView.addFooterView(footer,null,false);
                    break;
                case 1:
                    //Update data adapter and UI
                    adbNotifications.addListItemToAdapter((ArrayList<Notifications>)msg.obj);
                    //Renove Footer after update
                    NotificationsListView.removeFooterView(footer);
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

                adsList = notificationsData.get();

                if(adsList != null)
                {
                    ArrayList<Notifications> arrayList = null;
                    Gson gson = new Gson();
                    Type collectionType = new TypeToken<Collection<Notifications>>() {
                    }.getType();
                    Collection<Notifications> adsCollection = gson.fromJson(adsList, collectionType);
                    Notifications[] adsArray = adsCollection.toArray(new Notifications[adsCollection.size()]);

                    arrayList = new ArrayList<Notifications>(Arrays.asList(adsArray));


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

    private void LoadingData() throws InterruptedException, ExecutionException {
        String adsList = notificationsData.get();
        ArrayList<Notifications> arrayList = null;
        if(adsList == null)
            Toast.makeText(getContext(),"No Notifications",Toast.LENGTH_SHORT).show();
        else {

            Gson gson = new Gson();
            Type collectionType = new TypeToken<Collection<Notifications>>() {
            }.getType();
            Collection<Notifications> adsCollection = gson.fromJson(adsList, collectionType);
            Notifications[] adsArray = adsCollection.toArray(new Notifications[adsCollection.size()]);

            arrayList = new ArrayList<Notifications>(Arrays.asList(adsArray));

            //then populate myListItems
            adbNotifications = new NotificationsAdapter(getActivity(), 0, arrayList, getContext());
            if(adbNotifications == null)
            {
                Toast.makeText(getContext(),"No Notifications",Toast.LENGTH_SHORT).show();
            }
            else
            {
                NotificationsListView.setAdapter(adbNotifications);
            }

        }
    }

    public class GetNotificationsForUserAsyncTask extends AsyncTask<String, Void, String> {

        private Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(counter == 0 && notificationRefreshCounter == 0)
            {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_progressbar, null);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(view);
                dialog.show();
            }
            else if(notificationRefreshCounter == 0)
                NotificationsListView.addFooterView(footer,null,false);


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
                noNotifications.setVisibility(View.GONE);
                if(counter == 0)
                {
                    try {
                        LoadingData();
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
                    NotificationsListView.removeFooterView(footer);
            }

            if(dialog.isShowing()){
                // Dismiss/hide the progress dialog
                dialog.dismiss();
            }
            //adbNotifications.notifyDataSetChanged();
            pullToRefresh.setRefreshing(false);
            if(notificationRefreshCounter >= 1)
                notificationRefreshCounter = 0;

        }


    }

    public class MarkNotificationOldAsyncTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... params) {


            try {
                String urlAPI = CommonFunctions.API_URL + "Notifications/MarkNotificationOld";
                URL url = new URL(urlAPI);
                //Create URL connection
                HttpURLConnection conn = null;
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                //Create Json Object
                JSONObject jsonObject = new JSONObject();
                Globals g = Globals.getInstance();
                String token = g.getData();
                jsonObject.accumulate("Token",token);

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
            super.onPostExecute(s);
            Globals g = Globals.getInstance();
            g.setFlagForNewNotification(false);

        }
    }
}
