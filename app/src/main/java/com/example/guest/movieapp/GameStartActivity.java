package com.example.guest.movieapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GameStartActivity extends AppCompatActivity {
    @Bind(R.id.actorTextView) TextView mActorNameTextView;
    @Bind(R.id.actorImageView) ImageView mActorImageView;
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;
    int score = 0;
    String degrees = "";
    boolean callbackComplete = false;

    private static final int MAX_WIDTH = 400;
    private static final int MAX_HEIGHT = 600;

    public ArrayList<Actor> mFamousActors = new ArrayList<>();
    public ArrayList<Movie> movies = new ArrayList<>();
    public Actor actor;
    Random random = new Random();
    private Context mContext;
    private ActorMoviesListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_start);
        ButterKnife.bind(this);
        mContext = this;

        getActors();
    }

    private void getActors() {
        final MovieDBService movieDBService = new MovieDBService();

        movieDBService.findFamousActors(1, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                mFamousActors = movieDBService.processActorResults(response, "results", mFamousActors);
                movieDBService.findFamousActors(2, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        mFamousActors = movieDBService.processActorResults(response, "results", mFamousActors);

//                        callbackComplete = true;
                        int randomNumber = random.nextInt(mFamousActors.size());
                        actor = mFamousActors.get(randomNumber);

                        GameStartActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mActorNameTextView.setText(actor.getName());
                                degrees += actor.getName() + " was in ";
                                Picasso.with(mContext)
                                        .load(actor.getImageUrl())
                                        .resize(MAX_WIDTH, MAX_HEIGHT)
                                        .centerCrop()
                                        .into(mActorImageView);
                            }
                        });
                        movieDBService.findMovies(actor.getActorId(), new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Call call, Response response) {
                                movies = movieDBService.processMovieResults(response);
                                GameStartActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAdapter = new ActorMoviesListAdapter(getApplicationContext(), movies, score, degrees);
                                        mRecyclerView.setAdapter(mAdapter);
                                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(GameStartActivity.this);
                                        mRecyclerView.setLayoutManager(layoutManager);
                                        mRecyclerView.setHasFixedSize(true);
                                    }
                                });
                            }


                        });
                    }
                });

            }
        });

    }
}