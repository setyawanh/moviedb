package com.setyawan.moviedb;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.setyawan.moviedb.adapter.CreditAdapter;
import com.setyawan.moviedb.model.Cast;
import com.setyawan.moviedb.model.CastDetails;
import com.setyawan.moviedb.model.MovieCredits;
import com.setyawan.moviedb.model.ShowCredits;
import com.setyawan.moviedb.utils.ApiClient;
import com.setyawan.moviedb.utils.ApiInterface;

import java.util.Calendar;

import it.sephiroth.android.library.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.setyawan.moviedb.R.id.date;

public class DetailCastActivity extends AppCompatActivity {
    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView imgPoster;
    private TextView txtBiography, txtPob, txtDate;
    private RecyclerView movieView, showView;
    private ProgressBar pbMovie, pbShow;
    private Cast cast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_cast);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        ImageView imgHeader = (ImageView) findViewById(R.id.backdrop);
        imgPoster = (ImageView) findViewById(R.id.poster);
        TextView txtName = (TextView) findViewById(R.id.name);
        txtBiography = (TextView) findViewById(R.id.overview);
        txtDate = (TextView) findViewById(date);
        txtPob = (TextView) findViewById(R.id.pob);
        movieView = (RecyclerView) findViewById(R.id.movies);
        showView = (RecyclerView) findViewById(R.id.tv);
        pbMovie = (ProgressBar) findViewById(R.id.pb_movies);
        pbShow = (ProgressBar) findViewById(R.id.pb_tv);
        movieView.setNestedScrollingEnabled(false);
        showView.setNestedScrollingEnabled(false);

        // Get intent
        Intent i = getIntent();
        cast = new GsonBuilder().create().fromJson(i.getStringExtra("cast"),Cast.class);
        collapsingToolbar.setTitle(cast.getName());
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        Picasso.with(this)
                .load(ApiInterface.BASE_BACK_URL + cast.getProfilePath())
                .placeholder(R.drawable.cast)
                .into(imgHeader);
        Picasso.with(this)
                .load(ApiInterface.BASE_IMG_URL + cast.getProfilePath())
                .placeholder(R.drawable.cast)
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

        txtName.setText(cast.getName());

        movieView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        showView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        loadJSON();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share, menu);
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
                String txt = "Check who is this \n\""+ cast.getName()+"\"\n\nhttps://www.themoviedb.org/person/"+ cast.getId();
                i.putExtra(Intent.EXTRA_TEXT,txt);
                i.setType("text/plain");
                startActivity(Intent.createChooser(i,"Share \""+ cast.getName()+"\""));
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadJSON() {
        int id = cast.getId();

        ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);

        Call<CastDetails> cCall = apiInterface.getCast(id);
        cCall.enqueue(new Callback<CastDetails>() {
            @Override
            public void onResponse(Call<CastDetails> call, Response<CastDetails> response) {
                CastDetails c = response.body();
                if(c!=null){
                    txtPob.setText(c.getPlaceOfBirth());
                    String date = "";
                    if(c.getBirthday()!=null){
                        int age = Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(c.getBirthday().substring(0,4));
                        date = c.getBirthday().substring(0,4) + " \u25CF " + age;
                    }
                    txtDate.setText(date);

                    if(c.getBiography()==null || c.getBiography().equals("")){
                        txtBiography.setText("No biography found");
                    } else {
                        txtBiography.setText(c.getBiography());
                    }
                }
            }

            @Override
            public void onFailure(Call<CastDetails> call, Throwable t) {

            }
        });


        // get movie credit
        Call<MovieCredits> mCall = apiInterface.getMovieCredit(id);
        pbMovie.setVisibility(View.VISIBLE);
        mCall.enqueue(new Callback<MovieCredits>() {
            @Override
            public void onResponse(Call<MovieCredits> call, Response<MovieCredits> response) {
                pbMovie.setVisibility(View.GONE);
                CreditAdapter creditAdapter = new CreditAdapter(DetailCastActivity.this, "movie");
                movieView.setAdapter(creditAdapter);
                creditAdapter.setMovieList(response.body().getMovies());
                TextView txt = (TextView) findViewById(R.id.no_movies);
                if(creditAdapter.getItemCount()==0) txt.setVisibility(View.VISIBLE);
            }
            @Override
            public void onFailure(Call<MovieCredits> call, Throwable t) {
                pbMovie.setVisibility(View.GONE);
            }
        });

        // get show credit
        Call<ShowCredits> sCall = apiInterface.getShowCredit(id);
        pbShow.setVisibility(View.VISIBLE);
        sCall.enqueue(new Callback<ShowCredits>() {
            @Override
            public void onResponse(Call<ShowCredits> call, Response<ShowCredits> response) {
                pbShow.setVisibility(View.GONE);
                CreditAdapter creditAdapter = new CreditAdapter(DetailCastActivity.this, "shows");
                showView.setAdapter(creditAdapter);
                creditAdapter.setShowsList(response.body().getShows());
                TextView txt = (TextView) findViewById(R.id.no_tv);
                if(creditAdapter.getItemCount()==0) txt.setVisibility(View.VISIBLE);
            }
            @Override
            public void onFailure(Call<ShowCredits> call, Throwable t) {
                pbShow.setVisibility(View.GONE);
            }
        });
    }

    private void applyPalette(Palette palette) {
        int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
        int primary = getResources().getColor(R.color.colorPrimary);

        collapsingToolbar.setContentScrimColor(palette.getDarkMutedColor(primaryDark));
        LinearLayout bar = (LinearLayout) findViewById(R.id.bar);
        bar.setBackgroundColor(palette.getDarkMutedColor(primaryDark));
    }
}
