package com.setyawan.moviedb;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.setyawan.moviedb.adapter.FavoriteAdapter;
import com.setyawan.moviedb.adapter.MovieAdapter;
import com.setyawan.moviedb.adapter.SearchAdapter;
import com.setyawan.moviedb.database.DBHelper;
import com.setyawan.moviedb.model.Genre;
import com.setyawan.moviedb.model.GenreList;
import com.setyawan.moviedb.model.Movie;
import com.setyawan.moviedb.model.MovieList;
import com.setyawan.moviedb.utils.ApiClient;
import com.setyawan.moviedb.utils.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TYPE_NOW_PLAYING = "now_playing";
    private static final String TYPE_UPCOMING = "upcoming";
    private static final String TYPE_POPULAR = "popular";
    private static final String TYPE_TOP_RATED = "top_rated";
    private static final String TYPE_FAVORITE = "favorite";

    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private ApiInterface apiInterface;
    private List<Movie> movieList = new ArrayList<>();
    private ProgressBar progress;
    private DBHelper dbHelper;
    private TextView txtSearch, txtNoResult;
    private int currentPage = 1;
    private String type = TYPE_NOW_PLAYING;
    private Call<MovieList> call = null;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        gridLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new MovieAdapter(movieList,MainActivity.this);
        recyclerView.setAdapter(adapter);
        setRecyclerViewScrollListener();

        progress = (ProgressBar) findViewById(R.id.progress);
        dbHelper = new DBHelper(MainActivity.this);
        txtSearch = (TextView) findViewById(R.id.query);
        txtNoResult = (TextView) findViewById(R.id.no_search);

        getMovie();
        initGenre();

    }

    private void getMovie(){
        apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        switch (type) {
            case TYPE_POPULAR:
                call = apiInterface.getPopular(currentPage);
                break;
            case TYPE_TOP_RATED:
                call = apiInterface.getTopRated(currentPage);
                break;
            case TYPE_UPCOMING:
                call = apiInterface.getUpcoming(currentPage);
                break;
            case TYPE_NOW_PLAYING:
                call = apiInterface.getNowPlaying(currentPage);
                break;
            case TYPE_FAVORITE:
                recyclerView.setAdapter(new FavoriteAdapter(MainActivity.this));
                return;
        }

        progress.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                progress.setVisibility(View.GONE);
                MovieList movieList = response.body();
                if(recyclerView.getLayoutManager() == linearLayoutManager) recyclerView.setLayoutManager(gridLayoutManager);
                if(currentPage==1){
                    adapter=new MovieAdapter(movieList.getMovies(),MainActivity.this);
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.addMovieList(movieList.getMovies());
                }
            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable t) {
                progress.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search,menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
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
                    getMovie();
                }
                return true;
            }
        });
        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            txtSearch.setVisibility(View.GONE);
            txtNoResult.setVisibility(View.GONE);
            currentPage=1;

            if(id==R.id.popular){
                if(type.equals(TYPE_POPULAR) && recyclerView.getLayoutManager() != linearLayoutManager) return false;
                type = TYPE_POPULAR;
            } else if(id==R.id.top_rated) {
                if(type.equals(TYPE_TOP_RATED) && recyclerView.getLayoutManager() != linearLayoutManager) return false;
                type = TYPE_TOP_RATED;
            } else if(id==R.id.upcoming) {
                if(type.equals(TYPE_UPCOMING) && recyclerView.getLayoutManager() != linearLayoutManager) return false;
                type = TYPE_UPCOMING;
            } else if(id==R.id.now_playing) {
                if(type.equals(TYPE_NOW_PLAYING) && recyclerView.getLayoutManager() != linearLayoutManager) return false;
                type = TYPE_NOW_PLAYING;
            } else if(id==R.id.favorite) {
                if(type.equals(TYPE_FAVORITE) && recyclerView.getLayoutManager() != linearLayoutManager) return false;
                type = TYPE_FAVORITE;
            } else {
                return false;
            }
            getMovie();
            return true;
        }

    };

    private void initGenre(){
        ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        Call<GenreList> call = apiInterface.getGenre("movie");

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
        Call<MovieList> call = apiInterface.getSearch(query);
        call.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                progress.setVisibility(View.GONE);
                if(response.body().getMovies().size()==0){
                    txtNoResult.setVisibility(View.VISIBLE);
                } else {
                    txtNoResult.setVisibility(View.GONE);
                    recyclerView.setAdapter(new SearchAdapter(response.body().getMovies(),MainActivity.this));
                }
            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable t) {

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
                    getMovie();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(type.equals(TYPE_FAVORITE)) {
            recyclerView.setAdapter(new FavoriteAdapter(MainActivity.this));
        }
    }
}
