package com.openogy.fragments;

import android.app.Dialog;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.openogy.classes.UserProfileWishesAdapter;
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

public class UserWishListFragment extends Fragment {

    ListView WishListView;
    private int offset = 0;
    private int responseCode;
    private int counter = 0;
    public View footer;
    public Handler mHandler;
    public boolean isLoading = false;
    public UserProfileWishesAdapter adbWish;
    private GetWishesForUserAsyncTask wishData;
    private TextView noWishes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_userwishlistfragment,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        noWishes = getView().findViewById(R.id.tvNoWishes);

        WishListView = getView().findViewById(R.id.WishesListView);
        footer = getLayoutInflater().inflate(R.layout.listview_footer, WishListView,false);


        String urlForGettingAds = CommonFunctions.API_URL + "WishList/GetWishesForCurrentUser";
        wishData = (GetWishesForUserAsyncTask) new GetWishesForUserAsyncTask().execute(urlForGettingAds);

        mHandler = new MyHandler();

        WishListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


            }
        });


        WishListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView,  int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if(absListView.getLastVisiblePosition() == totalItemCount - 1 && WishListView.getCount() >= 7 && isLoading == false)
                {
                    isLoading = true;
                    offset = offset + 7;

                    String url = CommonFunctions.API_URL + "WishList/GetWishesForCurrentUser";
                    wishData = (GetWishesForUserAsyncTask) new GetWishesForUserAsyncTask().execute(url);
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
                    WishListView.addFooterView(footer,null,false);
                    break;
                case 1:
                    //Update data adapter and UI
                    adbWish.addListItemToAdapter((ArrayList<Wishes>)msg.obj);
                    //Renove Footer after update
                    WishListView.removeFooterView(footer);
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

                adsList = wishData.get();

                if(adsList != null)
                {
                    ArrayList<Wishes> arrayList = null;
                    Gson gson = new Gson();
                    Type collectionType = new TypeToken<Collection<Wishes>>() {
                    }.getType();
                    Collection<Wishes> adsCollection = gson.fromJson(adsList, collectionType);
                    Wishes[] adsArray = adsCollection.toArray(new Wishes[adsCollection.size()]);

                    arrayList = new ArrayList<Wishes>(Arrays.asList(adsArray));

                    for(int i = 0; i < arrayList.size(); i ++)
                    {
                        if(arrayList.get(i).WishImage != null)
                        {
                            byte[] bytes = android.util.Base64.decode(arrayList.get(i).WishImage, 0);

                            Wishes a =  arrayList.get(i);
                            a.WishImageBitmap  = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
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

    private void LoadingData() throws InterruptedException, ExecutionException {
        String adsList = wishData.get();
        ArrayList<Wishes> arrayList = null;
        if(adsList == null)
            Toast.makeText(getContext(),"No Wishes",Toast.LENGTH_SHORT).show();
        else {

            Gson gson = new Gson();
            Type collectionType = new TypeToken<Collection<Wishes>>() {
            }.getType();
            Collection<Wishes> adsCollection = gson.fromJson(adsList, collectionType);
            Wishes[] adsArray = adsCollection.toArray(new Wishes[adsCollection.size()]);

            arrayList = new ArrayList<Wishes>(Arrays.asList(adsArray));

            for(int i = 0; i < arrayList.size(); i ++)
            {
                if(arrayList.get(i).WishImage != null)
                {
                    byte[] bytes = android.util.Base64.decode(arrayList.get(i).WishImage, 0);

                    Wishes a =  arrayList.get(i);
                    a.WishImageBitmap  = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                }

            }

            //then populate myListItems
            adbWish = new UserProfileWishesAdapter(getActivity(), 0, arrayList, getContext());
            if(adbWish == null)
            {
                Toast.makeText(getContext(),"No Wishes",Toast.LENGTH_SHORT).show();
            }
            else
            {
                //BidListView.setDivider(null);
                WishListView.setAdapter(adbWish);
            }

        }
    }

    public class GetWishesForUserAsyncTask extends AsyncTask<String, Void, String> {

        private Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(counter == 0)
            {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_progressbar, null);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(view);
                dialog.show();
            }
            else
                WishListView.addFooterView(footer,null,false);


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
                noWishes.setVisibility(View.GONE);
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
                    WishListView.removeFooterView(footer);
            }

            if(dialog.isShowing()){
                // Dismiss/hide the progress dialog
                dialog.dismiss();
            }

        }


    }
}
