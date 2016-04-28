package com.example.guest.movieapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MovieListActivity extends AppCompatActivity {
    private static final int MAX_WIDTH = 400;
    private static final int MAX_HEIGHT = 600;

    @Bind(R.id.actorTextView) TextView mActorTextView;
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.actorImageView) ImageView mActorImageView;
    Context mContext;
    private MovieListAdapter mAdapter;
    private Actor actor;
    int score;
    String degrees;

    public ArrayList<Movie> mMovies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);

        mContext = this;
        Intent intent = getIntent();
        actor = Parcels.unwrap(intent.getParcelableExtra("actor"));
        score = intent.getIntExtra("score", 0);
        degrees = intent.getStringExtra("degrees");
        mActorTextView.setText(actor.getName());


        getMovies(actor.getActorId());
    }

    @Override
    public void onBackPressed() {
    }

    private void getMovies(String id) {
        final MovieDBService movieDBService = new MovieDBService();

        movieDBService.findMovies(id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                mMovies = movieDBService.processMovieResults(response);

                MovieListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.with(mContext)
                                .load(actor.getImageUrl())
                                .resize(MAX_WIDTH, MAX_HEIGHT)
                                .centerCrop()
                                .into(mActorImageView);
                        mAdapter = new MovieListAdapter(getApplicationContext(), mMovies, score, degrees);
                        mRecyclerView.setAdapter(mAdapter);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MovieListActivity.this);
                        mRecyclerView.setLayoutManager(layoutManager);
                        mRecyclerView.setHasFixedSize(true);
                    }
                });
            }
        });

    }

}
