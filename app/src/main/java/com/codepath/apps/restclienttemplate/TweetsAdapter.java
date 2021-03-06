package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import java.util.List;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;
    TwitterClient client;

    // Pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // For each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // Bind values on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get data at position
        Tweet tweet = tweets.get(position);
        // bind tweet to view holder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> tweetList) {
        tweets.addAll(tweetList);
        notifyDataSetChanged();
    }

    public synchronized void replaceAll(List<Tweet> tweetList) {
        tweets.clear();
        tweets.addAll(tweetList);
        notifyDataSetChanged();
    }

    // Define a viewHolder - a "tweet"
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvName;
        TextView tvScreenName;
        TextView tvTimestamp;
        ImageView ivRetweet;
        ImageView ivMedia;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvName = itemView.findViewById(R.id.tvName);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            ivRetweet = itemView.findViewById(R.id.ivRetweet);
        }

        public void bind(final Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText("@" + tweet.user.screenName);
            tvName.setText(tweet.user.name);
            tvTimestamp.setText(tweet.getRelativeTimeAgo());
            Glide.with(context).load(tweet.user.profileImageUrl).into(ivProfileImage);
            if(tweet.mediaUrl != null) {
                ivMedia.setVisibility(View.VISIBLE);
                Glide.with(context).load(tweet.mediaUrl).into(ivMedia);
            } else {
                ivMedia.setVisibility(View.GONE);
            }
            client = TwitterApp.getRestClient(context);
            if(tweet.retweeted != null && tweet.retweeted == true) {
                Glide.with(context).load(R.drawable.ic_vector_retweet_green).into(ivRetweet);
                ivRetweet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       unretweet(tweet);
                    }
                });
            } else {
                Glide.with(context).load(R.drawable.ic_vector_retweet).into(ivRetweet);
                ivRetweet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        retweet(tweet);
                    }
                });
            }
        }

        private void retweet(final Tweet tweet) {
            Log.i("TweetItem", "retweet clicked");
            // make api call to twitter to publish the tweet
            client.publishRetweet(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.i("TweetItem", "onSuccess to retweet tweet");
                    Glide.with(context).load(R.drawable.ic_vector_retweet_green).into(ivRetweet);
                    tweet.retweeted = true;
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.e("TweetItem", "onFailure to retweet tweet", throwable);
                }
            }, tweet.id);
        }

        private void unretweet(final Tweet tweet) {
            Log.i("TweetItem", "retweet clicked");
            // make api call to twitter to publish the tweet
            client.publishUnretweet(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.i("TweetItem", "onSuccess to unretweet tweet");
                    Glide.with(context).load(R.drawable.ic_vector_retweet).into(ivRetweet);
                    tweet.retweeted = false;
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.e("TweetItem", "onFailure to unretweet tweet", throwable);
                }
            }, tweet.id);
        }
    }
}
