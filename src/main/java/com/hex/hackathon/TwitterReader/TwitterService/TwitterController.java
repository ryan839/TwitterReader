package com.hex.hackathon.TwitterReader.TwitterService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TwitterController {

	@Autowired
	private TwitterService twitterService;
	
	
	@GetMapping("/getStatus")
	public List<Tweet> retrieveHome()
	{
		List<Tweet> tweetList=twitterService.readStatus();
		return tweetList;
	}
	
	
	@GetMapping("/getTweets/{name}")
	public List<Tweet> retrieveTweets(@PathVariable String name)
	{
		List<Tweet> tweetList=twitterService.getTweets(name);
		return tweetList;
	}
}
