import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.Serializable;

public class TwitterLifecycleManager implements LifecycleManager, Serializable {

    TwitterStream twitterStream;
    StatusListener statusListener;

    @Override
    public void start() {
        setTwitterStream();
        addStatusListener();
        filterTweets();
    }

    @Override
    public void stop() {
        removeStatusListener();
    }

   private void setTwitterStream() {
        String _consumerKey = System.getenv().get("TWITTER_CONSUMER_KEY");
        String _consumerSecret = System.getenv().get("TWITTER_CONSUMER_SECRET");
        String _accessToken = System.getenv().get("TWITTER_ACCESS_TOKEN");
        String _accessTokenSecret = System.getenv().get("TWITTER_ACCESS_TOKEN_SECRET");
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey(_consumerKey)
                .setOAuthConsumerSecret(_consumerSecret)
                .setOAuthAccessToken(_accessToken)
                .setOAuthAccessTokenSecret(_accessTokenSecret);

        TwitterStreamFactory tf = new TwitterStreamFactory(cb.build());
        this.twitterStream = tf.getInstance();
    }

    private void filterTweets() {
        String trackedTerms = System.getenv().get("TWITTER_TRACKED_TERMS");
        FilterQuery query = new FilterQuery();
        query.track(trackedTerms.split(","));
        this.twitterStream.filter(query);
    }

    private void addStatusListener() {
        this.statusListener =  new StatusListener() {

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onStatus(Status status) {
                TweetModel tweet = new TweetModel(status.getUser().getName(),
                        status.getText(), status.getCreatedAt());
                System.out.println(tweet);
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                System.out.println("[DEBUG] At StatusListener - onDeletionNotice");
            }

            @Override
            public void onTrackLimitationNotice(int i) {
                System.out.println("[DEBUG] At StatusListener - onTrackLimitationNotice");
            }

            @Override
            public void onScrubGeo(long l, long l1) {
                System.out.println("[DEBUG] At StatusListener - onScrubGeo");
            }

            @Override
            public void onStallWarning(StallWarning stallWarning) {
                System.out.println("[DEBUG] At StatusListener - onStallWarning");
            }
        };
        this.twitterStream.addListener(this.statusListener);
    }

    private void removeStatusListener() {
        this.twitterStream.removeListener(this.statusListener);
    }
}
