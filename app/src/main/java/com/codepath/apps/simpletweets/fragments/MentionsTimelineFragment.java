package com.codepath.apps.simpletweets.fragments;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.simpletweets.TwitterApp;
import com.codepath.apps.simpletweets.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by yingbwan on 10/3/2017.
 */

public class MentionsTimelineFragment extends TweetsListFragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private TwitterClient client;
    int mPage;

    public static MentionsTimelineFragment newInstance(int page) {
        MentionsTimelineFragment fragment = new MentionsTimelineFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApp.getRestClient();
      //  mPage = getArguments().getInt(ARG_PAGE);
        populateTimeline(1L, Long.MAX_VALUE - 1);
    }

@Override
    public void populateTimeline(Long sinceId, Long maxId) {
        //pd.show();
        client.getMentionsTimeline(sinceId, maxId, new JsonHttpResponseHandler() {

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
