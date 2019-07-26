package com.openogy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import com.openogy.www.anif.R;

public class SearchFragment extends Fragment {

    private Button btnAds;
    private Button btnWishList;
    private Button btnUsers;
    private SearchView searchView;
    private View rootView;
    private String searchText;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        searchView = getActivity().findViewById(R.id.search_toolbar);
        rootView = getActivity().findViewById(R.id.fragment_container);

        btnAds = getView().findViewById(R.id.btnAd);
        btnWishList = getView().findViewById(R.id.btnWishList);
        btnUsers = getView().findViewById(R.id.btnUsers);
        searchText = getArguments().getString("SearchText");

        btnAds.setTextSize(18);
        Bundle bundle = new Bundle();
        bundle.putString("SearchText", searchText);
        Fragment fragment = new SearchAdsFragment();
        fragment.setArguments(bundle);
        btnAds.requestFocus();
        getChildFragmentManager().beginTransaction().replace(R.id.Framelayout1, fragment).addToBackStack(null).commit();

        btnAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAds.setTextSize(18);
                btnWishList.setTextSize(15);
                btnUsers.setTextSize(15);
                Bundle bundle = new Bundle();
                bundle.putString("SearchText", searchText);
                Fragment fragment = new SearchAdsFragment();
                fragment.setArguments(bundle);
                getChildFragmentManager().beginTransaction().replace(R.id.Framelayout1,fragment).addToBackStack(null).commit();
            }
        });
        btnWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAds.setTextSize(15);
                btnWishList.setTextSize(18);
                btnUsers.setTextSize(15);
                Bundle bundle = new Bundle();
                bundle.putString("SearchText", searchText);
                Fragment fragment = new SearchWishListFragment();
                fragment.setArguments(bundle);
                getChildFragmentManager().beginTransaction().replace(R.id.Framelayout1,fragment).addToBackStack(null).commit();
            }
        });
        btnUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAds.setTextSize(15);
                btnWishList.setTextSize(15);
                btnUsers.setTextSize(18);
                Bundle bundle = new Bundle();
                bundle.putString("SearchText", searchText);
                Fragment fragment = new SearchUsersFragment();
                fragment.setArguments(bundle);
                getChildFragmentManager().beginTransaction().replace(R.id.Framelayout1,fragment).addToBackStack(null).commit();
            }
        });
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_search,container,false);
    }
}
