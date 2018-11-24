package com.hex.hackathon.TwitterReader.TwitterService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

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
			QueryResult result;
			query.setCount(15);
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
            tweets=generateTweets();
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
	
	public List<Tweet> getFinTweets(String username)
	{
		
		List<Tweet> tweets=new ArrayList<>();
		try
		{
			Query query = new Query("from:"+username);
			QueryResult result;
			query.setCount(15);
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
            tweets=generateTweets();
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
	
	private List<Tweet> generateTweets()
	{
		List<Tweet> tweets = new ArrayList<Tweet>();
		tweets.add(new Tweet("cnbc","WATCH: The Dow slides, down more than 500 points. Stocks have erased gains for 2018.#Rupee rallies to its highest level in nearly 3 months as it moves closer to the 70-mark against the Dollar. 10-year bonds hit their highest level in 6 months as #Brent crude prices crash 30% from October highs"));
		tweets.add(new Tweet("cnbc","#Rupee rallies to its highest level in nearly 3 months as it moves closer to the 70-mark against the Dollar. 10-year bonds hit their highest level in 6 months as #Brent crude prices crash 30% from October highs."));
		tweets.add(new Tweet("cnbc","....and Safety at the Border, or anywhere else. They know nothing about it and are making our Country unsafe. Our great Law Enforcement professionals MUST BE ALLOWED TO DO THEIR JOB! If not there will be only bedlam, chaos, injury and death. We want the Constitution as written!"));
		return tweets;
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
					if(category.getName().toLowerCase().contains("finance"))
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
        if(numberOfWords(message)>=20)
		{
			tempTweets.add(new Tweet(tempTweet.getUserName(),message));
			message="";
			user="";
			isFirst=0;
		}
		}
	else
	{
		if(numberOfWords(message)>=20)
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

		
		
}
