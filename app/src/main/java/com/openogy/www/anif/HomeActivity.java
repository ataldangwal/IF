package com.openogy.www.anif;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.openogy.classes.BottomNavigationViewHelper;
import com.openogy.classes.CommonFunctions;
import com.openogy.classes.Globals;
import com.openogy.classes.UserLoginTokenDb;
import com.openogy.fragments.AdsFragment;
import com.openogy.fragments.NotificationsFragment;
import com.openogy.fragments.PostAdFragment;
import com.openogy.fragments.ProfileFragment;
import com.openogy.fragments.SearchFragment;
import com.openogy.fragments.WishlistFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HomeActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    List<String> CategoryList = null;
    SearchView searchView;
    HttpGetCategories getCategories;
    Fragment selectedFragment;
    String selectedCategory;
    String selectedFilter;
    boolean isAd = true;
    boolean isWish = false;
    Menu bottomNavigationMenu;
    Menu topIconMenu;
    private boolean isFirstFragment = true;
    AutoCompleteTextView autoComplete;
    TextView toolbarTitle;
    MenuItem item;
    Globals g;
    boolean firstTimeWishClick = true;
    FrameLayout layoutFrame;
    boolean firstTimeAdClick = true;
    boolean firstTimePostAdClick = true;
    boolean firstTimeNotificationsClick = true;
    boolean firstTimeProfileClick = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById(R.id.mainlayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });

        searchView = findViewById(R.id.search_toolbar);
        searchView.clearFocus();

        toolbarTitle = findViewById(R.id.toolbar_title);
        g = Globals.getInstance();
        layoutFrame = findViewById(R.id.fragment_container);
        //Set searchview hint fontsize
        LinearLayout linearLayout1 = (LinearLayout) searchView.getChildAt(0);
        LinearLayout linearLayout2 = (LinearLayout) linearLayout1.getChildAt(2);
        LinearLayout linearLayout3 = (LinearLayout) linearLayout2.getChildAt(1);
        autoComplete = (AutoCompleteTextView) linearLayout3.getChildAt(0);
        Typeface myCustomFont = Typeface.create("nunito_sans",Typeface.NORMAL);
        autoComplete.setTypeface(myCustomFont);
        autoComplete.setTextSize(13);


        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        bottomNavigationMenu = bottomNavigationView.getMenu();

        item =   bottomNavigationMenu.findItem(R.id.navigation_notifications);

        final Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //toolbar.setNavigationIcon(R.drawable.ic_toolbar);
        toolbar.setTitle("");
        toolbar.setSubtitle("");

        if (checkNetworkConnection()) {


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String url = CommonFunctions.API_URL + "Notifications/GetUnReadNotificationsForUser";
                    new GetNotificationsForUserAsyncTask().execute(url);
                }
            }, 4000);



        }

       getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AdsFragment(),"AdStack").addToBackStack(null).commit();
        setSupportActionBar(toolbar);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(searchView.isIconified())
                    toolbarTitle.setVisibility(View.VISIBLE);
                else
                    toolbarTitle.setVisibility(View.GONE);


            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                toolbarTitle.setVisibility(View.VISIBLE);
                return false;
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchView.requestFocus();
                String searchQuery = s.trim();
                if(searchQuery.trim().isEmpty())
                    return false;
                else
                {
                    Bundle bundle = new Bundle();
                    bundle.putString("SearchText", searchQuery);
                    Fragment searchFragment = new SearchFragment();
                    searchFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, searchFragment).addToBackStack(null).commit();
                    searchView.clearFocus();
                    isFirstFragment = false;
                    return true;
                }

            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            MenuItem homeItem = bottomNavigationMenu.findItem(R.id.navigation_home);
            MenuItem wishItem = bottomNavigationMenu.findItem(R.id.navigation_wishlist);
            MenuItem postItem = bottomNavigationMenu.findItem(R.id.navigation_postad);
            MenuItem notificationItem = bottomNavigationMenu.findItem(R.id.navigation_notifications);
            MenuItem profileItem = bottomNavigationMenu.findItem(R.id.navigation_userprofile);

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    item.setIcon(R.drawable.icons_market_filled);
                    isFirstFragment = false;
                    //SetDefaultMenuValues();
                    firstTimePostAdClick = true;
                    firstTimeNotificationsClick = true;
                    firstTimeWishClick = true;
                    firstTimeProfileClick = true;
                    //Setting search to its default position
                    searchView.setQuery("",false);
                    if(!searchView.isIconified())
                    {
                        searchView.setIconified(true);
                        toolbarTitle.setVisibility(View.VISIBLE);
                    }

                    wishItem.setIcon(R.drawable.icons_wish_list);
                    postItem.setIcon(R.drawable.icons_add);
                    profileItem.setIcon(R.drawable.icons_profile);
                    notificationItem.setIcon(R.drawable.icons_notification);
                    isAd = true;
                    isWish = false;

                    FragmentManager AdfragmentManager = getSupportFragmentManager();
                    Fragment f1 = AdfragmentManager.findFragmentByTag("AdStack");

                    if(f1!=null)
                    {
                        if(firstTimeAdClick)
                        {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f1,"AdStack").addToBackStack(null).commit();
                            firstTimeAdClick = false;
                        }
                        else
                        {
                            if(f1.getView()!= null)
                            {
                                if(f1.getView().findViewById(R.id.AdOrCommentListView)!=null)
                                {
                                    ListView adOrCommentListView = f1.getView().findViewById(R.id.AdOrCommentListView);
                                    adOrCommentListView.setSelection(0);
                                }
                            }


                        }

                    }


                    //AdfragmentManager.popBackStack("AdStack",0);

                       /* if (!adfragmentPopped){ //fragment not in back stack, create it.

                        }*/

                    break;
                case R.id.navigation_wishlist:
                    item.setIcon(R.drawable.icons_wish_list_filled);
                    isFirstFragment = false;
                    //SetDefaultMenuValues();
                    firstTimePostAdClick = true;
                    firstTimeNotificationsClick = true;
                    firstTimeProfileClick = true;
                    firstTimeAdClick = true;
                    //Setting search to its default position
                    searchView.setQuery("",false);
                    if(!searchView.isIconified())
                    {
                        searchView.setIconified(true);
                        toolbarTitle.setVisibility(View.VISIBLE);
                    }

                    homeItem.setIcon(R.drawable.icons_market);
                    postItem.setIcon(R.drawable.icons_add);
                    notificationItem.setIcon(R.drawable.icons_notification);
                    profileItem.setIcon(R.drawable.icons_profile);


                    isWish = true;
                    isAd = false;

                    FragmentManager WishFragmentManager = getSupportFragmentManager();
                    Fragment f = WishFragmentManager.findFragmentByTag("WishStack");
                if(f!=null)
                {
                    if(firstTimeWishClick)
                    {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f,"WishStack").addToBackStack(null).commit();
                        firstTimeWishClick = false;

                    }
                    else
                    {
                        if(f.getView()!=null)
                        {
                            if(f.getView().findViewById(R.id.WishListView)!=null)
                            {
                                ListView wishListView = f.getView().findViewById(R.id.WishListView);
                                wishListView.setSelection(0);
                            }
                        }


                    }

                }
                else
                {
                    selectedFragment = new WishlistFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WishlistFragment(),"WishStack").addToBackStack(null).commit();
                }

                        /*if (!wishfragmentPopped){ //fragment not in back stack, create it.

                            selectedFragment = new WishlistFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WishlistFragment()).addToBackStack("WishStack").commit();
                        }*/

                    break;
                case R.id.navigation_notifications:

                    notificationItem.setIcon(R.drawable.icons_notification_filled);
                    isFirstFragment = false;
                    //SetDefaultMenuValues();
                    firstTimePostAdClick = true;
                    firstTimeProfileClick = true;
                    firstTimeWishClick = true;
                    firstTimeAdClick = true;
                    //Setting search to its default position
                    searchView.setQuery("",false);
                    if(!searchView.isIconified())
                    {
                        searchView.setIconified(true);
                        toolbarTitle.setVisibility(View.VISIBLE);
                    }

                    homeItem.setIcon(R.drawable.icons_market);
                    postItem.setIcon(R.drawable.icons_add);
                    wishItem.setIcon(R.drawable.icons_wish_list);
                    profileItem.setIcon(R.drawable.icons_profile);

                    isAd = true;
                    isWish = false;

                    FragmentManager notificationFragmentManager = getSupportFragmentManager();
                    Fragment f3 = notificationFragmentManager.findFragmentByTag("NotificationStack");
                    if(f3!=null)
                    {
                        if(firstTimeNotificationsClick)
                        {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f3,"NotificationStack").addToBackStack(null).commit();
                            firstTimeNotificationsClick = false;

                        }
                        else
                        {
                            if(f3.getView()!=null)
                            {
                                if(f3.getView().findViewById(R.id.NotificationsListView)!=null)
                                {
                                    ListView notificationListView = f3.getView().findViewById(R.id.NotificationsListView);
                                    notificationListView.setSelection(0);
                                }
                            }
                        }

                    }
                    else
                    {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotificationsFragment(),"NotificationStack").addToBackStack(null).commit();
                    }

                    break;
                case R.id.navigation_postad:
                    item.setIcon(R.drawable.icons_add_filled);
                    isFirstFragment = false;
                    //SetDefaultMenuValues();
                    firstTimeProfileClick = true;
                    firstTimeNotificationsClick = true;
                    firstTimeWishClick = true;
                    firstTimeAdClick = true;
                    //Setting search to its default position
                    searchView.setQuery("",false);
                    if(!searchView.isIconified())
                    {
                        searchView.setIconified(true);
                        toolbarTitle.setVisibility(View.VISIBLE);
                    }

                    homeItem.setIcon(R.drawable.icons_market);
                    wishItem.setIcon(R.drawable.icons_wish_list);
                    profileItem.setIcon(R.drawable.icons_profile);
                    notificationItem.setIcon(R.drawable.icons_notification);
                    isAd = true;
                    isWish = false;

                    FragmentManager postadFragmentManager = getSupportFragmentManager();
                    Fragment f4 = postadFragmentManager.findFragmentByTag("PostAdStack");
                    if(f4!=null)
                    {

                        if(firstTimePostAdClick)
                        {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f4,"PostAdStack").addToBackStack(null).commit();
                            firstTimePostAdClick = false;

                        }
                        else
                        {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostAdFragment(),"PostAdStack").addToBackStack(null).commit();
                        }

                    }
                    else
                    {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostAdFragment(),"PostAdStack").addToBackStack(null).commit();
                    }

                    break;
                case R.id.navigation_userprofile:
                    item.setIcon(R.drawable.icons_profile_filled);
                    isFirstFragment = false;
                    //SetDefaultMenuValues();
                    firstTimePostAdClick = true;
                    firstTimeNotificationsClick = true;
                    firstTimeWishClick = true;
                    firstTimeAdClick = true;
                    //Setting search to its default position
                    searchView.setQuery("",false);
                    if(!searchView.isIconified())
                    {
                        searchView.setIconified(true);
                        toolbarTitle.setVisibility(View.VISIBLE);
                    }


                    notificationItem.setIcon(R.drawable.icons_notification);
                    homeItem.setIcon(R.drawable.icons_market);
                    postItem.setIcon(R.drawable.icons_add);
                    wishItem.setIcon(R.drawable.icons_wish_list);

                    isAd = true;
                    isWish = false;

                    /*FragmentManager profileFragmentManager = getSupportFragmentManager();
                    Fragment f5 = profileFragmentManager.findFragmentByTag("ProfileStack");
                    if(f5!=null)
                    {

                        if(firstTimeProfileClick)
                        {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f5,"ProfileStack").addToBackStack(null).commit();
                            firstTimeProfileClick = false;

                        }
                        else
                        {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment(),"ProfileStack").addToBackStack(null).commit();
                        }

                    }
                    else
                    {*/
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment(),"ProfileStack").addToBackStack(null).commit();
                    //}

                    break;
            }

            return true;
        }
    };

    public void SetDefaultMenuValues() {
        selectedCategory = null;
        selectedFilter = null;
        if(CategoryList!=null)
        {
            if(CategoryList.size() > 0)
            {
                for (int i = 0; i < CategoryList.size(); i++) {
                    MenuItem menuItemCategory = topIconMenu.findItem(R.id.action_category);
                    SubMenu subMenu = menuItemCategory.getSubMenu();
                    MenuItem subMenuItem = subMenu.getItem(i);
                    String subMenuTitle = subMenuItem.getTitle().toString();
                    SpannableString s = new SpannableString(subMenuTitle);
                    s.setSpan(new StyleSpan(Typeface.NORMAL), 0, s.length(), 0);
                    subMenuItem.setTitle(s);
                }
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

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.topiconmenu, menu);
        topIconMenu = menu;

        getCategories = (HttpGetCategories) new HttpGetCategories().execute();
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public void onBackPressed() {

        if(isFirstFragment)
        {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please double tap back button to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
        else
        {
            Globals g = Globals.getInstance();
            g.setFlagForBackPressed(true);

                if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    super.onBackPressed();
                }

        }

        searchView.setQuery("",false);
        if(!searchView.isIconified())
            searchView.setIconified(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        boolean isCategory = true;
        //Fragment selectedFragment = new Fragment();

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_category:
                return false;
            case R.id.action_settings:
                return false;
            case R.id.action_help:
                return false;
            case R.id.action_about_us:
                isCategory = false;
                isFirstFragment = false;
                Intent aboutusIntent = new Intent(HomeActivity.this, aboutus_activity.class);
                startActivity(aboutusIntent);
                return true;

            case R.id.action_faqs:
                isCategory = false;
                Intent faqIntent = new Intent(HomeActivity.this, faqs_activity.class);
                startActivity(faqIntent);
                return true;

            case R.id.action_contact_us:
                isCategory = false;
                isFirstFragment = false;
                Intent contactusIntent = new Intent(HomeActivity.this, contact_us_activity.class);
                startActivity(contactusIntent);
                return true;

            case R.id.action_privacy_policy:
                isCategory = false;
                isFirstFragment = false;
               // selectedFragment = new InboxFragment();
                Intent privacyPolicyIntent = new Intent(HomeActivity.this, privacy_policy_activity.class);
                startActivity(privacyPolicyIntent);
                return true;
            case R.id.action_tnc:
                isCategory = false;
                isFirstFragment = false;
                Intent tncIntent = new Intent(HomeActivity.this, terms_and_conditions_activity.class);
                startActivity(tncIntent);
                return true;
            case R.id.action_logout:
                Globals g = Globals.getInstance();
                g.setData(""); //resetting global variable

                UserLoginTokenDb db = new UserLoginTokenDb(this);
                db.removeToken(); //removing login_token in local database
                //Redirecting to login page after logout
                Intent intent = new Intent(HomeActivity.this, Login_Activity.class);
                startActivity(intent);
                return false;
            case R.id.action_Filter:
                return false;
            case R.id.action_no_filter:
                if(isAd)
                {
                    for (int i = 0; i < 4; i++)
                    {
                        MenuItem menuItemCategory = topIconMenu.findItem(R.id.action_Filter);
                        SubMenu subMenu = menuItemCategory.getSubMenu();
                        MenuItem subMenuItem = subMenu.getItem(i);
                        String subMenuTitle = subMenuItem.getTitle().toString().trim();
                        SpannableString s = new SpannableString(subMenuTitle);
                        s.setSpan(new StyleSpan(Typeface.NORMAL), 0, s.length(), 0);
                        subMenuItem.setTitle(s);
                    }

                    if(selectedCategory!=null)
                    {
                        if(selectedCategory.equalsIgnoreCase("All categories"))
                            selectedCategory = null;
                    }


                    Fragment fragment = newInstanceForAdFragment(selectedCategory == null ? "" : selectedCategory,"");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment,"AdStack").addToBackStack(null).commit();
                    return true;
                }

                if(isWish)
                {
                    return false;
                }

            case R.id.action_new_item:
                if (isAd) {
                    for (int i = 0; i < 4; i++)
                    {
                        MenuItem menuItemCategory = topIconMenu.findItem(R.id.action_Filter);
                        SubMenu subMenu = menuItemCategory.getSubMenu();
                        MenuItem subMenuItem = subMenu.getItem(i);
                        String subMenuTitle = subMenuItem.getTitle().toString().trim();
                        SpannableString s = new SpannableString(subMenuTitle);
                        s.setSpan(new StyleSpan(Typeface.NORMAL), 0, s.length(), 0);
                        subMenuItem.setTitle(s);
                    }

                    selectedFilter = item.getTitle().toString().trim();

                    SpannableString s = new SpannableString(selectedFilter);
                    s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), 0);
                    item.setTitle(s);

                    /*SpannableString ss = new SpannableString("New");
                    ss.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), 0);
                    item.setTitle(s);
                    selectedFilter = item.getTitle().toString();*/

                    Fragment fragment = newInstanceForAdFragment(selectedCategory == null ? "" : selectedCategory, selectedFilter == null ? "" : selectedFilter);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment,"AdStack").addToBackStack(null).commit();
                    return true;

                } else if (isWish)
                    return false;
            case R.id.action_used_item:
                if (isAd) {
                    for (int i = 0; i < 4; i++)
                    {
                        MenuItem menuItemCategory = topIconMenu.findItem(R.id.action_Filter);
                        SubMenu subMenu = menuItemCategory.getSubMenu();
                        MenuItem subMenuItem = subMenu.getItem(i);
                        String subMenuTitle = subMenuItem.getTitle().toString().trim();
                        SpannableString s = new SpannableString(subMenuTitle);
                        s.setSpan(new StyleSpan(Typeface.NORMAL), 0, s.length(), 0);
                        subMenuItem.setTitle(s);
                    }

                    selectedFilter = item.getTitle().toString().trim();

                    SpannableString s = new SpannableString(selectedFilter);
                    s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), 0);
                    item.setTitle(s);

                    SpannableString sss = new SpannableString("Used");
                    s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), 0);
                    item.setTitle(s);
                    selectedFilter = item.getTitle().toString().trim();
                    Fragment fragment = newInstanceForAdFragment(selectedCategory == null ? "" : selectedCategory, selectedFilter == null ? "" : selectedFilter);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment,"AdStack").addToBackStack(null).commit();
                    return true;
                } else if (isWish)
                    return false;
            case R.id.action_most_demanded:
                if (isAd) {
                    for (int i = 0; i < 4; i++)
                    {
                        MenuItem menuItemCategory = topIconMenu.findItem(R.id.action_Filter);
                        SubMenu subMenu = menuItemCategory.getSubMenu();
                        MenuItem subMenuItem = subMenu.getItem(i);
                        String subMenuTitle = subMenuItem.getTitle().toString().trim();
                        SpannableString s = new SpannableString(subMenuTitle);
                        s.setSpan(new StyleSpan(Typeface.NORMAL), 0, s.length(), 0);
                        subMenuItem.setTitle(s);
                    }

                    selectedFilter = item.getTitle().toString().trim();

                    SpannableString s = new SpannableString(selectedFilter);
                    s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), 0);
                    item.setTitle(s);


                    SpannableString ssss = new SpannableString("Most demanded");
                    s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), 0);
                    item.setTitle(s);
                    selectedFilter = item.getTitle().toString().trim();

                    Fragment fragment = newInstanceForAdFragment(selectedCategory == null ? "" : selectedCategory, selectedFilter == null ? "" : selectedFilter);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment,"AdStack").addToBackStack(null).commit();
                    return true;

                } else if (isWish)
                    return false;

        }
        if (isCategory)
        {
            for (int i = 0; i <= CategoryList.size(); i++)
            {
                MenuItem menuItemCategory = topIconMenu.findItem(R.id.action_category);
                SubMenu subMenu = menuItemCategory.getSubMenu();
                MenuItem subMenuItem = subMenu.getItem(i);
                String subMenuTitle = subMenuItem.getTitle().toString().trim();
                SpannableString s = new SpannableString(subMenuTitle);
                s.setSpan(new StyleSpan(Typeface.NORMAL), 0, s.length(), 0);
                subMenuItem.setTitle(s);
            }

            selectedCategory = item.getTitle().toString();

            if(!selectedCategory.equalsIgnoreCase("All categories"))
            {
                SpannableString s = new SpannableString(selectedCategory);
                s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), 0);
                item.setTitle(s);

            }

            if (isAd) {
                if(selectedCategory.equalsIgnoreCase("All categories"))
                    selectedCategory = "";

                if(selectedFilter!=null)
                {
                    if(selectedFilter.equalsIgnoreCase("No filter"))
                        selectedFilter = null;
                }


                Fragment fragment = newInstanceForAdFragment(selectedCategory, selectedFilter == null ? "" : selectedFilter);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment,"AdStack").addToBackStack(null).commit();
                return true;
            } else if (isWish) {
                if(selectedCategory.equalsIgnoreCase("All categories"))
                    selectedCategory = "";

                Fragment fragment = newInstanceForWishFragment(selectedCategory);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment,"WishStack").addToBackStack(null).commit();
                return true;
            }
        }

       // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack(null).commit();
        return true;
    }



    private class HttpGetCategories extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... params) {

            String str = CommonFunctions.API_URL + "Category";
            URLConnection urlConn;
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(str);
                urlConn = url.openConnection();
                HttpURLConnection httpurlconnection = (HttpURLConnection) urlConn;
                bufferedReader = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream()));
                int responsecode = httpurlconnection.getResponseCode();
                if (responsecode == HttpURLConnection.HTTP_OK) {
                    List<String> Data = new ArrayList<>();
                    StringBuffer stringBuffer = new StringBuffer();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        Data.add(line);
                    }
                    return Data;
                } else
                    return null;
            } catch (Exception ex) {
                Log.e("App", "yourDataTask", ex);
                return null;
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(List<String> categories) {
            super.onPostExecute(categories);

            MenuItem menuItemCategory = topIconMenu.findItem(R.id.action_category);

            //Adding custom submenu categories fetched from database
            SubMenu subMenu = menuItemCategory.getSubMenu();

            List<String> Categories;
            try {
                Categories = getCategories.get();
                if (Categories != null) {
                    Globals g = Globals.getInstance();

                    String cityString = Categories.toString().substring(3, Categories.toString().length() - 3).replace('"', ' ');
                    String[] cityStringArray = cityString.split(",");

                    CategoryList = new ArrayList<>();

                    for (String s :
                            cityStringArray) {

                        SpannableString sstring = new SpannableString(s);

                        TypefaceSpan typeface = new TypefaceSpan("nunito_sans");
                        sstring.setSpan(typeface,
                                0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        CategoryList.add(sstring.toString());
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if (CategoryList != null) {
                g.setCategorySubmenuCount(CategoryList.size());
                for (int i = 0; i < CategoryList.size(); i++)
                    subMenu.add(Menu.NONE, i + 1, Menu.NONE, CategoryList.get(i));
            } else
                subMenu.add(Menu.NONE, 1, Menu.NONE, "No Category");


        }
    }

    public static AdsFragment newInstanceForAdFragment(String CategoryName, String Filter) {
        AdsFragment adsFragment = new AdsFragment();

        Bundle args = new Bundle();
        args.putString("CategoryName", CategoryName);
        args.putString("Filter", Filter);
        adsFragment.setArguments(args);
        return adsFragment;

    }

    public static WishlistFragment newInstanceForWishFragment(String CategoryName) {
        WishlistFragment adsFragment = new WishlistFragment();

        Bundle args = new Bundle();
        args.putString("CategoryName", CategoryName);
        adsFragment.setArguments(args);
        return adsFragment;

    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isConnected = false;
        if (networkInfo != null && (isConnected = networkInfo.isConnected())) {
            isConnected = true;


        } else {
            isConnected = false;
        }

        return isConnected;
    }

    public class GetNotificationsForUserAsyncTask extends AsyncTask<String, Void, String> {

        /*private Dialog dialog = new Dialog(HomeActivity.this, android.R.style.Theme_Black);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

                View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.layout_progressbar, null);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(view);
                dialog.show();


        }*/

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

            // 5. return response message
            return conn.getResponseMessage();

        }

        private  JSONObject buidJsonObject() throws JSONException {

            Globals g = Globals.getInstance();
            String token = g.getData();
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("Token",token);
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
            Globals g = Globals.getInstance();
            if(s!=null)
            {
                if(s.equalsIgnoreCase("ok"))
                {
                    item.setIcon(R.drawable.iconss_notification_alarm);

                    g.setFlagForNewNotification(true);
                }
                else
                    g.setFlagForNewNotification(false);
            }


           /* if(dialog.isShowing()){
                // Dismiss/hide the progress dialog
                dialog.dismiss();
            }*/

        }


    }
}
