package com.setyawan.moviedb;


import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.setyawan.moviedb.adapter.FavoriteShowAdapter;
import com.setyawan.moviedb.adapter.SearchShowsAdapter;
import com.setyawan.moviedb.adapter.ShowsAdapter;
import com.setyawan.moviedb.database.DBHelper;
import com.setyawan.moviedb.model.Genre;
import com.setyawan.moviedb.model.GenreList;
import com.setyawan.moviedb.model.Shows;
import com.setyawan.moviedb.model.ShowsList;
import com.setyawan.moviedb.utils.ApiClient;
import com.setyawan.moviedb.utils.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowsFragment extends Fragment {
    private static final String TYPE_OF = "tv";
    private static final String TYPE_AIRING_TODAY = "airing_today";
    private static final String TYPE_ON_TV = "on_tv";
    private static final String TYPE_POPULAR = "popular";
    private static final String TYPE_TOP_RATED = "top_rated";
    private static final String TYPE_FAVORITE = "favorite";

    private RecyclerView recyclerView;
    private ShowsAdapter adapter;
    private List<Shows> showsList = new ArrayList<>();
    private ProgressBar progress;
    private DBHelper dbHelper;
    private TextView txtSearch, txtNoResult;
    private int currentPage = 1;
    private String type = TYPE_AIRING_TODAY;
    private Call<ShowsList> call = null;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;

    public ShowsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_show,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("TV Shows");
        setHasOptionsMenu(true);
        BottomNavigationView navigation = (BottomNavigationView) view.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(getContext());
        gridLayoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new ShowsAdapter(showsList,getContext());
        recyclerView.setAdapter(adapter);
        setRecyclerViewScrollListener();

        progress = (ProgressBar) view.findViewById(R.id.progress);
        dbHelper = new DBHelper(getContext());
        txtSearch = (TextView) view.findViewById(R.id.query);
        txtNoResult = (TextView) view.findViewById(R.id.no_search);

        getShows();
        initGenre();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search,menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length()>0){
                    getSearch(newText);
                }
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if(recyclerView.getLayoutManager()==linearLayoutManager) {
                    txtSearch.setVisibility(View.GONE);
                    txtNoResult.setVisibility(View.GONE);
                    recyclerView.setAdapter(null);
                    currentPage = 1;
                    getShows();
                }
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void getShows(){
        ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        switch (type) {
            case TYPE_AIRING_TODAY:
                call = apiInterface.getAiringToday(currentPage);
                break;
            case TYPE_ON_TV:
                call = apiInterface.getOnTv(currentPage);
                break;
            case TYPE_POPULAR:
                call = apiInterface.getPopularShows(currentPage);
                break;
            case TYPE_TOP_RATED:
                call = apiInterface.getTopRatedShows(currentPage);
                break;
            case TYPE_FAVORITE:
                if(recyclerView.getLayoutManager() == linearLayoutManager) recyclerView.setLayoutManager(gridLayoutManager);
                recyclerView.setAdapter(new FavoriteShowAdapter(getContext()));
                return;
        }

        progress.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<ShowsList>() {
            @Override
            public void onResponse(Call<ShowsList> call, Response<ShowsList> response) {
                progress.setVisibility(View.GONE);
                ShowsList showsList = response.body();
                if(recyclerView.getLayoutManager() == linearLayoutManager) recyclerView.setLayoutManager(gridLayoutManager);
                if(currentPage==1){
                    adapter=new ShowsAdapter(showsList.getResults(),getContext());
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.addMovieList(showsList.getResults());
                }
            }

            @Override
            public void onFailure(Call<ShowsList> call, Throwable t) {
                progress.setVisibility(View.GONE);
            }
        });
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            txtSearch.setVisibility(View.GONE);
            txtNoResult.setVisibility(View.GONE);
            currentPage=1;
            if(id==R.id.airing_today) {
                if(type.equals(TYPE_AIRING_TODAY) && recyclerView.getLayoutManager() != linearLayoutManager) return false;
                type = TYPE_AIRING_TODAY;
            } else if(id==R.id.on_tv) {
                if(type.equals(TYPE_ON_TV) && recyclerView.getLayoutManager() != linearLayoutManager) return false;
                type = TYPE_ON_TV;
            } else if(id==R.id.popular){
                if(type.equals(TYPE_POPULAR) && recyclerView.getLayoutManager() != linearLayoutManager) return false;
                type = TYPE_POPULAR;
            } else if(id==R.id.top_rated) {
                if(type.equals(TYPE_TOP_RATED) && recyclerView.getLayoutManager() != linearLayoutManager) return false;
                type = TYPE_TOP_RATED;
            } else if(id==R.id.favorite) {
                if(type.equals(TYPE_FAVORITE) && recyclerView.getLayoutManager() != linearLayoutManager) return false;
                type = TYPE_FAVORITE;
            } else {
                return false;
            }
            getShows();
            return true;
        }

    };

    private void initGenre(){
        ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        Call<GenreList> call = apiInterface.getGenre(TYPE_OF);

        call.enqueue(new Callback<GenreList>() {
            @Override
            public void onResponse(Call<GenreList> call, Response<GenreList> response) {
                List<Genre> list = response.body().getGenres();
                for (Genre g:list) {
                    if(!dbHelper.isGenre(g.getId())){
                        dbHelper.addGenre(g.getId(),g.getName());
                    }
                }
                dbHelper.close();
            }

            @Override
            public void onFailure(Call<GenreList> call, Throwable t) {

            }
        });
    }

    public void getSearch(String query) {
        currentPage = 1;
        txtNoResult.setVisibility(View.GONE);
        recyclerView.setAdapter(null);
        recyclerView.setLayoutManager(linearLayoutManager);
        txtSearch.setVisibility(View.VISIBLE);
        txtSearch.setText("Result for: \""+query+"\"");
        progress.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        Call<ShowsList> call = apiInterface.getSearchShows(query);
        call.enqueue(new Callback<ShowsList>() {
            @Override
            public void onResponse(Call<ShowsList> call, Response<ShowsList> response) {
                progress.setVisibility(View.GONE);
                if(response.body().getResults().size()==0){
                    txtNoResult.setVisibility(View.VISIBLE);
                } else {
                    txtNoResult.setVisibility(View.GONE);
                    recyclerView.setAdapter(new SearchShowsAdapter(response.body().getResults(),getContext()));
                }
            }

            @Override
            public void onFailure(Call<ShowsList> call, Throwable t) {

            }
        });
    }

    private void setRecyclerViewScrollListener(){
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int totalItem = recyclerView.getLayoutManager().getItemCount();
                if(totalItem == gridLayoutManager.findLastVisibleItemPosition()+1
                        && recyclerView.getLayoutManager()==gridLayoutManager
                        && !type.equals(TYPE_FAVORITE)) {
                    currentPage+=1;
                    getShows();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(type.equals(TYPE_FAVORITE) && recyclerView.getLayoutManager() == gridLayoutManager) {
            recyclerView.setAdapter(new FavoriteShowAdapter(getContext()));
        }
    }
}
