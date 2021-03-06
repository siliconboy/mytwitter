package com.codepath.apps.simpletweets.fragments;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.simpletweets.TwitterApp;
import com.codepath.apps.simpletweets.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;

/**
 * Created by yingbwan on 10/3/2017.
 */

public class UserTimelineFragment extends TweetsListFragment {
    private Unbinder unbinder;
    TwitterClient client;

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment fragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApp.getRestClient();
     //   screenName = getArguments().getString("screen_name");
        populateTimeline(1L,0L); //Long.MAX_VALUE - 1);
    }


    @Override
    public void populateTimeline(Long sinceId, Long maxId) {
        String screenName = getArguments().getString("screen_name");
       // pd.show();
        client.getUserTimeline(maxId, screenName, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                addItems(response);
               // pd.dismiss();
                // for swipe
                Log.d("DEBUG", "swipt disable");
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), "Could not load more tweet, due to network error.", Toast.LENGTH_LONG).show();
                Log.d("DEBUG", "swipt disable-failure case");
                swipeContainer.setRefreshing(false);
                Log.d("Twitter.client", errorResponse.toString());
                throwable.printStackTrace();
            }

        });
    }


}
