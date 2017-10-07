package com.codepath.apps.simpletweets.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.activities.TweetActivity;
import com.codepath.apps.simpletweets.adapters.EndlessRecyclerViewScrollListener;
import com.codepath.apps.simpletweets.adapters.TweetAdapter;
import com.codepath.apps.simpletweets.helper.ItemClickSupport;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.networks.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

import static com.codepath.apps.simpletweets.models.Tweet.fromJson;

public class TweetsListFragment extends Fragment {

    TweetAdapter adapter;
    ArrayList<Tweet> tweets;

    @BindView(R.id.rvTweet)
    RecyclerView rvTweets;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    private EndlessRecyclerViewScrollListener scrollListener;
    private Unbinder unbinder;


    boolean hasLocal =false;
    String screenName;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_tweets_list, container, false);
        unbinder= ButterKnife.bind(this, v);


        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                Log.d("DEBUG", "swipe refresh called");
                populateTimeline(1L,Long.MAX_VALUE - 1);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        tweets = new ArrayList<>();
        adapter = new TweetAdapter(tweets);
        rvTweets.setAdapter(adapter);

        if (NetworkUtils.isNetworkAvailable(getContext()) || NetworkUtils.isOnline()) {
            Toast.makeText(getActivity(), "offline mode. Loading local data.", Toast.LENGTH_LONG).show();
            //     Snackbar.make(searchLayout, R.string.net_error, Snackbar.LENGTH_LONG).show();
            adapter.addAll(Tweet.recentItems());
            hasLocal = true;
            Log.d("DEBUG", "local load count:" + tweets.size());
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvTweets.setLayoutManager(linearLayoutManager);
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                Log.d("DEBUG", "network continue load before count:" + tweets.size());
                loadNextDataFromApi(page, view);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);

        rvTweets.setItemAnimator(new SlideInUpAnimator());

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(rvTweets.getContext(), DividerItemDecoration.VERTICAL);
        rvTweets.addItemDecoration(itemDecoration);

        ItemClickSupport.addTo(rvTweets).setOnItemClickListener(
                (recyclerView, position, view) -> {
                    //create intent
                    Intent intent = new Intent(getContext(), TweetActivity.class);
                    //get article
                    Tweet tweet = tweets.get(position);
                    intent.putExtra("tweet", Parcels.wrap(tweet));
                    //launch activity
                    startActivity(intent);
                }
        );

          if (hasLocal && NetworkUtils.isOnline()) {
              adapter.clear();
              hasLocal=false;
              populateTimeline(1L, Long.MAX_VALUE - 1);
          }

        return v;
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset, View view) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        final int curSize = tweets.size();// adapter.getItemCount();

        // Create the Handler object (on the main thread by default)
        Handler handler = new Handler();
        // Define the code block to be executed
        Runnable runnableCode = () -> {
            // reset only with first load.
            //scrollListener.resetState();
            Log.d("DEBUG", "before new load tweets size:" + curSize);
               if(hasLocal) {
                   adapter.clear();
                   hasLocal =false;
                   populateTimeline(1L,Long.MAX_VALUE - 1);
                }else {
                   populateTimeline(1L, tweets.get(curSize - 1).getId());
               }
        };
        // Run the above code block on the main thread after 500 miliseconds
        handler.postDelayed(runnableCode, 500);
    }

    public void addItems(JSONArray response){
        tweets.addAll(fromJson(response));
        adapter.notifyItemInserted(tweets.size() - 1);
    }

    public void addItem(JSONObject response){
        tweets.add(0, fromJson(response));
        adapter.notifyItemInserted(tweets.size() - 1);
        rvTweets.scrollToPosition(0);
    }

    public void  populateTimeline(Long sinceId, Long maxId){}


    // When binding a fragment in onCreateView, set the views to null in onDestroyView.
    // ButterKnife returns an Unbinder on the initial binding that has an unbind method to do this automatically.
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
