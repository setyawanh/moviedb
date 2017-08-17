package com.setyawan.moviedb;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.setyawan.moviedb.adapter.CastAdapter;
import com.setyawan.moviedb.adapter.SeasonAdapter;
import com.setyawan.moviedb.adapter.TrailerAdapter;
import com.setyawan.moviedb.database.DBHelper;
import com.setyawan.moviedb.model.Cast;
import com.setyawan.moviedb.model.CastList;
import com.setyawan.moviedb.model.Season;
import com.setyawan.moviedb.model.Shows;
import com.setyawan.moviedb.model.Trailer;
import com.setyawan.moviedb.model.TrailerList;
import com.setyawan.moviedb.utils.ApiClient;
import com.setyawan.moviedb.utils.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailShowsActivity extends AppCompatActivity {
    private CollapsingToolbarLayout collapsingToolbar;
    private RecyclerView trailerView, seasonView, castView;
    private List<Trailer> trailerList;
    private List<Cast> castList;
    private ImageView imgPoster;
    private ProgressBar pbTrailers, pbSeason, pbCast;
    private FloatingActionButton fab;
    private Shows shows;
    private DBHelper mDbHelper;
    private SeasonAdapter seasonAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_shows);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Binding
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        ImageView imgHeader = (ImageView) findViewById(R.id.backdrop);
        imgPoster = (ImageView) findViewById(R.id.poster);
        TextView txtTitle = (TextView) findViewById(R.id.title);
        TextView txtOverview = (TextView) findViewById(R.id.overview);
        TextView txtDate = (TextView) findViewById(R.id.date);
        TextView txtGenre = (TextView) findViewById(R.id.genre);
        trailerView = (RecyclerView) findViewById(R.id.trailer);
        seasonView = (RecyclerView) findViewById(R.id.season);
        castView = (RecyclerView) findViewById(R.id.casts);
        pbTrailers = (ProgressBar) findViewById(R.id.pb_trailers);
        pbSeason = (ProgressBar) findViewById(R.id.pb_season);
        pbCast = (ProgressBar) findViewById(R.id.pb_cast);
        trailerView.setNestedScrollingEnabled(false);
        seasonView.setNestedScrollingEnabled(false);
        castView.setNestedScrollingEnabled(false);
        fab = (FloatingActionButton) findViewById(R.id.fab_yes);

        //setup sqlite
        mDbHelper = new DBHelper(this);

        seasonAdapter = new SeasonAdapter(DetailShowsActivity.this, new ArrayList<Season>());

        // Get intent
        Intent i = getIntent();
        shows = new GsonBuilder().create().fromJson(i.getStringExtra("shows"),Shows.class);
        collapsingToolbar.setTitle(shows.getName());
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        Picasso.with(this)
                .load(ApiInterface.BASE_BACK_URL + shows.getBackdropPath())
                .placeholder(R.drawable.poster_placeholder)
                .into(imgHeader);

        Picasso.with(this)
                .load(ApiInterface.BASE_IMG_URL + shows.getPosterPath())
                .placeholder(R.drawable.poster_placeholder)
                .into(imgPoster, new it.sephiroth.android.library.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) imgPoster.getDrawable()).getBitmap();
                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            public void onGenerated(Palette palette) {
                                applyPalette(palette);
                            }
                        });
                    }

                    @Override
                    public void onError() {

                    }
                });

        String title = (shows.getName().equals(shows.getOriginalName()))? "<b>"+ shows.getName()+"</b>" : "<b>"+ shows.getName()+"</b><br><i>("+ shows.getOriginalName()+")</i>";

        txtTitle.setText(Html.fromHtml(title));
        txtDate.setText(shows.getFirstAirDate().split("-")[0] + " \u25CF " + String.valueOf(shows.getVoteAverage()));
        txtGenre.setText(mDbHelper.getGenre(shows.getGenreIds()));

        String overview = (shows.getOverview().equals(""))? "No synopsis found" : shows.getOverview();
        txtOverview.setText(overview);
        trailerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        castView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        seasonView.setLayoutManager(new LinearLayoutManager(this));

        loadJSON();

        // FAB (favorite/unfaforite)
        cekFavorite();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDbHelper.isFavorite(shows.getId())) {
                    mDbHelper.deleteFavorite(shows.getId());
                    Snackbar.make(view, "Removed from favorite lists", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    fab.setImageResource(R.drawable.ic_fav_no);
                } else {
                    fab.setImageResource(R.drawable.ic_fav_yes);
                    mDbHelper.addFavoriteShow(shows.getId(),new GsonBuilder().create().toJson(shows));
                    Snackbar.make(view, "Added to favorite lists", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_share:
                Intent i = new Intent(Intent.ACTION_SEND);
                String txt = "Check this out\n\""+ shows.getName()+"\"\n\nhttps://www.themoviedb.org/shows/"+ shows.getId();
                i.putExtra(Intent.EXTRA_TEXT,txt);
                i.setType("text/plain");
                startActivity(Intent.createChooser(i,"Share \""+ shows.getName()+"\""));
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadJSON() {
        int id = shows.getId();

        ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        // get trailer list
        Call<TrailerList> tCall = apiInterface.getTrailer("tv",id);
        pbTrailers.setVisibility(View.VISIBLE);
        tCall.enqueue(new Callback<TrailerList>() {
            @Override
            public void onResponse(Call<TrailerList> call, Response<TrailerList> response) {
                pbTrailers.setVisibility(View.GONE);
                trailerList = response.body().getResults();
                TrailerAdapter trailerAdapter = new TrailerAdapter(DetailShowsActivity.this,trailerList);
                trailerView.setAdapter(trailerAdapter);
                TextView txt = (TextView) findViewById(R.id.no_trailer);
                if(trailerList.size()==0) txt.setVisibility(View.VISIBLE);
            }
            @Override
            public void onFailure(Call<TrailerList> call, Throwable t) {
                pbTrailers.setVisibility(View.GONE);
            }
        });

        // get cast list
        Call<CastList> cCall = apiInterface.getCast("tv",id);
        pbCast.setVisibility(View.VISIBLE);
        cCall.enqueue(new Callback<CastList>() {
            @Override
            public void onResponse(Call<CastList> call, Response<CastList> response) {
                pbCast.setVisibility(View.GONE);
                castList = response.body().getCast();
                CastAdapter castAdapter = new CastAdapter(DetailShowsActivity.this,castList);
                castView.setAdapter(castAdapter);
                TextView txt = (TextView) findViewById(R.id.no_cast);
                if(castList.size()==0) txt.setVisibility(View.VISIBLE);
            }
            @Override
            public void onFailure(Call<CastList> call, Throwable t) {
                pbCast.setVisibility(View.GONE);
            }
        });


        // get season list
        seasonView.setAdapter(seasonAdapter);
        pbSeason.setVisibility(View.VISIBLE);
        getEpisodes(apiInterface,id,0);

    }

    public void cekFavorite(){
        if (!mDbHelper.isFavorite(shows.getId())){
            fab.setImageResource(R.drawable.ic_fav_no);
        } else  {
            fab.setImageResource(R.drawable.ic_fav_yes);
        }
    }

    private void applyPalette(Palette palette) {
        int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
        int primary = getResources().getColor(R.color.colorPrimary);
        int lightVibrantColor = palette.getLightVibrantColor(getResources().getColor(android.R.color.white));
        int vibrantColor = palette.getVibrantColor(getResources().getColor(R.color.colorAccent));

        collapsingToolbar.setContentScrimColor(palette.getDarkMutedColor(primaryDark));
        LinearLayout bar = (LinearLayout) findViewById(R.id.bar);
        bar.setBackgroundColor(palette.getDarkMutedColor(primaryDark));
        fab.setRippleColor(lightVibrantColor);
        fab.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));
        seasonAdapter.setColor(palette.getDarkMutedColor(primaryDark));
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    public void getEpisodes(final ApiInterface apiInterface, final int id, final int seasonNumber){
        Call<Season> sCall = apiInterface.getSeason(id,seasonNumber);
        sCall.enqueue(new Callback<Season>() {
            @Override
            public void onResponse(Call<Season> call, Response<Season> response) {
                if(response.body()!=null) {
                    seasonAdapter.addSeason(response.body());
                    getEpisodes(apiInterface, id , seasonNumber+1);
                } else if(response.body()==null && seasonNumber==0){
                    getEpisodes(apiInterface, id , 1);
                } else if(response.body()==null && seasonNumber>0){
                    pbSeason.setVisibility(View.GONE);

                    if(seasonAdapter.getItemCount()==0){
                        TextView txt = (TextView) findViewById(R.id.no_season);
                        txt.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<Season> call, Throwable t) {
                pbSeason.setVisibility(View.GONE);
            }
        });
    }
}
