package com.example.guest.movieapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class ActorListActivity extends AppCompatActivity {
    private static final int MAX_WIDTH = 400;
    private static final int MAX_HEIGHT = 600;

    @Bind(R.id.movieTitleTextView) TextView mMovieTitleTextView;
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.movieImageView) ImageView mMovieImageView;
    private ActorListAdapter mAdapter;
    private Movie movie;
    int score;
    String degrees;
    Context mContext;

    public ArrayList<Actor> mActors = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor_list);
        ButterKnife.bind(this);

        mContext = this;
        Intent intent = getIntent();
        movie = Parcels.unwrap(intent.getParcelableExtra("movie"));
        score = intent.getIntExtra("score", 0);
        degrees = intent.getStringExtra("degrees");
        String title = movie.getTitle();

        mMovieTitleTextView.setText(title);

        getActors(movie.getMovieId());
    }

    @Override
    public void onBackPressed() {
    }

    private void getActors(String id) {
        final MovieDBService actorDBService = new MovieDBService();

        actorDBService.findActors(id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }


            @Override
            public void onResponse(Call call, Response response) {
                mActors = actorDBService.processActorResults(response, "cast", mActors);

                ActorListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.with(mContext)
                                .load(movie.getPosterUrl())
                                .resize(MAX_WIDTH, MAX_HEIGHT)
                                .centerCrop()
                                .into(mMovieImageView);
                        mAdapter = new ActorListAdapter(getApplicationContext(), mActors, score, degrees);
                        mRecyclerView.setAdapter(mAdapter);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ActorListActivity.this);
                        mRecyclerView.setLayoutManager(layoutManager);
                        mRecyclerView.setHasFixedSize(true);
                    }
                });
            }
        });

    }
}
