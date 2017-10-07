package com.codepath.apps.simpletweets.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.TwitterApp;
import com.codepath.apps.simpletweets.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.ButterKnife;
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
        screenName = getArguments().getString("screen_name");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_usertimeline, container, false);
        unbinder = ButterKnife.bind(this,v);


        return v;
    }
    // When binding a fragment in onCreateView, set the views to null in onDestroyView.
    // ButterKnife returns an Unbinder on the initial binding that has an unbind method to do this automatically.
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void populateTimeline(Long sinceId, Long maxId,String screenName) {

        client.getUserTimeline(screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("Twitter.client", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                addItems(response);
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

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(getContext(), "Could not load more tweet, due to network error.", Toast.LENGTH_LONG).show();
                Log.d("DEBUG", "swipt disable");
                swipeContainer.setRefreshing(false);
                Log.d("Twitter.client", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getContext(), "Could not load more tweet, due to network error.", Toast.LENGTH_LONG).show();
                Log.d("Twitter.client", responseString);
                throwable.printStackTrace();
            }
        });
    }


}
