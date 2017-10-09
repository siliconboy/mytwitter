package com.codepath.apps.simpletweets.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.activities.ComposeActivity;
import com.codepath.apps.simpletweets.activities.ProfileActivity;
import com.codepath.apps.simpletweets.helper.GlideApp;
import com.codepath.apps.simpletweets.helper.MyUtils;
import com.codepath.apps.simpletweets.helper.PatternEditableBuilder;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.models.User;

import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yingbwan on 9/26/2017.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    List<Tweet> mTweets;
    Context context;

    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tweet tweet = mTweets.get(position);
        Log.d("DEBUG", "adapter bind holder tweet id :" + tweet.getId());
        holder.tvBody.setText(tweet.getBody());
        // Style clickable spans based on pattern

        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), Color.BLUE,
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Toast.makeText(context, "Clicked username: " + text,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }).addPattern(Pattern.compile("\\#(\\w+)"), Color.CYAN,
                            new PatternEditableBuilder.SpannableClickedListener() {
                               @Override
                              public void onSpanClicked(String text) {
                                  Toast.makeText(context, "Clicked hashtag: " + text,
                                     Toast.LENGTH_SHORT).show();
                    }
                }).into(holder.tvBody);

        User usr = User.byId(tweet.getUserId());
        holder.tvUserName.setText(usr.getName());
        holder.tvHandle.setText("@" + usr.getScreenName());
        holder.tvTimeGap.setText(MyUtils.getRelativeTimeAgo(tweet.getTimestamp()));
        GlideApp.with(context).load(usr.getProfileImageUrl()).override(200, 200).fitCenter().apply(RequestOptions.circleCropTransform()).into(holder.ivProfileImage);

        if(tweet.getMediaUrl()!=null && tweet.getMediaUrl().length()>0){
            holder.ivMedia.setImageDrawable(null);
            int len = holder.ivMedia.getLayoutParams().width;
            GlideApp.with(context).load(tweet.getMediaUrl()).override(len,len).into(holder.ivMedia);
            holder.ivMedia.setVisibility(View.VISIBLE);
            Log.d("DEBUG", "bind holder with image:" + tweet.getMediaUrl());
        }else{
            holder.ivMedia.setVisibility(View.GONE);
        }
        if(tweet.getFavoriteCount()>0){
            holder.tvFavorite.setText(String.valueOf(tweet.getFavoriteCount()));
        }
        if(tweet.getRetweetCount()>0){
            holder.tvRetweet.setText(String.valueOf(tweet.getRetweetCount()));
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

// Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivProfileImage)
        ImageView ivProfileImage;
        @BindView(R.id.tvUserName)
        TextView tvUserName;
        @BindView(R.id.tvBody)
        TextView tvBody;
        @BindView(R.id.tvTimeGap)
        TextView tvTimeGap;
        @BindView(R.id.tvHandle)
        TextView tvHandle;
        @BindView(R.id.ivMedia)
        ImageView ivMedia;
        @BindView(R.id.tvReply)
        TextView tvReply;
        @BindView(R.id.tvRepeat)
        TextView tvRetweet;
        @BindView(R.id.tvFavorite)
        TextView tvFavorite;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), ProfileActivity.class);
                    String sName = tvHandle.getText().toString().substring(1);
                    i.putExtra("screen_name", sName);
                    v.getContext().startActivity(i);
                }
            });
            tvReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), ComposeActivity.class);
                    String sName = tvHandle.getText().toString().substring(1);

                    i.putExtra("screen_name", "@"+sName);
                    v.getContext().startActivity(i);
                }
            });
        }
    }

}
