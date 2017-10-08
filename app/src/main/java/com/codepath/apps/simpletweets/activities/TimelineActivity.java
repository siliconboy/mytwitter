package com.codepath.apps.simpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.TwitterApp;
import com.codepath.apps.simpletweets.TwitterClient;
import com.codepath.apps.simpletweets.adapters.EndlessRecyclerViewScrollListener;
import com.codepath.apps.simpletweets.adapters.SmartFragmentStatePagerAdapter;
import com.codepath.apps.simpletweets.adapters.TweetsPagerAdapter;
import com.codepath.apps.simpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.simpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;


public class TimelineActivity extends AppCompatActivity {
    public static String POSITION = "POSITION";
    private final int REQUEST_CODE = 10;  //for compose tweet
    private TwitterClient client;
    //TweetsListFragment tweetsListFragment;
    private SmartFragmentStatePagerAdapter adapterViewPager;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.sliding_tabs) TabLayout tabLayout;
    @BindView(R.id.viewpager) ViewPager viewPager;

    User mUser;
    Long mMaxId;
    Long mSinceId;
    //boolean hasLocal =false;

    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        //ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapterViewPager = new TweetsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);// TweetsPagerAdapter(getSupportFragmentManager());

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);


        client = TwitterApp.getRestClient();

     //   tweetsListFragment = (TweetsListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       // getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_twitter);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        getCredential();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add) {
//            TweetFragment tweetFragment;
//            FragmentManager fm = getSupportFragmentManager();
//            tweetFragment = TweetFragment.newInstance(mUser);
//            tweetFragment.show(fm, "fragment_tweet");
            //activity
            Intent i = new Intent(this, ComposeActivity.class);
            i.putExtra("user", Parcels.wrap(mUser)); // pass user data to launched activity
            startActivityForResult(i, REQUEST_CODE);
        }

        if (id == R.id.action_profile) {
            Intent i = new Intent(this, ProfileActivity.class);
            i.putExtra("screen_name", mUser.getScreenName());
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, tabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        viewPager.setCurrentItem(savedInstanceState.getInt(POSITION));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            int code = data.getExtras().getInt("code", 0);
            if( code >=400) return;
            // Extract name value from result extras
            String message = data.getExtras().getString("message");
            // Toast the name to display temporarily on screen
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            //do post for tweet.
            onFinishDialog(message);
        }
    }


    public void onFinishDialog(String msg) {

        client.postTweet(msg, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Tweet tweet = fromJson(response);

                HomeTimelineFragment homeTimelineFragment= (HomeTimelineFragment) adapterViewPager.getRegisteredFragment(0);
                homeTimelineFragment.addItem(response);
                viewPager.setCurrentItem(0);
                // tweetsListFragment.addItem(response);
              //  adapter.notifyItemInserted(tweets.size() - 1);
              //  rvTweets.scrollToPosition(0);
               // Log.d("Twitter.return", tweet.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(TimelineActivity.this, "not able to post your tweet.", Toast.LENGTH_LONG).show();
                Log.d("Twitter.client", errorResponse.toString());
                throwable.printStackTrace();
            }

        });

    }

    public void getCredential() {
        client.getCredential(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    mUser = User.fromJSON(response);
                  //  getSupportActionBar().setTitle(mUser.getScreenName());
                    //populate user header
                 //   populateUserHeadline(mUser);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("Twitter.client", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(TimelineActivity.this, "Could not get profile, due to network error.", Toast.LENGTH_LONG).show();
                //         Log.d("Twitter.client", errorResponse.toString());
                throwable.printStackTrace();
            }

        });
    }

}
