package dwaipayan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsBearer;
import com.twitter.clientlib.TwitterCredentialsOAuth1;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.CreateTweetRequest;
import com.twitter.clientlib.model.CreateTweetRequestReply;
import com.twitter.clientlib.model.Tweet;
import com.twitter.clientlib.model.TweetCreateResponse;
import com.twitter.clientlib.model.TweetSearchResponse;

import io.github.cdimascio.dotenv.Dotenv;

public class App {
    private static Dotenv dotenv = Dotenv.configure().load();
    private static TwitterApi apiInstance = new TwitterApi();
    private static TwitterApi apiInstanceOAuth1 = new TwitterApi();

    // Instantiate auth credentials for bearer level access
    private static final TwitterCredentialsBearer credentials = new TwitterCredentialsBearer(
            dotenv.get("BEARER_TOKEN"));

    // Instantiate auth credentials for oauth1a level access
    private static final TwitterCredentialsOAuth1 credentialsOAuth1 = new TwitterCredentialsOAuth1(
            dotenv.get("API_KEY"), dotenv.get("API_KEY_SECRET"), dotenv.get("ACCESS_TOKEN"),
            dotenv.get("ACCESS_TOKEN_SECRET"));

    public static void searchAndReplyTweets(String searchQuery, int limit, String replyText) {
        try {
            TweetSearchResponse response = apiInstance.tweets().tweetsRecentSearch(searchQuery, null, null, null,
                    null, limit, null, null, null, null, null, null, null, null, null);
            for (Tweet t : response.getData()) {
                String id = t.getId();
                CreateTweetRequest createTweetRequest = new CreateTweetRequest();
                createTweetRequest.text(replyText);

                CreateTweetRequestReply createTweetRequestReply = new CreateTweetRequestReply();
                createTweetRequestReply.setInReplyToTweetId(id);
                createTweetRequest.reply(createTweetRequestReply);
                TweetCreateResponse result = null;
                try {
                    result = apiInstanceOAuth1.tweets().createTweet(createTweetRequest);
                } catch (Exception e) {
                    System.out.println("Failed to reply to this tweet");
                } finally {
                    if (result != null)
                        System.out.println(result);
                }
            }
        } catch (ApiException e) {
            e.printStackTrace();
            System.out.println(e.getResponseBody());
        }
    }

    public static void main(String[] args) throws IOException {
        apiInstance.setTwitterCredentials(credentials);
        apiInstanceOAuth1.setTwitterCredentials(credentialsOAuth1);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the search query string");
        String query = br.readLine();
        System.out.println("Enter the number of records feched");
        int limit = Integer.parseInt(br.readLine());
        System.out.println("Enter the reply");
        String reply = br.readLine();
        searchAndReplyTweets(query, limit, reply);
        System.out.println("Sucessfully replied to all tweets");
    }
}
