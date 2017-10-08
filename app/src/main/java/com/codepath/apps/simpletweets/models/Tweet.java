package com.codepath.apps.simpletweets.models;

import com.codepath.apps.simpletweets.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yingbwan on 9/26/2017.
 */

@Table(database = MyDatabase.class)
@Parcel(analyze = {Tweet.class})
public class Tweet extends BaseModel {

    // Define database columns and associated fields
    @PrimaryKey
    @Column
    Long id;
    @Column
    Long userId;

    @Column
    String timestamp;
    @Column
    String body;
    @Column
    int favoriteCount;
    @Column
    int retweetCount;
    @Column
    int replyCount;
    @Column
    String mediaUrl;
    @Column
    String mediaType;


    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getBody() {
        return body;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Tweet() {
    }

    // Add a constructor that creates an object from the JSON response
    public Tweet(JSONObject object) {
        super();

        try {
            this.id = object.getLong("id");
            this.userId = object.getJSONObject("user").getLong("id");
            if (User.byId(this.userId) == null) {
                User user = User.fromJSON(object.getJSONObject("user"));
                user.save();
            }
            this.retweetCount = object.getInt("retweet_count");
            this.favoriteCount = object.getInt("favorite_count");
            this.timestamp = object.getString("created_at");
            this.body = object.getString("text");
            JSONArray array=null;
            try {
                JSONObject  jObj= object.getJSONObject("extended_entities");
                 array = jObj.getJSONArray("media");
                if ( array!= null && array.length()>0) {
                    this.mediaUrl = array.getJSONObject(0).getString("media_url");
                    this.mediaType = array.getJSONObject(0).getString("type");
                }
            }catch (JSONException e) {
                    this.mediaType="";
                    this.mediaUrl="";
            }
            try {
                this.replyCount = object.getInt("reply_count");
            } catch (JSONException e) {
                //skip
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Tweet> fromJson(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject tweetJson = null;
            try {
                tweetJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Tweet tweet = new Tweet(tweetJson);
            tweet.save();
            tweets.add(tweet);
        }

        return tweets;
    }

    public static Tweet fromJson(JSONObject jsonObject) {
        Tweet tweet = new Tweet(jsonObject);
        tweet.save();
        return tweet;
    }

    public static ArrayList<Tweet> fromSearch(JSONObject jsonObject) {
        JSONArray array = null;
        try {
            array = jsonObject.getJSONArray("statuses");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<Tweet> tweets = new ArrayList<Tweet>(array.length());

        for (int i = 0; i < array.length(); i++) {
            JSONObject tweetJson = null;
            try {
                tweetJson = array.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Tweet tweet = new Tweet(tweetJson);
            tweet.save();
            tweets.add(tweet);
        }

        return tweets;
    }


    // Record Finders
    public static Tweet byId(long id) {
        return new Select().from(Tweet.class).where(Tweet_Table.id.eq(id)).querySingle();
    }

    public static List<Tweet> recentItems() {
        return new Select().from(Tweet.class).orderBy(Tweet_Table.id, false).limit(300).queryList();
    }

}
