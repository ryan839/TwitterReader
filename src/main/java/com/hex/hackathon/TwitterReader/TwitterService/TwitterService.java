package com.hex.hackathon.TwitterReader.TwitterService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import twitter4j.conf.ConfigurationBuilder;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;


@Component
public class TwitterService {
	
	ConfigurationBuilder configBuilder = new ConfigurationBuilder().setDebugEnabled(true)
	  .setOAuthConsumerKey("qWHs2ZMrs4CGG906CfbyImZmF")
	  .setOAuthConsumerSecret("3eTXTXZC6fpCLJzpGubWWcY056LUk72Jxz7c5GNuYkS1J3vCpY")
	  .setOAuthAccessToken("1062167800373014530-32zo3GTzwLj7AjVXaWRYA6ofP9vjiz")
	  .setOAuthAccessTokenSecret("oAGnvnRBcwKHkHrEsKUQ9aYonDpJfHhazJX0Cv0pYJlm9");
	
	TwitterFactory tf = new TwitterFactory(configBuilder.build());
	Twitter twitter = tf.getInstance();
	
	public List<Tweet> readStatus()
	{
		String userName=new String();
		List<Status> statuses=new ArrayList<>();
		try
		{
	 statuses = twitter.getHomeTimeline();
	 userName=twitter.verifyCredentials().getName();
		}
		catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get timeline: " + te.getMessage());
            System.exit(-1);
        } 
		
	List<Tweet> tweets=new ArrayList<>();
	
	for (Status status : statuses)
	{
		tweets.add(new Tweet(userName,status.getText()));
	}
	return tweets;
			
	
	}
	
	
	public List<Tweet> getTweets(String username)
	{
		
		List<Tweet> tweets=new ArrayList<>();
		try
		{
			Query query = new Query("from:"+username);
			QueryResult result;
			do {
                result = twitter.search(query);
                List<Status> pulledTweets = result.getTweets();
                for (Status tweet : pulledTweets) {
                	tweets.add(new Tweet(tweet.getUser().getScreenName(),
                			tweet.getText().replaceAll("[\\S]+://[\\S]+", "").replaceAll("[^A-Za-z0-9 ]", " ").replaceAll("( )+", " ")));
                }
            } while ((query = result.nextQuery()) != null);
			
		}
		catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get timeline: " + te.getMessage());
            System.exit(-1);
        } 
		
		return tweets;
	}
	
	
	public Tweet getAllTweets(String username)
	{
		String tweetMessage=new String();
		Tweet tweets=new Tweet();
		try
		{
			Query query = new Query("from:"+username);
			QueryResult result;
			do {
                result = twitter.search(query);
                List<Status> pulledTweets = result.getTweets();
                for (Status tweet : pulledTweets) {
                	tweetMessage=tweetMessage+" "+tweet.getText().replaceAll("[\\S]+://[\\S]+", "");
                	
                }
            } while ((query = result.nextQuery()) != null);
			
		 tweetMessage=tweetMessage.replaceAll("[^A-Za-z0-9 ]", " ");
		 tweetMessage=tweetMessage.replaceAll("( )+", " ");
			tweets.setUserName(username);
			tweets.setMessage(tweetMessage);
			
		}
		catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get timeline: " + te.getMessage());
            System.exit(-1);
        } 
		
		return tweets;
	}
	
	
	
}
