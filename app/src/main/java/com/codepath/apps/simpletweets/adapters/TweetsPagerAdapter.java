package com.codepath.apps.simpletweets.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.codepath.apps.simpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.simpletweets.fragments.MentionsTimelineFragment;

/**
 * Created by yingbwan on 10/6/2017.
 */
//FragmentPagerAdapter
public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "HOME", "MENTIONS" };
    //private Context context;

    public TweetsPagerAdapter(FragmentManager fm) {
        super(fm);
      //  this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if(0==position){
            return HomeTimelineFragment.newInstance(0);
        }else if(1==position){
            return MentionsTimelineFragment.newInstance(1);
        }else {
            return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
