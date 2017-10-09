package com.codepath.apps.simpletweets.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.helper.GlideApp;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.models.User;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.codepath.apps.simpletweets.R.id.tvFavorite;


public class TweetActivity extends AppCompatActivity {

    // show detail of a tweet
    @BindView(R.id.ivImage)
    ImageView ivImage;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvScreen)
    TextView tvScreen;
    @BindView(R.id.tvBody)
    TextView tvBody;
    @BindView(R.id.ivMedia)
    ImageView ivMedia;
    @BindView(R.id.tvReply)
    TextView tvReply;
    @BindView(R.id.tvFavorite)
    TextView tvFavorite;
    @BindView(R.id.tvRepeat)
    TextView tvRetweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
        ButterKnife.bind(this);

        Tweet tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        tvBody.setText(tweet.getBody());
        User usr = User.byId(tweet.getUserId());
        tvName.setText(usr.getName());
        tvScreen.setText(usr.getScreenName());
        GlideApp.with(this).load(usr.getProfileImageUrl()).override(250,250).fitCenter().apply(RequestOptions.circleCropTransform()).into(ivImage);

        if(tweet.getMediaUrl()!=null && tweet.getMediaUrl().length()>0){
            ivMedia.setImageDrawable(null);
            int len = ivMedia.getLayoutParams().width;
            GlideApp.with(this).load(tweet.getMediaUrl()).override(len,len).into(ivMedia);
            ivMedia.setVisibility(View.VISIBLE);
            Log.d("DEBUG", "bind holder with image:" + tweet.getMediaUrl());
        }else{
            ivMedia.setVisibility(View.GONE);
        }
        if(tweet.getFavoriteCount()>0){
            tvFavorite.setText(String.valueOf(tweet.getFavoriteCount()));
        }
        if(tweet.getRetweetCount()>0){
            tvRetweet.setText(String.valueOf(tweet.getRetweetCount()));
        }

        //Log.d("DEBUG", "data: " + tweet.getBody());
    }


}
