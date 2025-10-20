package twitter;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class SocialNetworkTest {

    // Test Case 1: Empty List of Tweets
    @Test
    public void testEmptyListOfTweets() {
        List<Tweet> tweets = Collections.emptyList();
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(tweets);
        
        assertTrue("Graph should be empty for empty tweet list", graph.isEmpty());
    }

        // Test Case 2: Tweets Without Mentions - WITH DEBUGGING
    @Test
    public void testTweetsWithoutMentions() {
        List<Tweet> tweets = Arrays.asList(
            new Tweet(1, "user1", "This is a simple tweet without mentions", Instant.now()),
            new Tweet(2, "user2", "Another tweet with no @mentions", Instant.now())
        );
        
        // Check what Extract.getMentionedUsers returns for these tweets
        Set<String> mentions1 = Extract.getMentionedUsers(Collections.singletonList(tweets.get(0)));
        Set<String> mentions2 = Extract.getMentionedUsers(Collections.singletonList(tweets.get(1)));
        
        System.out.println("DEBUG: Mentions in tweet 1: " + mentions1);
        System.out.println("DEBUG: Mentions in tweet 2: " + mentions2);
        
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(tweets);
        
        System.out.println("DEBUG: Graph contents: " + graph);
        
        assertFalse("Graph should contain users", graph.isEmpty());
        assertTrue("user1 should be in graph", graph.containsKey("user1"));
        assertTrue("user2 should be in graph", graph.containsKey("user2"));
        
        // Check if follows sets exist and are empty
        assertNotNull("user1 should have a follows set", graph.get("user1"));
        assertNotNull("user2 should have a follows set", graph.get("user2"));
        assertTrue("user1 should have empty follows set, but has: " + graph.get("user1"), 
                   graph.get("user1").isEmpty());
        assertTrue("user2 should have empty follows set, but has: " + graph.get("user2"), 
                   graph.get("user2").isEmpty());
    }

    // Test Case 3: Single Mention
    @Test
    public void testSingleMention() {
        List<Tweet> tweets = Arrays.asList(
            new Tweet(1, "alice", "Hello @bob how are you?", Instant.now())
        );
        
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(tweets);
        
        assertEquals("Graph should have 1 user", 1, graph.size());
        assertTrue("alice should be in graph", graph.containsKey("alice"));
        assertTrue("alice should follow bob", graph.get("alice").contains("bob"));
        assertEquals("alice should follow exactly 1 user", 1, graph.get("alice").size());
    }

    // Test Case 4: Multiple Mentions
    @Test
    public void testMultipleMentions() {
        List<Tweet> tweets = Arrays.asList(
            new Tweet(1, "alice", "Hello @bob and @charlie!", Instant.now())
        );
        
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(tweets);
        
        Set<String> expectedFollows = new HashSet<>(Arrays.asList("bob", "charlie"));
        assertEquals("alice should follow 2 users", expectedFollows, graph.get("alice"));
    }

    // Test Case 5: Multiple Tweets from One User
    @Test
    public void testMultipleTweetsFromOneUser() {
        List<Tweet> tweets = Arrays.asList(
            new Tweet(1, "alice", "Hello @bob", Instant.now()),
            new Tweet(2, "alice", "Hi @charlie", Instant.now()),
            new Tweet(3, "alice", "Hey @bob again", Instant.now())
        );
        
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(tweets);
        
        Set<String> expectedFollows = new HashSet<>(Arrays.asList("bob", "charlie"));
        assertEquals("alice should follow both bob and charlie", expectedFollows, graph.get("alice"));
        assertEquals("Should have exactly 2 follows", 2, graph.get("alice").size());
    }

    // Test Case 6: Self-mention should be ignored
    @Test
    public void testSelfMentionIgnored() {
        List<Tweet> tweets = Arrays.asList(
            new Tweet(1, "alice", "Hello @alice this is me!", Instant.now())
        );
        
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(tweets);
        
        assertTrue("alice should not follow herself", graph.get("alice").isEmpty());
    }

    // Test Case 7: Case insensitive handling
    @Test
    public void testCaseInsensitive() {
        List<Tweet> tweets = Arrays.asList(
            new Tweet(1, "Alice", "Hello @Bob and @CHARLIE!", Instant.now())
        );
        
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(tweets);
        
        assertTrue("alice (lowercase) should be in graph", graph.containsKey("alice"));
        assertTrue("should contain bob (lowercase)", graph.get("alice").contains("bob"));
        assertTrue("should contain charlie (lowercase)", graph.get("alice").contains("charlie"));
    }

    // Test Case 8: Empty Graph for influencers
    @Test
    public void testInfluencersEmptyGraph() {
        Map<String, Set<String>> emptyGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(emptyGraph);
        
        assertTrue("Influencers list should be empty for empty graph", influencers.isEmpty());
    }

    // Test Case 9: Single User Without Followers
    @Test
    public void testSingleUserWithoutFollowers() {
        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("alice", new HashSet<>());
        
        List<String> influencers = SocialNetwork.influencers(graph);
        
        assertTrue("Influencers list should be empty when no one has followers", influencers.isEmpty());
    }

    // Test Case 10: Single Influencer
    @Test
    public void testSingleInfluencer() {
        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("alice", new HashSet<>(Arrays.asList("bob")));
        graph.put("charlie", new HashSet<>(Arrays.asList("bob")));
        graph.put("dave", new HashSet<>(Arrays.asList("bob")));
        
        List<String> influencers = SocialNetwork.influencers(graph);
        
        assertEquals("Should have exactly 1 influencer", 1, influencers.size());
        assertEquals("bob should be the only influencer", "bob", influencers.get(0));
    }

    // Test Case 11: Multiple Influencers
    @Test
    public void testMultipleInfluencers() {
        Map<String, Set<String>> graph = new HashMap<>();
        // bob has 3 followers
        graph.put("alice", new HashSet<>(Arrays.asList("bob")));
        graph.put("charlie", new HashSet<>(Arrays.asList("bob")));
        graph.put("dave", new HashSet<>(Arrays.asList("bob")));
        
        // charlie has 2 followers
        graph.put("eve", new HashSet<>(Arrays.asList("charlie")));
        graph.put("frank", new HashSet<>(Arrays.asList("charlie")));
        
        // dave has 1 follower
        graph.put("grace", new HashSet<>(Arrays.asList("dave")));
        
        List<String> influencers = SocialNetwork.influencers(graph);
        
        assertEquals("Should have 3 influencers", 3, influencers.size());
        assertEquals("bob should be first (most followers)", "bob", influencers.get(0));
        assertEquals("charlie should be second", "charlie", influencers.get(1));
        assertEquals("dave should be third", "dave", influencers.get(2));
    }

    // Test Case 12: Tied Influence
    @Test
    public void testTiedInfluence() {
        Map<String, Set<String>> graph = new HashMap<>();
        // bob and charlie both have 2 followers
        graph.put("alice", new HashSet<>(Arrays.asList("bob", "charlie")));
        graph.put("dave", new HashSet<>(Arrays.asList("bob")));
        graph.put("eve", new HashSet<>(Arrays.asList("charlie")));
        
        List<String> influencers = SocialNetwork.influencers(graph);
        
        assertEquals("Should have 2 influencers", 2, influencers.size());
        // Both should be present, order doesn't matter for ties in this implementation
        assertTrue("Should contain bob", influencers.contains("bob"));
        assertTrue("Should contain charlie", influencers.contains("charlie"));
    }

    // Test Case 13: Complex scenario with multiple users and mentions
    @Test
    public void testComplexScenario() {
        List<Tweet> tweets = Arrays.asList(
            new Tweet(1, "alice", "Hello @bob @charlie", Instant.now()),
            new Tweet(2, "bob", "Hi @alice @dave", Instant.now()),
            new Tweet(3, "charlie", "Hey @alice @bob @eve", Instant.now()),
            new Tweet(4, "alice", "Thanks @bob!", Instant.now())
        );
        
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(tweets);
        
        // Verify graph structure
        assertEquals("Should have 3 users", 3, graph.size());
        assertEquals("alice should follow bob and charlie", 
                     new HashSet<>(Arrays.asList("bob", "charlie")), graph.get("alice"));
        assertEquals("bob should follow alice and dave", 
                     new HashSet<>(Arrays.asList("alice", "dave")), graph.get("bob"));
        assertEquals("charlie should follow alice, bob, and eve", 
                     new HashSet<>(Arrays.asList("alice", "bob", "eve")), graph.get("charlie"));
        
        // Test influencers
        List<String> influencers = SocialNetwork.influencers(graph);
        
        // alice has 2 followers (bob, charlie)
        // bob has 2 followers (alice, charlie)  
        // charlie has 1 follower (alice)
        // dave has 1 follower (bob)
        // eve has 1 follower (charlie)
        
        assertTrue("alice should be an influencer", influencers.contains("alice"));
        assertTrue("bob should be an influencer", influencers.contains("bob"));
        // Check that the top influencers are alice and bob (both have 2 followers)
        assertTrue("Top influencers should include alice and bob", 
                   influencers.indexOf("alice") <= 1 && influencers.indexOf("bob") <= 1);
    }

    // Additional test to debug the failing case
    @Test
    public void testSpecificTweetsWithoutMentions() {
        // Create tweets that definitely have no mentions
        List<Tweet> tweets = Arrays.asList(
            new Tweet(1, "user1", "This is a simple tweet", Instant.now()),
            new Tweet(2, "user2", "Another tweet here", Instant.now())
        );
        
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(tweets);
        
        System.out.println("Graph contents: " + graph);
        System.out.println("user1 follows: " + graph.get("user1"));
        System.out.println("user2 follows: " + graph.get("user2"));
        
        assertTrue("user1 should have empty follows set", graph.get("user1").isEmpty());
        assertTrue("user2 should have empty follows set", graph.get("user2").isEmpty());
    }
}