package com.hex.hackathon.TwitterReader.TwitterService;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hex.hackathon.TwitterReader.Beans.CategoriesBean;
import com.hex.hackathon.TwitterReader.Beans.CategoryBean;
import com.hex.hackathon.TwitterReader.Beans.EntitiesBean;
import com.hex.hackathon.TwitterReader.Beans.EntityBean;
import com.hex.hackathon.TwitterReader.Beans.EntityReturnBean;
import com.hex.hackathon.TwitterReader.Beans.FinPercentBean;
import com.hex.hackathon.TwitterReader.Beans.SentimentAylienBean;

import twitter4j.conf.ConfigurationBuilder;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import com.aylien.textapi.TextAPIClient;
import com.aylien.textapi.TextAPIException;
import com.aylien.textapi.parameters.*;
import com.aylien.textapi.responses.*;

@Component
public class TwitterService {
	
	ConfigurationBuilder configBuilder = new ConfigurationBuilder().setDebugEnabled(true)
	  .setOAuthConsumerKey("Ca6ATrsfxqL5S8W5lILHzkYPH")
	  .setOAuthConsumerSecret("nSDkfI0tZUAtjBD9H2JPCGayd4Hg6OhSgJ3zRmVxprJ6CUvQP9")
	  .setOAuthAccessToken("1062167800373014530-HEnbWw71j6mOcfpAvqHbGLXV3iRZ9E")
	  .setOAuthAccessTokenSecret("UKrkXcWDIZX9JEONqxQH7yXuHgJyR5ClKBNjx5KQ3Imss");
	
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
			//Query query = new Query(username);
			QueryResult result;
			query.setCount(30);
		//	for (int i=0;i<5;i++)
		//	{
                result = twitter.search(query);
                List<Status> pulledTweets = result.getTweets();
                for (Status tweet : pulledTweets) {
                tweets.add(new Tweet(tweet.getUser().getScreenName(),
                			tweet.getText().replaceAll("[\\S]+://[\\S]+", "").replaceAll("[^A-Za-z0-9 ]", " ").replaceAll("( )+", " ")));
                }
      //      }
			
		}
		catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get timeline: " + te.getMessage());
     //       tweets=generateTweets();
        } 
		List<Tweet> mergedTweets=mergeTweets(tweets);
		
		return mergedTweets;
	}
	
	
	public Tweet getAllTweets(String username)
	{
		String tweetMessage=new String();
		Tweet tweets=new Tweet();
		try
		{
			Query query = new Query("from:"+username);
			//Query query = new Query(username+"-rt");
			QueryResult result;
			query.setCount(30);
                result = twitter.search(query);
                List<Status> pulledTweets = result.getTweets();
                for (Status tweet : pulledTweets) {
                	tweetMessage=tweetMessage+" "+tweet.getText().replaceAll("[\\S]+://[\\S]+", "");
                	
                }
          
			
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
	
	public List<Tweet> getFinTweets(String username)
	{
		
		List<Tweet> tweets=new ArrayList<>();
		try
		{
			Query query = new Query("from:"+username);
			QueryResult result;
			query.setCount(30);
			//for (int i=0;i<5;i++)
			//{
                result = twitter.search(query);
                List<Status> pulledTweets = result.getTweets();
                for (Status tweet : pulledTweets) {
                	System.out.println(tweet.getText());
                tweets.add(new Tweet(tweet.getUser().getScreenName(),
                			tweet.getText().replaceAll("[\\S]+://[\\S]+", "").replaceAll("[^A-Za-z0-9 ]", " ").replaceAll("( )+", " ")));
            //    }
            }
			
		}
		catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get timeline: " + te.getMessage());
         //   tweets=generateTweets();
        } 
		
		List<Tweet> mergedTweets=mergeTweets(tweets);
          List<Tweet> tweetsCopy=new ArrayList<Tweet>(mergedTweets);
            
		for(Tweet t:mergedTweets)
		{
			if (numberOfWords(t.getMessage()) >20) //for now disqualify any with less than 20 chars.
											//option:implemented mergeTweet :)
			{
			if (!isFinancial(t))
			{
				System.out.println("Not Fin");
				tweetsCopy.remove(t);
			}
			}
			else
			{
				System.out.println("Too Short");
				tweetsCopy.remove(t);
			}
		}
		
		
		return tweetsCopy;
	}
	
	
	public Tweet getAllFinTweets(String username)
	{
		List<Tweet> allFinTweetList= getFinTweets(username);
		Tweet allFinTweets=new Tweet();
		String tweetMessage=new String();
		
		for(Tweet t: allFinTweetList)
		{
			tweetMessage=tweetMessage+" "+t.getMessage();
		}
		
		tweetMessage=tweetMessage.replaceAll("[^A-Za-z0-9 ]", " ");
		 tweetMessage=tweetMessage.replaceAll("( )+", " ");
		 allFinTweets.setUserName(username);
		 allFinTweets.setMessage(tweetMessage);
		
		return allFinTweets;
	}

	//Function that will be user to make a call to the Google NL service to determine if the message is financial related/
		public boolean isFinancial(Tweet t)
		{
			
			String url="https://language.googleapis.com/v1/documents:classifyText?fields=categories&key=AIzaSyBVIlYJgfTnYcBKzCMTjQdH_DO23o9U3ik";
			
			try {
				HashMap<String, String> requestBody = new HashMap<String, String>();
				requestBody.put("type", "PLAIN_TEXT");
				requestBody.put("content", t.getMessage());

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);

				
				// Jackson ObjectMapper to convert requestBody to JSON
				String json = new ObjectMapper().writeValueAsString(requestBody);
				json="{\"document\": "+json+"}";
				HttpEntity<String> httpEntity = new HttpEntity<>(json, headers);
				System.out.println(json);
				RestTemplate rt=new RestTemplate();

				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
				MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
				messageConverter.setPrettyPrint(false);
				messageConverter.setObjectMapper(mapper);
				
				rt.getMessageConverters().removeIf(m -> m.getClass().getName().equals(MappingJackson2HttpMessageConverter.class.getName()));
				rt.getMessageConverters().add(messageConverter);
				
				ResponseEntity<CategoriesBean> responseEntity=rt.exchange(url, HttpMethod.POST,	httpEntity, CategoriesBean.class);
				 		
				CategoriesBean categories =responseEntity.getBody();
				
				for(CategoryBean category: categories.getCategories())
				{
					System.out.println(category.getName().toLowerCase());
					if(category.getName().toLowerCase().contains("finance") || category.getName().toLowerCase().contains("business")  )
					{
						return true;
					}
				}
				
			} catch (RestClientException e) {
				// TODO Auto-generated catch block
				System.out.println("Failed check Fin: " + e.getMessage());
				System.out.println("Failed check Fin: " + e.toString());
				e.printStackTrace(); 
				System.exit(-1);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace(); 
				System.exit(-1);
			}
			
			
			return false;
		}
	
		public static int numberOfWords(String input)
			{ 
			if (input == null || input.isEmpty())
			{ return 0; } 
			
			String[] words = input.split("\\s+"); 
			return words.length; 
			}


public static List<Tweet> mergeTweets(List<Tweet> t)
{
	List<Tweet> tweets = new ArrayList<Tweet>(t);
	List<Tweet> tempTweets = new ArrayList<Tweet>();
	
	int isFirst=0;
	
	String message=new String("");
	String user=new String("");
	Tweet tempTweet=new Tweet();
	ListIterator<Tweet> tweetIterator=tweets.listIterator();
	while(tweetIterator.hasNext())
	{
		tempTweet=tweetIterator.next();
		if(isFirst==0)
		{
			isFirst=1;
			user=tempTweet.getUserName();
		}
		message=message+" "+tempTweet.getMessage();
		
		if(user.equals(tempTweet.getUserName()))
		{
        if(numberOfWords(message)>=25)
		{
			tempTweets.add(new Tweet(tempTweet.getUserName(),message));
			message="";
			user="";
			isFirst=0;
		}
		}
	else
	{
		if(numberOfWords(message)>=25)
		{
			tempTweets.add(new Tweet(tempTweet.getUserName(),message));
			message="";
			user="";
			isFirst=0;
		}	
		else {
			System.out.println("BYPASS "+message);
			message="";
			user="";
			isFirst=0;
		}
	}
				
	}
	
	return tempTweets;
	
}



public FinPercentBean getFinPercent(String username)
{
	FinPercentBean finPercent=new FinPercentBean();
	
	List<Tweet> tweets=new ArrayList<>();
	try
	{
		Query query = new Query("from:"+username);
		QueryResult result;
		query.setCount(30);
		//for (int i=0;i<5;i++)
		//{
            result = twitter.search(query);
            List<Status> pulledTweets = result.getTweets();
            for (Status tweet : pulledTweets) {
            	System.out.println(tweet.getText());
            tweets.add(new Tweet(tweet.getUser().getScreenName(),
            			tweet.getText().replaceAll("[\\S]+://[\\S]+", "").replaceAll("[^A-Za-z0-9 ]", " ").replaceAll("( )+", " ")));
        //    }
        }
		
	}
	catch (TwitterException te) {
        te.printStackTrace();
        System.out.println("Failed to get timeline: " + te.getMessage());
     //   tweets=generateTweets();
    } 
	
	List<Tweet> mergedTweets=mergeTweets(tweets);
      List<Tweet> tweetsCopy=new ArrayList<Tweet>(mergedTweets);
        
	for(Tweet t:mergedTweets)
	{
		if (numberOfWords(t.getMessage()) >25) //for now disqualify any with less than 20 chars.
										//option:implemented mergeTweet :)
		{
		if (!isFinancial(t))
		{
			System.out.println("Not Fin");
			tweetsCopy.remove(t);
		}
		}
		else
		{
			System.out.println("Too Short");
			tweetsCopy.remove(t);
		}
	}
	
	finPercent.setMergedTweetCounts(mergedTweets.size());
	finPercent.setMergedFinTweetCounts(tweetsCopy.size());
	finPercent.calcPercent();
	
	return finPercent;

}		

public List<EntityReturnBean> getAllEntities (String username)
{
	List<EntityReturnBean> retEntities=new ArrayList<EntityReturnBean>();
	
	try
	{System.out.println(twitter.verifyCredentials().getName());}
	catch (Exception e)
	{
	System.out.println(e);
	}
	
	Tweet tweet=this.getAllFinTweets(username);
	
	String url="https://language.googleapis.com/v1/documents:analyzeEntitySentiment?fields=entities&key=AIzaSyBVIlYJgfTnYcBKzCMTjQdH_DO23o9U3ik";
	
	try {
		HashMap<String, String> requestBody = new HashMap<String, String>();
		requestBody.put("type", "PLAIN_TEXT");
		requestBody.put("content", tweet.getMessage());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		
		// Jackson ObjectMapper to convert requestBody to JSON
		String json = new ObjectMapper().writeValueAsString(requestBody);
		json="{\"document\": "+json+"}";
		System.out.println(json);
		HttpEntity<String> httpEntity = new HttpEntity<>(json, headers);
		System.out.println(json);
		RestTemplate rt=new RestTemplate();

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
		messageConverter.setPrettyPrint(false);
		messageConverter.setObjectMapper(mapper);
		
		rt.getMessageConverters().removeIf(m -> m.getClass().getName().equals(MappingJackson2HttpMessageConverter.class.getName()));
		rt.getMessageConverters().add(messageConverter);
		
		ResponseEntity<EntitiesBean> responseEntity=rt.exchange(url, HttpMethod.POST,	httpEntity, EntitiesBean.class);
		 		
		EntitiesBean entities =responseEntity.getBody();
		
		for(EntityBean entity: entities.getEntities())
		{
			retEntities.add(new EntityReturnBean(entity.getName(),entity.getSalience()));
		}
		
		
		
	} catch (RestClientException e) {
		// TODO Auto-generated catch block
		System.out.println("Failed check Fin: " + e.getMessage());
		System.out.println("Failed check Fin: " + e.toString());
		e.printStackTrace(); 
		System.exit(-1);
	} catch (JsonProcessingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace(); 
		System.exit(-1);
	}
	
	
	return retEntities;
	
}

public List<SentimentAylienBean> getProductSentiment(String name)
{
String subject=name;
	List<Tweet> tweets=new ArrayList<>();
	try
	{
		Query query = new Query(subject);
		QueryResult result;
		query.setCount(30);
	//	for (int i=0;i<5;i++)
	//	{
            result = twitter.search(query);
            List<Status> pulledTweets = result.getTweets();
            for (Status tweet : pulledTweets) {
            tweets.add(new Tweet(tweet.getUser().getScreenName(),
            			tweet.getText().replaceAll("[\\S]+://[\\S]+", "").replaceAll("[^A-Za-z0-9 ]", " ").replaceAll("( )+", " ")));
            }
  //      }
		
	}
	catch (TwitterException te) {
        te.printStackTrace();
        System.out.println("Failed to get pull Sub Tweets: " + te.getMessage());
 //       tweets=generateTweets();
    } 
	
		List<Sentiment> sentiments=new ArrayList<Sentiment>();
		List<SentimentAylienBean> retSentiments=new ArrayList<SentimentAylienBean>();
		
		TextAPIClient client = new TextAPIClient("e2c925a1", "da7e7516a7d5248f03742e5b8232a272");
	    SentimentParams.Builder builder = SentimentParams.newBuilder();
	    
	    //Sentiment sentiment=new Sentiment();
		try {
			
			for(Tweet t: tweets)
			{
			builder.setText(t.getMessage());
			sentiments.add(client.sentiment(builder.build()));
			}
		} catch (TextAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		for(Sentiment s: sentiments)
		{
			Integer pos=-1;
						
			for(SentimentAylienBean r: retSentiments)
			{
				if(r.getPolarity().equals(s.getPolarity()))
				{
					pos=retSentiments.indexOf(r);
				}
					
			}
			
			if(pos==-1)
			{
				retSentiments.add(new SentimentAylienBean(s.getPolarity(),1));
			}
			else
			{
				SentimentAylienBean tmp=retSentiments.get(pos);
				Integer a=tmp.getCount();
				tmp.setCount(a+1);
				retSentiments.set(pos, tmp);
			}
				
		}
		
		return retSentiments;
	  
}



}